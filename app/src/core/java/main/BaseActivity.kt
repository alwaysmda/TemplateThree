package main

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.xodus.templatethree.R
import com.zeugmasolutions.localehelper.LocaleAwareCompatActivity
import event.OnActivityResultEvent
import event.OnRequestPermissionResultEvent
import http.*
import kotlinx.android.synthetic.main.activity_base.*
import org.greenrobot.eventbus.EventBus
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import util.*
import view.TemplateFragment
import view.TemplateRoomFragment
import java.util.*

class BaseActivity : LocaleAwareCompatActivity(), OnResponseListener, KodeinAware {
    companion object {
        const val TAB_ONE = 0
        const val TAB_TWO = 1
    }

    override val kodein: Kodein by closestKodein()
    private lateinit var fragmentTable: ArrayList<ArrayList<BaseFragment>>
    private lateinit var currentFragment: BaseFragment
    private var currentTabIndex = TAB_ONE
    private val startMode = StartMode.MultiInstance
    private val exitMode = ExitMode.BackToFirstTab
    private var barHeight: Int = 0
    private val client : Client by instance()
    private val appClass : ApplicationClass by instance()

    private enum class StartMode {
        SingleInstance, MultiInstance
    }

    private enum class ExitMode {
        Normal, BackToFirstTab
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        appClass.initFont()
        super.onCreate(savedInstanceState)
        handleIntent()
        setContentView(R.layout.activity_base)
        initFragmentTable(
            TemplateFragment.newInstance(),
            TemplateRoomFragment.newInstance()
            //            TemplateFragment.newInstance(),
            //            TemplateFragment.newInstance(),
        )
        initBottomBar()
        bar.post { barHeight = bar.height }
    }

    override fun onResume() {
        super.onResume()
        Handler().postDelayed({ appClass.setPref(Constant.PREF_CRASH_REPEATING, false) }, 2000)
    }

    override fun onResponse(response: Response) {
        log("BASE ACTIVITY", response.toJSONObject().toString())
    }

    override fun onProgress(request: Request, bytesWritten: Long, totalSize: Long, percent: Int) {

    }

    private fun handleIntent() {
        intent.extras?.let {
            val jsonObject = convertBundleToJson(it)
            try {
                if (jsonObject.has("crash")) {
                    Toast.makeText(appClass, "-=REPORTING CRASH=-", Toast.LENGTH_SHORT).show()
                    client.request(
                        API.ReportError(
                            this,
                            jsonObject.getString("time"),
                            jsonObject.getString("class"),
                            jsonObject.getString("method"),
                            jsonObject.getString("message")
                        )
                    )
                } else if (jsonObject.has("class")) {
                    if (jsonObject.getString("class") == "BaseActivity") {
                        if (jsonObject.has("action")) {
                            when (jsonObject.getString("action")) {
                                "toast" -> toast(jsonObject.toString())
                                "pref"  -> appClass.setPref(Constant.valueOf(jsonObject.getString("pref_key")), jsonObject.get("pref_value"))
                            }
                        }
                    } else {
                        startActivity(Intent(this, Class.forName(applicationInfo.packageName.replace(".debug", "") + ".activity." + jsonObject.getString("class"))).putExtras(it))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun start(fragment: BaseFragment, replace: Boolean = false) {
        val frag = supportFragmentManager.findFragmentByTag(currentTabIndex.toString() + fragment.javaClass.toString()) as BaseFragment?
        val fragmentTransaction = supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)

        val sharedElementList = currentFragment.getSharedElementListOut()
        if (sharedElementList.isNotEmpty()) {
            for (i in sharedElementList.indices) {
                if (sharedElementList[i].id != 0)
                    fragmentTransaction.addSharedElement(
                        sharedElementList[i].view!!,
                        sharedElementList[i].tag
                    )
            }
            fragment.sharedElementListIn = sharedElementList
        }

        if (replace) {
            fragmentTransaction.remove(currentFragment)
            if (frag == null || startMode == StartMode.MultiInstance) {
                fragmentTransaction.add(R.id.main_frameLayout, fragment, currentTabIndex.toString() + fragment.javaClass.toString())
                currentFragment = fragment
                fragmentTable[currentTabIndex].removeAt(fragmentTable[currentTabIndex].size - 1)
                fragmentTable[currentTabIndex].add(currentFragment)
            } else {
                fragmentTransaction.show(frag)
                currentFragment = frag
                fragmentTable[currentTabIndex].removeAt(fragmentTable[currentTabIndex].size - 1)
                for (i in 0 until fragmentTable[currentTabIndex].size) {
                    if (fragmentTable[currentTabIndex][i].tag.equals(currentTabIndex.toString() + fragment.javaClass.toString())) {
                        fragmentTable[currentTabIndex].removeAt(i)
                        fragmentTable[currentTabIndex].add(currentFragment)
                        break
                    }
                }
            }
        } else {
            fragmentTransaction.hide(currentFragment)
            if (frag == null || startMode == StartMode.MultiInstance) {
                fragmentTransaction.add(R.id.main_frameLayout, fragment, currentTabIndex.toString() + fragment.javaClass.toString())
                currentFragment = fragment
                fragmentTable[currentTabIndex].add(currentFragment)
            } else {
                fragmentTransaction.show(frag)
                currentFragment = frag
                for (i in 0 until fragmentTable[currentTabIndex].size) {
                    if (fragmentTable[currentTabIndex][i].tag.equals(currentTabIndex.toString() + fragment.javaClass.toString())) {
                        fragmentTable[currentTabIndex].removeAt(i)
                        fragmentTable[currentTabIndex].add(currentFragment)
                        break
                    }
                }
            }
        }
        fragmentTransaction.commit()
    }


    fun show(fragment: BaseFragment) {
        val fragmentTransaction = supportFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)

        val sharedElementList = currentFragment.getSharedElementListOut()
        if (sharedElementList.isNotEmpty()) {
            for (i in sharedElementList.indices) {
                fragmentTransaction.addSharedElement(
                    sharedElementList[i].view!!,
                    sharedElementList[i].tag
                )
            }
            fragment.sharedElementListIn = sharedElementList
        }

        val view = main_frameLayout
        fragment.background = convertBitmapToDrawable(
            convertViewToBitmap(
                view,
                view.measuredWidth,
                view.measuredHeight
            )
        )

        fragmentTransaction
            .hide(currentFragment)
            .add(R.id.main_frameLayout, fragment, currentTabIndex.toString() + fragment.javaClass.toString())
            .commit()
        fragmentTable[currentTabIndex].add(fragment)
        currentFragment = fragment
    }

    fun selectTab(index: Int) {
        when (index) {
            TAB_ONE   -> bar.selectedItemId = R.id.navigation_one
            TAB_TWO   -> bar.selectedItemId = R.id.navigation_two
        }
    }


    fun initFragmentTable(vararg fragments: BaseFragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        for (item in supportFragmentManager.fragments) {
            fragmentTransaction.remove(item)
        }
        fragmentTransaction.commit()

        fragmentTable = ArrayList()
        for (i in fragments.indices) {
            val fragmentList = ArrayList<BaseFragment>()
            fragmentList.add(fragments[i])
            fragmentTable.add(fragmentList)
            supportFragmentManager.beginTransaction()
                .add(R.id.main_frameLayout, fragments[i], "base" + i + fragments[i].javaClass.toString())
                .hide(fragments[i])
                .commit()
        }
        currentFragment = fragments[0]
        supportFragmentManager.beginTransaction()
            .show(currentFragment)
            .commit()
    }

    fun getTabFragmentTable(): List<BaseFragment> {
        return fragmentTable[currentTabIndex]
    }

    fun getFragmentTable(): List<List<BaseFragment>>? {
        return fragmentTable
    }


    fun getCurrentFragment(): Fragment? {
        return currentFragment
    }

    private fun tabSelected() {
        //        GlobalClass.setStatusbarColor(this, ContextCompat.getColor(appClass, R.color.colorPrimaryDark));
        val selectedFragment = fragmentTable[currentTabIndex][fragmentTable[currentTabIndex].size - 1]
        supportFragmentManager.beginTransaction().hide(currentFragment).show(selectedFragment).commit()
        currentFragment = selectedFragment
    }

    private fun tabReselected() {
        if (fragmentTable[currentTabIndex].size > 1) {
            for (size in fragmentTable[currentTabIndex].size - 1 downTo 2) {
                supportFragmentManager.beginTransaction()
                    .remove(fragmentTable[currentTabIndex][size])
                    .commit()
                fragmentTable[currentTabIndex].removeAt(size)
            }

            currentFragment = fragmentTable[currentTabIndex][1]
            supportFragmentManager.beginTransaction()
                .show(currentFragment)
                .commit()
            onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        EventBus.getDefault().post(OnActivityResultEvent(requestCode, resultCode, data))
    }

    override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<String>, @NonNull grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EventBus.getDefault().post(OnRequestPermissionResultEvent(requestCode, permissions, grantResults))
    }

    fun setNavigationBarEnabled(enabled: Boolean) {
        for (i in 0 until bar.menu.size()) {
            bar.menu.getItem(i).isEnabled = enabled
        }
    }

    fun showHideNavigationBar(hide: Boolean, duration: Long = 500, onFinish: () -> (Unit) = {}) {
        if (barHeight == 0) {
            bar.post {
                barHeight = bar.height
                showHideNavigationBar(hide, 0, onFinish)
            }
        } else {
            val animator: ValueAnimator = if (hide) {
                ValueAnimator.ofInt(bar.height, 0)
            } else {
                ValueAnimator.ofInt(bar.height, barHeight)
            }
            animator.duration = duration
            animator.addUpdateListener {
                val params = bar.layoutParams as LinearLayout.LayoutParams
                params.height = it.animatedValue as Int
                bar.layoutParams = params
            }
            animator.start()
            animator.doOnEnd {
                onFinish()
            }
        }
    }


    private fun initBottomBar() {
        bar.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_one   -> if (currentTabIndex == TAB_ONE) {
                    tabReselected()
                } else {
                    currentTabIndex = TAB_ONE
                    tabSelected()
                }
                R.id.navigation_two   -> if (currentTabIndex == TAB_TWO) {
                    tabReselected()
                } else {
                    currentTabIndex = TAB_TWO
                    tabSelected()
                }
            }
            true
        }
    }

    override fun onBackPressed() {
        if (currentFragment.isBackDisabled) {
            currentFragment.onBackPressed?.onBackPressed()
        } else {
            val currentFragmentList = fragmentTable[currentTabIndex]
            if (currentFragmentList.size > 1) {
                val fragmentTransaction = supportFragmentManager
                    .beginTransaction()
                    .setReorderingAllowed(true)
                val sharedElementList = currentFragment.sharedElementListIn
                if (currentFragment.view != null && sharedElementList.isNotEmpty()) {
                    val recyclerView = currentFragmentList[currentFragmentList.size - 2].sharedElementRecyclerView
                    recyclerView?.let { rv ->
                        val tag = currentFragment.sharedElementRecyclerViewReturnView?.tag.toString()
                        val holder = rv.findViewHolderForAdapterPosition(currentFragment.sharedElementRecyclerViewPosition)
                        holder?.let { viewHolder ->
                            val view: View? = viewHolder.itemView.findViewById(currentFragmentList[currentFragmentList.size - 2].sharedElementRecyclerViewViewID)
                            view?.let { _view ->
                                ViewCompat.setTransitionName(_view, tag)
                                fragmentTransaction.addSharedElement(_view, tag)
                            }
                        }
                    } ?: run {
                        for (i in sharedElementList.indices) {
                            if (sharedElementList[i].isEnable) {
                                val tag = sharedElementList[i].tag
                                val view: View? = currentFragment.view?.findViewWithTag(tag)
                                view?.let { fragmentTransaction.addSharedElement(it, tag) }
                            }
                        }
                    }
                }
                fragmentTransaction
                    .remove(currentFragment)
                    .show(currentFragmentList[currentFragmentList.size - 2])
                    .commit()
                currentFragmentList.removeAt(currentFragmentList.size - 1)
                currentFragment = currentFragmentList[currentFragmentList.size - 1]
            } else if (currentTabIndex != 0 && exitMode == ExitMode.BackToFirstTab) {
                bar.selectedItemId = R.id.navigation_one
            } else {
                super.onBackPressed()
            }
        }
    }
}

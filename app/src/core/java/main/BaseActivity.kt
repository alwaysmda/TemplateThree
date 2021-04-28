package main

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.animation.doOnEnd
import androidx.core.view.ViewCompat
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentTransaction
import com.xodus.templatethree.R
import com.xodus.templatethree.databinding.ActivityBaseBinding
import event.OnActivityResultEvent
import event.OnRequestPermissionResultEvent
import kotlinx.android.synthetic.main.activity_base.*
import org.greenrobot.eventbus.EventBus
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import util.*


class BaseActivity : ThemeAwareActivity(), KodeinAware {
    companion object {
        @Volatile
        private lateinit var instance: BaseActivity
        fun getInstance() = instance

        const val TAB_ONE = 0
        const val TAB_TWO = 1
    }

    override val kodein: Kodein by closestKodein()
    private val appClass: ApplicationClass by instance()
    lateinit var fragmentTable: ArrayList<ArrayList<BaseFragment<ViewDataBinding, BaseViewModel>>>
    lateinit var currentFragment: BaseFragment<ViewDataBinding, BaseViewModel>
    private var barHeight: Int = 0
    lateinit var binding: ActivityBaseBinding
    var currentTabIndex = TAB_ONE

    //Options
    private val startMode = StartMode.MultiInstance
    private val exitMode = ExitMode.BackToFirstTab
    private val transitionAnimation: Int = FragmentTransaction.TRANSIT_FRAGMENT_FADE

    private enum class StartMode {
        SingleInstance, MultiInstance
    }

    private enum class ExitMode {
        Normal, BackToFirstTab
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this
        binding = ActivityBaseBinding.inflate(layoutInflater)
        appClass.initFont()
        handleIntent()
        setContentView(binding.root)
        initFragmentTable(
            BaseFragmentFactory.templateFragment(),
            BaseFragmentFactory.templateRoomFragment()
        )
        initBottomBar()
        binding.baseBar.post { barHeight = binding.baseBar.height }
    }


    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({ appClass.setPref(PREF_CRASH_REPEATING, false) }, 2000)
    }

    fun setLoading(loading: Boolean, hideLoader: Boolean = false) {
        binding.baseLlLoading.fade(loading)
        binding.basePbLoading.fade(hideLoader.not())
    }

    private fun handleIntent() {
        intent.extras?.let {
            val jsonObject = convertBundleToJson(it)
            try {
                if (jsonObject.has("crash")) {
                    Toast.makeText(appClass, "-=REPORTING CRASH=-", Toast.LENGTH_SHORT).show()
                    //                    client.request(
                    //                        API.ReportError(
                    //                            this,
                    //                            jsonObject.getString("time"),
                    //                            jsonObject.getString("class"),
                    //                            jsonObject.getString("method"),
                    //                            jsonObject.getString("message")
                    //                        )
                    //                    )
                } else if (jsonObject.has("class")) {
                    if (jsonObject.getString("class") == "BaseActivity") {
                        if (jsonObject.has("action")) {
                            when (jsonObject.getString("action")) {
                                "toast" -> toast(jsonObject.toString())
                                "pref" -> appClass.setPref(jsonObject.getString("pref_key"), jsonObject.get("pref_value"))
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

    @Suppress("UNCHECKED_CAST")
    fun start(fragment: BaseFragment<ViewDataBinding, BaseViewModel>, replace: Boolean = false) {
        val frag = supportFragmentManager.findFragmentByTag(currentTabIndex.toString() + fragment.javaClass.toString()) as BaseFragment<ViewDataBinding, BaseViewModel>?
        val fragmentTransaction = supportFragmentManager
            .beginTransaction()
            .setTransition(transitionAnimation)
            .setReorderingAllowed(true)


        val sharedElementList = currentFragment.sharedElementListOut
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
                fragmentTransaction.add(R.id.base_frameLayout, fragment, currentTabIndex.toString() + fragment.javaClass.toString())
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
                fragmentTransaction.add(R.id.base_frameLayout, fragment, currentTabIndex.toString() + fragment.javaClass.toString())
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


    fun show(fragment: BaseFragment<ViewDataBinding, BaseViewModel>) {
        val fragmentTransaction = supportFragmentManager
            .beginTransaction()
            .setTransition(transitionAnimation)
            .setReorderingAllowed(true)


        val sharedElementList = currentFragment.sharedElementListOut
        if (sharedElementList.isNotEmpty()) {
            for (i in sharedElementList.indices) {
                fragmentTransaction.addSharedElement(
                    sharedElementList[i].view!!,
                    sharedElementList[i].tag
                )
            }
            fragment.sharedElementListIn = sharedElementList
        }

        val view = base_frameLayout
        fragment.background = convertBitmapToDrawable(
            convertViewToBitmap(
                view,
                view.measuredWidth,
                view.measuredHeight
            )
        )

        fragmentTransaction
            .hide(currentFragment)
            .add(R.id.base_frameLayout, fragment, currentTabIndex.toString() + fragment.javaClass.toString())
            .commit()
        fragmentTable[currentTabIndex].add(fragment)
        currentFragment = fragment
    }

    fun selectTab(index: Int) {
        when (index) {
            TAB_ONE -> binding.baseBar.selectedItemId = R.id.navigation_one
            TAB_TWO -> binding.baseBar.selectedItemId = R.id.navigation_two
        }
    }


    fun initFragmentTable(vararg fragments: BaseFragment<ViewDataBinding, BaseViewModel>) {
        currentTabIndex = 0
        val fragmentTransaction = supportFragmentManager
            .beginTransaction()
            .setTransition(transitionAnimation)

        for (item in supportFragmentManager.fragments) {
            fragmentTransaction.remove(item)
        }
        fragmentTransaction.commit()

        fragmentTable = ArrayList()
        for (i in fragments.indices) {
            val fragmentList = ArrayList<BaseFragment<ViewDataBinding, BaseViewModel>>()
            fragmentList.add(fragments[i])
            fragmentTable.add(fragmentList)
            supportFragmentManager
                .beginTransaction()
                .setTransition(transitionAnimation)
                .add(R.id.base_frameLayout, fragments[i], "base" + i + fragments[i].javaClass.toString())
                .hide(fragments[i])
                .commit()
        }
        currentFragment = fragments[0]
        supportFragmentManager
            .beginTransaction()
            .setTransition(transitionAnimation)
            .show(currentFragment)
            .commit()
    }

    fun getTabFragmentTable(): List<BaseFragment<ViewDataBinding, BaseViewModel>> {
        return fragmentTable[currentTabIndex]
    }

    private fun tabSelected() {
        //        GlobalClass.setStatusbarColor(this, ContextCompat.getColor(appClass, R.color.colorPrimaryDark));
        val selectedFragment = fragmentTable[currentTabIndex][fragmentTable[currentTabIndex].size - 1]

        supportFragmentManager
            .beginTransaction()
            .setTransition(transitionAnimation)
            .hide(currentFragment)
            .show(selectedFragment)
            .commit()

        currentFragment = selectedFragment
    }

    private fun tabReselected() {
        if (fragmentTable[currentTabIndex].size > 1) {
            for (size in fragmentTable[currentTabIndex].size - 1 downTo 2) {
                supportFragmentManager
                    .beginTransaction()
                    .setTransition(transitionAnimation)
                    .remove(fragmentTable[currentTabIndex][size])
                    .commit()
                fragmentTable[currentTabIndex].removeAt(size)
            }

            currentFragment = fragmentTable[currentTabIndex][1]
            supportFragmentManager
                .beginTransaction()
                .setTransition(transitionAnimation)
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
        for (i in 0 until binding.baseBar.menu.size()) {
            binding.baseBar.menu.getItem(i).isEnabled = enabled
        }
    }

    fun showHideNavigationBar(hide: Boolean, duration: Long = 500, onFinish: () -> (Unit) = {}) {
        if (barHeight == 0) {
            binding.baseBar.post {
                barHeight = binding.baseBar.height
                showHideNavigationBar(hide, 0, onFinish)
            }
        } else {
            val animator: ValueAnimator = if (hide) {
                ValueAnimator.ofInt(binding.baseBar.height, 0)
            } else {
                ValueAnimator.ofInt(binding.baseBar.height, barHeight)
            }
            animator.duration = duration
            animator.addUpdateListener {
                val params = binding.baseBar.layoutParams as LinearLayout.LayoutParams
                params.height = it.animatedValue as Int
                binding.baseBar.layoutParams = params
            }
            animator.start()
            animator.doOnEnd {
                onFinish()
            }
        }
    }

    fun resetBottomBarTitles() {
        binding.baseBar.menu.getItem(0).title = appClass.one
        binding.baseBar.menu.getItem(1).title = appClass.two
    }

    private fun initBottomBar() {
        resetBottomBarTitles()
        binding.baseBar.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_one -> if (currentTabIndex == TAB_ONE) {
                    tabReselected()
                } else {
                    currentTabIndex = TAB_ONE
                    tabSelected()
                }
                R.id.navigation_two -> if (currentTabIndex == TAB_TWO) {
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
        if (currentFragment.viewModel.onBackPressed(currentFragment.snackBack)) {
            val currentFragmentList = fragmentTable[currentTabIndex]
            if (currentFragmentList.size > 1) {
                val fragmentTransaction = supportFragmentManager
                    .beginTransaction()
                    .setTransition(transitionAnimation)
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
                binding.baseBar.selectedItemId = R.id.navigation_one
            } else {
                super.onBackPressed()
            }
        }
    }
}

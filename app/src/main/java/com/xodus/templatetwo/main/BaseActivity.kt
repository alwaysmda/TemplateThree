package com.xodus.templatetwo.main

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.xodus.templatetwo.R
import com.xodus.templatetwo.event.OnActivityResultEvent
import com.xodus.templatetwo.event.OnRequestPermissionResultEvent
import com.xodus.templatetwo.fragment.TemplateFragment
import com.xodus.templatetwo.http.Client
import kotlinx.android.synthetic.main.activity_base.*
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

class BaseActivity : AppCompatActivity() {

    lateinit var appClass: ApplicationClass
    private lateinit var fragmentTable: ArrayList<ArrayList<BaseFragment>>
    private lateinit var currentFragment: BaseFragment
    private var currentTabIndex = 0
    private val startMode = StartMode.MultiInstance
    private val exitMode = ExitMode.BackToFirstTab
    private var barHeight: Int = 0
    lateinit var client: Client

    private enum class StartMode {
        SingleInstance, MultiInstance
    }

    private enum class ExitMode {
        Normal, BackToFirstTab
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        appClass = ApplicationClass().getInstance(this)
        client = Client(this)
        initFragmentTable(
            TemplateFragment.newInstance()
        )
        initBottomBar()
        bar.post { barHeight = bar.height }
    }


    fun replace(fragment: BaseFragment) {
        val frag =
            supportFragmentManager.findFragmentByTag(currentTabIndex.toString() + fragment.javaClass.toString()) as BaseFragment
        if (startMode == StartMode.MultiInstance) {
            val fragmentTransaction = supportFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)

            val sharedElementList = currentFragment.getSharedElementListOut()
            if (sharedElementList.isNotEmpty()) {
                for (i in sharedElementList.indices) {
                    fragmentTransaction.addSharedElement(
                        sharedElementList[i].view!!,
                        sharedElementList[i].tag!!
                    )
                }
                fragment.sharedElementListIn = sharedElementList
            }

            fragmentTransaction
                .remove(currentFragment)
                .add(R.id.main_frameLayout, fragment, currentTabIndex.toString() + fragment.javaClass.toString())
                .commit()
            fragmentTable[currentTabIndex].removeAt(fragmentTable[currentTabIndex].size - 1)
            fragmentTable[currentTabIndex].add(fragment)
            currentFragment = fragment
        } else {
            frag.arguments = fragment.arguments
            val fragmentTransaction = supportFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)

            val sharedElementList = currentFragment.getSharedElementListOut()
            if (sharedElementList.isNotEmpty()) {
                for (i in sharedElementList.indices) {
                    fragmentTransaction.addSharedElement(
                        sharedElementList[i].view!!,
                        sharedElementList[i].tag!!
                    )
                }
                fragment.sharedElementListIn = sharedElementList
            }

            fragmentTransaction
                .remove(currentFragment)
                .show(frag)
                .commit()
            fragmentTable[currentTabIndex].removeAt(fragmentTable[currentTabIndex].size - 1)
            for (i in 0 until fragmentTable[currentTabIndex].size) {
                if (fragmentTable[currentTabIndex][i].tag.equals(currentTabIndex.toString() + fragment.javaClass.toString())) {
                    fragmentTable[currentTabIndex].removeAt(i)
                    fragmentTable[currentTabIndex].add(frag)
                    break
                }
            }
            currentFragment = frag
        }
    }


    fun start(fragment: BaseFragment) {
        val frag =
            supportFragmentManager.findFragmentByTag(currentTabIndex.toString() + fragment.javaClass.toString()) as BaseFragment
        if (startMode == StartMode.MultiInstance) {
            val fragmentTransaction = supportFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)

            val sharedElementList = currentFragment.getSharedElementListOut()
            if (sharedElementList.isNotEmpty()) {
                for (i in sharedElementList.indices) {
                    if (sharedElementList[i].id != 0)
                        fragmentTransaction.addSharedElement(
                            sharedElementList[i].view!!,
                            sharedElementList[i].tag!!
                        )
                }
                fragment.sharedElementListIn = sharedElementList
            }

            fragmentTransaction
                .hide(currentFragment)
                .add(R.id.main_frameLayout, fragment, currentTabIndex.toString() + fragment.javaClass.toString())
                .commit()
            fragmentTable[currentTabIndex].add(fragment)
            currentFragment = fragment
        } else {
            frag.arguments = fragment.arguments
            val fragmentTransaction = supportFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)

            val sharedElementList = currentFragment.getSharedElementListOut()
            if (sharedElementList.isNotEmpty()) {
                for (i in sharedElementList.indices) {
                    if (sharedElementList[i].id != 0)
                        fragmentTransaction.addSharedElement(
                            sharedElementList[i].view!!,
                            sharedElementList[i].tag!!
                        )
                }
                fragment.sharedElementListIn = sharedElementList
            }

            fragmentTransaction
                .show(frag)
                .hide(currentFragment)
                .commit()
            currentFragment = frag
            for (i in 0 until fragmentTable[currentTabIndex].size) {
                if (fragmentTable[currentTabIndex][i].tag.equals(currentTabIndex.toString() + fragment.javaClass.toString())) {
                    fragmentTable[currentTabIndex].removeAt(i)
                    fragmentTable[currentTabIndex].add(currentFragment)
                }
            }
            currentFragment = frag
        }
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
                    sharedElementList[i].tag!!
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
            0 -> bar.selectedItemId = R.id.navigation_explore
            1 -> bar.selectedItemId = R.id.navigation_feed
        }
    }


    private fun initFragmentTable(vararg fragments: BaseFragment) {
        fragmentTable = ArrayList<ArrayList<BaseFragment>>()
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

    fun hideNavigationBar(hide: Boolean, duration: Long = 500) {
        if (barHeight == 0) {
            bar.post {
                barHeight = bar.height
                hideNavigationBar(hide)
            }
        } else {
            val animator: ValueAnimator = if (hide) {
                ValueAnimator.ofInt(barHeight, 0)
            } else {
                ValueAnimator.ofInt(0, barHeight)
            }
            animator.duration = duration
            animator.addUpdateListener { animation ->
                val params = bar.layoutParams as LinearLayout.LayoutParams
                params.height = animation.animatedValue as Int
                bar.layoutParams = params
            }
            animator.start()
        }
    }


    private fun initBottomBar() {
        bar.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_explore -> if (currentTabIndex == 0) {
                    tabReselected()
                } else {
                    currentTabIndex = 0
                    tabSelected()
                }
                R.id.navigation_feed    -> if (currentTabIndex == 1) {
                    tabReselected()
                } else {
                    currentTabIndex = 1
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
                    val recyclerView =
                        currentFragmentList[currentFragmentList.size - 2].sharedElementRecyclerView
                    recyclerView?.let { rv ->
                        {
                            val tag = currentFragment.sharedElementRecyclerViewReturnView?.tag.toString()
                            val holder =
                                rv.findViewHolderForAdapterPosition(currentFragment.sharedElementRecyclerViewPosition)
                            holder?.let { viewHolder ->
                                val view: View? =
                                    viewHolder.itemView.findViewById(currentFragmentList[currentFragmentList.size - 2].sharedElementRecyclerViewViewID)
                                view?.let { _view ->
                                    ViewCompat.setTransitionName(_view, tag)
                                    fragmentTransaction.addSharedElement(_view, tag)
                                }
                            }
                        }
                    } ?: run {
                        for (i in sharedElementList.indices) {
                            if (sharedElementList[i].isEnable) {
                                val tag = sharedElementList[i].tag
                                tag?.let { string ->
                                    val view: View? = currentFragment.view?.findViewWithTag(string)
                                    view?.let { fragmentTransaction.addSharedElement(it, string) }
                                }
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
                bar.selectedItemId = R.id.navigation_explore
            } else {
                super.onBackPressed()
            }
        }
    }
}

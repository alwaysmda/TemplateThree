package com.xodus.templatetwo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private enum class StartMode {
        SingleInstance, MultiInstance
    }

    private enum class ExitMode {
        Normal, BackToFirstTab
    }


//    private var appClass: ApplicationClass? = null
//    private var baseActivity: BaseActivity? = null
//    private var fragmentTable: MutableList<List<BaseFragment>>? = null
//    private var currentFragment: BaseFragment? = null
//    private var bar: BottomNavigationView? = null
//    private var currentTabIndex = 0
//    private val startMode = StartMode.MultiInstance
//    private val exitMode = ExitMode.BackToFirstTab
//    private var barHeight: Int = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}

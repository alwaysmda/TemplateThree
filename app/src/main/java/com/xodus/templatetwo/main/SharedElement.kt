package com.xodus.templatetwo.main

import android.view.View
import java.util.ArrayList

data class SharedElement(
    var view: View? = null,
    var id: Int = 0,
    var tag: String = "",
    var isEnable: Boolean = false
)

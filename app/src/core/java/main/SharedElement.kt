package main

import android.view.View

data class SharedElement(
    var view: View? = null,
    var id: Int = 0,
    var tag: String = "",
    var isEnable: Boolean = false
)

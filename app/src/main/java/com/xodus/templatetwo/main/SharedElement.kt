package com.xodus.templatetwo.main

import android.view.View
import java.util.ArrayList

data class SharedElement(
    var view: View? = null,
    var id: Int = 0,
    var tag: String = "",
    var isEnable: Boolean = false
) {


    constructor(sharedElement: SharedElement) : this(
        sharedElement.view,
        sharedElement.id,
        sharedElement.tag,
        sharedElement.isEnable
    )

    companion object {
        fun cloneList(requestList: List<SharedElement>): List<SharedElement> {
            val clonedList = ArrayList<SharedElement>(requestList.size)
            for (item in requestList) {
                clonedList.add(SharedElement(item))
            }
            return clonedList
        }
    }
}

package com.xodus.templatetwo.main

import android.view.View
import java.util.ArrayList

class SharedElement {
    var view: View? = null
    var id: Int = 0
    var tag: String? = null
    var isEnable: Boolean = false

    constructor()

    constructor(view: View, id: Int, tag: String, enable: Boolean) {
        this.view = view
        this.id = id
        this.tag = tag
        this.isEnable = enable
    }

    constructor(sharedElement: SharedElement) {
        this.view = sharedElement.view
        this.id = sharedElement.id
        this.tag = sharedElement.tag
        this.isEnable = sharedElement.isEnable
    }

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

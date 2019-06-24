package com.xodus.templatetwo.customview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.widget.TextView
import com.xodus.templatetwo.R
import com.xodus.templatetwo.main.ApplicationClass

class TextViewFonted @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextView(context, attrs, defStyleAttr) {


    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.TextViewFonted, 0, 0)
            val fontEnum = typedArray.getInt(typedArray.getResourceId(R.styleable.TextViewFonted_tvf_font, 0), 0)
            val font: Typeface?
            if (isInEditMode) {
                when (fontEnum) {
                    0    -> {
                        font = Typeface.createFromAsset(context.assets, "iran_sans_bold.ttf")
                    }
                    1    -> {
                        font = Typeface.createFromAsset(context.assets, "iran_sans_light.ttf")
                    }
                    else -> {
                        font = Typeface.createFromAsset(context.assets, "iran_sans_bold.ttf")
                    }
                }
                font?.let { f ->
                    typeface = f
                }
            } else {
                val appClass = ApplicationClass.getInstance()
                when (fontEnum) {
                    0    -> {
                        font = appClass.fontIranSansBold
                    }
                    1    -> {
                        font = appClass.fontIranSansLight
                    }
                    else -> {
                        font = appClass.fontIranSansBold
                    }
                }
            }
            typedArray.recycle()
            font?.let { f ->
                typeface = f
            }
        }
    }


}
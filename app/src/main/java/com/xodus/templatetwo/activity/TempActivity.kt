package com.xodus.templatetwo.activity

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xodus.templatetwo.R
import com.xodus.templatetwo.extention.toast
import com.xodus.templatetwo.fragment.TemplateFragment

class TempActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.temp)
        toast("123")

        supportFragmentManager.beginTransaction()
            .add(R.id.frame, TemplateFragment().newInstance(), "base" )
            .commit()
    }
}
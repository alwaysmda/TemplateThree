package com.xodus.templatetwo.main

import android.app.AlarmManager
import android.app.Application
import android.app.ApplicationErrorReport
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import com.pddstudio.preferences.encrypted.EncryptedPreferences
import com.xodus.templatetwo.BuildConfig
import com.xodus.templatetwo.billing.Market
import com.xodus.templatetwo.main.Constant.*;
import kotlin.system.exitProcess

open class ApplicationClass : Application() {
    companion object {
        @Volatile
        private lateinit var instance: ApplicationClass

        fun getInstance() = instance
    }

    private lateinit var encryptedPreferences: EncryptedPreferences
    lateinit var market: Market
    var fontIranSansBold: Typeface? = null
    var fontIranSansLight: Typeface? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            handleUncaughtException(throwable)
        }
        encryptedPreferences = EncryptedPreferences.Builder(this).withEncryptionPassword(BuildConfig.APPLICATION_ID).build()
        initMarket()
        initFont()
    }


    private fun initMarket() {
        market = when (BuildConfig.FLAVOR) {
            "bazaar"     -> Market.init(Market.MarketType.BAZAAR)
            "myket"      -> Market.init(Market.MarketType.MYKET)
            "iranapps"   -> Market.init(Market.MarketType.IRANAPPS)
            "googleplay" -> Market.init(Market.MarketType.GOOGLEPLAY)
            else         -> Market.init(Market.MarketType.BAZAAR)
        }
    }

    private fun initFont() {
        assets.list("")?.let {
            if (it.contains("iran_sans_bold.ttf")) {
                fontIranSansBold = Typeface.createFromAsset(assets, "iran_sans_bold.ttf")
            }
            if (it.contains("iran_sans_light.ttf")) {
                fontIranSansLight = Typeface.createFromAsset(assets, "iran_sans_light.ttf")
            }
        }
    }

    fun getStringPref(key: Constant): String? {
        val f = encryptedPreferences.getString(key.toString(), "")
        return if (f == "") null else f
    }

    fun getBooleanPref(key: Constant): Boolean {
        return encryptedPreferences.getBoolean(key.toString(), false)
    }

    fun getIntPref(key: Constant): Int {
        return encryptedPreferences.getInt(key.toString(), 0)
    }

    fun setPref(key: Constant, value: Any) {
        when (value) {
            is String  -> {
                encryptedPreferences.edit().putString(key.value, value).apply()
            }
            is Int     -> {
                encryptedPreferences.edit().putInt(key.value, value).apply()
            }
            is Boolean -> {
                encryptedPreferences.edit().putBoolean(key.value, value).apply()
            }
            is Float   -> {
                encryptedPreferences.edit().putFloat(key.value, value).apply()
            }
            is Long    -> {
                encryptedPreferences.edit().putLong(key.value, value).apply()
            }
        }
    }

    fun setCurrentClass(currentClass: String) {
        setPref(PREF_CURRENT_CLASS, currentClass)
    }

    fun setCurrentMethod(currentMethod: String) {
        setPref(PREF_CURRENT_METHOD, currentMethod)
    }


    private fun handleUncaughtException(paramThrowable: Throwable) {
        if (!getBooleanPref(PREF_CRASH_REPEATING)) {
            setPref(PREF_CRASH_REPEATING, true)
            val intent = Intent(this, BaseActivity::class.java)
            intent.putExtra("crash", true)
            intent.putExtra("message", paramThrowable.toString())
            intent.putExtra("method", getStringPref(PREF_CURRENT_METHOD) ?: paramThrowable.stackTrace[2].methodName + ":" + paramThrowable.stackTrace[2].lineNumber)
            intent.putExtra("class", getStringPref(PREF_CURRENT_CLASS) ?: paramThrowable.stackTrace[2].fileName + ":" + paramThrowable.stackTrace[2].className)
            intent.putExtra("time", System.currentTimeMillis())
            intent.addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP
                        or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        or Intent.FLAG_ACTIVITY_NEW_TASK
            )
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            val mgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, pendingIntent)
        }
        exitProcess(2)
    }
}
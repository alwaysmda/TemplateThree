package main

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.core.content.res.ResourcesCompat
import billing.Market
import com.google.firebase.FirebaseApp
import com.pddstudio.preferences.encrypted.EncryptedPreferences
import com.xodus.templatethree.BuildConfig
import com.xodus.templatethree.R
import com.zeugmasolutions.localehelper.LocaleAwareApplication
import db.TemplateDatabase
import http.Client
import main.Constant.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import util.ViewModelFactory
import util.copyToClipboard
import kotlin.system.exitProcess

open class ApplicationClass : LocaleAwareApplication(), KodeinAware {
    override val kodein by Kodein.lazy {
        import(androidXModule(this@ApplicationClass))
        bind() from singleton { this@ApplicationClass }
        bind() from singleton { Client() }
        bind() from singleton { TemplateDatabase(instance()) }
        bind() from singleton { instance<TemplateDatabase>().templateDao() }
        bind() from provider { ViewModelFactory(instance(), instance(), instance()) }
    }


    companion object {
        @Volatile
        private lateinit var instance: ApplicationClass

        fun getInstance() = instance
    }

    private lateinit var encryptedPreferences: EncryptedPreferences
    lateinit var market: Market
    var fontUltraBold: Typeface? = null
    var fontBold: Typeface? = null
    var fontMedium: Typeface? = null
    var fontLight: Typeface? = null
    var fontUltraLight: Typeface? = null
    lateinit var recyclerViewAnimation: LayoutAnimationController


    override fun onCreate() {
        super.onCreate()
        instance = this
        //        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
        //            handleUncaughtException(throwable)
        //        }
        encryptedPreferences = EncryptedPreferences.Builder(applicationContext).withEncryptionPassword(BuildConfig.APPLICATION_ID).build()
        initSharedPreferences()
        initMarket()
        initFont()
        recyclerViewAnimation = AnimationUtils.loadLayoutAnimation(applicationContext, R.anim.anim_layout_animation)
        FirebaseApp.initializeApp(this)
    }


    private fun initSharedPreferences() {
        if (getBooleanPref(PREF_PREFERENCES_INITIALIZED).not()) {
            setPref(PREF_LANGUAGE, CON_LANG_EN.value)
            setPref(PREF_PREFERENCES_INITIALIZED, true)
        }
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

    fun initFont() {
        if (resources.getIdentifier("font_light", "font", packageName) != 0) {
            fontLight = ResourcesCompat.getFont(this, resources.getIdentifier("font_light", "font", packageName))
        }

        fontBold = if (resources.getIdentifier("font_bold", "font", packageName) != 0) {
            ResourcesCompat.getFont(this, resources.getIdentifier("font_bold", "font", packageName))
        } else {
            fontLight
        }

        fontMedium = if (resources.getIdentifier("font_medium", "font", packageName) != 0) {
            ResourcesCompat.getFont(this, resources.getIdentifier("font_medium", "font", packageName))
        } else {
            fontLight
        }

        fontUltraBold = if (resources.getIdentifier("font_ultra_bold", "font", packageName) != 0) {
            ResourcesCompat.getFont(this, resources.getIdentifier("font_ultra_bold", "font", packageName))
        } else {
            fontLight
        }

        fontUltraLight = if (resources.getIdentifier("font_ultra_light", "font", packageName) != 0) {
            ResourcesCompat.getFont(this, resources.getIdentifier("font_ultra_light", "font", packageName))
        } else {
            fontLight
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
            intent.putExtra("method", getStringPref(PREF_CURRENT_METHOD) ?: paramThrowable.stackTrace[0].methodName + ":" + paramThrowable.stackTrace[0].lineNumber)
            intent.putExtra("class", getStringPref(PREF_CURRENT_CLASS) ?: paramThrowable.stackTrace[0].fileName + ":" + paramThrowable.stackTrace[0].className)
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
        copyToClipboard("message:$paramThrowable\n\nfile:${paramThrowable.stackTrace[0].fileName}\n\nclass:${paramThrowable.stackTrace[0].className}method:${paramThrowable.stackTrace[0].methodName}\n\nline:${paramThrowable.stackTrace[0].lineNumber}")
        exitProcess(2)
    }
}
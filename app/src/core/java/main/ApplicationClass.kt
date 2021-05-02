package main

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import androidx.multidex.MultiDex
import billing.Market
import com.google.firebase.FirebaseApp
import com.pddstudio.preferences.encrypted.EncryptedPreferences
import com.xodus.templatethree.BuildConfig
import com.xodus.templatethree.R
import db.TemplateDatabase
import http.Client
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

open class ApplicationClass : Application(), KodeinAware {
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
        changeLang(Languages.valueOf(getStringPref(PREF_LANGUAGE) ?: Languages.DEFAULT_LANGUAGE.value))
        changeTheme(Themes.valueOf(getStringPref(PREF_THEME) ?: Themes.DEFAULT_THEME.value))
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    private fun initSharedPreferences() {
        if (getBooleanPref(PREF_PREFERENCES_INITIALIZED).not()) {
            setPref(PREF_PREFERENCES_INITIALIZED, true)
        }
    }

    private fun initMarket() {
        market = when (BuildConfig.FLAVOR) {
            "bazaar" -> Market.init(Market.MarketType.BAZAAR)
            "myket" -> Market.init(Market.MarketType.MYKET)
            "iranapps" -> Market.init(Market.MarketType.IRANAPPS)
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

    fun getStringPref(key: String): String? {
        val f = encryptedPreferences.getString(key, "")
        return if (f == "") null else f
    }

    fun getBooleanPref(key: String): Boolean {
        return encryptedPreferences.getBoolean(key, false)
    }

    fun getIntPref(key: String): Int {
        return encryptedPreferences.getInt(key, 0)
    }

    fun getLongPref(key: String): Long {
        return encryptedPreferences.getLong(key, 0L)
    }

    fun getFloatPref(key: String): Float {
        return encryptedPreferences.getFloat(key, 0F)
    }

    fun setPref(key: String, value: Any) {
        when (value) {
            is String -> {
                encryptedPreferences.edit().putString(key, value).apply()
            }
            is Int -> {
                encryptedPreferences.edit().putInt(key, value).apply()
            }
            is Boolean -> {
                encryptedPreferences.edit().putBoolean(key, value).apply()
            }
            is Float -> {
                encryptedPreferences.edit().putFloat(key, value).apply()
            }
            is Long -> {
                encryptedPreferences.edit().putLong(key, value).apply()
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


    /**======================================================================**/
    /**============================== Language ==============================**/
    /**======================================================================**/

    /**============================== Default ==============================**/
    var appName = "Template Three"
    var cancel = "Cancel"
    private var placeholderPrefix = "Prefix "
    private var placeholderSuffix = " Suffix"
    var example = "Example"
    var template = "Template"
    var tapToExit = "Tap again to exit"
    var errorConnection = "Connection Error"
    var errorConnectionDesc = "You are not connected to internet. Please check your connection and try again."
    var errorServer = "Server Error"
    var errorServerDesc = "Could not connect to Server. Please try again."
    var retry = "Retry"
    var okay = "Okay"
    var error = "Error"
    var value = "Value"
    var get = "Get"
    var post = "Post"
    var upload = "Upload"
    var download = "Download"
    var notify = "Notify"
    var changeTheme = "Change Theme"
    var changeLanguage = "Change Language"
    var request = "Request"
    var clickToFetchData = "Change Theme"
    var add = "Add"
    var remove = "Remove"
    var reset = "Reset"
    var addAll = "Add All"
    var removeAll = "Remove All"
    var sort = "Sort"
    var one = "One"
    var two = "Two"


    fun placeholderString(count: Int) = run { "$placeholderPrefix$count$placeholderSuffix" }

    /**============================== English ==============================**/
    private var appNameEN = "Template Three"
    private var cancelEN = "Cancel"
    private var placeholderPrefixEN = "Prefix "
    private var placeholderSuffixEN = " Suffix"
    private var exampleEN = "Example"
    private var templateEN = "Template"
    private var tapToExitEN = "Tap again to exit"
    private var errorConnectionEN = "Connection Error"
    private var errorConnectionDescEN = "You are not connected to internet. Please check your connection and try again."
    private var errorServerEN = "Server Error"
    private var errorServerDescEN = "Could not connect to Server. Please try again."
    private var retryEN = "Retry"
    private var okayEN = "Okay"
    private var errorEN = "Error"
    private var valueEN = "Value"
    private var getEN = "Get"
    private var postEN = "Post"
    private var uploadEN = "Upload"
    private var downloadEN = "Download"
    private var notifyEN = "Notify"
    private var changeThemeEN = "Change Theme"
    private var changeLanguageEN = "Change Language"
    private var requestEN = "Request"
    private var clickToFetchDataEN = "Change Theme"
    private var addEN = "Add"
    private var removeEN = "Remove"
    private var resetEN = "Reset"
    private var addAllEN = "Add All"
    private var removeAllEN = "Remove All"
    private var sortEN = "Sort"
    private var oneEN = "One"
    private var twoEN = "Two"

    /**============================== Farsi ==============================**/
    private var appNameFA = "Template Three FA"
    private var cancelFA = "Cancel FA"
    private var placeholderPrefixFA = "Prefix  FA"
    private var placeholderSuffixFA = " Suffix FA"
    private var exampleFA = "Example FA"
    private var templateFA = "Template FA"
    private var tapToExitFA = "Tap again to exit FA"
    private var errorConnectionFA = "Connection Error FA"
    private var errorConnectionDescFA = "You are not connected to internet. Please check your connection and try again. FA"
    private var errorServerFA = "Server Error FA"
    private var errorServerDescFA = "Could not connect to Server. Please try again. FA"
    private var retryFA = "Retry FA"
    private var okayFA = "Okay FA"
    private var errorFA = "Error FA"
    private var valueFA = "Value FA"
    private var getFA = "GFA"
    private var postFA = "PoFA"
    private var uploadFA = "UploFA"
    private var downloadFA = "DownloFA"
    private var notifyFA = "NotiFA"
    private var changeThemeFA = "Change TheFA"
    private var changeLanguageFA = "Change Language FA"
    private var requestFA = "Request FA"
    private var clickToFetchDataFA = "Change TheFA"
    private var addFA = "Add FA"
    private var removeFA = "Remove FA"
    private var resetFA = "Reset FA"
    private var addAllFA = "Add All FA"
    private var removeAllFA = "Remove All FA"
    private var sortFA = "Sort FA"
    private var oneFA = "One FA"
    private var twoFA = "Two FA"


    var currentLanguage = Languages.DEFAULT_LANGUAGE
    fun changeLang(lang: Languages) {
        currentLanguage = lang
        setPref(PREF_LANGUAGE, lang.value)
        when (lang) {
            Languages.EN -> {
                appName = appNameEN
                cancel = cancelEN
                placeholderPrefix = placeholderPrefixEN
                placeholderSuffix = placeholderSuffixEN
                example = exampleEN
                template = templateEN
                tapToExit = tapToExitEN
                errorConnection = errorConnectionEN
                errorConnectionDesc = errorConnectionDescEN
                errorServer = errorServerEN
                errorServerDesc = errorServerDescEN
                retry = retryEN
                okay = okayEN
                error = errorEN
                value = valueEN
                get = getEN
                post = postEN
                upload = uploadEN
                download = downloadEN
                notify = notifyEN
                changeTheme = changeThemeEN
                changeLanguage = changeLanguageEN
                request = requestEN
                clickToFetchData = clickToFetchDataEN
                add = addEN
                remove = removeEN
                reset = resetEN
                addAll = addAllEN
                removeAll = removeAllEN
                sort = sortEN
                one = oneEN
                two = twoEN
            }
            Languages.FA -> {
                appName = appNameFA
                cancel = cancelFA
                placeholderPrefix = placeholderPrefixFA
                placeholderSuffix = placeholderSuffixFA
                example = exampleFA
                template = templateFA
                tapToExit = tapToExitFA
                errorConnection = errorConnectionFA
                errorConnectionDesc = errorConnectionDescFA
                errorServer = errorServerFA
                errorServerDesc = errorServerDescFA
                retry = retryFA
                okay = okayFA
                error = errorFA
                value = valueFA
                get = getFA
                post = postFA
                upload = uploadFA
                download = downloadFA
                notify = notifyFA
                changeTheme = changeThemeFA
                changeLanguage = changeLanguageFA
                request = requestFA
                clickToFetchData = clickToFetchDataFA
                add = addFA
                remove = removeFA
                reset = resetFA
                addAll = addAllFA
                removeAll = removeAllFA
                sort = sortFA
                one = oneFA
                two = twoFA
            }
            else         -> Unit
        }
    }

    /**============================== Language ==============================**/


    val themeIsDark
        get() = currentTheme.value.contains("DARK")
    var currentTheme = Themes.LIGHT_PINK
    fun changeTheme(theme: Themes) {
        setPref(PREF_THEME, theme.value)
        currentTheme = theme
        if (themeIsDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
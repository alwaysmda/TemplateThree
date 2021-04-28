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

    /**============================== Korean ==============================**/
    private var appNameKO = "Template Three KO"
    private var cancelKO = "Cancel KO"
    private var placeholderPrefixKO = "Prefix  KO"
    private var placeholderSuffixKO = " Suffix KO"
    private var exampleKO = "Example KO"
    private var templateKO = "Template KO"
    private var tapToExitKO = "Tap again to exit KO"
    private var errorConnectionKO = "Connection Error KO"
    private var errorConnectionDescKO = "You are not connected to internet. Please check your connection and try again. KO"
    private var errorServerKO = "Server Error KO"
    private var errorServerDescKO = "Could not connect to Server. Please try again. KO"
    private var retryKO = "Retry KO"
    private var okayKO = "Okay KO"
    private var errorKO = "Error KO"
    private var valueKO = "Value KO"
    private var getKO = "GKO"
    private var postKO = "PoKO"
    private var uploadKO = "UploKO"
    private var downloadKO = "DownloKO"
    private var notifyKO = "NotiKO"
    private var changeThemeKO = "Change TheKO"
    private var changeLanguageKO = "Change Language KO"
    private var requestKO = "Request KO"
    private var clickToFetchDataKO = "Change TheKO"
    private var addKO = "Add KO"
    private var removeKO = "Remove KO"
    private var resetKO = "Reset KO"
    private var addAllKO = "Add All KO"
    private var removeAllKO = "Remove All KO"
    private var sortKO = "Sort KO"
    private var oneKO = "One KO"
    private var twoKO = "Two KO"


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
                appName = appNameKO
                cancel = cancelKO
                placeholderPrefix = placeholderPrefixKO
                placeholderSuffix = placeholderSuffixKO
                example = exampleKO
                template = templateKO
                tapToExit = tapToExitKO
                errorConnection = errorConnectionKO
                errorConnectionDesc = errorConnectionDescKO
                errorServer = errorServerKO
                errorServerDesc = errorServerDescKO
                retry = retryKO
                okay = okayKO
                error = errorKO
                value = valueKO
                get = getKO
                post = postKO
                upload = uploadKO
                download = downloadKO
                notify = notifyKO
                changeTheme = changeThemeKO
                changeLanguage = changeLanguageKO
                request = requestKO
                clickToFetchData = clickToFetchDataKO
                add = addKO
                remove = removeKO
                reset = resetKO
                addAll = addAllKO
                removeAll = removeAllKO
                sort = sortKO
                one = oneKO
                two = twoKO
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
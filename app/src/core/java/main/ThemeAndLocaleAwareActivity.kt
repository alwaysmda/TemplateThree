package main

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.xodus.templatethree.R
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.util.*


open class ThemeAndLocaleAwareActivity : AppCompatActivity(), KodeinAware {
    override val kodein: Kodein by closestKodein()
    private val appClass: ApplicationClass by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(if (appClass.getBooleanPref(PREF_DARK_THEME)) R.style.AppThemeDark else R.style.AppTheme)
        AppCompatDelegate.setDefaultNightMode(if (appClass.getBooleanPref(PREF_DARK_THEME)) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(appClass, if (appClass.getBooleanPref(PREF_DARK_THEME)) R.color.md_black_1000 else R.color.md_white_1000)
        }
        if (appClass.getStringPref(PREF_LANGUAGE) == null) {
            appClass.setPref(PREF_LANGUAGE, CON_LANG_DEFAULT)
            updateLocale(Locale(CON_LANG_DEFAULT))
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateBaseContextLocale(base))
        Handler().postDelayed({
            window?.decorView?.layoutDirection = View.LAYOUT_DIRECTION_LOCALE
        }, 1000)
    }

    private fun updateBaseContextLocale(context: Context): Context? {
        val language = (context.applicationContext as ApplicationClass).getStringPref(PREF_LANGUAGE) ?: CON_LANG_EN
        val locale = Locale(language)
        Locale.setDefault(locale)
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            updateResourcesLocale(context, locale)
        } else updateResourcesLocaleLegacy(context, locale)
    }

    private fun updateResourcesLocale(context: Context, locale: Locale): Context {
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }

    private fun updateResourcesLocaleLegacy(context: Context, locale: Locale): Context? {
        val resources = context.resources
        val configuration = resources.configuration
        val displayMetrics = resources.displayMetrics
        configuration.locale = locale
        resources.updateConfiguration(configuration, displayMetrics)
        return context
    }

    override fun applyOverrideConfiguration(overrideConfiguration: Configuration?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            // update overrideConfiguration with your locale
            updateLocale(overrideConfiguration) // you will need to implement this

        }
        super.applyOverrideConfiguration(overrideConfiguration)
    }

    private fun updateLocale(configuration: Configuration?) {
        configuration?.let {
            val displayMetrics = resources.displayMetrics
            configuration.setLocale(Locale(appClass.getStringPref(PREF_LANGUAGE) ?: CON_LANG_EN))
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                applicationContext.createConfigurationContext(configuration)
            } else {
                resources.updateConfiguration(configuration, displayMetrics)
            }
            recreate()
        }
    }

    fun updateLocale(locale: Locale) {
        val resources = resources
        val configuration: Configuration = resources.configuration
        val displayMetrics = resources.displayMetrics
        configuration.setLocale(locale)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            applicationContext.createConfigurationContext(configuration)
        } else {
            resources.updateConfiguration(configuration, displayMetrics)
        }
        recreate()
    }

    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        theme.applyStyle(if (appClass.getBooleanPref(PREF_DARK_THEME)) R.style.AppThemeDark else R.style.AppTheme, true)
        return theme
    }
}

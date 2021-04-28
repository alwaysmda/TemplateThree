package main

import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.xodus.templatethree.R
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance


open class ThemeAwareActivity : AppCompatActivity(), KodeinAware {
    override val kodein: Kodein by closestKodein()
    private val appClass: ApplicationClass by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(getCurrentThemeId())
        AppCompatDelegate.setDefaultNightMode(if (appClass.themeIsDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.navigationBarColor = ContextCompat.getColor(appClass, if (appClass.themeIsDark) R.color.md_black_1000 else R.color.md_white_1000)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.navigationBarDividerColor = ContextCompat.getColor(appClass, if (appClass.themeIsDark) R.color.md_white_1000 else R.color.md_black_1000)
        }
    }

    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        theme.applyStyle(getCurrentThemeId(), true)
        return theme
    }

    private fun getCurrentThemeId(): Int {
        return when (appClass.currentTheme) {
            Themes.LIGHT_PINK -> R.style.AppTheme_Light_Pink
            Themes.DARK_BLUE -> R.style.AppTheme_Dark_Blue
            else              -> R.style.AppTheme_Dark_Blue
        }
    }
}

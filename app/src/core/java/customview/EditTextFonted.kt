package customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.xodus.templatethree.R
import main.ApplicationClass
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class EditTextFonted @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatEditText(context, attrs, defStyleAttr), KodeinAware {
    override val kodein by closestKodein()
    private val appClass: ApplicationClass by instance()

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.EditTextFonted, 0, 0)
            typeface = when (typedArray.getInt(typedArray.getResourceId(R.styleable.EditTextFonted_etf_font, 0), 0)) {
                0    -> appClass.fontLight!!
                1    -> appClass.fontBold ?: appClass.fontLight!!
                2    -> appClass.fontMedium ?: appClass.fontLight!!
                3    -> appClass.fontUltraBold ?: appClass.fontLight!!
                4    -> appClass.fontUltraLight ?: appClass.fontLight!!
                else -> appClass.fontLight!!
            }
            typedArray.recycle()
        }
    }
}
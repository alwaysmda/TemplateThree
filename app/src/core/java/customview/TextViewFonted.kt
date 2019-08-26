package customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.xodus.templatethree.R
import main.ApplicationClass
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance

class TextViewFonted @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr), KodeinAware {
    override val kodein by closestKodein()
    private val appClass: ApplicationClass by instance()

    init {
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.TextViewFonted, 0, 0)
            typeface = when (typedArray.getInt(typedArray.getResourceId(R.styleable.TextViewFonted_tvf_font, 0), 0)) {
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
package util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.xodus.templatethree.R

class BindingAdapterUtils {
    companion object {
        @JvmStatic
        @BindingAdapter("imageUrl")
        fun imageUrl(imageView: ImageView, url: String?) {
            if (url.isNullOrEmpty().not()) {
                Picasso.get().load(url).placeholder(R.color.md_grey_500).fit().into(imageView)
            }
        }
    }
}
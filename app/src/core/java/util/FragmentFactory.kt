package util

import android.os.Bundle
import androidx.databinding.ViewDataBinding
import main.BaseFragment
import main.BaseViewModel
import view.TemplateFragment
import view.TemplateRoomFragment

@Suppress("UNCHECKED_CAST")
object BaseFragmentFactory {
    const val ARG_NAME = "ARG_NAME"
    fun templateFragment(): BaseFragment<ViewDataBinding, BaseViewModel> {
        val arg = Bundle().apply {

        }
        return TemplateFragment().apply { arguments = arg } as BaseFragment<ViewDataBinding, BaseViewModel>
    }

    fun templateRoomFragment(name: String? = null): BaseFragment<ViewDataBinding, BaseViewModel> {
        val arg = Bundle().apply {
            putString(ARG_NAME, name)
        }
        return TemplateRoomFragment().apply { arguments = arg } as BaseFragment<ViewDataBinding, BaseViewModel>
    }


}
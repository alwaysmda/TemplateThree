package view

import android.content.Context
import android.os.Bundle
import android.view.View
import com.xodus.templatethree.R
import com.xodus.templatethree.databinding.FragmentTemplateBinding
import main.BaseFragment
import viewmodel.TemplateViewModel

class TemplateFragment : BaseFragment<FragmentTemplateBinding, TemplateViewModel>() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initialize(R.layout.fragment_template, TemplateViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.handleIntent(arguments)
        init(binding.root)
        observe()
    }

    private fun init(v: View) {

    }


    private fun observe() {
        viewModel.apply {
            hideNavBar.observe(viewLifecycleOwner, {
                it?.let {
                    baseActivity.showHideNavigationBar(it)
                }
            })
        }
    }
}




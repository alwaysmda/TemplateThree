package view

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xodus.templatethree.R
import com.xodus.templatethree.databinding.FragmentTemplateBinding
import main.BaseFragment
import org.greenrobot.eventbus.EventBus
import util.snack
import viewmodel.TemplateViewModel

class TemplateFragment : BaseFragment() {


    companion object {
        fun newInstance(): TemplateFragment {
            val args = Bundle()
            val fragment = TemplateFragment()
            fragment.arguments = args
            return fragment
        }
    }


    //Element
    private var _binding: FragmentTemplateBinding? = null //todo : FragmentTemplateBinding
    private val binding get() = _binding!!
    private lateinit var viewModel: TemplateViewModel
    private var exit: Boolean = false

    //View


    override fun onDetach() {
        super.onDetach()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //        if (!EventBus.getDefault().isRegistered(this)) {
        //            EventBus.getDefault().register(this)
        //        }
        _binding = DataBindingUtil.inflate<FragmentTemplateBinding>(inflater, R.layout.fragment_template, container, false).apply {
            //todo : fragment_template
            this@TemplateFragment.viewModel = ViewModelProvider(this@TemplateFragment, viewModelFactory).get(TemplateViewModel::class.java) //todo : TemplateViewModel
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@TemplateFragment.viewModel
            appClass = this@TemplateFragment.appClass
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel?.handleIntent(arguments)
        init(binding.root)
        observe()
        onBackPressed(object : OnBackPressedListener {
            override fun onBackPressed() {
                if (exit) {
                    requireActivity().finishAffinity()
                } else {
                    exit = true
                    snack(view, R.string.tab_again_to_exit, true)
                    Handler().postDelayed({ exit = false }, 3500)
                }
            }
        })
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun init(v: View) {

    }


    private fun observe() {
        viewModel.apply {
            showDialog.observe(viewLifecycleOwner, Observer {
                it?.show(parentFragmentManager)
            })
            doBack.observe(viewLifecycleOwner, Observer {
                doBack()
            })
            snack.observe(viewLifecycleOwner, Observer {
                snack(view, it)
            })
            snackString.observe(viewLifecycleOwner, Observer {
                snack(view, it)
            })
            startFragment.observe(viewLifecycleOwner, Observer {
                it?.let {
                    start(it)
                }
            })
            changeLocale.observe(viewLifecycleOwner, Observer {
                it?.let {
                    baseActivity.updateLocale(it)
                }
            })
            hideNavBar.observe(viewLifecycleOwner, Observer {
                it?.let {
                    baseActivity.showHideNavigationBar(it)
                }
            })
        }
    }
}
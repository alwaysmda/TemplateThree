package view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.xodus.templatethree.R
import com.xodus.templatethree.databinding.FragmentTemplateRoomBinding
import main.BaseFragment
import org.greenrobot.eventbus.EventBus
import util.snack
import viewmodel.TemplateRoomViewModel

class TemplateRoomFragment : BaseFragment() {


    companion object {
        fun newInstance(): TemplateRoomFragment {
            val args = Bundle()
            val fragment = TemplateRoomFragment()
            fragment.arguments = args
            return fragment
        }
    }


    //Element
    private var _binding: FragmentTemplateRoomBinding? = null //todo : FragmentTemplateBinding
    private val binding get() = _binding!!
    private lateinit var viewModel: TemplateRoomViewModel
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
        _binding = DataBindingUtil.inflate<FragmentTemplateRoomBinding>(inflater, R.layout.fragment_template_room, container, false).apply {
            //todo : fragment_template
            this@TemplateRoomFragment.viewModel = ViewModelProvider(this@TemplateRoomFragment, viewModelFactory).get(TemplateRoomViewModel::class.java) //todo : TemplateViewModel
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@TemplateRoomFragment.viewModel
            appClass = this@TemplateRoomFragment.appClass
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel?.handleIntent(arguments)
        init(binding.root)
        observe()
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
        }
    }
}
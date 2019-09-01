package view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.franmontiel.localechanger.utils.ActivityRecreationHelper
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
    private lateinit var binding: FragmentTemplateBinding //todo : FragmentTemplateBinding

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
        binding = DataBindingUtil.inflate<FragmentTemplateBinding>(inflater, R.layout.fragment_template, container, false).apply {
            //todo : fragment_template
            viewModel = ViewModelProvider(this@TemplateFragment, viewModelFactory).get(TemplateViewModel::class.java) //todo : TemplateViewModel
            lifecycleOwner = viewLifecycleOwner
            viewModel = viewModel
            appClass = this@TemplateFragment.appClass
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel?.handleIntent(arguments)
        init(binding.root)
        observe()
    }


    private fun init(v: View) {
        setupToolbar(v)
    }

    private fun setupToolbar(v: View) {
        setHasOptionsMenu(true)
        val activity: AppCompatActivity = baseActivity
        activity.setSupportActionBar(v.findViewById(R.id.toolbar))
        activity.supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false)
            it.setDisplayHomeAsUpEnabled(false)
            it.setDisplayShowHomeEnabled(true)
        }
    }

    private fun observe() {
        binding.viewModel?.showDialog?.observe(viewLifecycleOwner, Observer {
            it.show(fragmentManager)
        })
        binding.viewModel?.doBack?.observe(viewLifecycleOwner, Observer {
            doBack()
        })
        binding.viewModel?.snack?.observe(viewLifecycleOwner, Observer {
            snack(view, it)
        })
        binding.viewModel?.startFragment?.observe(viewLifecycleOwner, Observer {
            start(it)
        })
        binding.viewModel?.reset?.observe(viewLifecycleOwner, Observer {
            ActivityRecreationHelper.recreate(baseActivity, true)
        })
    }
}
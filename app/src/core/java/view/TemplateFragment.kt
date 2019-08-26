package view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.xodus.templatethree.R
import com.xodus.templatethree.databinding.FragmentTemplateBinding
import main.ApplicationClass
import main.BaseFragment
import org.greenrobot.eventbus.EventBus
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import util.ViewModelFactory
import util.snack
import viewmodel.TemplateViewModel

class TemplateFragment : BaseFragment(), KodeinAware {


    companion object {
        fun newInstance(): TemplateFragment {
            val args = Bundle()
            val fragment = TemplateFragment()
            fragment.arguments = args
            return fragment
        }
    }


    //Element
    override val kodein by closestKodein()
    private val viewModelFactory: ViewModelFactory by instance()
    private val appClass: ApplicationClass by instance()
    private lateinit var binding: FragmentTemplateBinding //todo : FragmentTemplateBinding
    private lateinit var viewModel: TemplateViewModel //todo : TemplateViewModel (also add to factory)

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_template, container, false) //todo : fragment_template
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TemplateViewModel::class.java) //todo : TemplateViewModel
        binding.viewModel = viewModel
        binding.appClass = appClass
        viewModel.handleIntent(arguments)
        init(binding.root)
        observe()
        return binding.root
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
        viewModel.showDialog.observe(viewLifecycleOwner, Observer {
            it.show(fragmentManager)
        })
        viewModel.doBack.observe(viewLifecycleOwner, Observer {
            doBack()
        })
        viewModel.snack.observe(viewLifecycleOwner, Observer {
            snack(view, it)
        })
        viewModel.startFragment.observe(viewLifecycleOwner, Observer {
            start(it)
        })
    }
}
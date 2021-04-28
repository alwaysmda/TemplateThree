package view

import android.content.Context
import android.os.Bundle
import android.view.View
import com.xodus.templatethree.R
import com.xodus.templatethree.databinding.FragmentTemplateRoomBinding
import main.BaseFragment
import org.greenrobot.eventbus.EventBus
import viewmodel.TemplateRoomViewModel

class TemplateRoomFragment : BaseFragment<FragmentTemplateRoomBinding, TemplateRoomViewModel>() {

    override fun onAttach(context: Context) {
        super.onAttach(context)
        initialize(R.layout.fragment_template_room, TemplateRoomViewModel::class.java)
    }

    override fun onDetach() {
        super.onDetach()
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel?.handleIntent(arguments)
        init(binding.root)
        observe()
    }

    private fun init(v: View) {

    }

    private fun observe() {
        viewModel.apply {

        }
    }
}
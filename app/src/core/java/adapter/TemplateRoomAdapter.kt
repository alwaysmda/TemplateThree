package adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.xodus.templatethree.R
import com.xodus.templatethree.databinding.RowTemplateRoomBinding
import model.TemplateRoom
import util.getRainbow
import viewmodel.TemplateRoomViewModel

class TemplateRoomAdapter(private val templateRoom: TemplateRoomViewModel) :
    BaseAdapter<TemplateRoom>(false, false, 0, templateRoomViewModel = templateRoom) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType < 0) {
            super.onCreateViewHolder(parent, viewType)
        } else {
            val binding = DataBindingUtil.inflate<RowTemplateRoomBinding>(LayoutInflater.from(parent.context), R.layout.row_template_room, parent, false)
            if (bindList.any { it.first == viewType }.not()) {
                bindList.add(Pair(viewType, binding))
            }
            TemplateRoomHolder(binding, sectionIndex, templateRoom)
        }
    }


    companion object {
        class TemplateRoomHolder(private val binding: RowTemplateRoomBinding, private val sectionIndex: Int, templateRoom: TemplateRoomViewModel) :
            BaseAdapter.Companion.BaseViewHolder<TemplateRoom>(binding, templateRoomViewModel = templateRoom) { //todo RowNumberBinding, TemplateHolder, Template
            override fun bindData(data: TemplateRoom) { //todo Template
                super.bindData(data)
                binding.apply {
                    rowTemplateRoomTvNumber.setTextColor(getRainbow()[adapterPosition % getRainbow().size])
                }
            }
        }
    }

}
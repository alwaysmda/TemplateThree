package adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.xodus.templatethree.R
import com.xodus.templatethree.databinding.RowTemplateBinding
import model.Template
import util.getRainbow
import viewmodel.TemplateViewModel

class TemplateAdapter(private val template: TemplateViewModel) :
    BaseAdapter<Template>(false, false, 0, template) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType < 0) {
            super.onCreateViewHolder(parent, viewType)
        } else {
            val binding = DataBindingUtil.inflate<RowTemplateBinding>(LayoutInflater.from(parent.context), R.layout.row_template, parent, false)
            if (bindList.any { it.first == viewType }.not()) {
                bindList.add(Pair(viewType, binding))
            }
            TemplateHolder(binding, sectionIndex, template)
        }
    }


    companion object {
        class TemplateHolder(private val binding: RowTemplateBinding, private val sectionIndex: Int, templateViewModel: TemplateViewModel?) :
            BaseAdapter.Companion.BaseViewHolder<Template>(binding, templateViewModel = templateViewModel) { //todo RowNumberBinding, TemplateHolder, Template
            override fun bindData(data: Template) { //todo Template
                super.bindData(data)
                binding.apply {
                    rowTemplateTvNumber.setTextColor(getRainbow()[adapterPosition % getRainbow().size])
                    rowTemplateTvNumber.setOnClickListener { templateViewModel?.onTvItemClick(data, rowTemplateTvNumber) }
                }
            }
        }
    }
}
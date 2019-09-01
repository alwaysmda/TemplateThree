package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xodus.templatethree.databinding.RowTemplateBinding
import model.Template
import viewmodel.TemplateViewModel

class TemplateAdapter(
    private val viewModel: TemplateViewModel
) : RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder>() {
    var list: ArrayList<Template> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        return TemplateViewHolder(RowTemplateBinding.inflate(LayoutInflater.from(parent.context), parent, false), viewModel)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class TemplateViewHolder(val binding: RowTemplateBinding, val itemViewModel: TemplateViewModel) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Template) {
            binding.setVariable(BR.data, data)
            binding.executePendingBindings()
            //            itemViewModel.snack.observe(binding.lifecycleOwner!!, Observer {
            //                viewModel.snack.value = it
            //            })
        }
    }

    fun updateList(newList: ArrayList<Template>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return list[oldItemPosition] == newList[newItemPosition]
            }

            override fun getOldListSize(): Int {
                return list.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return list[oldItemPosition] == newList[newItemPosition]
            }
        })
        list.clear()
        list.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

}
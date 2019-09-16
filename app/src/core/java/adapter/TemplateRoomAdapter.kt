package adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xodus.templatethree.R
import model.Template
import model.TemplateRoom
import viewmodel.TemplateRoomViewModel
import viewmodel.TemplateViewModel

class TemplateRoomAdapter(private val viewModel: TemplateRoomViewModel) : RecyclerView.Adapter<TemplateRoomAdapter.TemplateViewHolder>() {

    var list: ArrayList<TemplateRoom> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        return TemplateViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false))
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int): Int {
        return R.layout.row_template_room //todo row_template_room
    }

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        holder.bind(list[position], viewModel)
    }

    inner class TemplateViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TemplateRoom, viewModel: TemplateRoomViewModel) {
            binding.setVariable(BR.data, data)
            binding.setVariable(BR.viewModel, viewModel)
            binding.executePendingBindings()
        }
    }

    fun updateList(newList: ArrayList<TemplateRoom>) {
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
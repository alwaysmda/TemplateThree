package adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xodus.templatethree.R
import kotlinx.android.synthetic.main.row_template.view.*
import model.Template
import util.inflate

class TemplateAdapter(
    val list: ArrayList<String>,
    val action: (TemplateViewHolder, View?, Int) -> (Unit)
) : RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
        return TemplateViewHolder(parent.inflate(R.layout.row_template))
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
        holder.bind(list[position])
    }

    inner class TemplateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(v: View?) {
            action(this, v, adapterPosition)
        }

        fun bind(data: String) {
            itemView.setOnClickListener(this)
            itemView.rowTemplate_tvTemplate.setOnClickListener(this)
            itemView.rowTemplate_tvTemplate.text = data
        }

    }

    fun updateList(newList: ArrayList<Template>) {
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getOldListSize(): Int {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun getNewListSize(): Int {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        diffResult.dispatchUpdatesTo(this)
    }

}
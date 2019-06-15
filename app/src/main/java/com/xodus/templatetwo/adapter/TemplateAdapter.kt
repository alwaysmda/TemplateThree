package com.xodus.templatetwo.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xodus.templatetwo.R
import com.xodus.templatetwo.extention.inflate
import kotlinx.android.synthetic.main.row_template.view.*

class TemplateAdapter(
    private val context: Context,
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

}
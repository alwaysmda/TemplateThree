package com.xodus.templatetwo.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xodus.templatetwo.R
import com.xodus.templatetwo.adapter.TemplateAdapter
import com.xodus.templatetwo.extention.getLocation
import com.xodus.templatetwo.extention.getRandomString
import com.xodus.templatetwo.extention.toast
import kotlinx.android.synthetic.main.fragment_template.view.*
import kotlinx.android.synthetic.main.row_template.view.*

class TemplateFragment : Fragment() {

    fun newInstance(): TemplateFragment {
        val args = Bundle()
        val fragment = TemplateFragment()
        fragment.arguments = args
        return fragment
    }

    //View
    var recyclerView: RecyclerView? = null


    //Element
    var adapter: TemplateAdapter? = null
    var list: ArrayList<String> = ArrayList()
    var ctx : Context = context!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(ctx).inflate(R.layout.fragment_template, container, false)
        for (index in 0..20) {
            list.add("Item $index")
        }
        adapter = TemplateAdapter(context!!, list) { holder,v, position ->
            when (v.id){
                R.id.rowTemplate_tvTemplate -> {
                    context!!.toast(list[position])
                    holder.itemView.rowTemplate_tvTemplate.setTextColor(ContextCompat.getColor(context!!,R.color.md_red_500))
                    adapter!!.notifyItemChanged(position)
                }
                else -> {
                    adapter!!.list[position] = "Hello"
                    context!!.toast(list[position] + "ITEM")
                }
            }

        }
        recyclerView = view.template_recyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView!!.adapter = adapter


        return view
    }
}
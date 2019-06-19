package com.xodus.templatetwo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.xodus.templatetwo.R
import com.xodus.templatetwo.main.log
import com.xodus.templatetwo.main.toast
import com.xodus.templatetwo.http.OnResponseListener
import com.xodus.templatetwo.http.Request
import com.xodus.templatetwo.http.Response
import com.xodus.templatetwo.main.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.toolbar_template.view.*
import org.greenrobot.eventbus.EventBus

class TemplateFragment : BaseFragment(), View.OnClickListener, OnResponseListener {


    companion object {
        fun newInstance(): TemplateFragment {
            val args = Bundle()
            val fragment = TemplateFragment()
            fragment.arguments = args
            return fragment
        }
    }

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
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        init(view)
        setupToolbar(view)

        return view
    }


    private fun init(v: View) {
        //Elements

        //View
        v.home_tvMessage.text = getID()

        //        var adapter = TemplateAdapter(appClass, ArrayList()) { viewHolder, view, i -> onRecyclerItemClick(viewHolder, view, i) }
        //        client.request(API.Get(this, "https://www.httpbin.org/get"))

        doKotlin()
    }

    private fun doKotlin() {
        val items = ArrayList<String>()
        items.add("123")
        items.add("234")
        items.add("345")
        items.add("567")

        val second = ArrayList<String>()

        //boolean : if lambda condition is valid for all
        val a: Boolean = items.all { it.isNotEmpty() }

        //boolean : if lambda is valid for any
        val b: Boolean = items.any { it.isEmpty() }

        //Any : returns item in index or the function result (can be type) if null
        val c: Any = items.getOrElse(6) { items.size * 2 }

        //int : number of items with lambda condition
        val d: Int = items.count { it.length < 5 }

        //String : the last item with lambda condition
        val e: String = items.last { it.contains("3") }

        //String : the first item with lambda condition
        val f: String = items.first { it.contains("3") }

        //List : ArrayList of lambda condition result
        val g: List<Int> = items.map { it.length }

        //List : ArrayList of lambda condition result
        val h: List<Boolean> = items.map { it.contains("3") }

        //ArrayList : ArrayList of lambda condition result (destination and result are same)
        val i: ArrayList<String> = items.mapTo(second, { it })

        //Map<String,T> : created map in lambda condition
        val j: Map<String, Int> = items.associate { Pair(it + "K", it.length) }

        //Map<String,Any> : created map in lambda conditions. first is for key, second is for value
        val k: Map<String, Int> = items.associateBy({ it + "K" }, { it.length })

        // Unit : sorts the list
        val l: Unit = items.sortBy { it.length }

        //Unit : loops through the list
        val m: Unit = items.forEach { second.add(it) }


        var data: String = ""
        items.forEach { data += "$it," }

        log("DATA IS", data.dropLast(1))

        val result: ArrayList<Any> = ArrayList()
        result.add(a)
        result.add(b)
        result.add(c)
        result.add(d)
        result.add(e)
        result.add(f)
        result.add(g)
        result.add(h)
        result.add(i)
        result.add(j)
        result.add(k)
        result.add(l)
        result.add(m)
        result.forEach {
            log(it.javaClass.simpleName + " : " + it)
        }
    }

    private fun setupToolbar(v: View) {
        setHasOptionsMenu(true)
        val toolbar = v.include_toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar.findViewById(R.id.toolbar))
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.toolbar_tvTitle.setOnClickListener(this)
        toolbar.toolbar_tvTitle.text = "Home Base"
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.toolbar_tvTitle -> {
            }
            R.id.home_tvMessage  -> {
            }
        }
    }

    fun onRecyclerItemClick(viewHolder: RecyclerView.ViewHolder, view: View?, position: Int) {
        context?.toast("Item $position of type ${view?.javaClass?.simpleName} is clicked!")
    }

    override fun onResponse(response: Response) {
        log("RESPONSE=" + response.body)
    }

    override fun onProgress(request: Request, bytesWritten: Long, totalSize: Long, percent: Int) {

    }
}
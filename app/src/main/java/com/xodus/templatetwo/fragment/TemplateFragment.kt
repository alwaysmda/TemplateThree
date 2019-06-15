package com.xodus.templatetwo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.xodus.templatetwo.R
import com.xodus.templatetwo.extention.log
import com.xodus.templatetwo.extention.toast
import com.xodus.templatetwo.http.API
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

//        var adapter = TemplateAdapter(appClass, ArrayList()) { viewHolder, view, i -> onRecyclerItemClick(viewHolder,view,i)}
//       client.request(API.GET("https://www.httpbin.org/get",this))

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
            R.id.home_tvMessage -> {
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
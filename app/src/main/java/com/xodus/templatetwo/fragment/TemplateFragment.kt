package com.xodus.templatetwo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.xodus.templatetwo.R
import com.xodus.templatetwo.http.OnResponseListener
import com.xodus.templatetwo.http.Request
import com.xodus.templatetwo.http.Response
import com.xodus.templatetwo.listener.OnRecyclerItemClickListener
import com.xodus.templatetwo.main.BaseFragment
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.toolbar_template.view.*

class TemplateFragment : BaseFragment(), View.OnClickListener, OnRecyclerItemClickListener, OnResponseListener {

    companion object {
        fun newInstance(): TemplateFragment {
            val args = Bundle()
            val fragment = TemplateFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this)
//        }
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        init(view)
        setupToolbar(view)
        registerClientListeners(this)
        return view
    }


    private fun init(v: View) {
        //Elements


        //View
        v.home_tvMessage.text = getID()
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

    override fun onResponse(response: Response) {

    }

    override fun onProgress(request: Request, bytesWritten: Long, totalSize: Long, percent: Int) {

    }

    override fun OnItemClick(v: View, position: Int) {
        Toast.makeText(appClass, position.toString() + "", Toast.LENGTH_SHORT).show()
    }

}
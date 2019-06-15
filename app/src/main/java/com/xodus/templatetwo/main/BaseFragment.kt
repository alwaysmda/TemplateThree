package com.xodus.templatetwo.main

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import com.xodus.templatetwo.extention.getRandomInt
import com.xodus.templatetwo.extention.getRandomString
import com.xodus.templatetwo.http.Client
import com.xodus.templatetwo.http.OnResponseListener
import com.xodus.templatetwo.http.Request
import com.xodus.templatetwo.http.Response
import org.greenrobot.eventbus.EventBus
import java.util.*

open class BaseFragment : Fragment() {

    //ELEMENT
    lateinit var appClass: ApplicationClass
    private var ID: String? = null
    lateinit var baseActivity: BaseActivity
    lateinit var client: Client
    private var tabIndex: Int = 0
    var base: Boolean = false
    private var REQUEST_CODE: Int = 0
    var sharedElementListIn: List<SharedElement> = ArrayList()
    private var sharedElementListOut: MutableList<SharedElement> = ArrayList()
    var background: Drawable? = null
    var sharedElementRecyclerView: RecyclerView? = null
    var sharedElementRecyclerViewViewID: Int = 0
    var sharedElementRecyclerViewPosition: Int = 0
    var sharedElementRecyclerViewReturnView: View? = null
    var onBackPressed: OnBackPressedListener? = null
    var isBackDisabled: Boolean = false


    interface OnBackPressedListener {
        fun onBackPressed()
    }


    override fun onStart() {
        super.onStart()
        if (ID == null) {
            val tmp = this.toString().substring(this.toString().indexOf("{") + 1)
            ID = tmp.substring(0, tmp.indexOf(" "))
        }
    }

    override fun onDetach() {
        super.onDetach()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
        baseActivity = activity as BaseActivity
        appClass = baseActivity.appClass
        client = baseActivity.client
    }

    private fun setBase() {
        tag?.let {
            val tmp = this.toString().substring(this.toString().indexOf("{") + 1)
            ID = if (tmp.contains(" ")) {
                tmp.substring(0, tmp.indexOf(" "))
            } else {
                tmp.substring(0, tmp.length - 1)
            }

            REQUEST_CODE = getRandomInt(4)

            if (it.substring(0, 4) == "base") {
                base = true
                tabIndex = Integer.valueOf(it.substring(4, 5))
            } else {
                tabIndex = Integer.valueOf(it.substring(0, 1))
            }
        }
    }


    /**
     *
     * @return unique ID of the current fragment
     */
    fun getID(): String? {
        if (ID == null) {
            setBase()
        }
        return ID
    }

    /**
     *
     * @return a random 4 digit request code to use anywhere needed
     */
    fun getREQUEST_CODE(): Int {
        if (ID == null) {
            setBase()
        }
        return REQUEST_CODE
    }

    /**
     *
     * @return if it is the first fragment in the current tab or not
     */
    fun isBase(): Boolean {
        if (ID == null) {
            setBase()
        }
        return base
    }

    /**
     *
     * @return all the fragments in the current tab
     */
    fun getTabFragmentTable(): List<BaseFragment> = baseActivity.getTabFragmentTable()

    /**
     *
     * @return all the fragments
     */
    fun getFragmentTable(): List<List<BaseFragment>> = baseActivity.getFragmentTable()!!


    /**
     * Request runtime permissions. The result will be posted by {OnRequestPermissionResultEvent} event
     * @param permission example: Manifest.permission.READ_EXTERNAL_STORAGE
     */
    fun grantPremission(vararg permission: String) {
        ActivityCompat.requestPermissions(activity!!, permission, REQUEST_CODE)
    }

    /**
     *
     * @return index of the current tab in nvaigation bar
     */
    fun getTabIndex(): Int {
        if (ID == null) {
            setBase()
        }
        return tabIndex
    }

    /**
     * Instatiates and shows the fragment in the current tab and hides the current frgmant
     * @param fragment the new frgment to show
     */
    fun start(fragment: BaseFragment) {
        baseActivity.start(fragment)
    }

    /**
     * this is meant to be a dialog. Captures the current fragment's view and passes it
     * to the desired fragment to be shown in the background.
     * @param fragment the fragment to start
     */
    fun show(fragment: BaseFragment) {
        baseActivity.show(fragment)
    }

    /**
     * Replaces the current fragment with the desired fragment.
     * The fragments stack will not increase and the current fragment will be removed.
     * @param fragment the desired fragment to start
     */
    fun replace(fragment: BaseFragment) {
        baseActivity.replace(fragment)
    }

    /**
     *
     * @param index tab index of navigation bar to show
     */
    fun selectTab(index: Int) {
        baseActivity.selectTab(index)
    }

    fun hideNavigationBar(hide: Boolean) {
        baseActivity.hideNavigationBar(hide)
    }

    fun hideNavigationBar(hide: Boolean, duration: Long) {
        baseActivity.hideNavigationBar(hide, duration)
    }

    /**
     * Disables the default onBackPressed and listens to you.
     * Call doBack() to fire the default onBackPressed.
     * Call enableBack() to restore the default onBackPressed.
     * @param onBackPressed listener to onBackPressed
     */
    fun onBackPressed(onBackPressed: OnBackPressedListener) {
        isBackDisabled = true
        this.onBackPressed = onBackPressed
    }

    fun disableBack() {
        isBackDisabled = true
    }

    fun enableBack() {
        isBackDisabled = false
    }

    fun doBack() {
        isBackDisabled = false
        activity!!.onBackPressed()
    }

    /*
    NORMAL TRANSITION : Use this method for normal transitions;
    1-call addSharedElement in sending fragment;
    2-start receiving fragment;
    3-call receiveTransition in receiving fragment's onCreateView and pass the whole view;
    */

    /**
     * Prepares sending and receiving fragment for shared element transitions.
     * Must be called before starting receiving fragment.
     *
     * @param view shared element view in current fragment
     * @param id   shared element view id in next fragment
     */
    fun addSharedElement(view: View, id: Int) {
        for (size in sharedElementListOut.size - 1 downTo -1 + 1) {
            if (sharedElementListOut[size].id == id) {
                sharedElementListOut.removeAt(size)
            }
        }
        val tag = view.id.toString() + getRandomString(10)
        ViewCompat.setTransitionName(view, tag)
        sharedElementListOut.add(SharedElement(view, id, tag, true))
    }

    /**
     * Call this method in OnCreateView method of receiving fragment to do the shared element transition.
     *
     * @param view the view which contains the shared element. Sending the whole view is recommended.
     */
    fun receiveTransition(view: View) {
        for (i in 0 until sharedElementListIn.size) {
            val tag = sharedElementListIn[i].tag
            val id = sharedElementListIn[i].id
            val sharedView = view.findViewById<View>(id)
            sharedView?.let {
                ViewCompat.setTransitionName(sharedView, tag)
                sharedView.tag = tag
            }
        }
    }

    /*
    DELAYED TRANSITION : Use this method if the shared element is not available in onCreateView and it will be available later;
    1-call addSharedElement in sending fragment;
    2-start receiving fragment;
    3-call receiveTransitionLater in receiving fragment's onCreateView and pass the whole view;
    4-call receiveTransition in receiving fragment when the delayed view has drawn
    */

    /**
     * Call this method in OnCreateView method of receiving fragment to postpones the shared element transition.
     * This is used when the shared element view is not available yet. Used usually with viewpager.
     * When the shared element view is available, receiveTransition() must be called to do the transition.
     *
     * @param view the view which contains the shared element. Sending the whole view is recommended.
     */
    fun receiveTransitionLater() {
        postponeEnterTransition()
    }

    /**
     * Call this method to do the delayed shared element transition.
     * receiveTransitionLater(view) method must be called before.
     */
    fun receiveTransition() {
        view?.let {
            startPostponedEnterTransition()
            receiveTransition(it)
        }
    }

    /*
    RECYCLERVIEW-VIEWPAGER TRANSITION : Use this method for shared element transition between recyclerview and viewpager;
    1-call setSharedElementRecyclerView in sending fragment (recyclerview);
    2-start receiving fragment (viewpager);
    3-override the onBackPressed in receiving fragment (viewpager) and call setSharedElementRecyclerViewReturnView and doBack in it;

    note : in case of bugs, call recyclerview.post in sending fragment and then fill the recyclerview;
    */


    fun setSharedElementRecyclerView(view: View, id: Int, sharedElementRecyclerView: RecyclerView) {
        this.sharedElementRecyclerViewViewID = view.id
        this.sharedElementRecyclerView = sharedElementRecyclerView

        for (size in sharedElementListOut.size - 1 downTo -1 + 1) {
            if (sharedElementListOut[size].id == id) {
                sharedElementListOut.removeAt(size)
            }
        }
        val tag = view.id.toString() + getRandomString(10)
        ViewCompat.setTransitionName(view, tag)
        sharedElementListOut.add(SharedElement(view, id, tag, true))
    }

    fun setSharedElementRecyclerViewReturnView(sharedElementRecyclerViewReturnView: View, position: Int) {
        val tag = sharedElementRecyclerViewReturnView.id.toString() + getRandomString(10)
        sharedElementRecyclerViewReturnView.tag = tag
        this.sharedElementRecyclerViewReturnView = sharedElementRecyclerViewReturnView
        this.sharedElementRecyclerViewPosition = position
        ViewCompat.setTransitionName(sharedElementRecyclerViewReturnView, tag)
    }


    //
    //    /**
    //     * Add all views that is possible to use as shared element when returning.
    //     * All views including addSharedElement view should be added in order.
    //     * In receiving fragment, updateSharedElement should be called when the view is changed.
    //     * removeSharedElements should be called before this method.
    //     * Must be called in sending fragment.
    //     *
    //     * @param view shared element view
    //     */
    //    public void addReturnSharedElement(View view) {
    //        for (int size = sharedElementListOut.size() - 1; size > -1; size--) {
    //            if (sharedElementListOut.get(size).getView() == view) {
    //                sharedElementListOut.remove(size);
    //            }
    //        }
    //        String tag = view.getId() + GlobalClass.getRandomString(10);
    //        ViewCompat.setTransitionName(view, tag);
    //        sharedElementListOut.add(new SharedElement(view, 0, tag, false));
    //    }
    //
    //    /**
    //     * Update returning shared element view to prepare for return.
    //     * In sending fragment addReturnSharedElement should be called first.
    //     * Must be called in receiving fragment.
    //     *
    //     * @param oldPosition last position of view in list
    //     * @param newPosition current position of view in list
    //     * @param view        the new view in second fragment
    //     */
    //    public void updateSharedElement(int oldPosition, int newPosition, View view) {
    //        if (view != null) {
    //            if (oldPosition > -1 && oldPosition < sharedElementListIn.size()) {
    //                sharedElementListIn.get(oldPosition).setEnable(false);
    //            }
    //            if (newPosition > -1 && newPosition < sharedElementListIn.size()) {
    //                sharedElementListIn.get(newPosition).setEnable(true);
    //                ViewCompat.setTransitionName(view, sharedElementListIn.get(newPosition).getTag());
    //                view.setTag(sharedElementListIn.get(newPosition).getTag());
    //            }
    //        }
    //    }
    //
    fun removeSharedElements() {
        sharedElementListOut.clear()
        //        sharedElementListIn.clear();
    }

    fun getSharedElementListOut(): List<SharedElement> {
        return sharedElementListOut
    }

    fun setSharedElementListOut(sharedElementListOut: MutableList<SharedElement>) {
        this.sharedElementListOut = sharedElementListOut
    }
}
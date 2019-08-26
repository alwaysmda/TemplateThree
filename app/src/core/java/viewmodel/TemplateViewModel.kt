package viewmodel

import adapter.TemplateAdapter
import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dialog.CustomDialog
import http.API
import http.Client
import http.OnResponseListener
import http.Request
import http.Response
import main.ApplicationClass
import main.BaseFragment


class TemplateViewModel(private val repository: Client, private val appClass: ApplicationClass) : ViewModel(),
    OnResponseListener {
    override fun onResponse(response: Response) {
        when (response.statusName) {
            Response.StatusName.OK                   -> {
                when (response.request._ID) {
                    API.Download.ID -> gotDownload(response)
                }
            }
            Response.StatusName.NoInternetConnection -> {
                showDialog.value = CustomDialog(appClass)
                    .setTitle("No Connection")
                    .setContent("Check your internet connection and try again.")
                    .setPositiveText("Try Again")
                    .setCancelabel(false)
                    .onPositive { repository.request(response.request) }
            }
            else                                     -> {
                showDialog.value = CustomDialog(appClass)
                    .setTitle("Connection Error")
                    .setContent("Server is out of reach. Please try again.")
                    .setPositiveText("Try Again")
                    .setCancelabel(false)
                    .onPositive { repository.request(response.request) }
            }
        }
    }


    override fun onProgress(request: Request, bytesWritten: Long, totalSize: Long, percent: Int) {
    }

    //Local
    private val list: ArrayList<String> = ArrayList(
        arrayListOf(
            "temp 1",
            "temp 2",
            "temp 3",
            "temp 4",
            "temp 5",
            "temp 6",
            "temp 7",
            "temp 8",
            "temp 9"
        )
    )

    //Event
    val showDialog: MutableLiveData<CustomDialog> = MutableLiveData()
    val snack: MutableLiveData<Int> = MutableLiveData()
    val doBack: MutableLiveData<Boolean> = MutableLiveData()
    val startFragment: MutableLiveData<BaseFragment> = MutableLiveData()

    //Binding
    val adapter: ObservableField<TemplateAdapter> = ObservableField()

    init {
        adapter.set(TemplateAdapter(list, this::onItemClick))
    }

    fun handleIntent(bundle: Bundle?) {
        bundle?.let {

        }
    }

    private fun onItemClick(holder: TemplateAdapter.TemplateViewHolder, view: View?, position: Int) {

    }

    fun onIvBackClick() {
        doBack.value = true
    }

    private fun gotDownload(response: Response) {

    }
}
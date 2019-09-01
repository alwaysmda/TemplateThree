package viewmodel

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xodus.templatethree.R
import dialog.CustomDialog
import http.*
import main.ApplicationClass
import main.BaseFragment


class TemplateItemViewModel(private val repository: Client, private val appClass: ApplicationClass) : ViewModel(),
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
                    .setTitle(R.string.error_title_no_internet)
                    .setContent(R.string.error_text_no_internet)
                    .setPositiveText(R.string.try_again)
                    .setCancelabel(false)
                    .onPositive { repository.request(response.request) }
            }
            else                                     -> {
                showDialog.value = CustomDialog(appClass)
                    .setTitle(R.string.error_title_connection_error)
                    .setContent(R.string.error_text_connection_error)
                    .setPositiveText(R.string.try_again)
                    .setCancelabel(false)
                    .onPositive { repository.request(response.request) }
            }
        }
    }


    override fun onProgress(request: Request, bytesWritten: Long, totalSize: Long, percent: Int) {
    }

    //Local


    //Event
    val showDialog: MutableLiveData<CustomDialog> = MutableLiveData()
    val snack: MutableLiveData<Int> = MutableLiveData()
    val snackString: MutableLiveData<String> = MutableLiveData()
    val doBack: MutableLiveData<Boolean> = MutableLiveData()
    val startFragment: MutableLiveData<BaseFragment> = MutableLiveData()


    //Binding

    init {

    }

    fun handleIntent(bundle: Bundle?) {
        bundle?.let {

        }
    }


    fun onTvItemClick() {
        snack.value = R.string.app_name
    }

    private fun gotDownload(response: Response) {

    }
}
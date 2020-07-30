package viewmodel

import adapter.TemplateAdapter
import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xodus.templatethree.R
import dialog.CustomDialog
import http.*
import main.ApplicationClass
import main.BaseFragment
import main.Constant
import model.Template
import util.log
import java.util.*
import kotlin.collections.ArrayList


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
                    .setTitle(R.string.error_title_no_internet)
                    .setContent(R.string.error_text_no_internet)
                    .setPositiveText(R.string.try_again)
                    .setNegativeText(R.string.cancel)
                    .setCancelabel(false)
                    .onPositive { retry(response.request) }
                    .onNegative { onCancel(response.request._ID) }
            }
            else                                     -> {
                showDialog.value = CustomDialog(appClass)
                    .setTitle(R.string.error_title_connection_error)
                    .setContent(R.string.error_text_connection_error)
                    .setPositiveText(R.string.try_again)
                    .setCancelabel(false)
                    .onPositive { retry(response.request) }
                    .onNegative { onCancel(response.request._ID) }
            }
        }
    }


    override fun onProgress(request: Request, bytesWritten: Long, totalSize: Long, percent: Int) {
    }

    //Local
    private val list: ArrayList<Template> = ArrayList(
        arrayListOf(
            Template("temp 1", 0, false),
            Template("temp 2", 0, false),
            Template("temp 3", 0, false),
            Template("شماره ۴", 0, false),
            Template("شماره ۵", 0, false),
            Template("شماره ۶", 0, false),
            Template("temp 7", 0, false),
            Template("temp 8", 0, false),
            Template("temp 9", 0, false)
        )
    )

    //Event
    val showDialog: MutableLiveData<CustomDialog> = MutableLiveData()
    val snack: MutableLiveData<Int> = MutableLiveData()
    val snackString: MutableLiveData<String> = MutableLiveData()
    val doBack: MutableLiveData<Boolean> = MutableLiveData()
    val startFragment: MutableLiveData<BaseFragment> = MutableLiveData()
    val changeLocale: MutableLiveData<Locale> = MutableLiveData()

    //Binding
    val tvTitleText: ObservableInt = ObservableInt(R.string.app_name)
    val adapter: ObservableField<TemplateAdapter> = ObservableField(TemplateAdapter(this))

    init {
        tvTitleText.set(R.string.app_name)
        adapter.get()?.updateList(list)
    }

    fun handleIntent(bundle: Bundle?) {
        bundle?.let {

        }
    }

    private fun retry(request: Request) {
        repository.request(request)
    }

    private fun onCancel(id: Int) {
        when (id) {
            API.Download.ID -> {

            }
        }
    }

    fun onTvItemClick(data: Template, view: View) {
        log("${view.javaClass.simpleName} index=${list.indexOf(data)} data=$data")
        if (appClass.getStringPref(Constant.PREF_LANGUAGE) == Constant.CON_LANG_FA.value) {
            appClass.setPref(Constant.PREF_LANGUAGE, Constant.CON_LANG_EN.value)
            changeLocale.value = Locale(Constant.CON_LANG_EN.value)
        } else {
            appClass.setPref(Constant.PREF_LANGUAGE, Constant.CON_LANG_FA.value)
            changeLocale.value = Locale(Constant.CON_LANG_FA.value)
        }
    }

    private fun gotDownload(response: Response) {

    }
}
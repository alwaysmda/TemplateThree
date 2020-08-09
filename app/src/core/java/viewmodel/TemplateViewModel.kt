package viewmodel

import adapter.TemplateAdapter
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import com.xodus.templatethree.R
import dialog.CustomDialog
import http.*
import main.*
import model.Template
import util.SingleLiveEvent
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
    val showDialog: SingleLiveEvent<CustomDialog> = SingleLiveEvent()
    val snack: SingleLiveEvent<Int> = SingleLiveEvent()
    val snackString: SingleLiveEvent<String> = SingleLiveEvent()
    val doBack: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val startFragment: SingleLiveEvent<BaseFragment> = SingleLiveEvent()
    val changeLocale: SingleLiveEvent<Locale> = SingleLiveEvent()
    val hideNavBar: SingleLiveEvent<Boolean> = SingleLiveEvent()

    //Binding
    val tvTitleText: ObservableInt = ObservableInt(R.string.app_name)
    val adapter: ObservableField<TemplateAdapter> = ObservableField(TemplateAdapter(this))

    init {
        tvTitleText.set(R.string.app_name)
        adapter.get()?.updateList(list)

        repository.request(API.Get(this, "https://www.httpbin.org/get"))
    }

    fun handleIntent(bundle: Bundle?) {
        bundle?.let {

        }
    }

    fun onTvTitleClick() {
        if (appClass.getBooleanPref(PREF_DARK_THEME)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            appClass.setPref(PREF_DARK_THEME, false)
            Handler().postDelayed({ hideNavBar.value = false }, 300)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            appClass.setPref(PREF_DARK_THEME, true)
            Handler().postDelayed({ hideNavBar.value = false }, 300)
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
        if (appClass.getStringPref(PREF_LANGUAGE) == CON_LANG_FA) {
            appClass.setPref(PREF_LANGUAGE, CON_LANG_EN)
            changeLocale.value = Locale(CON_LANG_EN)
        } else {
            appClass.setPref(PREF_LANGUAGE, CON_LANG_FA)
            changeLocale.value = Locale(CON_LANG_FA)
        }
    }

    private fun gotDownload(response: Response) {

    }
}
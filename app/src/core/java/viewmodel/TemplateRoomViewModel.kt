package viewmodel

import adapter.TemplateAdapter
import adapter.TemplateRoomAdapter
import android.os.Bundle
import android.text.Selection.selectAll
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.util.ArrayUtils.removeAll
import com.xodus.templatethree.R
import db.TemplateDao
import dialog.CustomDialog
import http.API
import http.Client
import http.OnResponseListener
import http.Request
import http.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import main.ApplicationClass
import main.BaseFragment
import main.Constant
import model.Template
import model.TemplateRoom
import util.log
import java.util.*
import kotlin.collections.ArrayList


class TemplateRoomViewModel(private val repository: Client, private val appClass: ApplicationClass, private val templateDao: TemplateDao) : ViewModel(),
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
    private val list: ArrayList<TemplateRoom> = ArrayList()

    //Event
    val showDialog: MutableLiveData<CustomDialog> = MutableLiveData()
    val snack: MutableLiveData<Int> = MutableLiveData()
    val doBack: MutableLiveData<Boolean> = MutableLiveData()
    val startFragment: MutableLiveData<BaseFragment> = MutableLiveData()
    val changeLocale: MutableLiveData<Locale> = MutableLiveData()

    //Binding
    val tvTitleText: ObservableInt = ObservableInt(R.string.app_name)
    val adapter: ObservableField<TemplateRoomAdapter> = ObservableField(TemplateRoomAdapter(this))

    init {
        tvTitleText.set(R.string.app_name)
        CoroutineScope(IO).launch {
            list.addAll(selectAll())
            updateRecyclerView()
        }
    }

    fun handleIntent(bundle: Bundle?) {
        bundle?.let {

        }
    }

    fun onBtnAddClick() {
        CoroutineScope(IO).launch {
            addItem(TemplateRoom(0, "Item ${list.size + 1}", true))
            updateRecyclerView()
        }
    }

    private suspend fun addItem(item: TemplateRoom) {
        templateDao.insert(item)
        list.add(item)
    }

    fun onBtnRemoveClick() {
        CoroutineScope(IO).launch {
            removeItem(list[list.size - 1])
            updateRecyclerView()
        }
    }

    private suspend fun removeItem(item: TemplateRoom) {
        if (list.isNotEmpty()) {
            templateDao.delete(list[list.size - 1]._templateInt.toLong())
            list.remove(item)
        }
    }

    fun onBtnResetClick() {
        CoroutineScope(IO).launch {
            removeAll()
            list.clear()
            updateRecyclerView()
            delay(500)
            insertAll()
            list.addAll(selectAll())
            updateRecyclerView()
        }
    }

    private suspend fun removeAll() {
        templateDao.deleteAll()
    }

    private suspend fun insertAll() {
        templateDao.insertAll(arrayListOf(
            TemplateRoom(0, "Temp 1", true),
            TemplateRoom(1, "Temp 2", true),
            TemplateRoom(2, "Temp 3", true)
        ))
    }

    private suspend fun selectAll(): List<TemplateRoom> {
        return templateDao.selectAll()
    }

    private suspend fun updateRecyclerView() {
        withContext(Main) {
            adapter.get()?.updateList(list)
        }
    }

    private fun onItemClick(holder: TemplateAdapter.TemplateViewHolder, view: View?, position: Int) {
        if (appClass.getStringPref(Constant.PREF_LANGUAGE) == Constant.CON_LANG_FA.value) {
            appClass.setPref(Constant.PREF_LANGUAGE, Constant.CON_LANG_EN.value)
            changeLocale.value = Locale(Constant.CON_LANG_EN.value)
        } else {
            appClass.setPref(Constant.PREF_LANGUAGE, Constant.CON_LANG_FA.value)
            changeLocale.value = Locale(Constant.CON_LANG_FA.value)
        }
    }

    fun onTvItemClick(data: TemplateRoom, view: View) {
        log("${view.javaClass.simpleName} index=${list.indexOf(data)} data=$data")
    }

    fun onIvBackClick() {
        doBack.value = true
    }

    private fun gotDownload(response: Response) {

    }
}
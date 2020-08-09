package viewmodel

import adapter.TemplateRoomAdapter
import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xodus.templatethree.R
import db.TemplateDao
import dialog.CustomDialog
import http.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import main.ApplicationClass
import main.BaseFragment
import model.TemplateRoom
import util.SingleLiveEvent
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
    private val list: ArrayList<TemplateRoom> = ArrayList()
    private var sortDesc = false

    //Event
    val showDialog: SingleLiveEvent<CustomDialog> = SingleLiveEvent()
    val snack: SingleLiveEvent<Int> = SingleLiveEvent()
    val snackString: SingleLiveEvent<String> = SingleLiveEvent()
    val doBack: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val startFragment: SingleLiveEvent<BaseFragment> = SingleLiveEvent()
    val changeLocale: SingleLiveEvent<Locale> = SingleLiveEvent()

    //Binding
    val tvTitleText: ObservableInt = ObservableInt(R.string.app_name)
    val adapter: ObservableField<TemplateRoomAdapter> = ObservableField(TemplateRoomAdapter(this))

    init {
        tvTitleText.set(R.string.app_name)
        viewModelScope.launch {
            list.addAll(selectAll())
            updateRecyclerView()
        }
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

    fun onBtnAddClick() {
        viewModelScope.launch {
            val item = TemplateRoom("Item ${list.size + 1}", true)
            list.add(item)
            updateRecyclerView()
            addItem(item)
        }
    }

    fun onBtnRemoveClick() {
        if (list.isNotEmpty()) {
            viewModelScope.launch {
                val item = list[list.size - 1]
                list.remove(item)
                updateRecyclerView()
                removeItem(item)
            }
        }
    }

    fun onBtnResetClick() {
        viewModelScope.launch {
            removeAll()
            list.clear()
            updateRecyclerView()
            delay(500)
            insertAll()
            list.addAll(selectAll())
            updateRecyclerView()
        }
    }

    fun onBtnAddAllClick() {
        viewModelScope.launch {
            insertAll()
            list.clear()
            list.addAll(selectAll())
            updateRecyclerView()
        }
    }

    fun onBtnRemoveAllClick() {
        viewModelScope.launch {
            removeAll()
            list.clear()
            updateRecyclerView()
        }
    }

    fun onBtnSortClick() {
        viewModelScope.launch {
            val result = selectAll()
            list.clear()
            sortDesc = sortDesc.not()
            if (sortDesc) {
                list.addAll(result.sortedByDescending { it.templateInt })
            } else {
                list.addAll(result.sortedBy { it.templateInt })
            }
            updateRecyclerView()
        }
    }


    private suspend fun addItem(item: TemplateRoom) {
        templateDao.insert(item)
    }

    private suspend fun removeItem(item: TemplateRoom) {
        templateDao.delete(item._templateInt.toLong())
    }

    private suspend fun removeAll() {
        templateDao.deleteAll()
    }

    private suspend fun insertAll() {
        val result: ArrayList<TemplateRoom> = ArrayList()
        for (i in 1..20) {
            result.add(TemplateRoom("Item $i", true))
        }
        templateDao.insertAll(result)
    }

    private suspend fun selectAll(): List<TemplateRoom> {
        return templateDao.selectAll()
    }

    private suspend fun updateRecyclerView() {
        withContext(Main) {
            adapter.get()?.updateList(list)
        }
    }

    fun onTvItemClick(data: TemplateRoom, view: View) {
        log("${view.javaClass.simpleName} index=${list.indexOf(data)} data=$data")
        val index = list.indexOf(data)
        list.shuffle()
        list.remove(data)
        list.add(index, data)
        adapter.get()?.updateList(list)
    }

    fun onIvBackClick() {
        doBack.value = true
    }

    private fun gotDownload(response: Response) {

    }
}
package viewmodel

import adapter.TemplateRoomAdapter
import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import db.TemplateDao
import http.API
import http.Client
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import main.*
import model.TemplateRoom
import util.BaseFragmentFactory
import util.log


class TemplateRoomViewModel(private val repository: Client, private val appClass: ApplicationClass, private val templateDao: TemplateDao) : BaseViewModel(repository, appClass) {

    //Local
    private val list: ArrayList<TemplateRoom> = ArrayList()
    private var sortDesc = false

    //Event
    //    val showDialog: SingleLiveEvent<CustomDialog> = SingleLiveEvent()

    //Binding
    val tvTitleText: ObservableField<String> = ObservableField(appClass.appName)
    val adapter: ObservableField<TemplateRoomAdapter> = ObservableField(TemplateRoomAdapter(this))

    init {
        viewModelScope.launch {
            list.addAll(selectAll())
            updateRecyclerView()
        }
    }

    fun handleIntent(bundle: Bundle?) {
        bundle?.let {
            log("GOT INTENT DATA : ${it.getString(BaseFragmentFactory.ARG_NAME, "NO")}")
        }
    }

    fun onTitleClick() {
        startFragment.value = BaseFragmentFactory.templateRoomFragment("YES")
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

    fun onBtnChangeThemeClick() {
        if (appClass.currentTheme == Themes.LIGHT_PINK) {
            appClass.changeTheme(Themes.DARK_BLUE)
        } else {
            appClass.changeTheme(Themes.LIGHT_PINK)
        }
    }

    fun onBtnChangeLanguageClick() {
        if (appClass.currentLanguage == Languages.FA) {
            appClass.changeLang(Languages.EN)
        } else {
            appClass.changeLang(Languages.FA)
        }
        BaseActivity.getInstance().resetBottomBarTitles()
        rebind.value = appClass
    }

    fun onBtnRequestClick() {
        get(true)
    }

    private fun get(retry: Boolean = false) {
        showLoading.value = LoadingValue(true)
        repository.request(API.Get("https://www.httpbin.org/get"), { //OnSuccess //String Body
            log("THIS IS REQUEST BODY : $it")
        }, { //OnError
            if (retry) {
                onError(it, { //OnRetry
                    get()
                }, { //OnCancel
                    log("REQUEST CANCELED")
                })
            } else {
                onError(it) { //OnCancel
                    log("REQUEST CANCELED")
                }
            }
        }, { id, bytesWritten, totalSize, percent -> //OnProgress
            log("THIS IS REQUEST PROGRESS ID=$id WRITTEN=$bytesWritten TOTAL=$totalSize PERCENT=$percent")
        }) { //Full Response
            showLoading.value = LoadingValue(false)
            log("THIS IS FULL REQUEST DATA : ${it.toJSONObject()}")
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
}
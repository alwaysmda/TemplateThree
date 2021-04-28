package main

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import dialog.CustomDialog
import http.Client
import http.Request
import http.Response
import kotlinx.coroutines.*
import util.SingleLiveEvent
import util.log
import java.util.*
import kotlin.concurrent.schedule
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

abstract class BaseViewModel(private val repository: Client, private val appClass: ApplicationClass) : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job
    var initialized = false
    var disableBack = false
    var isBase = false
    var tabIndex = 0

    val showDialog: SingleLiveEvent<CustomDialog> = SingleLiveEvent()
    val snack: SingleLiveEvent<SnackValue> = SingleLiveEvent()
    val toast: SingleLiveEvent<SnackValue> = SingleLiveEvent()
    val doBack: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val showLoading: SingleLiveEvent<LoadingValue> = SingleLiveEvent()
    val rebind: SingleLiveEvent<Any> = SingleLiveEvent()
    val startFragment: SingleLiveEvent<BaseFragment<ViewDataBinding, BaseViewModel>> = SingleLiveEvent()

    class SnackValue(
        val message: String?,
        val long: Boolean = false
    )

    class LoadingValue(
        val show: Boolean,
        val hideLoader: Boolean = false
    )


    protected fun <T> doAsync(task: suspend () -> T, parse: (T) -> Unit = {}) {
        CoroutineScope(Dispatchers.Default).launch {
            val result = withContext(Dispatchers.Unconfined) {
                task.invoke()
            }
            withContext(Dispatchers.Main) {
                parse(result)
            }

        }
    }

    fun onDestroy() {
        job.cancel()
    }

    fun doAfter(delay: Long, action: () -> Unit) {
        val d = if (delay < 0) 0 else delay
        Timer().schedule(d) {
            CoroutineScope(Dispatchers.Main).launch {
                action()
            }
        }
    }


    private var exit: Boolean = false
    open fun onBackPressed(doSnack: Boolean): Boolean {
        if (disableBack) {
            return false
        } else if (doSnack) {
            if (exit) {
                exitProcess(0)
            } else {
                exit = true
                snack.value = SnackValue(appClass.tapToExit, true)
                doAfter(3500) {
                    exit = false
                }
            }
        } else {
            return true
        }
        return false
    }

    fun doBack() {
        doBack.value = true
    }

    open fun onAddClick() {
        log("BaseViewModel : OnAddClick")
    }


    open fun onError(response: Response, onRetry: (() -> Unit)? = null, onCancel: (Request) -> Unit = {}) {
        when (response.statusName) {
            Response.StatusName.NoInternetConnection -> {
                if (onRetry == null) {
                    showDialog.value = CustomDialog(appClass)
                        .setTitle(appClass.errorConnection)
                        .setContent(appClass.errorConnectionDesc)
                        .setPositiveText(appClass.okay)
                        .setCancelabel(false)
                        .onPositive { onCancel(response.request) }
                } else {
                    showDialog.value = CustomDialog(appClass)
                        .setTitle(appClass.errorConnection)
                        .setContent(appClass.errorConnectionDesc)
                        .setPositiveText(appClass.retry)
                        .setNegativeText(appClass.cancel)
                        .setCancelabel(false)
                        .onPositive { onRetry() }
                        .onNegative { onCancel(response.request) }
                }
            }
            else                                     -> {
                if (onRetry == null) {
                    showDialog.value = CustomDialog(appClass)
                        .setTitle(appClass.errorServer)
                        .setContent(appClass.errorServerDesc)
                        .setPositiveText(appClass.okay)
                        .setCancelabel(false)
                        .onPositive { onCancel(response.request) }
                } else {
                    showDialog.value = CustomDialog(appClass)
                        .setTitle(appClass.errorServer)
                        .setContent(appClass.errorServerDesc)
                        .setPositiveText(appClass.retry)
                        .setNegativeText(appClass.cancel)
                        .setCancelabel(false)
                        .onPositive { onRetry() }
                        .onNegative { onCancel(response.request) }
                }
            }
        }
    }
}
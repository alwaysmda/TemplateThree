package viewmodel

import adapter.TemplateAdapter
import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import http.Client
import main.ApplicationClass
import main.BaseViewModel
import model.Template
import util.SingleLiveEvent


class TemplateViewModel(private val repository: Client, private val appClass: ApplicationClass) : BaseViewModel(repository, appClass) {


    //Local
    private val list: ArrayList<Template> = ArrayList(
        arrayListOf(
            Template("temp 1"),
            Template("temp 2"),
            Template("temp 3"),
            Template("temp 4"),
            Template("temp 5"),
            Template("temp 6"),
            Template("temp 7"),
            Template("temp 8"),
            Template("temp 9")
        )
    )

    //Event
    val hideNavBar: SingleLiveEvent<Boolean> = SingleLiveEvent()

    //Binding
    val tvTitleText: ObservableField<String> = ObservableField(appClass.appName)
    val adapter: ObservableField<TemplateAdapter> = ObservableField(TemplateAdapter(this))

    init {
        adapter.get()?.updateList(list)
    }


    fun handleIntent(bundle: Bundle?) {
        bundle?.let {

        }
    }

    fun onTvTitleClick() {

    }

    override fun onBackPressed(doSnack: Boolean): Boolean {
        return super.onBackPressed(isBase)
    }

    fun onTvItemClick(data: Template, view: View) {
        snack.value = SnackValue("On ${data.templateString} Click : ${view.javaClass.simpleName}")
    }

}


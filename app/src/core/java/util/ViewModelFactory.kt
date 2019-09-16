package util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import db.TemplateDao
import http.Client
import main.ApplicationClass
import viewmodel.TemplateRoomViewModel
import viewmodel.TemplateViewModel

class ViewModelFactory(private val repository: Client, private val appClass: ApplicationClass, private val templateDao: TemplateDao) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            TemplateViewModel::class.java          -> {
                TemplateViewModel(repository, appClass) as T
            }
            TemplateRoomViewModel::class.java -> {
                TemplateRoomViewModel(repository, appClass, templateDao) as T
            }


            else                                   -> {
                TemplateViewModel(repository, appClass) as T
            }
        }
    }
}
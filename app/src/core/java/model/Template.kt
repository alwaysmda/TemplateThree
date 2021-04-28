package model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import org.json.JSONArray
import org.json.JSONObject

data class Template(
    var _templateString: String = "",
    var _templateInt: Int = 0,
    var _templateBoolean: Boolean = false
) : BaseObservable() {

    var templateString: String
        @Bindable get() = _templateString
        set(value) {
            _templateString = value
            notifyPropertyChanged(BR.templateString)
        }

    var templateInt: Int
        @Bindable get() = _templateInt
        set(value) {
            _templateInt = value
            notifyPropertyChanged(BR.templateInt)
        }

    var templateBoolean: Boolean
        @Bindable get() = _templateBoolean
        set(value) {
            _templateBoolean = value
            notifyPropertyChanged(BR.templateBoolean)
        }

    constructor(item: Template) : this(
        item.templateString,
        item.templateInt,
        item.templateBoolean
    )

    fun toJson(): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("templateString", this.templateString)
            jsonObject.put("templateInt", this.templateInt)
            jsonObject.put("templateBoolean", this.templateBoolean)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return jsonObject
    }

    companion object {

        fun init(response: String): ArrayList<Template> {
            val list = ArrayList<Template>()
            try {
                val jsonArray = JSONObject(response).getJSONArray("items")
                for (i in 0 until jsonArray.length()) {
                    list.add(toModel(jsonArray.getJSONObject(i)))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return list
        }


        fun toJson(userList: ArrayList<Template>): JSONArray {
            val jsonArray = JSONArray()
            try {
                for (i in userList.indices) {
                    jsonArray.put(userList[i].toJson())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return jsonArray
        }

        fun toModel(jsonArray: JSONArray): ArrayList<Template> {
            val list = ArrayList<Template>()
            try {
                for (i in 0 until jsonArray.length()) {
                    list.add(toModel(jsonArray.getJSONObject(i)))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return list
        }

        fun toModel(jsonObject: JSONObject): Template {
            val item = Template()
            try {
                item.templateString = jsonObject.getString("templateString")
                item.templateInt = jsonObject.getInt("templateInt")
                item.templateBoolean = jsonObject.getBoolean("templateBoolean")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return item
        }


        fun cloneList(requestList: ArrayList<Template>): ArrayList<Template> {
            val clonedList = ArrayList<Template>(requestList.size)
            for (item in requestList) {
                clonedList.add(Template(item))
            }
            return clonedList
        }
    }
}

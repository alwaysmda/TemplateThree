package model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.library.baseAdapters.BR
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

@Entity(tableName = "tbl_template_room")
data class TemplateRoom(
    @PrimaryKey(autoGenerate = true)
    var _templateInt: Int = 0,
    var _templateString: String = "",
    var _templateBoolean: Boolean = false
) : BaseObservable() {

    var templateInt: Int
        @Ignore
        @Bindable get() = _templateInt
        set(value) {
            _templateInt = value
            notifyPropertyChanged(BR.templateInt)
        }

    var templateString: String
        @Ignore
        @Bindable get() = _templateString
        set(value) {
            _templateString = value
            notifyPropertyChanged(BR.templateString)
        }

    var templateBoolean: Boolean
        @Ignore
        @Bindable get() = _templateBoolean
        set(value) {
            _templateBoolean = value
            notifyPropertyChanged(BR.templateBoolean)
        }

    constructor(item: TemplateRoom) : this(
        item.templateInt,
        item.templateString,
        item.templateBoolean
    )

    fun toJson(): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("templateInt", this.templateInt)
            jsonObject.put("templateString", this.templateString)
            jsonObject.put("templateBoolean", this.templateBoolean)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return jsonObject
    }

    companion object {

        fun init(response: String): ArrayList<TemplateRoom> {
            val list = ArrayList<TemplateRoom>()
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


        fun toJson(userList: ArrayList<TemplateRoom>): JSONArray {
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

        fun toModel(jsonArray: JSONArray): ArrayList<TemplateRoom> {
            val list = ArrayList<TemplateRoom>()
            try {
                for (i in 0 until jsonArray.length()) {
                    list.add(toModel(jsonArray.getJSONObject(i)))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return list
        }

        fun toModel(jsonObject: JSONObject): TemplateRoom {
            val item = TemplateRoom()
            try {
                item.templateInt = jsonObject.getInt("templateInt")
                item.templateString = jsonObject.getString("templateString")
                item.templateBoolean = jsonObject.getBoolean("templateBoolean")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return item
        }


        fun cloneList(requestList: ArrayList<TemplateRoom>): ArrayList<TemplateRoom> {
            val clonedList = ArrayList<TemplateRoom>(requestList.size)
            for (item in requestList) {
                clonedList.add(TemplateRoom(item))
            }
            return clonedList
        }
    }
}

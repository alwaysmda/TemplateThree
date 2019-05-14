package com.xodus.templatetwo.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

class Template {
    var templateString: String? = null
    var templateInt: Int = 0
    var isTemplateBoolean: Boolean = false

    constructor() {}

    constructor(templateString: String, templateInt: Int, templateBoolean: Boolean) {
        this.templateString = templateString
        this.templateInt = templateInt
        this.isTemplateBoolean = templateBoolean
    }

    constructor(item: Template) {
        this.templateString = item.templateString
        this.templateInt = item.templateInt
        this.isTemplateBoolean = item.isTemplateBoolean
    }

    fun toJson(): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("string", this.templateString)
            jsonObject.put("int", this.templateInt)
            jsonObject.put("boolean", this.isTemplateBoolean)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return jsonObject
    }

    companion object {

        fun init(response: String): List<Template> {
            val list = ArrayList<Template>()
            try {
                val responseJson = JSONObject(response)
                val jsonArray = responseJson.getJSONArray("items")
                for (i in 0 until jsonArray.length()) {
                    list.add(toModel(jsonArray.getJSONObject(i)))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return list
        }


        fun toJson(userList: List<Template>): JSONArray {
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

        fun toModel(jsonArray: JSONArray): List<Template> {
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

                item.templateString = jsonObject.getString("string")
                item.templateInt = jsonObject.getInt("int")
                item.isTemplateBoolean = jsonObject.getBoolean("boolean")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return item
        }


        fun cloneList(requestList: List<Template>): List<Template> {
            val clonedList = ArrayList<Template>(requestList.size)
            for (item in requestList) {
                clonedList.add(Template(item))
            }
            return clonedList
        }
    }
}

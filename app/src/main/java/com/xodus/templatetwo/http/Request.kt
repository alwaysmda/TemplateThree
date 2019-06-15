package com.xodus.templatetwo.http

import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

open class Request {
    enum class Method {
        POST, GET, PUT, DELETE, DOWNLOAD, RAW
    }



    var ID: Int = 0
    var tag: String = ""
    var method: Method = Method.GET
    var url: String = ""
    var requestName: String = ""
    var headers: HashMap<String, String> = HashMap()
    var params: HashMap<String, Any> = HashMap()
    var raw: String = ""
    var retryMax: Int = 0
    var retryAttempt: Int = 0
    lateinit var onResponse : OnResponseListener

    fun putHeader(key: String, value: String) {
        headers[key] = value
    }

    fun addRetryAttempt() {
        retryAttempt++
    }


    fun toJSONObject(): JSONObject {
        val jsonObject = JSONObject()
        val paramObject = JSONObject()
        val headerObject = JSONObject()
        try {
                for (key in params.keys) {
                    paramObject.put(key, params[key])
                }
                for (key in headers.keys) {
                    headerObject.put(key, headers[key])
                }
            jsonObject.put("id", ID)
            jsonObject.put("method", method)
            jsonObject.put("url", url)
            jsonObject.put("name", requestName)
            jsonObject.put("raw", raw)
            jsonObject.put("params", paramObject)
            jsonObject.put("headers", headerObject)
            jsonObject.put("retry_max", retryMax)
            jsonObject.put("retry_attempt", retryAttempt)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }
}
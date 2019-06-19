package com.xodus.templatetwo.http

import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

open class Request {
    enum class Method {
        POST, GET, PUT, DELETE, DOWNLOAD, RAW
    }



    var _ID: Int = 0
    var _tag: String = ""
    var _method: Method = Method.GET
    var _url: String = ""
    var _name: String = ""
    var _headers: HashMap<String, String> = HashMap()
    var _params: HashMap<String, Any> = HashMap()
    var _raw: String = ""
    var _retryMax: Int = 0
    var _retryAttempt: Int = 0
    lateinit var _onResponse : OnResponseListener

    fun putHeader(key: String, value: String) {
        _headers[key] = value
    }

    fun addRetryAttempt() {
        _retryAttempt++
    }


    fun toJSONObject(): JSONObject {
        val jsonObject = JSONObject()
        val paramObject = JSONObject()
        val headerObject = JSONObject()
        try {
                for (key in _params.keys) {
                    paramObject.put(key, _params[key])
                }
                for (key in _headers.keys) {
                    headerObject.put(key, _headers[key])
                }
            jsonObject.put("id", _ID)
            jsonObject.put("method", _method)
            jsonObject.put("url", _url)
            jsonObject.put("name", _name)
            jsonObject.put("raw", _raw)
            jsonObject.put("params", paramObject)
            jsonObject.put("headers", headerObject)
            jsonObject.put("retry_max", _retryMax)
            jsonObject.put("retry_attempt", _retryAttempt)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jsonObject
    }
}
package http

import http.Response.StatusName.*
import okhttp3.Headers
import org.json.JSONException
import org.json.JSONObject
import java.io.File

open class Response() {
    enum class Status {
        SUCCESS,
        FAILURE
    }

    enum class StatusName {
        NoInternetConnection,  //0
        OK,  //200
        Created,  //201
        NoContent,  //204
        NotModified,  //304
        BadRequest,  //400
        Unauthorized,  //401
        Forbidden,  //403
        NotFound,  //404
        Conflict,  //409
        InternalServerError,  //500
        Other  //Other
    }

    lateinit var request: Request
    var statusCode: Int = 0
    var statusName: StatusName? = null
    var headers: Headers? = null
    var body: String = ""
    var status: Status? = null

    constructor(request: Request, statusCode: Int, headers: Headers?, body: String, status: Status) : this() {
        this.request = request
        this.statusCode = statusCode
        this.headers = headers
        this.body = body
        this.status = status
        return when (statusCode) {
            0 -> this.statusName = NoInternetConnection
            200 //OK
            -> this.statusName = OK
            201 //Created
            -> this.statusName = Created
            204 //No Content
            -> this.statusName = NoContent
            304 //Not Modified
            -> this.statusName = NotModified
            400 //Bad Request
            -> this.statusName = BadRequest
            401 //Unauthorized
            -> this.statusName = Unauthorized
            403 //Forbidden
            -> this.statusName = Forbidden
            404 //Not Found
            -> this.statusName = NotFound
            409 //Conflict
            -> this.statusName = Conflict
            500 //Internal Server Error
            -> this.statusName = InternalServerError
            else //Other
              -> this.statusName = Other
        }
    }

    fun toJSONObject(): JSONObject {
        val jsonObject = JSONObject()
        val headerObject = JSONObject()
        try {
            headers?.let {
                for (name in it.names()) {
                    it.get(name)
                    headerObject.put(name, it.get(name))
                }
            }
            jsonObject.put("request", request.toJSONObject())
            jsonObject.put("status", status)
            jsonObject.put("status_code", statusCode)
            jsonObject.put("status_name", statusName)
            jsonObject.put("header", headerObject)
            jsonObject.put("body", body)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonObject
    }


    fun toJSONObject(file: File): JSONObject {
        val jsonObject = JSONObject()
        val headerObject = JSONObject()
        try {
            headers?.let {
                for (name in it.names()) {
                    it.get(name)
                    headerObject.put(name, it.get(name))
                }
            }
            jsonObject.put("request", request.toJSONObject())
            jsonObject.put("status", status)
            jsonObject.put("status_code", statusCode)
            jsonObject.put("status_name", statusName)
            jsonObject.put("header", headerObject)
            jsonObject.put("body", file.path)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return jsonObject
    }
}
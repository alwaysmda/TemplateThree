package com.xodus.templatetwo.http

import com.xodus.templatetwo.main.Constant.*
import java.util.HashMap

open class API : Request() {
    protected val BASE_API = "https://www.xodus.ir/api/v1"
    companion object {
        val PARAM_NAME_DOWNLOAD_PATH = "KEY_NAME_DOWNLOAD_PATH"
        val PARAM_NAME_DOWNLOAD_NAME = "KEY_NAME_DOWNLOAD_NAME"
    }


    class GetMain(urlParam: String, params: HashMap<String, Any>, headers: HashMap<String, String>) : API() {
        init {
            this.ID = 1
            this.requestName = javaClass.simpleName
            this.method = Method.POST
            this.url = BASE_API + API_Main.toString(urlParam)
            this.params = params
            this.headers = headers
            this.retryMax = Integer.MAX_VALUE
        }
    }


    class UpdateFCMToken(token: String) : API() {

        init {
            this.ID = 2
            this.requestName = javaClass.simpleName
            this.method = Method.POST
            this.url = BASE_API + API_UpdateFCMToken
            val params = HashMap<String, Any>()
            params["token"] = token
            this.params = params
        }
    }


    class Download(url: String, path: String, name: String) : API() {

        init {
            this.ID = 3
            this.requestName = javaClass.simpleName
            this.method = Method.DOWNLOAD
            this.url = url
            val downloadData = HashMap<String, Any>()
            downloadData[PARAM_NAME_DOWNLOAD_PATH] = path
            downloadData[PARAM_NAME_DOWNLOAD_NAME] = name
            this.params = downloadData
        }
    }


    class GET(url: String) : API() {

        init {
            this.ID = 4
            this.requestName = javaClass.simpleName
            this.method = Method.GET
            this.url = url
            retryMax = 3
        }

    }


    class POST(url: String, params: HashMap<String, Any>) : API() {

        init {
            this.ID = 5
            this.requestName = javaClass.simpleName
            this.method = Method.POST
            this.url = url
            this.params = params
        }

    }

    class ReportError(eTime: String, eClass: String, eMethod: String, eMessage: String) : API() {

        init {
            this.ID = 6
            this.requestName = javaClass.simpleName
            this.method = Method.POST
            this.url = "$API_Main/log/error"
            val params = HashMap<String, Any>()
            params["time"] = eTime
            params["class"] = eClass
            params["method"] = eMethod
            params["message"] = eMessage
            this.params = params
        }
    }


    class GetLicenseKey(market: String) : API() {

        init {
            this.ID = 7
            this.requestName = javaClass.simpleName
            this.method = Method.POST
            this.url = "$API_Main/billing/get-key"
            val params = HashMap<String, Any>()
            params["market"] = market
            this.params = params
        }

    }

    class GetSKU : API() {
        init {
            this.ID = 8
            this.requestName = javaClass.simpleName
            this.method = Method.GET
            this.url = "$API_Main/billing/get-sku"
        }

    }


    class GetPayload : API() {
        init {
            this.ID = 9
            this.requestName = javaClass.simpleName
            this.method = Method.GET
            this.url = "$API_Main/billing/get-payload"
        }

    }


    class VerifyToken(market: String, sku: String, type: String, token: String, payload: String, extraData: String) :
        API() {

        init {
            this.ID = 10
            this.requestName = javaClass.simpleName
            this.method = Method.POST
            this.url = "$API_Main/billing/verify-token"
            val params = HashMap<String, Any>()
            params["market"] = market
            params["sku"] = sku
            params["type"] = type
            params["token"] = token
            params["payload"] = payload
            params["data"] = extraData
            this.params = params
        }

    }

}
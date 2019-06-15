package com.xodus.templatetwo.http

import androidx.core.content.pm.PackageInfoCompat
import com.xodus.templatetwo.BuildConfig
import com.xodus.templatetwo.extention.getAndroidID
import com.xodus.templatetwo.extention.getPackageInfo
import com.xodus.templatetwo.main.ApplicationClass
import com.xodus.templatetwo.main.Constant
import com.xodus.templatetwo.main.Constant.*
import java.util.HashMap

open class API : Request() {
    protected val BASE_API = "https://www.xodus.ir/api/v1"


    companion object {
        const val PARAM_NAME_DOWNLOAD_PATH = "KEY_NAME_DOWNLOAD_PATH"
        const val PARAM_NAME_DOWNLOAD_NAME = "KEY_NAME_DOWNLOAD_NAME"
    }


    class GetMain(
        urlParam: String,
        params: HashMap<String, Any>,
        headers: HashMap<String, String>,
        onResponse: OnResponseListener
    ) : API() {
        init {
            this.ID = 1
            this.requestName = javaClass.simpleName
            this.onResponse = onResponse
            this.method = Method.POST
            this.url = BASE_API
            this.params = params
            this.headers = headers
            this.retryMax = Integer.MAX_VALUE
        }
    }


    class UpdateFCMToken(token: String, onResponse: OnResponseListener) : API() {

        init {
            this.ID = 2
            this.requestName = javaClass.simpleName
            this.onResponse = onResponse
            this.method = Method.POST
            this.url = "$BASE_API/token/update"
            val params = HashMap<String, Any>()
            params["token"] = token
            this.params = params
        }
    }


    class Download(url: String, path: String, name: String, onResponse: OnResponseListener) : API() {

        init {
            this.ID = 3
            this.requestName = javaClass.simpleName
            this.onResponse = onResponse
            this.method = Method.DOWNLOAD
            this.url = url
            val downloadData = HashMap<String, Any>()
            downloadData[PARAM_NAME_DOWNLOAD_PATH] = path
            downloadData[PARAM_NAME_DOWNLOAD_NAME] = name
            this.params = downloadData
        }
    }


    class GET(url: String, onResponse: OnResponseListener) : API() {

        init {
            this.ID = 4
            this.requestName = javaClass.simpleName
            this.onResponse = onResponse
            this.method = Method.GET
            this.url = url
            retryMax = 3
        }

    }


    class POST(url: String, params: HashMap<String, Any>, onResponse: OnResponseListener) : API() {

        init {
            this.ID = 5
            this.requestName = javaClass.simpleName
            this.onResponse = onResponse
            this.method = Method.POST
            this.url = url
            this.params = params
        }

    }

    class ReportError(
        eTime: String,
        eClass: String,
        eMethod: String,
        eMessage: String,
        onResponse: OnResponseListener
    ) : API() {

        init {
            this.ID = 6
            this.requestName = javaClass.simpleName
            this.onResponse = onResponse
            this.method = Method.POST
            this.url = "$BASE_API/log/error"
            val params = HashMap<String, Any>()
            params["time"] = eTime
            params["class"] = eClass
            params["method"] = eMethod
            params["message"] = eMessage
            this.params = params
        }
    }


    class GetLicenseKey(market: String, onResponse: OnResponseListener) : API() {

        init {
            this.ID = 7
            this.requestName = javaClass.simpleName
            this.onResponse = onResponse
            this.method = Method.POST
            this.url = "$BASE_API/billing/get-key"
            val params = HashMap<String, Any>()
            params["market"] = market
            this.params = params
        }

    }

    class GetSKU(onResponse: OnResponseListener) : API() {
        init {
            this.ID = 8
            this.requestName = javaClass.simpleName
            this.onResponse = onResponse
            this.method = Method.GET
            this.url = "$BASE_API/billing/get-sku"
        }

    }


    class GetPayload(onResponse: OnResponseListener) : API() {
        init {
            this.ID = 9
            this.requestName = javaClass.simpleName
            this.onResponse = onResponse
            this.method = Method.GET
            this.url = "$BASE_API/billing/get-payload"
        }

    }


    class VerifyToken(
        market: String,
        sku: String,
        type: String,
        token: String,
        payload: String,
        extraData: String,
        onResponse: OnResponseListener
    ) :
        API() {

        init {
            this.ID = 10
            this.requestName = javaClass.simpleName
            this.onResponse = onResponse
            this.method = Method.POST
            this.url = "$BASE_API/billing/verify-token"
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
package com.xodus.templatetwo.http

import java.util.HashMap

open class API : Request() {
    protected val BASE_API = "https://www.xodus.ir/api/v1"


    companion object {
        const val PARAM_NAME_DOWNLOAD_PATH = "KEY_NAME_DOWNLOAD_PATH"
        const val PARAM_NAME_DOWNLOAD_NAME = "KEY_NAME_DOWNLOAD_NAME"
    }

    class Get(onResponse: OnResponseListener, url: String) : API() {
        companion object {
            var ID = 1
        }

        init {
            _ID = ID
            _name = javaClass.simpleName
            _onResponse = onResponse
            _method = Method.GET
            _url = url
            _retryMax = 3
        }
    }

    class Post(onResponse: OnResponseListener, url: String, vararg params: Pair<String, Any>) : API() {
        companion object {
            var ID = 2
        }

        init {
            _ID = ID
            _name = javaClass.simpleName
            _onResponse = onResponse
            _method = Method.POST
            _url = url
            for (t in params) {
                _params[t.first] = t.second
            }
        }
    }

    class Download(onResponse: OnResponseListener, url: String, path: String, name: String) : API() {
        companion object {
            var ID = 3
        }

        init {
            _ID = ID
            _name = javaClass.simpleName
            _onResponse = onResponse
            _method = Method.DOWNLOAD
            _url = url
            _params[PARAM_NAME_DOWNLOAD_PATH] = path
            _params[PARAM_NAME_DOWNLOAD_NAME] = name
        }
    }

    class GetMain(
        urlParam: String,
        params: HashMap<String, Any>,
        headers: HashMap<String, String>,
        onResponse: OnResponseListener
    ) : API() {
        companion object {
            var ID = 4
        }

        init {
            _ID = ID
            _name = javaClass.simpleName
            _onResponse = onResponse
            _method = Method.POST
            _url = BASE_API
            _params = params
            _headers = headers
            _retryMax = Integer.MAX_VALUE
        }
    }


    class UpdateFCMToken(token: String, onResponse: OnResponseListener) : API() {

        companion object {
            var ID = 5
        }

        init {
            _ID = ID
            _name = javaClass.simpleName
            _onResponse = onResponse
            _method = Method.POST
            _url = "$BASE_API/token/update"
            _params["token"] = token
        }
    }


    class ReportError(
        eTime: String,
        eClass: String,
        eMethod: String,
        eMessage: String,
        onResponse: OnResponseListener
    ) : API() {
        companion object {
            var ID = 6
        }

        init {
            _ID = ID
            _name = javaClass.simpleName
            _onResponse = onResponse
            _method = Method.POST
            _url = "$BASE_API/log/error"
//            _url = "https://www.httpbin.org/post"
            _params["time"] = eTime
            _params["class"] = eClass
            _params["method"] = eMethod
            _params["message"] = eMessage
        }
    }


    class GetLicenseKey(market: String, onResponse: OnResponseListener) : API() {
        companion object {
            var ID = 7
        }

        init {
            _ID = ID
            _name = javaClass.simpleName
            _onResponse = onResponse
            _method = Method.POST
            _url = "$BASE_API/billing/get-key"
            _params["market"] = market
        }

    }

    class GetSKU(onResponse: OnResponseListener) : API() {
        companion object {
            var ID = 8
        }

        init {
            _ID = ID
            _name = javaClass.simpleName
            _onResponse = onResponse
            _method = Method.GET
            _url = "$BASE_API/billing/get-sku"
        }

    }


    class GetPayload(onResponse: OnResponseListener) : API() {
        companion object {
            var ID = 9
        }

        init {
            _ID = ID
            _name = javaClass.simpleName
            _onResponse = onResponse
            _method = Method.GET
            _url = "$BASE_API/billing/get-payload"
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
        companion object {
            var ID = 10
        }

        init {
            _ID = ID
            _name = javaClass.simpleName
            _onResponse = onResponse
            _method = Method.POST
            _url = "$BASE_API/billing/verify-token"
            _params["market"] = market
            _params["sku"] = sku
            _params["type"] = type
            _params["token"] = token
            _params["payload"] = payload
            _params["data"] = extraData
        }

    }

}
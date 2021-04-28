package http

open class API : Request() {
    protected val BASE_API = "https://www.xodus.ir/api/v1"


    companion object {
        const val PARAM_NAME_DOWNLOAD_PATH = "KEY_NAME_DOWNLOAD_PATH"
        const val PARAM_NAME_DOWNLOAD_NAME = "KEY_NAME_DOWNLOAD_NAME"
    }

    class Get(url: String) : API() {
        companion object {
            var ID = 1
        }

        init {
            _ID = ID
            _name = javaClass.simpleName
            _method = Method.GET
            _url = url
            _retryMax = 3
        }
    }

    class Post(url: String, vararg params: Pair<String, Any>) : API() {
        companion object {
            var ID = 2
        }

        init {
            _ID = ID
            _name = javaClass.simpleName
            _method = Method.POST
            _url = url
            for (t in params) {
                _params[t.first] = t.second
            }
        }
    }

    class Download(url: String, path: String, name: String) : API() {
        companion object {
            var ID = 3
        }

        init {
            _ID = ID
            _name = javaClass.simpleName
            _method = Method.DOWNLOAD
            _url = url
            _params[PARAM_NAME_DOWNLOAD_PATH] = path
            _params[PARAM_NAME_DOWNLOAD_NAME] = name
        }
    }

    class UpdateFCMToken(token: String) : API() {
        companion object {
            var ID = 4
        }

        init {
            _ID = ID
            _name = javaClass.simpleName
            _method = Method.POST
            _url = "$BASE_API/token/update"
            _params["token"] = token
        }
    }

}
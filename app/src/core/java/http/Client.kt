package http

import android.os.Handler
import androidx.core.content.pm.PackageInfoCompat
import com.xodus.templatethree.BuildConfig
import http.Request.Method.*
import http.Response.Status.FAILURE
import http.Response.Status.SUCCESS
import http.Response.StatusName.*
import main.ApplicationClass
import main.Constant
import okhttp3.*
import okio.BufferedSink
import okio.Okio
import okio.Source
import util.getAndroidID
import util.getPackageInfo
import util.getRandomString
import util.log
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

class Client() {
    companion object {
        @Volatile
        private var instance: Client? = null

        fun getInstance() = instance ?: synchronized(this) {
            instance ?: Client().also { instance = it }
        }
    }

    private val appClass: ApplicationClass = ApplicationClass.getInstance()
    private lateinit var client: OkHttpClient
    private lateinit var requestBuilder: okhttp3.Request.Builder
    private lateinit var requestBody: RequestBody
    private lateinit var callbackString: Callback
    private lateinit var callbackFile: Callback
    private val requestList = HashMap<String, Request>()


    init {
        initClient()
        initCallback()
    }


    fun initClient(): Boolean {
        try {
            client = OkHttpClient.Builder()
                .hostnameVerifier { _, _ -> true }
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            //            var clientBuilder = OkHttpClient.Builder()
            //                .hostnameVerifier { _, _ -> true }
            //                .connectTimeout(30, TimeUnit.SECONDS)
            //                .writeTimeout(30, TimeUnit.SECONDS)
            //                .readTimeout(30, TimeUnit.SECONDS)
            //            clientBuilder = getCertificatePinner()?.let {
            //                clientBuilder.certificatePinner(it)
            //            } ?: run {
            //                clientBuilder
            //            }
            //            clientBuilder = getProxy()?.let {
            //                clientBuilder.proxy(it)
            //            } ?: run {
            //                clientBuilder
            //            }
            //            clientBuilder = getProxyAuthenticator()?.let {
            //                clientBuilder.proxyAuthenticator(it)
            //            } ?: run {
            //                clientBuilder
            //            }
            //            client = clientBuilder.build()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }


    //    private fun getCertificatePinner(): CertificatePinner? {
    //        val certificatePinner = CertificatePinner.Builder()
    //        val keys = appClass.getStringPref(Constant.PREF_SSL_KEYS) ?: ""
    //
    //        if (keys.isEmpty()) {
    //            return null
    //        }
    //
    //        val keyJsonArray = JSONArray(keys)
    //        for (i in 0..keyJsonArray.length()) {
    //            certificatePinner.add("xodus.com", MCryptAES(Constant.CON_AES_SSL_KEY.value, Constant.CON_AES_SSL_IV.value).decrypt(keyJsonArray.getString(i)))
    //        }
    //
    //        return certificatePinner.build()
    //    }
    //
    //    private fun getProxy(): Proxy? {
    //        return if (appClass.getBooleanPref(Constant.PREF_PROXY_ENABLED)) {
    //            //            try {
    //            Proxy(
    //                Proxy.Type.HTTP,
    //                InetSocketAddress(
    //                    appClass.getStringPref(Constant.PREF_PROXY_HOST)!!,
    //                    appClass.getStringPref(Constant.PREF_PROXY_PORT)!!.toInt()
    //                )
    //            )
    //            //            }catch (e : Exception){
    //            //                log(e.toString())
    //            //                null
    //            //            }
    //        } else {
    //            null
    //        }
    //    }
    //
    //    private fun getProxyAuthenticator(): Authenticator? {
    //        return if (appClass.getBooleanPref(Constant.PREF_PROXY_ENABLED) && appClass.getStringPref(Constant.PREF_PROXY_PASSWORD).isNullOrEmpty().not()) {
    //            return Authenticator { route, response ->
    //                val credential = Credentials.basic(
    //                    appClass.getStringPref(Constant.PREF_PROXY_USERNAME)!!,
    //                    appClass.getStringPref(Constant.PREF_PROXY_PASSWORD)!!
    //                )
    //                response.request().newBuilder()
    //                    .header("Proxy-Authorization", credential)
    //                    .build()
    //            }
    //        } else {
    //            null
    //        }
    //    }

    private fun initCallback() {

        callbackString = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(appClass.mainLooper)
                    .post {
                        val response =
                            Response(requestList[call.request().tag()]!!, 0, null, e.message ?: "", FAILURE)
                        requestList.remove(call.request().tag())
                        log(response.toJSONObject().toString())
                        responseHandler(response)
                    }
            }

            override fun onResponse(call: Call, res: okhttp3.Response) {
                try {
                    val result = res.body()!!.string()
                    Handler(appClass.mainLooper)
                        .post {
                            val response = Response(
                                requestList[res.request().tag()]!!,
                                res.code(),
                                res.headers(),
                                result,
                                if (res.isSuccessful) SUCCESS else FAILURE
                            )
                            requestList.remove(res.request().tag())
                            log(response.toJSONObject().toString())
                            responseHandler(response)
                        }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }

        callbackFile = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(appClass.mainLooper)
                    .post {
                        val response = Response(requestList[call.request().tag()]!!, 0, null, e.message ?: "", FAILURE)
                        requestList.remove(call.request().tag())
                        log(response.toString())
                        responseHandler(response)
                    }
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, res: okhttp3.Response) {
                val request = requestList[res.request().tag()]
                requestList.remove(res.request().tag())
                val directory = File(request?._params?.get(API.PARAM_NAME_DOWNLOAD_PATH)?.toString())
                if (!directory.exists()) {
                    directory.mkdirs()
                }
                val file = File(
                    request?._params?.get(API.PARAM_NAME_DOWNLOAD_PATH)?.toString(),
                    request?._params?.get(API.PARAM_NAME_DOWNLOAD_NAME)?.toString()
                )
                if (file.exists()) {
                    file.delete()
                }
                val response = Response(
                    request!!,
                    res.code(),
                    res.headers(),
                    file.path,
                    if (res.isSuccessful) SUCCESS else FAILURE
                )
                log(response.toJSONObject(file).toString())
                val sink = Okio.buffer(Okio.sink(file))
                handleWrites(request, sink, res.body()!!.source(), res.body()!!.contentLength())
                sink.close()
                Handler(appClass.mainLooper)
                    .post {
                        try {
                            responseHandler(response)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
            }
        }
    }

    @Throws(IOException::class)
    private fun handleWrites(request: Request, fileSink: BufferedSink, source: Source, totalSize: Long) {
        var bytesWritten: Long = 0
        var readCount: Long = 0

        var limit: Long = 10000

        while (readCount != -1L) {
            readCount = source.read(fileSink.buffer(), 2048)
            bytesWritten += readCount

            //show every 50kb
            if (bytesWritten > limit) {
                limit += 50000
                var percent = ((bytesWritten * 100) / totalSize).toInt()
                if (percent > 100) {
                    percent = 100
                }
                val finalBytesWritten = bytesWritten
                val finalPercent = percent
                Handler(appClass.mainLooper)
                    .post {
                        try {
                            request._onResponse.onProgress(request, finalBytesWritten, totalSize, finalPercent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

            }
        }

    }

    private fun addMainHeaders(request: Request) {
        if (appClass.getStringPref(Constant.PREF_ACCESS_TOKEN) != null) {
            request.putHeader("Authorization", "Bearer " + appClass.getStringPref(Constant.PREF_ACCESS_TOKEN))
        }
        request.putHeader("Device-Id", getAndroidID())
        request.putHeader(
            "User-Agent",
            (BuildConfig.APPLICATION_ID.substring(BuildConfig.APPLICATION_ID.lastIndexOf('.')).toUpperCase(Locale.ENGLISH)
                    + " "
                    + getPackageInfo().versionName
                    + " (Android; "
                    + PackageInfoCompat.getLongVersionCode(getPackageInfo()) + "; "
                    + appClass.market.name + "; "
                    + "IR; "
                    + getAndroidID()
                    + "; "
                    + appClass.getStringPref(Constant.PREF_LANGUAGE) + ")")
        )
    }


    fun request(request: Request) {
        val url = getUrl(request) ?: return
        addMainHeaders(request)
        requestBuilder = okhttp3.Request.Builder()
        applyHeaders(request)
        applyParams(request)
        val tag = getRandomString(8)
        requestBuilder.tag(tag)
        requestList[tag] = request
        val call: Call
        when (request._method) {
            DOWNLOAD -> {
                call = client.newCall(requestBuilder.url(url).get().build())
                call.enqueue(callbackFile)
            }
            GET      -> {
                call = client.newCall(requestBuilder.url(url).get().build())
                call.enqueue(callbackString)
            }
            POST     -> {
                call = client.newCall(requestBuilder.url(url).post(requestBody).build())
                call.enqueue(callbackString)
            }
            PUT      -> {
                call = client.newCall(requestBuilder.url(url).put(requestBody).build())
                call.enqueue(callbackString)
            }
            DELETE   -> {
                call = client.newCall(requestBuilder.url(url).delete(requestBody).build())
                call.enqueue(callbackString)
            }
            RAW      -> {
                call = client.newCall(requestBuilder.url(url).post(requestBody).build())
                call.enqueue(callbackString)
            }
        }
    }

    private fun getUrl(request: Request): URL? {
        var message = ""
        var url: URL? = null
        try {
            url = URL(request._url)
            val uri = URI(url.protocol, url.userInfo, url.host, url.port, url.path, url.query, url.ref)
            url = uri.toURL()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
            message = e.message!!
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            message = e.message!!
        }

        if (url == null) {
            val response = Response(request, 0, null, message, FAILURE)
            log(response.toJSONObject().toString())
            responseHandler(response)
        }
        return url
    }

    private fun applyHeaders(request: Request) {
        for (key in request._headers.keys) {
            val item = request._headers[key]
            if (item != null) {
                requestBuilder.addHeader(key, item)
            }
        }
    }

    private fun applyParams(request: Request) {
        when (request._method) {
            GET, DOWNLOAD     -> {
            }
            POST, PUT, DELETE -> {
                if (hasFiles(request)) {
                    val body = MultipartBody.Builder()
                    body.setType(MultipartBody.FORM)
                    if (request._params.isNotEmpty()) {
                        for (key in request._params.keys) {
                            val item = request._params[key]
                            if (item != null) {
                                if (item is File) {
                                    val file = item as File?
                                    body.addFormDataPart(
                                        key,
                                        file!!.name,
                                        RequestBody.create(MediaType.parse("multipart/form-data"), file)
                                    )
                                } else {
                                    body.addFormDataPart(key, item.toString())
                                }
                            }
                        }
                        requestBody = body.build()
                    } else {
                        requestBody = RequestBody.create(null, ByteArray(0))
                    }
                } else {
                    val body = FormBody.Builder()
                    for (key in request._params.keys) {
                        val item = request._params[key]
                        if (item != null) {
                            body.add(key, item.toString())
                        }
                    }
                    requestBody = body.build()
                }
            }
            RAW               -> requestBody = if (request._raw.isEmpty()) {
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"), ByteArray(0))
            } else {
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"), request._raw)
            }
        }
    }

    private fun hasFiles(request: Request): Boolean {
        var isFile = false
        for (key in request._params.keys) {
            val item = request._params[key]
            if (item != null) {
                if (item is File) {
                    isFile = true
                }
            }
        }
        return isFile
    }

    fun cancelAllRequests() {
        client.dispatcher().cancelAll()
    }

    private fun responseHandler(response: Response) {
        if (response.status === FAILURE && response.request._retryMax > response.request._retryMax) {
            response.request.addRetryAttempt()
            request(response.request)
            return
        }
        val tabCount = when {
            response.request._name.length < 8  -> "\t\t\t"
            response.request._name.length < 16 -> "\t\t"
            response.request._name.length < 24 -> "\t"
            else                               -> "\t"
        }
        log("CLR BDY ${response.request._name}$tabCount${response.body}")
        log("CLR REQ ${response.request._name}$tabCount${response.request.toJSONObject()}")
        log("CLR RES ${response.request._name}$tabCount${response.toJSONObject()}")
        response.request._onResponse.onResponse(response)
        when (response.statusName) {
            NoInternetConnection //0
            -> {
            }
            OK //200
            -> {
            }
            Created //201
            -> {
            }
            NoContent //204
            -> {
            }
            NotModified //304
            -> {
            }
            BadRequest //400
            -> {
            }
            Unauthorized //401
            -> {
            }
            Forbidden //403
            -> {
            }
            NotFound //404
            -> {
            }
            Conflict //409
            -> {
            }
            InternalServerError //500
            -> {
            }
            Other //Other
            -> {
            }
        }
    }
}

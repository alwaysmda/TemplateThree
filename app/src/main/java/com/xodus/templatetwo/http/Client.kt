package com.xodus.templatetwo.http

import android.content.Context
import android.os.Handler
import androidx.core.content.pm.PackageInfoCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.xodus.templatetwo.BuildConfig
import com.xodus.templatetwo.extention.getAndroidID
import com.xodus.templatetwo.extention.getPackageInfo
import com.xodus.templatetwo.extention.getRandomString
import com.xodus.templatetwo.extention.log
import com.xodus.templatetwo.http.Request.Method.*
import com.xodus.templatetwo.http.Response.Status.FAILURE
import com.xodus.templatetwo.http.Response.Status.SUCCESS
import com.xodus.templatetwo.http.Response.StatusName.*
import com.xodus.templatetwo.main.ApplicationClass
import com.xodus.templatetwo.main.Constant
import okhttp3.*
import okio.BufferedSink
import okio.Okio
import okio.Source
import java.io.File
import java.io.IOException
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.util.*
import java.util.concurrent.TimeUnit

class Client(private val context: Context) {
    private val appClass: ApplicationClass = ApplicationClass().getInstance(context)
    private var client: OkHttpClient? = null
    private lateinit var requestBuilder: okhttp3.Request.Builder
    private lateinit var requestBody: RequestBody
    private lateinit var callbackString: Callback
    private lateinit var callbackFile: Callback
    private val requestList = HashMap<String, Request>()


    init {
        initClient()
        initCallback()
    }


    private fun initClient() {
        client = OkHttpClient.Builder()
            .hostnameVerifier { _, _ -> true }
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun initCallback() {

        callbackString = object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(context.mainLooper)
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
                    Handler(context.mainLooper)
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
                Handler(context.mainLooper)
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
                val directory = File(request?.params?.get(API.PARAM_NAME_DOWNLOAD_PATH)?.toString())
                if (!directory.exists()) {
                    directory.mkdirs()
                }
                val file = File(
                    request?.params?.get(API.PARAM_NAME_DOWNLOAD_PATH)?.toString(),
                    request?.params?.get(API.PARAM_NAME_DOWNLOAD_NAME)?.toString()
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
                Handler(context.mainLooper)
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
                Handler(context.mainLooper)
                    .post {
                        try {
                            request.onResponse.onProgress(request, finalBytesWritten, totalSize, finalPercent)
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
        request.putHeader("Device-Id", getAndroidID(context))
        request.putHeader(
            "User-Agent",
            (BuildConfig.APPLICATION_ID.substring(BuildConfig.APPLICATION_ID.lastIndexOf('.')).toUpperCase()
                    + " "
                    + getPackageInfo(context).versionName
                    + " (Android; "
                    + PackageInfoCompat.getLongVersionCode(getPackageInfo(context)) + "; "
                    + BuildConfig.MARKET + "; "
                    + "IR; "
                    + getAndroidID(context)
                    + "; "
                    + appClass.getStringPref(Constant.PREF_LANGUAGE) + ")")
        )
    }


    fun request(request: Request) {
        val url = getUrl(request) ?: return
        addMainHeaders(request)
        log(request.toJSONObject().toString())
        requestBuilder = okhttp3.Request.Builder()
        applyHeaders(request)
        applyParams(request)
        val tag = getRandomString(8)
        requestBuilder.tag(tag)
        requestList[tag] = request
        val call: Call
        when (request.method) {
            DOWNLOAD -> {
                call = client!!.newCall(requestBuilder.url(url).get().build())
                call.enqueue(callbackFile)
            }
            GET -> {
                call = client!!.newCall(requestBuilder.url(url).get().build())
                call.enqueue(callbackString)
            }
            POST -> {
                call = client!!.newCall(requestBuilder.url(url).post(requestBody).build())
                call.enqueue(callbackString)
            }
            PUT -> {
                call = client!!.newCall(requestBuilder.url(url).put(requestBody).build())
                call.enqueue(callbackString)
            }
            DELETE -> {
                call = client!!.newCall(requestBuilder.url(url).delete(requestBody).build())
                call.enqueue(callbackString)
            }
            RAW -> {
                call = client!!.newCall(requestBuilder.url(url).post(requestBody).build())
                call.enqueue(callbackString)
            }
        }
    }

    private fun getUrl(request: Request): URL? {
        var message = ""
        var url: URL? = null
        try {
            url = URL(request.url)
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
        for (key in request.headers.keys) {
            val item = request.headers[key]
            if (item != null) {
                requestBuilder.addHeader(key, item)
            }
        }
    }

    private fun applyParams(request: Request) {
        val body = MultipartBody.Builder()

        when (request.method) {
            GET, DOWNLOAD -> {
            }
            POST, PUT, DELETE -> {
                body.setType(MultipartBody.FORM)
                if (request.params.isNotEmpty()) {
                    for (key in request.params.keys) {
                        val item = request.params[key]
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
            }
            RAW -> requestBody = if (request.raw.isEmpty()) {
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"), ByteArray(0))
            } else {
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"), request.raw)
            }
        }
    }

    fun cancelAllRequests() {
        client!!.dispatcher().cancelAll()
    }

    private fun responseHandler(response: Response) {
        if (response.status === FAILURE && response.request.retryMax > response.request.retryMax) {
            response.request.addRetryAttempt()
            request(response.request)
            return
        }
        response.request.onResponse.onResponse(response)
        when (response.statusName) {
            NoInternetConnection//0
            -> MaterialDialog.Builder(context)
                .cancelable(false)
                .title("No Connection")
                .content("Please check your internet connection and try again.")
                .positiveText("Retry").onPositive { dialog, _ ->
                    response.request.retryAttempt = 0
                    request(response.request)
                    dialog.dismiss()
                }
                .negativeText("Cancel")
                .onNegative { dialog, _ -> dialog.dismiss() }.show()
            OK//200
            -> {
            }
            Created//201
            -> {
            }
            NoContent//204
            -> {
            }
            NotModified//304
            -> {
            }
            BadRequest//400
            -> {
            }
            Unauthorized//401
            -> {
            }
            Forbidden//403
            -> {
            }
            NotFound//404
            -> {
            }
            Conflict//409
            -> {
            }
            InternalServerError//500
            -> {
            }
            Other//Other
            -> {
            }
        }
    }
}

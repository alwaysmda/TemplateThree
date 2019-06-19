package com.xodus.templatetwo.main

import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.*
import android.content.ClipboardManager
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.media.MediaScannerConnection
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.text.*
import android.text.style.ClickableSpan
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat.setTint
import androidx.core.graphics.drawable.DrawableCompat.wrap
import androidx.core.graphics.drawable.TintAwareDrawable
import androidx.core.view.ViewCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import com.xodus.templatetwo.BuildConfig
import com.xodus.templatetwo.R
import com.xodus.templatetwo.http.*
import org.apache.commons.codec.binary.Hex
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec


fun getLocation(): String {
    val CALL_STACK_INDEX = 1
    // DO NOT switch this to Thread.getCurrentThread().getStackTrace(). The test will pass
    // because Robolectric runs them on the JVM but on Android the elements are different.
    val stackTrace = Throwable().stackTrace
    if (stackTrace.size <= CALL_STACK_INDEX) {
        throw IllegalStateException(
            "Synthetic stacktrace didn't have enough elements: are you using proguard?"
        )
    }
    return ".(" + stackTrace[CALL_STACK_INDEX].toString().substring(
        stackTrace[CALL_STACK_INDEX].toString().lastIndexOf(
            "("
        ) + 1, stackTrace[CALL_STACK_INDEX].toString().lastIndexOf(")")
    ) + ")"
}

fun getLocation(index: Int): String {
    val CALL_STACK_INDEX = 1
    val stackTrace = Throwable().stackTrace
    if (stackTrace.size <= CALL_STACK_INDEX) {
        throw IllegalStateException(
            "Synthetic stacktrace didn't have enough elements: are you using proguard?"
        )
    }
    //        for (int i = 0; i < stackTrace.length; i++) {
    //            Log.e(LOG, "TRACETRACE INDEX=" + i + "|" + stackTrace[i].toString());
    //        }

    return ".(" + stackTrace[index].toString().substring(
        stackTrace[index].toString().lastIndexOf("(") + 1,
        stackTrace[index].toString().lastIndexOf(")")
    ) + ")"
}

fun getStackTrace(): String {
    val stackTrace = Throwable().stackTrace
    var stack = ""
    for (i in stackTrace.indices) {
        stack += "\n.(" + stackTrace[i].toString().substring(
            stackTrace[i].toString().lastIndexOf("(") + 1,
            stackTrace[i].toString().lastIndexOf(")")
        ) + ")"
    }
    return stack
}

fun getRandomString(length: Int): String {
    val AB = "0123456789abcdefghijklmnopqrstuvwxyz"
    val rnd = Random()
    val sb = StringBuilder(length)
    for (i in 0 until length)
        sb.append(AB[rnd.nextInt(AB.length)])
    return sb.toString()
}

fun getRandomInt(length: Int): Int {
    val AB = "0123456789"
    val rnd = Random()
    val sb = StringBuilder(length)
    for (i in 0 until length)
        sb.append(AB[rnd.nextInt(AB.length)])
    return Integer.valueOf(sb.toString())
}


fun Context.translateToPersian(c: Int): String {
    return translateToPersian(c.toString())
}

fun translateToEnglish(c: Int): String {
    return translateToEnglish(c.toString())
}

fun Context.translateToPersian(_c: String): String {
    var c = _c
    val enN = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    val faN = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")


    if (ApplicationClass().getInstance(this).getStringPref(Constant.PREF_LANGUAGE).equals("fa")) {
        for (i in 0..9) {
            c = c.replace(enN[i], faN[i])
        }
    } else {
        for (i in 0..9) {
            c = c.replace(faN[i], enN[i])
        }
    }
    return c
}

fun translateToEnglish(_c: String): String {
    var c = _c
    val faN = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
    val enN = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    for (i in 0..9) {
        c = c.replace(faN[i], enN[i])
    }
    return c
}


fun Context.getShape(
    orientation: GradientDrawable.Orientation,
    firstColor: Int,
    secondColor: Int,
    borderColor: Int,
    borderWidth: Int,
    topLeftRadiusX: Int,
    topLeftRadiusY: Int,
    topRightRadiusX: Int,
    topRightRadiusY: Int,
    downRightRadiusX: Int,
    downRightRadiusY: Int,
    downLeftRadiusX: Int,
    downLeftRadiusY: Int
): GradientDrawable {
    val shape = GradientDrawable(orientation, intArrayOf(firstColor, secondColor))
    shape.shape = GradientDrawable.RECTANGLE
    shape.cornerRadii = floatArrayOf(
        convertDPtoPX(topLeftRadiusX.toFloat()),
        convertDPtoPX(topLeftRadiusY.toFloat()),
        convertDPtoPX(topRightRadiusX.toFloat()),
        convertDPtoPX(topRightRadiusY.toFloat()),
        convertDPtoPX(downRightRadiusX.toFloat()),
        convertDPtoPX(downRightRadiusY.toFloat()),
        convertDPtoPX(downLeftRadiusX.toFloat()),
        convertDPtoPX(downLeftRadiusY.toFloat())
    )
    shape.setStroke(borderWidth, borderColor)
    return shape
}

fun Context.getShape(
    orientation: GradientDrawable.Orientation,
    firstColor: Int,
    secondColor: Int,
    borderColor: Int,
    borderWidth: Int,
    topLeftRadius: Int,
    topRightRadius: Int,
    downRightRadius: Int,
    downLeftRadius: Int
): GradientDrawable {
    return getShape(
        orientation,
        firstColor,
        secondColor,
        borderColor,
        borderWidth,
        topLeftRadius,
        topLeftRadius,
        topRightRadius,
        topRightRadius,
        downRightRadius,
        downRightRadius,
        downLeftRadius,
        downLeftRadius
    )
}

fun Context.getShape(
    orientation: GradientDrawable.Orientation,
    firstColor: Int,
    secondColor: Int,
    borderColor: Int,
    borderWidth: Int,
    radius: Int
): GradientDrawable {
    return getShape(orientation, firstColor, secondColor, borderColor, borderWidth, radius, radius, radius, radius)
}

fun Context.getShape(
    orientation: GradientDrawable.Orientation,
    firstColor: Int,
    secondColor: Int,
    radius: Int
): GradientDrawable {
    return getShape(
        orientation,
        firstColor,
        secondColor,
        R.color.md_transparent_1000,
        0,
        radius,
        radius,
        radius,
        radius
    )
}

fun Context.getShape(
    backgroundColor: Int,
    borderColor: Int,
    borderWidth: Int,
    radius: Int
): GradientDrawable {
    return getShape(
        GradientDrawable.Orientation.RIGHT_LEFT,
        backgroundColor,
        backgroundColor,
        borderColor,
        borderWidth,
        radius,
        radius,
        radius,
        radius
    )
}

fun Context.getShape(
    backgroundColor: Int,
    topLeftRadius: Int,
    topRightRadius: Int,
    downRightRadius: Int,
    downLeftRadius: Int
): GradientDrawable {
    return getShape(
        GradientDrawable.Orientation.RIGHT_LEFT,
        backgroundColor,
        backgroundColor,
        R.color.md_transparent_1000,
        0,
        topLeftRadius,
        topRightRadius,
        downRightRadius,
        downLeftRadius
    )
}

fun Context.getShape(
    backgroundColor: Int,
    radius: Int
): GradientDrawable {
    return getShape(
        GradientDrawable.Orientation.RIGHT_LEFT,
        backgroundColor,
        backgroundColor,
        R.color.md_transparent_1000,
        0,
        radius,
        radius,
        radius,
        radius
    )
}

fun Context.convertImageToBase64(imageResource: Int): String {
    val bm = BitmapFactory.decodeResource(resources, imageResource)
    val baos = ByteArrayOutputStream()
    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos) //bm is the bitmap object
    val b = baos.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
}

fun Context.convertBase64ToBitmapDrawable(encodedBase64String: String): BitmapDrawable {
    val decodedString = Base64.decode(encodedBase64String, Base64.DEFAULT)
    val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    return BitmapDrawable(resources, decodedByte)
}

fun reduceBitmapQuality(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int): Bitmap {
    val bytearrayoutputstream = ByteArrayOutputStream()
    bitmap.compress(format, quality, bytearrayoutputstream)
    val BYTE = bytearrayoutputstream.toByteArray()
    return BitmapFactory.decodeByteArray(BYTE, 0, BYTE.size)
}

fun reduceBitmapSize(_bitmap: Bitmap, format: Bitmap.CompressFormat, maxSize: Int): Bitmap {
    var bitmap = _bitmap
    val bytearrayoutputstream = ByteArrayOutputStream()
    bitmap.compress(format, 90, bytearrayoutputstream)
    val BYTE = bytearrayoutputstream.toByteArray()
    bitmap = BitmapFactory.decodeByteArray(BYTE, 0, BYTE.size)
    return if (bitmap.byteCount > maxSize) {
        reduceBitmapSize(bitmap, format, maxSize)
    } else {
        bitmap
    }
}

fun reduceBitmapResolution(image: Bitmap, maxSize: Int): Bitmap {
    var width = image.width
    var height = image.height
    val bitmapRatio = width.toFloat() / height.toFloat()
    if (bitmapRatio > 1) {
        width = maxSize
        height = (width / bitmapRatio).toInt()
    } else {
        height = maxSize
        width = (height * bitmapRatio).toInt()
    }
    return Bitmap.createScaledBitmap(image, width, height, true)
}

fun resizeImage(image: Bitmap, width: Int, height: Int): Bitmap {
    return Bitmap.createScaledBitmap(image, width, height, true)
}

fun pathToBitmap(path: String): Bitmap? {
    val imgFile = File(path)
    return if (imgFile.exists()) {
        BitmapFactory.decodeFile(imgFile.absolutePath)
    } else {
        null
    }
}

fun Context.convertBitmapToDrawable(bitmap: Bitmap): Drawable {
    return BitmapDrawable(resources, bitmap)
}

fun Context.convertDrawableToBitmap(resourceId: Int): Bitmap {
    return BitmapFactory.decodeResource(resources, resourceId)
}


fun convertDrawableToBitmap(drawable: Drawable): Bitmap {
    val bitmap: Bitmap?
    if (drawable is BitmapDrawable) {
        if (drawable.bitmap != null) {
            return drawable.bitmap
        }
    }

    if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
        bitmap =
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
    } else {
        bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    }

    val canvas = Canvas(bitmap!!)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}


fun convertBitmapToFile(bitmap: Bitmap, path: String, fileName: String): File? {
    val filePath = File(path)
    // Make sure the path directory exists.
    if (!filePath.exists()) {
        // Make it, if it doesn't exit
        Log.e(Constant.TAG.toString(), "Create Directory=" + filePath.mkdirs())
    }
    val file = File(path, fileName)
    //Convert bitmap to byte array
    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
    val bitmapdata = bos.toByteArray()

    //write the bytes in file
    try {
        val fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
        fos.close()
        return file
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }

}

fun convertFileToBitmap(file: File): Bitmap {
    return BitmapFactory.decodeFile(file.path)
}

fun Context.getAndroidID(): String {
    return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
}

fun getUUID(): String {
    return UUID.randomUUID().toString()
}

fun getUUID(name: String): String {
    return UUID.fromString(name).toString()
}


fun Context.getPackageInfo(): PackageInfo {
    return packageManager.getPackageInfo(packageName, 0)
}

fun getMCryptAESKey(password: String): String? {
    var keyString: String? = null
    try {
        var key = password.toByteArray(Charsets.UTF_8)
        val sha = MessageDigest.getInstance("SHA-1")
        key = sha.digest(key)
        key = Arrays.copyOf(key, 16) // use only first 128 bit
        keyString = String(key, Charsets.UTF_8)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return keyString
}

fun moveFile(inputPath: String, inputFile: String, outputPath: String): Boolean {
    val inputStream: InputStream?
    val outputStream: OutputStream?
    try {
        //create output directory if it doesn't exist
        val dir = File(outputPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        inputStream = FileInputStream(inputPath + inputFile)
        outputStream = FileOutputStream(outputPath + inputFile)
        val buffer = ByteArray(1024)
        var read: Int
        read = inputStream.read(buffer)
        while (read != -1) {
            outputStream.write(buffer, 0, read)
            read = inputStream.read(buffer)
        }
        inputStream.close()
        // write the output file
        outputStream.flush()
        outputStream.close()
        // delete the original file
        File(inputPath + inputFile).delete()
    } catch (e: Exception) {
        Log.e("tag", e.message)
        return false
    }

    return true
}

fun deleteFile(inputPath: String, inputFile: String): Boolean {
    try {
        // delete the original file
        return File(inputPath + inputFile).delete()
    } catch (e: Exception) {
        Log.e("tag", e.message)
        return false
    }

}

fun deleteFolder(path: String) {
    val file = File(path)
    if (file.isDirectory)
        for (child in file.listFiles()!!)
            deleteFolder(child.path)
    file.delete()
}

fun copyFile(inputPath: String, inputFile: String, outputPath: String): Boolean {
    var inputStream: InputStream?
    var outputStream: OutputStream?
    try {
        //create output directory if it doesn't exist
        val dir = File(outputPath)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        inputStream = FileInputStream(inputPath + inputFile)
        outputStream = FileOutputStream(outputPath + inputFile)
        val buffer = ByteArray(1024)
        var read: Int
        read = inputStream.read(buffer)
        while (read != -1) {
            outputStream.write(buffer, 0, read)
            read = inputStream.read(buffer)
        }
        inputStream.close()
        // write the output file (You have now copied the file)
        outputStream.flush()
        outputStream.close()
    } catch (e: FileNotFoundException) {
        Log.e("tag", e.message)
        return false
    } catch (e: Exception) {
        Log.e("tag", e.message)
        return false
    }

    return true
}

fun copyFile(inputPath: String, outputPath: String): Boolean {
    var inputStream: InputStream?
    var outputStream: OutputStream?
    try {
        //create output directory if it doesn't exist
        val dir = File(outputPath.substring(0, outputPath.lastIndexOf("/")))
        if (!dir.exists()) {
            dir.mkdirs()
        }
        inputStream = FileInputStream(inputPath)
        outputStream = FileOutputStream(outputPath)
        val buffer = ByteArray(1024)
        var read: Int
        read = inputStream.read(buffer)
        while (read != -1) {
            outputStream.write(buffer, 0, read)
            read = inputStream.read(buffer)
        }
        inputStream.close()
        // write the output file (You have now copied the file)
        outputStream.flush()
        outputStream.close()
    } catch (fnfe1: FileNotFoundException) {
        Log.e("tag", fnfe1.message)
        return false
    } catch (e: Exception) {
        Log.e("tag", e.message)
        return false
    }

    return true
}

fun renameFile(path: String, fileName: String, newName: String): Boolean {
    val oldPath = "$path/$fileName"
    val newPath = "$path/$newName"
    val file = File(oldPath)
    val newFile = File(newPath)
    return file.renameTo(newFile)
}

fun createFileFromString(data: String?, inputPath: String, fileName: String): Boolean {
    data?.let {
        // Get the directory for the user's public pictures directory.
        val path = File(inputPath)
        // Make sure the path directory exists.
        if (!path.exists()) {
            // Make it, if it doesn't exit
            path.mkdirs()
        }
        val file = File(path, fileName)
        // Save your stream, don't forget to flush() it before closing it.
        try {
            file.createNewFile()
            val fOut = FileOutputStream(file)
            val myOutWriter = OutputStreamWriter(fOut)
            myOutWriter.append(it)
            myOutWriter.close()
            fOut.flush()
            fOut.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
            return false
        }

        Log.e(Constant.TAG.toString(), "History File Created")
        return true
    }.run {
        Log.e(Constant.TAG.toString(), "No Data")
        return false
    }
}

fun getScreenWidth(): Int {
    return Resources.getSystem().displayMetrics.widthPixels
}

fun getScreenHeight(): Int {
    return Resources.getSystem().displayMetrics.heightPixels
}

private fun getSoftButtonsBarHeight(activity: Activity): Int {
    // getRealMetrics is only available with API 17 and +
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val usableHeight = metrics.heightPixels
        activity.windowManager.defaultDisplay.getRealMetrics(metrics)
        val realHeight = metrics.heightPixels
        return if (realHeight > usableHeight)
            realHeight - usableHeight
        else
            0
    }
    return 0
}

fun Context.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

fun Context.convertDPtoPX(dp: Float): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics)
}

fun Context.convertPXtoDP(px: Float): Float {
    return px / (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.convertSPtoPX(sp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)
        .toInt()
}

fun Context.convertDPtoSP(dp: Float): Int {
    return (convertDPtoPX(dp) / resources.displayMetrics.scaledDensity).toInt()
}

fun Context.getScreenDensity(): Float {
    return resources.displayMetrics.density
}

fun getColor(view: ImageView, x: Int, y: Int): Int {
    return (view.drawable as BitmapDrawable).bitmap.getPixel(x, y)
}

fun HMACSHA256(key: String, value: String): String? {
    var result: String? = null
    try {
        val keyBytes = key.toByteArray()
        val signingKey = SecretKeySpec(keyBytes, "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(signingKey)
        val rawHmac = mac.doFinal(value.toByteArray())
        val hexBytes = Hex().encode(rawHmac)
        result = String(hexBytes, Charsets.UTF_8)
    } catch (e: Exception) {
        log(e.message)
    }
    return result
}


fun unpackZip(path: String, zipname: String): Boolean {
    val inputStream: InputStream
    val zis: ZipInputStream
    try {
        var filename: String
        inputStream = FileInputStream(path + zipname)
        zis = ZipInputStream(BufferedInputStream(inputStream))
        var ze: ZipEntry
        val buffer = ByteArray(1024)
        var count: Int

        ze = zis.nextEntry
        while (ze != null) {
            // zapis do souboru
            filename = ze.name

            // Need to create directories if not exists, or
            // it will generate an Exception...
            if (ze.isDirectory) {
                val fmd = File(path + filename)
                fmd.mkdirs()
                continue
            }

            val fout = FileOutputStream(path + filename)

            count = zis.read(buffer)
            while (count != -1) {
                fout.write(buffer, 0, count)
                count = zis.read(buffer)
            }

            fout.close()
            zis.closeEntry()
            ze = zis.nextEntry
        }

        zis.close()
    } catch (e: IOException) {
        e.printStackTrace()
        return false
    }

    return true
}

fun unzip(inputPath: String, fileName: String, outputPath: String): Boolean {
    val zipFile = File(inputPath + fileName)
    val targetDirectory = File(outputPath)
    var ze: ZipEntry
    try {
        val zis = ZipInputStream(
            BufferedInputStream(FileInputStream(zipFile))
        )


        var count: Int
        val buffer = ByteArray(8192)
        ze = zis.nextEntry
        while (ze != null) {
            val file = File(targetDirectory, ze.name)
            val dir = if (ze.isDirectory) file else file.parentFile
            if (!dir.isDirectory && !dir.mkdirs())
                throw FileNotFoundException("Failed to ensure directory: " + dir.absolutePath)
            if (ze.isDirectory)
                continue
            val fout = FileOutputStream(file)
            count = zis.read(buffer)
            while (count != -1) {
                fout.write(buffer, 0, count)
                count = zis.read(buffer)
            }
            fout.close()

            /* if time should be restored as well
        long time = ze.getTime();
        if (time > 0)
            file.setLastModified(time);
        */
            ze = zis.nextEntry
        }
        zis.close()
    } catch (e: Exception) {
        return false
    }

    return true
}

fun convertStreamToString(inputStream: InputStream): String {
    var sb = StringBuilder()
    try {
        val reader = BufferedReader(InputStreamReader(inputStream))
        sb = StringBuilder()
        var line: String
        line = reader.readLine()
        while (line != null) {
            sb.append(line)
            line = reader.readLine()
        }
        reader.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return sb.toString()
}

fun getStringFromFile(file: File): String? {
    val ret: String
    try {
        if (file.exists()) {
            val fin = FileInputStream(file)
            ret = convertStreamToString(fin)
            //Make sure you close all streams.
            fin.close()
        } else {
            return null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

    return ret
}

fun getStringFromFile(filePath: String): String? {
    return getStringFromFile(File(filePath))
}


fun getPrivateKeyFromRSA(filepath: String): PrivateKey? {
    var key: PrivateKey? = null
    try {
        val file = File(filepath)
        val size = file.length().toInt()
        val bytes = ByteArray(size)
        val spec = PKCS8EncodedKeySpec(bytes)
        val kf = KeyFactory.getInstance("RSA")
        key = kf.generatePrivate(spec)
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return key
}

//    public static File getDirectory(String variableName, String... paths) {
fun getExternalSDCardDirectory(): File? {
    val path = System.getenv("SECONDARY_STORAGE")
    path?.let { p ->
        if (!TextUtils.isEmpty(p)) {
            if (p.contains(":")) {
                for (_path in p.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    val file = File(_path)
                    if (file.exists()) {
                        return file
                    }
                }
            } else {
                val file = File(p)
                if (file.exists()) {
                    return file
                }
            }
        }
    }
    return null
}

fun getInternalDirectory(): File {
    return Environment.getExternalStorageDirectory()
}

fun getDataDirectory(): File {
    return File(Environment.getExternalStorageDirectory().path + "/Android/data/" + BuildConfig.APPLICATION_ID)
}

fun hideSoftKeyboard(activity: Activity) {
    try {
        val inputMethodManager = activity.getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            activity.currentFocus!!.windowToken, 0
        )
    } catch (e: Exception) {

    }

}

fun Context.showSoftKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Context.copyToClipboard(text: String) {
    val clipboard = getSystemService(Application.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("copy", text)
    clipboard.primaryClip = clip
}

fun shortenNumber(number: Double, decimalCount: Int): String {
    val format = "%." + decimalCount + "f"
    return if (number < 1000) {
        String.format(format, number)
    } else if (number < 1000000) {
        String.format(format, number / 1000) + "K"
    } else if (number < 1000000000) {
        String.format(format, number / 1000000) + "M"
    } else {
        String.format(format, number / 1000000000) + "B"
    }
}

fun shortenNumber(number: Double): String {
    return shortenNumber(number, 1)
}

fun separateNumberBy3(number: Long): String {
    val formatter = DecimalFormat("#,###,###")
    return formatter.format(number)
}

fun Context.scanMedia(path: String) {
    Log.e(Constant.TAG.toString(), "SCANNING=" + Uri.fromFile(File(path)))
    MediaScannerConnection.scanFile(
        this,
        arrayOf(path),
        null
    ) { p, uri -> Log.e(Constant.TAG.toString(), "SCAN COMPLETE|PATH=$p|URI=$uri") }
}

fun <T : Any> extractModel(obj: T, output: String): String {
    var result = output
    try {
        val fields = obj.javaClass.declaredFields
        for (field in fields) {
            field.isAccessible = true
            val type = field.get(obj)
            if (type is Int || type is String || type is Long || type is Double || type is Float
                || type is Boolean
            ) {
                result += field.name + " : " + field.get(obj) + "\n"
            } else {
                extractModel(field.get(obj), result)
            }
            field.isAccessible = false
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return result
}

fun getStringLog(vararg s: Any?): String {
    val value = StringBuilder()
    for (item in s) {
        value.append(item.toString()).append("\n")
    }
    return value.toString()
}

fun log(vararg s: Any?) {
    Log.e(BuildConfig.APPLICATION_ID.toUpperCase() + getLocation(3), getStringLog(*s))
}

fun Context.logToFile(force: Boolean, fileName: String, vararg s: Any?) {
    if (force || ApplicationClass().getInstance(this).getBooleanPref(Constant.PREF_LOG)) {
        try {
            Thread(Runnable {
                val file = File(
                    Environment.getExternalStorageDirectory().path + "/Android/data/" + BuildConfig.APPLICATION_ID,
                    "$fileName.txt"
                )
                var a = getStringFromFile(file)
                val string = getStringLog(*s)
                if (a == null) {
                    a = ""
                } else {
                    if (a.length > 1000000) {
                        a = a.substring(string.length)
                    }
                }
                a += "\n\n\n" + getStackTrace() + "\n" + string
                createFileFromString(
                    a,
                    Environment.getExternalStorageDirectory().path + "/Android/data/" + BuildConfig.APPLICATION_ID,
                    "$fileName.txt"
                )
            }).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * Symbol  Meaning                Kind         Example
 * D       day in year             Number        189
 * E       day of week             Text          E/EE/EEE:Tue, EEEE:Tuesday, EEEEE:T
 * F       day of week in month    Number        2 (2nd Wed in July)
 * G       era designator          Text          AD
 * H       hour in day (0-23)      Number        0
 * K       hour in am/pm (0-11)    Number        0
 * L       stand-alone month       Text          L:1 LL:01 LLL:Jan LLLL:January LLLLL:J
 * M       month in year           Text          M:1 MM:01 MMM:Jan MMMM:January MMMMM:J
 * S       fractional seconds      Number        978
 * W       week in month           Number        2
 * Z       time zone (RFC 822)     Time Zone     Z/ZZ/ZZZ:-0800 ZZZZ:GMT-08:00 ZZZZZ:-08:00
 * a       am/pm marker            Text          PM
 * c       stand-alone day of week Text          c/cc/ccc:Tue, cccc:Tuesday, ccccc:T
 * d       day in month            Number        10
 * h       hour in am/pm (1-12)    Number        12
 * k       hour in day (1-24)      Number        24
 * m       minute in hour          Number        30
 * s       second in minute        Number        55
 * w       week in year            Number        27
 * G       era designator          Text          AD
 * y       year                    Number        yy:10 y/yyy/yyyy:2010
 * z       time zone               Time Zone     z/zz/zzz:PST zzzz:Pacific Standard
 */
fun convertTimestampToDate(timestamp: Long, dateFormat: String): String {
    // Create a DateFormatter object for displaying date in specified format.
    val formatter = SimpleDateFormat(dateFormat)

    // Create a calendar object that will convert the date and time value in milliseconds to date.
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    return formatter.format(calendar.time)
}


fun convertTimestampToDate(timestamp: Long): String {
    return convertTimestampToDate(timestamp, "yyyy/MM/dd hh:mm")
}

fun convertDateToTimestamp(date: String, dateFormat: String): Long {
    val sdf = SimpleDateFormat(dateFormat)
    return try {
        sdf.parse(date).time
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }

}

fun convertTimestampToCalendar(timestamp: Long): Calendar {
    val d = Date(timestamp)
    val c = Calendar.getInstance()
    c.time = d
    return c
}


fun convertBundleToString(bundle: Bundle?): String {
   return bundle?.let {
        var content = "Bundle :\n{\n"
        for (key in it.keySet()) {
            content += "\"" + key + "\":\"" + it.get(key) + "\",\n"
        }
       content.substring(0, content.length - 2) + "\n}"
    }.run {
       "{}"
    }
}

fun convertBundleToJson(bundle: Bundle?): JSONObject {
    val jsonObject = JSONObject()
    try {
        if (bundle != null) {
            for (key in bundle.keySet()) {
                jsonObject.put(key, bundle.get(key))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return jsonObject
}

fun isGooglePlayServicesAvailable(activity: Activity, showDialog: Boolean): Boolean {
    val googleApiAvailability = GoogleApiAvailability.getInstance()
    val status = googleApiAvailability.isGooglePlayServicesAvailable(activity)
    if (status != ConnectionResult.SUCCESS) {
        if (googleApiAvailability.isUserResolvableError(status) && showDialog) {
            googleApiAvailability.getErrorDialog(activity, status, 2404).show()
        }
        return false
    }
    return true
}

fun <T> convertJSONArrayToList(jsonArray: JSONArray): List<T> {
    val list = ArrayList<T>()
    try {
        for (i in 0 until jsonArray.length()) {
            list.add(jsonArray.get(i) as T)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return list
}

fun <T> convertListToJSONArray(list: List<T>): JSONArray {
    val jsonArray = JSONArray()
    try {
        for (i in list.indices) {
            jsonArray.put(list[i])
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return jsonArray
}

fun setTextViewGradient(textView: TextView, firstColor: Int, secondColor: Int, center: Boolean) {
    val shader: Shader
    shader = if (center) {
        RadialGradient(
            (textView.width / 2).toFloat(), (textView.height / 2).toFloat(), (textView.width / 2).toFloat(),
            firstColor, secondColor, Shader.TileMode.MIRROR
        )
    } else {
        RadialGradient(
            textView.x, textView.y, textView.width.toFloat(),
            firstColor, secondColor, Shader.TileMode.MIRROR
        )
    }
    textView.paint.shader = shader
}

fun getDominantColor(bitmap: Bitmap): Int {
    val newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true)
    val color = newBitmap.getPixel(0, 0)
    newBitmap.recycle()
    return color
}

fun Context.getDominantColor(resourceId: Int): Int {
    return getDominantColor(Bitmap.createScaledBitmap(convertDrawableToBitmap(resourceId), 1, 1, true))
}

fun Context.convertUriToPath(contentUri: Uri): String {
    var cursor: Cursor? = null
    try {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        cursor = contentResolver.query(contentUri, proj, null, null, null)
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    } finally {
        cursor?.close()
    }
}


fun setEditTextCursorColor(view: EditText, @ColorInt color: Int) {
    try {
        // Get the cursor resource id
        var field = TextView::class.java.getDeclaredField("mCursorDrawableRes")
        field.isAccessible = true
        val drawableResId = field.getInt(view)

        // Get the editor
        field = TextView::class.java.getDeclaredField("mEditor")
        field.isAccessible = true
        val editor = field.get(view)

        // Get the drawable and set a color filter
        val drawable: Drawable? = ContextCompat.getDrawable(view.context, drawableResId)
        drawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        val drawables = drawable?.let { arrayOf(it, it) }

        // Set the drawables
        field = editor.javaClass.getDeclaredField("mCursorDrawable")
        field.isAccessible = true
        field.set(editor, drawables)
    } catch (ignored: Exception) {
    }

}


fun setEditTextCursor(view: EditText, @DrawableRes drawable: Int) {
    try {
        val fEditor = TextView::class.java.getDeclaredField("mEditor")
        fEditor.isAccessible = true
        val editor = fEditor.get(view)

        val fSelectHandleLeft = editor.javaClass.getDeclaredField("mSelectHandleLeft")
        val fSelectHandleRight = editor.javaClass.getDeclaredField("mSelectHandleRight")
        val fSelectHandleCenter = editor.javaClass.getDeclaredField("mSelectHandleCenter")

        fSelectHandleLeft.isAccessible = true
        fSelectHandleRight.isAccessible = true
        fSelectHandleCenter.isAccessible = true

        fSelectHandleLeft.set(editor, drawable)
        fSelectHandleRight.set(editor, drawable)
        fSelectHandleCenter.set(editor, drawable)
    } catch (ignored: Exception) {
    }

}


fun setStatusbarColor(activity: Activity, color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = color
    }
}

/**
 * @param factor must be less than 1. less is darker.
 */
fun darkenColor(color: Int, factor: Float): Int {
    val a = Color.alpha(color)
    val r = Math.round(Color.red(color) * factor)
    val g = Math.round(Color.green(color) * factor)
    val b = Math.round(Color.blue(color) * factor)
    return Color.argb(
        a,
        Math.min(r, 255),
        Math.min(g, 255),
        Math.min(b, 255)
    )
}

/**
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
 */
fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun mergeJSONObjects(vararg jsonObject: JSONObject): JSONObject {
    if (jsonObject.size < 2) {
        return jsonObject[0]
    } else {
        var result = JSONObject()
        for (aJsonObject in jsonObject) {
            result = mergeJSONObjects(result, aJsonObject)
        }
        return result
    }
}

fun mergeJSONObjects(jsonObject1: JSONObject, jsonObject2: JSONObject): JSONObject {
    val merged = JSONObject()
    try {
        val objs = arrayOf(jsonObject1, jsonObject2)
        for (obj in objs) {
            val it = obj.keys()
            while (it.hasNext()) {
                val key = it.next() as String
                merged.put(key, obj.get(key))
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return merged
}

fun Context.isAppAvailable(appName: String): Boolean {
    val pm = packageManager
    try {
        pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES)
        return true
    } catch (e: PackageManager.NameNotFoundException) {
        return false
    }

}

fun convertCelsiusToFahrenheit(temperatureInCelsius: Int): Int {
    return (temperatureInCelsius * 1.8f).toInt() + 32
}

fun convertFahrenheitToCelsius(temperatureInFahrenheit: Int): Int {
    return ((temperatureInFahrenheit - 32) / 1.8f).toInt()
}


interface OnTextViewAnimationListener {
    fun onAnimationFinished()
}


fun animateTextViewText(textView: TextView, text: String) {
    animateTextViewText(textView, text, 60, null)
}

fun animateTextViewText(textView: TextView, text: String, delay: Int) {
    animateTextViewText(textView, text, delay, null)
}

fun animateTextViewText(
    textView: TextView,
    text: String,
    delay: Int,
    animationListener: OnTextViewAnimationListener?
) {

    for (i in 0 until text.length) {
        Handler().postDelayed({ textView.text = text.substring(0, i + 1) }, (i * delay).toLong())
    }
    if (animationListener != null) {
        Handler().postDelayed({ animationListener.onAnimationFinished() }, (text.length * delay).toLong())
    }
}


fun animateEditTextHint(editText: EditText, text: String) {
    animateEditTextHint(editText, text, 60)
}

fun animateEditTextHint(editText: EditText, text: String, delay: Int) {
    for (i in 0 until text.length) {
        Handler().postDelayed({ editText.hint = text.substring(0, i + 1) }, (i * delay).toLong())
    }
}

fun Activity.shareImage(image: File?) {
    if (image == null || !image.exists()) {
        log("GlobalClass: shareImage: Error: image is not valid")
        return
    }
    val share = Intent(Intent.ACTION_SEND)
    share.type = "image/jpeg"
    share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(image))
    startActivity(Intent.createChooser(share, "Share To"))
}

fun Activity.shareImage(url: String?) {
    if (url == null) {
        log("GlobalClass: shareImage: Error: url is null")
        return
    }
    val client = Client(this)
    client.request(API.Download(object : OnResponseListener {
        override fun onResponse(response: Response) {
            if (response.statusName === Response.StatusName.OK) {
                shareImage(File(response.body))
            } else {
                log("GlobalClass: shareImage: Error: download image failed: code: " + response.statusCode + " url: " + response.request._url)
            }
        }

        override fun onProgress(request: Request, bytesWritten: Long, totalSize: Long, percent: Int) {

        }
    }, url, getDataDirectory().path, "temp.jpg"))
}

fun validateJSON(jsonString: String): Boolean {
    var isObject: Boolean
    var isArray: Boolean
    var jsonObject: JSONObject? = null
    var jsonArray: JSONArray? = null
    try {
        jsonObject = JSONObject(jsonString)
        isObject = true
    } catch (e: JSONException) {
        e.printStackTrace()
        isObject = false
    }

    try {
        jsonArray = JSONArray(jsonString)
        isArray = true
    } catch (e: JSONException) {
        e.printStackTrace()
        isArray = false
    }

    return if (isObject && jsonString.trim { it <= ' ' }.replace("\\", "").replace("/", "").replace(
            " ",
            ""
        ).replace("\n", "").length == jsonObject!!.toString().replace("\\", "").replace(" ", "").replace(
            "\n",
            ""
        ).replace("/", "").length
    ) {
        true
    } else isArray && jsonString.trim { it <= ' ' }.replace("\\", "").replace("/", "").replace(" ", "").replace(
        "\n",
        ""
    ).length == jsonArray!!.toString().replace("\\", "").replace("/", "").length
}

fun convertViewToBitmap(v: View, width: Int, height: Int): Bitmap {
    val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val c = Canvas(b)
    v.layout(0, 0, width, height)
    v.draw(c)
    return b
}

fun openGallery(activity: Activity, requestCode: Int, multiple: Boolean) {
    val intent = Intent()
    intent.type = "image/*"
    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, multiple)
    intent.action = Intent.ACTION_GET_CONTENT
    activity.startActivityForResult(Intent.createChooser(intent, "Select Image"), requestCode)
}

fun getIntentImages(data: Intent): ArrayList<Uri> {
    val uriList = ArrayList<Uri>()
    data.data?.let {
        uriList.add(it)
    }.run {
        data.clipData?.let {
            for (i in 0 until it.itemCount) {
                uriList.add(it.getItemAt(i).uri)
            }
        }
    }
    return uriList
}


//OTHERS START


/**
 * Adds TextWatcher to the EditText
 */
fun EditText.onTextChanged(listener: (String) -> Unit) {
    addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            listener(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}


/**
 * Shows the Snackbar inside an Activity or Fragment
 *
 * @param messageRes Text to be shown inside the Snackbar
 * @param length Duration of the Snackbar
 * @param f Action of the Snackbar
 */
fun View.showSnackbar(@StringRes messageRes: Int, length: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    val snackBar = Snackbar.make(this, resources.getString(messageRes), length)
    snackBar.f()
    snackBar.show()
}

/**
 * Adds action to the Snackbar
 *
 * @param actionRes Action text to be shown inside the Snackbar
 * @param color Color of the action text
 * @param listener Onclick listener for the action
 */

fun Snackbar.action(@StringRes actionRes: Int, color: Int? = null, listener: (View) -> Unit) {
    setAction(actionRes, listener)
    color?.let { setActionTextColor(color) }
}

/**
 * Check if the Internet connectivity is available
 */
fun Context.isInternetAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

/**
 * Loads URL into an ImageView using Picasso
 *
 * @param url URL to be loaded
 */
//fun ImageView.loadFromUrl(url: String) {
//    Picasso.get().load(url).into(this)
//}


//Source: https://gist.github.com/Jeevuz/cdc9a2dd3c9fa3fdddb28bdf3bf2738f
inline fun SharedPreferences.edit(changes: SharedPreferences.Editor.() -> SharedPreferences.Editor) {
    edit().changes().apply()
}

fun ImageView.tintSrc(@ColorRes colorRes: Int) {
    val drawable = wrap(drawable)
    setTint(drawable, ContextCompat.getColor(context, colorRes))
    setImageDrawable(drawable)
    if (drawable is TintAwareDrawable) invalidate() // Because in this case setImageDrawable will not call invalidate()
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

/**
 * Compat version of setExactAndAllowWhileIdle()
 */
fun AlarmManager.setExactAndAllowWhileIdleCompat(alarmType: Int, timeMillis: Long, pendingIntent: PendingIntent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        // This version added Doze
        setExactAndAllowWhileIdle(alarmType, timeMillis, pendingIntent)

    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        // This version changed set() to be inexact
        setExact(alarmType, timeMillis, pendingIntent)

    } else {
        set(alarmType, timeMillis, pendingIntent)
    }
}

/**
 * Helps to set clickable part in text.
 *
 * Don't forget to set android:textColorLink="@color/link" (click selector) and
 * android:textColorHighlight="@color/window_background" (background color while clicks)
 * in the TextView where you will use
 */
fun SpannableString.withClickableSpan(clickablePart: String, onClickListener: () -> Unit): SpannableString {
    val clickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View?) = onClickListener.invoke()
    }
    val clickablePartStart = indexOf(clickablePart)
    setSpan(
        clickableSpan,
        clickablePartStart,
        clickablePartStart + clickablePart.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return this
}

/**
 * Helps to get Map, List, Set or other generic type from Json using Gson.
 */
//inline fun <reified T: Any> Gson.fromJsonToGeneric(json: String): T {
//    val type = object : TypeToken<T>() {}.type
//    return fromJson(json, type)
//}

// The way to pass server errors from custom error response to the Rx onError.
// BaseReply has field error: ApiError and ApiError extends Exception.
//fun <T : BaseReply> Single<T>.apiErrorsToOnError(): Single<T> {
//    // .map the source and
//    return map { reply ->
//        // throw if reply with error or
//        reply.error?.let { throw it }
//        // return if not
//        reply
//    }
//}

// Adds polling to the request. BaseReply is general response type that has error field
//fun <T : BaseReply> Single<T>.poll(delay: Long, errorConsumer: Consumer<Throwable>): Observable<T> = this
//    .repeatWhen { completed -> completed.delay(delay, TimeUnit.SECONDS) }
//    .doOnError { errorConsumer.accept(it) }
//    .retryWhen { errors -> errors.delay(delay, TimeUnit.SECONDS) }
//    .toObservable()

fun View.visible(visible: Boolean, useGone: Boolean = true) {
    visibility = if (visible) View.VISIBLE else if (useGone) View.GONE else View.INVISIBLE
}

// Helps to set status bar color with api version check
fun Activity.setStatusBarColor(@ColorRes colorRes: Int): Unit {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.statusBarColor = ContextCompat.getColor(this, colorRes)
    }
}

// Adds flags to make window fullscreen
fun Activity.setFullscreenLayoutFlags() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}

// Adds window insets to the view while entire activity is fullscreen.
fun View.applyWindowInsets(applyTopInset: Boolean = true, applyOtherInsets: Boolean = true): Unit {
    if (applyTopInset || applyOtherInsets) {
        ViewCompat.setOnApplyWindowInsetsListener(
            this
        ) { view, insets ->
            // Set padding for needed insets
            view.setPadding(
                if (applyOtherInsets) insets.systemWindowInsetLeft else view.paddingLeft,
                if (applyTopInset) insets.systemWindowInsetTop else view.paddingTop,
                if (applyOtherInsets) insets.systemWindowInsetRight else view.paddingRight,
                if (applyOtherInsets) insets.systemWindowInsetBottom else view.paddingBottom
            )

            // Return without consumed insets
            insets.replaceSystemWindowInsets(
                if (applyOtherInsets) 0 else insets.systemWindowInsetLeft,
                if (applyTopInset) 0 else insets.systemWindowInsetTop,
                if (applyOtherInsets) 0 else insets.systemWindowInsetRight,
                if (applyOtherInsets) 0 else insets.systemWindowInsetBottom
            )
        }
    } else {
        // Listener is not needed
        ViewCompat.setOnApplyWindowInsetsListener(this, null)
    }
}

fun String.onlyDigits(): String = replace(Regex("\\D*"), "")

fun View.showKeyboard(show: Boolean) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (show) {
        if (requestFocus()) imm.showSoftInput(this, 0)
    } else {
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}

//OTHERS END

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

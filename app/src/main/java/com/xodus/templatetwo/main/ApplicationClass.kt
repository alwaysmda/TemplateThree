package com.xodus.templatetwo.main

import android.app.AlarmManager
import android.app.Application
import android.app.ApplicationErrorReport
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.pddstudio.preferences.encrypted.EncryptedPreferences
import com.xodus.templatetwo.BuildConfig
import com.xodus.templatetwo.MainActivity
import com.xodus.templatetwo.main.Constant.*;

open class ApplicationClass : Application() {

    private var encryptedPreferences: EncryptedPreferences? = null

    override fun onCreate() {
        super.onCreate()
        encryptedPreferences = EncryptedPreferences.Builder(this).withEncryptionPassword(BuildConfig.APPLICATION_ID).build()
    }

    fun getStringPref(key: Constant): String? {
        var f: String? = encryptedPreferences!!.getString(key.toString(), "")
        if (f == "") {
            f = null
        }
        return f
    }

    fun getBooleanPref(key: Constant): Boolean {
        return encryptedPreferences!!.getBoolean(key.toString(), false)
    }

    fun getIntPref(key: Constant): Int {
        return encryptedPreferences!!.getInt(key.toString(), 0)
    }

    fun setPref(key: Constant, value: String?) {
        var value = value
        if (value == null) {
            value = ""
        }
        encryptedPreferences!!.edit()
            .putString(key.toString(), value)
            .apply()
    }

    fun setPref(key: Constant, value: Boolean) {
        encryptedPreferences!!.edit()
            .putBoolean(key.toString(), value)
            .apply()
    }

    fun setPref(key: Constant, value: Int) {
        encryptedPreferences!!.edit()
            .putInt(key.toString(), value)
            .apply()
    }

    fun setCurrentClass(currentClass: String) {
        setPref(PREF_CurrentClass, currentClass)
    }

    fun setCurrentMethod(currentMethod: String) {
        setPref(PREF_CurrentMethod, currentMethod)
    }


    fun handleUncaughtException(paramThread: Thread, paramThrowable: Throwable) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("crash", true)
        intent.putExtra("message", paramThrowable.toString())
        intent.putExtra("method", getStringPref(PREF_CurrentMethod))
        intent.putExtra("class", getStringPref(PREF_CurrentClass))
        intent.putExtra("time", System.currentTimeMillis())
        intent.addFlags(
            Intent.FLAG_ACTIVITY_CLEAR_TOP
                    or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    or Intent.FLAG_ACTIVITY_NEW_TASK
        )

        val pendingIntent = PendingIntent.getActivity(baseContext, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val mgr = baseContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 500, pendingIntent)
        System.exit(2)
    }

    private var mCrashInfo: ApplicationErrorReport.CrashInfo? = null
    private var mErrorMessage = ""

    private fun reportError(exception: Throwable): String {

        mCrashInfo = ApplicationErrorReport.CrashInfo(exception)

        if (mCrashInfo!!.exceptionMessage == null) {

            mErrorMessage = "<unknown error>"
        } else {

            mErrorMessage = mCrashInfo!!.exceptionMessage
                .replace(": " + mCrashInfo!!.exceptionClassName, "")
        }

        val throwFile = if (mCrashInfo!!.throwFileName == null)
            "<unknown file>"
        else
            mCrashInfo!!.throwFileName

        return ("\n************ " + mCrashInfo!!.exceptionClassName + " ************\n"
                + mErrorMessage + CharCategory.LINE_SEPARATOR
                + "\n File: " + throwFile
                + "\n Method: " + mCrashInfo!!.throwMethodName + "()"
                + "\n Line No.: " + Integer.toString(mCrashInfo!!.throwLineNumber)
                + CharCategory.LINE_SEPARATOR)
        //          + "Class: " + crashInfo.throwClassName + LINE_SEPARATOR
    }

    fun getInstance(context: Context): ApplicationClass {
        return context.applicationContext as ApplicationClass
    }
}
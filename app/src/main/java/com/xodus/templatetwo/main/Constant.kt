package com.xodus.templatetwo.main

import com.xodus.templatetwo.BuildConfig

enum class Constant {

    API_Base("http://www.xodus.ir/templateone/api/"), TAG(BuildConfig.APPLICATION_ID.toUpperCase()),
    API_Main(""),
    API_ErrorReport("error/report"),
    API_UpdateFCMToken("fcm-token"),
    API_DownloadFile("download-file"),
    PREF_NotificationToken("NotificationToken"),
    PREF_AccessToken("AccessToken"),
    PREF_FCMToken("FCMToken"),
    PREF_Language("Language"),
    PREF_Log("LOG"),
    PREF_CurrentClass("CurrentClass"),
    PREF_CurrentMethod("CurrentMethod"),
    PREF_LicenceKey("LicenceKey"),
    PREF_LicenceMarket("LicenceMarket")
    ;

    var value = ""
    var desc = ""
    var error = ""

    private constructor(value: String) {
        this.value = value
    }

    private constructor(value: String, desc: String) {
        this.value = value
        this.desc = desc
    }

    private constructor(value: String, desc: String, error: String) {
        this.value = value
        this.desc = desc
        this.error = error
    }

    override fun toString(): String {
        return this.value
    }

    fun toString(vararg vars: String): String {
        return String.format(this.value!!, *vars)
    }


}

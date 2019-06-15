package com.xodus.templatetwo.main

import com.xodus.templatetwo.BuildConfig

enum class Constant {

    TAG(BuildConfig.APPLICATION_ID.toUpperCase()),
    PREF_NOTIFICATION_TOKEN("NotificationToken"),
    PREF_ACCESS_TOKEN("AccessToken"),
    PREF_FCM_TOKEN("FCMToken"),
    PREF_LANGUAGE("Language"),
    PREF_LOG("LOG"),
    PREF_CURRENT_CLASS("CurrentClass"),
    PREF_CURRENT_METHOD("CurrentMethod"),
    PREF_LICENCE_KEY("LicenceKey"),
    PREF_LICENCE_MARKET("LicenceMarket")
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
        return String.format(this.value, *vars)
    }


}

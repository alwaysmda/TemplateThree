package com.xodus.templatetwo.main

import com.xodus.templatetwo.BuildConfig

enum class Constant {

    TAG(BuildConfig.APPLICATION_ID.toUpperCase()),
    PREF_NOTIFICATION_TOKEN("PREF_NOTIFICATION_TOKEN"),
    PREF_ACCESS_TOKEN("PREF_ACCESS_TOKEN"),
    PREF_FCM_TOKEN("PREF_FCM_TOKEN"),
    PREF_LANGUAGE("PREF_LANGUAGE"),
    PREF_LOG("PREF_LOG"),
    PREF_CURRENT_CLASS("PREF_CURRENT_CLASS"),
    PREF_CURRENT_METHOD("PREF_CURRENT_METHOD"),
    PREF_CRASH_REPEATING("PREF_CRASH_REPEATING"),
    PREF_LICENCE_KEY("PREF_LICENCE_KEY"),
    PREF_LICENCE_MARKET("PREF_LICENCE_MARKET")
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

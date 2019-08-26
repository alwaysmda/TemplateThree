package main

import com.xodus.templatethree.BuildConfig
import java.util.*

enum class Constant {

    TAG(BuildConfig.APPLICATION_ID.toUpperCase(Locale.ENGLISH)),
    PREF_NOTIFICATION_TOKEN("PREF_NOTIFICATION_TOKEN"),
    PREF_ACCESS_TOKEN("PREF_ACCESS_TOKEN"),
    PREF_FCM_TOKEN("PREF_FCM_TOKEN"),
    PREF_LANGUAGE("PREF_LANGUAGE"),
    PREF_LOG("PREF_LOG"),
    PREF_CURRENT_CLASS("PREF_CURRENT_CLASS"),
    PREF_CURRENT_METHOD("PREF_CURRENT_METHOD"),
    PREF_CRASH_REPEATING("PREF_CRASH_REPEATING"),
    PREF_LICENCE_KEY("PREF_LICENCE_KEY"),
    PREF_LICENCE_MARKET("PREF_LICENCE_MARKET"),
    PREF_SSL_KEYS("PREF_SSL_KEYS"),
    CON_AES_SSL_KEY("dde717bc4fd78bbbd98ccc7d8516ba79"),
    CON_AES_SSL_IV("a3da2dab4e2b44d1"),
    CON_LANG_FA("fa"),
    CON_LANG_EN("en"),
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

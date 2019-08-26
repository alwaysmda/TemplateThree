package billing

import android.content.Intent
import android.net.Uri
import com.xodus.templatethree.BuildConfig
import com.xodus.templatethree.R
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

data class Market(
    var type: MarketType = MarketType.BAZAAR,
    var name: String = "",
    var nameFa: String = "",
    var packageName: String = "",
    var intentBilling: Intent = Intent(),
    var intentDetail: Intent = Intent(),
    var intentComment: Intent = Intent(),
    var prefixLinkDetail: String = "",
    var prefixLinkComment: String = "",
    var storeLink: String = "",
    var rateText: Int = 0,
    var notAvailable: Int = 0
) {

    enum class MarketType {
        BAZAAR,
        MYKET,
        IRANAPPS,
        GOOGLEPLAY;
    }
    //    class Builder {
    //        private var templateString: String = ""
    //        private var templateInt: Int = 0
    //        private var isTemplateBoolean: Boolean = false
    //
    //        fun templateString(templateString: String) = apply { this.templateString = templateString }
    //        fun templateInt(templateInt: Int) = apply { this.templateInt = templateInt }
    //        fun isTemplateBoolean(isTemplateBoolean: Boolean) = apply { this.isTemplateBoolean = isTemplateBoolean }
    //
    //        fun build() = Template(
    //            templateString,
    //            templateInt,
    //            isTemplateBoolean
    //        )
    //    }

    constructor(item: Market) : this(
        item.type,
        item.name,
        item.nameFa,
        item.packageName,
        item.intentBilling,
        item.intentDetail,
        item.intentComment,
        item.prefixLinkDetail,
        item.prefixLinkComment,
        item.storeLink,
        item.rateText,
        item.notAvailable
    )

    fun toJson(): JSONObject {
        val jsonObject = JSONObject()
        try {
            jsonObject.put("type", this.type.toString())
            jsonObject.put("name", this.name)
            jsonObject.put("nameFa", this.nameFa)
            jsonObject.put("packageName", this.packageName)
            jsonObject.put("intentBilling", this.intentBilling.toString())
            jsonObject.put("intentDetail", this.intentDetail.toString())
            jsonObject.put("intentComment", this.intentComment.toString())
            jsonObject.put("prefixLinkDetail", this.prefixLinkDetail)
            jsonObject.put("prefixLinkComment", this.prefixLinkComment)
            jsonObject.put("storeLink", this.storeLink)
            jsonObject.put("rateText", this.rateText)
            jsonObject.put("notAvailable", this.notAvailable)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return jsonObject
    }

    companion object {
        const val PACKAGE_BAZAAR = "com.farsitel.bazaar"
        const val PACKAGE_MYKET = "ir.mservices.market"
        const val PACKAGE_IRANAPPS = "ir.tgbs.android.iranapp"
        const val PACKAGE_GOOGLEPLAY = "com.android.vending"
        fun init(marketType: MarketType): Market {
            return when (marketType) {
                MarketType.BAZAAR     -> {
                    Market(
                        marketType,
                        "bazaar",
                        "بازار",
                        PACKAGE_BAZAAR,
                        Intent("ir.cafebazaar.pardakht.InAppBillingService.BIND").setPackage(PACKAGE_BAZAAR),
                        Intent(Intent.ACTION_VIEW, Uri.parse("bazaar://details?id=${BuildConfig.APPLICATION_ID}")).setPackage(PACKAGE_BAZAAR),
                        Intent(Intent.ACTION_VIEW, Uri.parse("bazaar://details?id=${BuildConfig.APPLICATION_ID}")).setPackage(PACKAGE_BAZAAR),
                        "bazaar://details?id=",
                        "bazaar://details?id=",
                        "https://cafebazaar.ir/app/${BuildConfig.APPLICATION_ID}/?l=fa",
                        R.string.md_store_rate_bazaar,
                        R.string.market_unavailable_bazaar
                    )
                }
                MarketType.MYKET      -> {
                    Market(
                        marketType,
                        "myket",
                        "مایکت",
                        PACKAGE_MYKET,
                        Intent("ir.mservices.market.InAppBillingService.BIND").setPackage(PACKAGE_MYKET),
                        Intent(Intent.ACTION_VIEW, Uri.parse("myket://details?id=${BuildConfig.APPLICATION_ID}")).setPackage(PACKAGE_MYKET),
                        Intent(Intent.ACTION_VIEW, Uri.parse("myket://comment?id=${BuildConfig.APPLICATION_ID}")).setPackage(PACKAGE_MYKET),
                        "myket://details?id=",
                        "myket://comment?id=",
                        "http://myket.ir/app/${BuildConfig.APPLICATION_ID}",
                        R.string.md_store_rate_myket,
                        R.string.market_unavailable_myket
                    )
                }
                MarketType.IRANAPPS   -> {
                    Market(
                        marketType,
                        "iranapps",
                        "ایران‌اپس",
                        PACKAGE_IRANAPPS,
                        Intent("ir.tgbs.iranapps.billing.InAppBillingService.BIND").setPackage(PACKAGE_IRANAPPS),
                        Intent(Intent.ACTION_VIEW, Uri.parse("iranapps://app/${BuildConfig.APPLICATION_ID}")).setPackage(PACKAGE_IRANAPPS),
                        Intent(Intent.ACTION_VIEW, Uri.parse("iranapps://app/${BuildConfig.APPLICATION_ID}")).setPackage(PACKAGE_IRANAPPS),
                        "iranapps://app/",
                        "iranapps://app/",
                        "http://iranapps.com/app/${BuildConfig.APPLICATION_ID}",
                        R.string.md_store_rate_iranapps,
                        R.string.market_unavailable_iranapps
                    )
                }
                MarketType.GOOGLEPLAY -> {
                    Market(
                        marketType,
                        "googleplay",
                        "گوگل‌پلی",
                        PACKAGE_GOOGLEPLAY,
                        Intent("com.android.vending.billing.InAppBillingService.BIND").setPackage(PACKAGE_GOOGLEPLAY),
                        Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")).setPackage(PACKAGE_GOOGLEPLAY),
                        Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")).setPackage(PACKAGE_GOOGLEPLAY),
                        "market://details?id=",
                        "market://details?id=",
                        "http://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}",
                        R.string.md_store_rate_googleplay,
                        R.string.market_unavailable_happyinsta
                    )
                }
            }
        }


        fun toJson(userList: List<Market>): JSONArray {
            val jsonArray = JSONArray()
            try {
                for (i in userList.indices) {
                    jsonArray.put(userList[i].toJson())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return jsonArray
        }

        fun toModel(jsonArray: JSONArray): List<Market> {
            val list = ArrayList<Market>()
            try {
                for (i in 0 until jsonArray.length()) {
                    list.add(toModel(jsonArray.getJSONObject(i)))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return list
        }

        fun toModel(jsonObject: JSONObject): Market {
            val item = Market()
            try {
                item.type = MarketType.valueOf(jsonObject.getString("type"))
                item.name = jsonObject.getString("name")
                item.nameFa = jsonObject.getString("nameFa")
                item.packageName = jsonObject.getString("packageName")
                item.intentBilling = Intent(jsonObject.getString("intentBilling")).setPackage(item.packageName)
                item.intentDetail = Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("intentDetail"))).setPackage(item.packageName)
                item.intentComment = Intent(Intent.ACTION_VIEW, Uri.parse(jsonObject.getString("intentComment"))).setPackage(item.packageName)
                item.prefixLinkDetail = jsonObject.getString("prefixLinkDetail")
                item.prefixLinkComment = jsonObject.getString("prefixLinkComment")
                item.storeLink = jsonObject.getString("storeLink")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return item
        }


        fun cloneList(requestList: List<Market>): List<Market> {
            val clonedList = ArrayList<Market>(requestList.size)
            for (item in requestList) {
                clonedList.add(Market(item))
            }
            return clonedList
        }
    }
}

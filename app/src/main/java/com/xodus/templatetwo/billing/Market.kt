package com.xodus.templatetwo.billing

import android.content.Intent
import android.net.Uri
import com.xodus.templatetwo.BuildConfig
import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

data class Market(
    var type: MarketType = MarketType.BAZAAR,
    var name: String = "",
    var nameFa: String = "",
    var packageName: String = "",
    var intentBilling: Intent = Intent(),
    var intentDetail: Intent = Intent(),
    var intentComment: Intent = Intent(),
    var storeLink: String = ""
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
        item.storeLink
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
            jsonObject.put("storeLink", this.storeLink)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return jsonObject
    }

    companion object {

        fun init(marketType: MarketType): Market {
            return when (marketType) {
                MarketType.BAZAAR   -> {
                    Market(
                        marketType,
                        "bazaar",
                        "بازار",
                        "com.farsitel.bazaar",
                        Intent("ir.cafebazaar.pardakht.InAppBillingService.BIND").setPackage("com.farsitel.bazaar"),
                        Intent(Intent.ACTION_VIEW, Uri.parse("bazaar://details?id=${BuildConfig.APPLICATION_ID}")).setPackage("com.farsitel.bazaar"),
                        Intent(Intent.ACTION_VIEW, Uri.parse("bazaar://details?id=${BuildConfig.APPLICATION_ID}")).setPackage("com.farsitel.bazaar"),
                        "https://cafebazaar.ir/app/${BuildConfig.APPLICATION_ID}/?l=fa"
                    )
                }
                MarketType.MYKET    -> {
                    Market(
                        marketType,
                        "myket",
                        "مایکت",
                        "ir.mservices.market",
                        Intent("ir.mservices.market.InAppBillingService.BIND").setPackage("ir.mservices.market"),
                        Intent(Intent.ACTION_VIEW, Uri.parse("myket://details?id=${BuildConfig.APPLICATION_ID}")).setPackage("ir.mservices.market"),
                        Intent(Intent.ACTION_VIEW, Uri.parse("myket://comment?id=${BuildConfig.APPLICATION_ID}")).setPackage("ir.mservices.market"),
                        "http://myket.ir/app/${BuildConfig.APPLICATION_ID}"
                    )
                }
                MarketType.IRANAPPS -> {
                    Market(
                        marketType,
                        "iranapps",
                        "ایران‌اپس",
                        "ir.tgbs.android.iranapp",
                        Intent("ir.tgbs.iranapps.billing.InAppBillingService.BIND").setPackage("ir.tgbs.android.iranapp"),
                        Intent(Intent.ACTION_VIEW, Uri.parse("iranapps://app/${BuildConfig.APPLICATION_ID}")).setPackage("ir.tgbs.android.iranapp"),
                        Intent(Intent.ACTION_VIEW, Uri.parse("iranapps://app/${BuildConfig.APPLICATION_ID}")).setPackage("ir.tgbs.android.iranapp"),
                        "http://iranapps.com/app/${BuildConfig.APPLICATION_ID}"
                    )
                }
                MarketType.GOOGLEPLAY->{
                    Market(
                        marketType,
                        "googleplay",
                        "گوگل‌پلی",
                        "com.android.vending",
                        Intent("com.android.vending.billing.InAppBillingService.BIND").setPackage("com.android.vending"),
                        Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")).setPackage("com.android.vending"),
                        Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")).setPackage("com.android.vending"),
                        "http://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
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

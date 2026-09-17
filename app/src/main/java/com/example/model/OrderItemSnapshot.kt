package com.example.model

import org.json.JSONArray
import org.json.JSONObject

data class OrderItemSnapshot(
    val productId: String,
    val nameEn: String,
    val nameMy: String,
    val brand: String = "",
    val sku: String = "",
    val variant: String = "",
    val quantity: Int,
    val unitPriceMMK: Int,
    val subtotalMMK: Int,
    val spiceLevel: String = "Medium",
    val addOns: List<String> = emptyList(),
    val specialNotes: String = "",
    val includeColdStorage: Boolean = false,
    val includeSpecialPrep: Boolean = false,
    val fulfillmentPreference: String = "Standard Delivery"
) {
    fun toJsonObject(): JSONObject {
        val json = JSONObject()
        json.put("productId", productId)
        json.put("nameEn", nameEn)
        json.put("nameMy", nameMy)
        json.put("brand", brand)
        json.put("sku", sku)
        json.put("variant", variant)
        json.put("quantity", quantity)
        json.put("unitPriceMMK", unitPriceMMK)
        json.put("subtotalMMK", subtotalMMK)
        json.put("spiceLevel", spiceLevel)
        val addOnsArray = JSONArray()
        addOns.forEach { addOnsArray.put(it) }
        json.put("addOns", addOnsArray)
        json.put("specialNotes", specialNotes)
        json.put("includeColdStorage", includeColdStorage)
        json.put("includeSpecialPrep", includeSpecialPrep)
        json.put("fulfillmentPreference", fulfillmentPreference)
        return json
    }

    companion object {
        fun fromJsonObject(json: JSONObject): OrderItemSnapshot {
            val addOnsList = mutableListOf<String>()
            val addOnsArray = json.optJSONArray("addOns")
            if (addOnsArray != null) {
                for (i in 0 until addOnsArray.length()) {
                    addOnsList.add(addOnsArray.optString(i))
                }
            }
            return OrderItemSnapshot(
                productId = json.optString("productId", ""),
                nameEn = json.optString("nameEn", ""),
                nameMy = json.optString("nameMy", ""),
                brand = json.optString("brand", ""),
                sku = json.optString("sku", ""),
                variant = json.optString("variant", ""),
                quantity = json.optInt("quantity", 1),
                unitPriceMMK = json.optInt("unitPriceMMK", 0),
                subtotalMMK = json.optInt("subtotalMMK", 0),
                spiceLevel = json.optString("spiceLevel", "Medium"),
                addOns = addOnsList,
                specialNotes = json.optString("specialNotes", ""),
                includeColdStorage = json.optBoolean("includeColdStorage", false),
                includeSpecialPrep = json.optBoolean("includeSpecialPrep", false),
                fulfillmentPreference = json.optString("fulfillmentPreference", "Standard Delivery")
            )
        }

        fun listToJsonString(items: List<OrderItemSnapshot>): String {
            val array = JSONArray()
            items.forEach { array.put(it.toJsonObject()) }
            return array.toString()
        }

        fun listFromJsonString(jsonString: String): List<OrderItemSnapshot> {
            if (jsonString.isBlank()) return emptyList()
            return try {
                val array = JSONArray(jsonString)
                val list = mutableListOf<OrderItemSnapshot>()
                for (i in 0 until array.length()) {
                    val obj = array.optJSONObject(i)
                    if (obj != null) {
                        list.add(fromJsonObject(obj))
                    }
                }
                list
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}

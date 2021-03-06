package app.beer.restaurant.model.basket

import com.google.gson.annotations.SerializedName

data class Basket(
    @SerializedName("b_id")
    var b_id: Int = 0,
    @SerializedName("product_id")
    var productId: Int = 0,
    @SerializedName("product_count")
    var productCount: Int = 0,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("photo")
    var photoUrl: String = "",
    @SerializedName("name_RU")
    var name_RU: String = "",
    @SerializedName("name_BG")
    var name_BG: String = "",
    @SerializedName("name_DE")
    var name_DE: String = "",
    @SerializedName("description")
    var description: String = "",
    @SerializedName("description_RU")
    var description_RU: String = "",
    @SerializedName("description_BG")
    var description_BG: String = "",
    @SerializedName("description_DE")
    var description_DE: String = "",
    @SerializedName("price")
    var price: String = "",
    @SerializedName("price_EURO")
    var price_EURO: String = "",
    @SerializedName("price_USD")
    var price_USD: String = ""
)
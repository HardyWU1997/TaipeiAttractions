package tw.hardy.taipeiattractions.data.remote.response

import com.google.gson.annotations.SerializedName
import tw.hardy.base.ui.base.BaseEquatable

data class AttractionResponse(
    @SerializedName("data")
    val dataList: List<AttractionData>?,
    @SerializedName("total")
    val total: Int?,
)

data class AttractionData(
    @SerializedName("address")
    val address: String?,
    @SerializedName("category")
    val category: List<AttractionCategory>?,
    @SerializedName("distric")
    val district: String?,
    @SerializedName("elong")
    val eLong: Double?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("facebook")
    val facebook: String?,
    @SerializedName("fax")
    val fax: String?,
    @SerializedName("files")
    val files: List<Any>?,
    @SerializedName("friendly")
    val friendly: List<AttractionFriendly>?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("images")
    val images: List<AttractionImage>?,
    @SerializedName("introduction")
    val introduction: String?,
    @SerializedName("links")
    val links: List<AttractionLink>?,
    @SerializedName("modified")
    val modified: String?,
    @SerializedName("months")
    val months: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("name_zh")
    val nameZh: String?,
    @SerializedName("nlat")
    val nLat: Double?,
    @SerializedName("official_site")
    val officialSite: String?,
    @SerializedName("open_status")
    val openStatus: Int?,
    @SerializedName("open_time")
    val openTime: String?,
    @SerializedName("remind")
    val remind: String?,
    @SerializedName("service")
    val service: List<AttractionService>?,
    @SerializedName("staytime")
    val stayTime: String?,
    @SerializedName("target")
    val target: List<AttractionTarget>?,
    @SerializedName("tel")
    val tel: String?,
    @SerializedName("ticket")
    val ticket: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("zipcode")
    val zipcode: String?,
) : BaseEquatable {
    override fun getItem() = id.toString()
}

data class AttractionCategory(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
)

data class AttractionFriendly(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
)

data class AttractionImage(
    @SerializedName("ext")
    val ext: String?,
    @SerializedName("src")
    val src: String?,
    @SerializedName("subject")
    val subject: String?,
)

data class AttractionLink(
    @SerializedName("src")
    val src: String?,
    @SerializedName("subject")
    val subject: String?,
)

data class AttractionService(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
)

data class AttractionTarget(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
)
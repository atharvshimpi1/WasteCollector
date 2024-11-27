package my.android.wastecollector.model

// Your LocationData data class
data class LatLong(
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class LocationData(
    val status:String?=null,
    val name: String? = null,
    val customer:Boolean?=null,
    val uid: String? = null,
    val latLong: LatLong? = null // Changed to use LatLong
)

data class UserData(
    val name: String? = null,
    val customer: Boolean? = null,
    val status: String?=null
)


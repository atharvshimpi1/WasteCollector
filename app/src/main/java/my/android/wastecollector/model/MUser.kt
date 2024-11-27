package my.android.wastecollector.model

data class MUser (
    val id:String="",
    val name:String="",
    val customer:Boolean=true,
    val phoneNumber:String="",
    val aadhaarNumber:String="",
    val email:String="",
    val address:String="",
    val createdAt:String="",
    val licenseNumber :String = "",
    val profileCompleted:Boolean=false,
    val status:String="inactive",
    val driverArea:String=""
)

fun mUserToMap(user: MUser): Map<String, Any> {
    return mapOf(
        "id" to user.id,
        "name" to user.name,
        "customer" to user.customer,
        "phoneNumber" to user.phoneNumber,
        "aadhaarNumber" to user.aadhaarNumber,
        "email" to user.email,
        "address" to user.address,
        "createdAt" to user.createdAt,
        "licenseNumber" to user.licenseNumber,
        "profileCompleted" to user.profileCompleted,
        "status" to user.status,
        "driverArea" to user.driverArea
    )
}
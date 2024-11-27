package my.android.wastecollector.model


data class WasteRequest(
    var id:String="",
    var customerName:String="",
    var customerId:String="",
    var customerNumber:String="",
    var customerAddress:String="",
    var selectedDateSlot: String = "",
    var selectedTimeSlot: String = "",
    var selectedWasteType: String = "",
    var specialInstructions: String = "",
    var paymentMethod: String = "",
    var customerRating:Int = 0,
    var customerReview:String = "",
    var driverNumber:String="",
    var driverName:String="",
    var driverId:String="",
    var status:String="",
    var money:Int=0,
    var area:String="",
    var lat:String="",
    var long:String="",
)

fun wasteRequestToMap(request: WasteRequest): Map<String, Any?> {
    return mapOf(
        "id" to request.id,
        "customerName" to request.customerName,
        "customerId" to request.customerId,
        "customerNumber" to request.customerNumber,
        "customerAddress" to request.customerAddress,
        "selectedDateSlot" to request.selectedDateSlot,
        "selectedTimeSlot" to request.selectedTimeSlot,
        "selectedWasteType" to request.selectedWasteType,
        "specialInstructions" to request.specialInstructions,
        "customerRating" to request.customerRating,
        "customerReview" to request.customerReview,
        "paymentMethod" to request.paymentMethod,
        "driverName" to request.driverName,
        "driverNumber" to request.driverNumber,
        "driverId" to request.driverId,
        "status" to request.status,
        "money" to request.money,
        "area" to request.area,
        "lat" to request.lat,
        "long" to request.long,
    )
}
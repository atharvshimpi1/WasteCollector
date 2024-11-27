package my.android.wastecollector.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.serialization.Serializable

class BookingViewModel : ViewModel() {

    private val _bookingData = MutableLiveData(BookingData())
    val bookingData: LiveData<BookingData> = _bookingData

    fun updateSelectedDateSlot(date: String) {
        _bookingData.value = _bookingData.value?.copy(selectedDateSlot = date)
    }

    fun updateSelectedTimeSlot(timeSlot: String) {
        _bookingData.value = _bookingData.value?.copy(selectedTimeSlot = timeSlot)
    }

    fun updateSelectedWasteType(wasteType: String) {
        _bookingData.value = _bookingData.value?.copy(selectedWasteType = wasteType)
    }

    fun toggleService(service: String, isSelected: Boolean) {
        _bookingData.value = _bookingData.value?.let { currentData ->
            val updatedServices = if (isSelected) {
                currentData.selectedServices + service
            } else {
                currentData.selectedServices - service
            }
            currentData.copy(selectedServices = updatedServices)
        }
    }

    fun updateSpecialInstructions(instructions: String) {
        _bookingData.value = _bookingData.value?.copy(specialInstructions = instructions)
    }

    fun updatePaymentMethod(paymentMethod: String) {
        _bookingData.value = _bookingData.value?.copy(paymentMethod = paymentMethod)
    }
}

@Serializable
data class BookingData(
    val selectedDateSlot: String? = null,
    val selectedTimeSlot: String? = null,
    val selectedWasteType: String? = null,
    val selectedServices: List<String> = emptyList(),
    val specialInstructions: String = "",
    val paymentMethod: String? = null
)

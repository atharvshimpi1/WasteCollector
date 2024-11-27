package my.android.wastecollector.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import my.android.wastecollector.model.WasteRequest

class DriverSideViewmodel:ViewModel() {
    val firestore = FirebaseFirestore.getInstance()

    private val _pendingRequests = MutableStateFlow<List<WasteRequest>>(emptyList())  // Initial state is null
    val pendingRequests: StateFlow<List<WasteRequest>> = _pendingRequests

    fun fetchPendingRideRequests(area:String) {
        viewModelScope.launch {
            val ridesRef = FirebaseFirestore.getInstance().collection("wasteRequests")
            ridesRef.whereEqualTo("status", "pending").whereEqualTo("area",area)
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null) {
                        val rideRequests = snapshot.toObjects(WasteRequest::class.java)
                        _pendingRequests.value=rideRequests
                    }
                }
        }}

    fun updateBusy(driverId: String){
        viewModelScope.launch {
            firestore.collection("wasteUsers").document(driverId)
                .update("status", "busy")
                .addOnSuccessListener {  }
                .addOnFailureListener {  }
        }

    }
    fun updateArea(driverId: String,area: String){
        viewModelScope.launch {
            firestore.collection("wasteUsers").document(driverId)
                .update("driverArea", area)
                .addOnSuccessListener {  }
                .addOnFailureListener {  }
        }

    }

    fun updateInactive(driverId: String){
        viewModelScope.launch {
            firestore.collection("wasteUsers").document(driverId)
                .update("status", "inactive")
                .addOnSuccessListener {  }
                .addOnFailureListener {  }
        }

    }

}
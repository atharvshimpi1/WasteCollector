package my.android.wastecollector.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import my.android.wastecollector.model.WasteRequest
import my.android.wastecollector.model.wasteRequestToMap

class CustomerSideViewmodel:ViewModel() {

    val firestore= FirebaseFirestore.getInstance()

    private val _pendingRequests = MutableStateFlow<WasteRequest?>(null)  // Initial state is null
    val pendingRequests: StateFlow<WasteRequest?> = _pendingRequests

    private val _acceptedRequest = MutableStateFlow<WasteRequest?>(null)
    val acceptedRequest: StateFlow<WasteRequest?> = _acceptedRequest

    private val _completedRequest = MutableStateFlow(emptyList<WasteRequest>())
    val completedRequest: StateFlow<List<WasteRequest>> = _completedRequest.asStateFlow()

    fun sendBookingRequest(wasteRequest: WasteRequest) {
        viewModelScope.launch {
            val requestsCollection = firestore.collection("wasteRequests")


            val documentRef = requestsCollection.document()
            val uniqueId = documentRef.id


            val updatedRideRequest = wasteRequest.copy(id = uniqueId)

            documentRef
                .set(wasteRequestToMap(updatedRideRequest))
                .addOnSuccessListener { /* Notify success */ }
                .addOnFailureListener { /* Handle failure */ }

            firestore.collection("wasteUsers").document(wasteRequest.customerId)
                .update("status", "pending")
                .addOnSuccessListener {  }
                .addOnFailureListener {  }


        }

    }

    fun fetchPendingRideRequests(customerId:String) {
        viewModelScope.launch {
            val ridesRef = FirebaseFirestore.getInstance().collection("wasteRequests")
            ridesRef.whereEqualTo("status", "pending").whereEqualTo("customerId",customerId).limit(1)
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null && !snapshot.isEmpty ) {
                        val rideRequests = snapshot.documents[0].toObject(WasteRequest::class.java)
                        _pendingRequests.value=rideRequests
                    }
                }
        }}

    fun fetchAcceptedRequests(customerId:String) {
        viewModelScope.launch {
            val ridesRef = FirebaseFirestore.getInstance().collection("wasteRequests")
            ridesRef.whereEqualTo("status", "accepted").whereEqualTo("customerId",customerId).limit(1)
                .addSnapshotListener { snapshot, e ->
                    if (snapshot != null && !snapshot.isEmpty) {
                        val rideRequests = snapshot.documents[0].toObject(WasteRequest::class.java)
                        _acceptedRequest.value=rideRequests
                    }
                }
        }}

    fun fetchCompletedRequests(customerId: String) {
        viewModelScope.launch {
            firestore.collection("wasteRequests")
                .whereEqualTo("status", "completed")
                .whereEqualTo("customerId", customerId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        // Handle error
                        println("Error fetching completed requests: ${e.message}")
                        return@addSnapshotListener
                    }

                    val completedRequests = snapshot?.toObjects(WasteRequest::class.java) ?: emptyList()
                    _completedRequest.value = completedRequests
                }
        }
    }

    fun submitRating(wasteRequestId: String, rating: Int, review: String) {
        viewModelScope.launch {
            try {
                firestore.collection("wasteRequests").document(wasteRequestId)
                    .update(mapOf(
                        "rating" to rating,
                        "review" to review
                    ))
                    .addOnSuccessListener {
                        // Handle successful update
                        Log.d("SubmitRating", "Rating and review successfully updated.")
                    }
                    .addOnFailureListener { exception ->
                        // Handle failure
                        Log.e("SubmitRating", "Error updating rating and review: ${exception.message}")
                    }
            } catch (e: Exception) {
                Log.e("SubmitRating", "Exception occurred: ${e.message}")
            }
        }
    }


    fun completeRideRequest(wasteRequestId: String,customerId: String) {
        viewModelScope.launch {
            firestore.collection("wasteRequests").document(wasteRequestId)
                .update("status", "completed")
                .addOnSuccessListener { /* Notify customer */ }
                .addOnFailureListener { /* Handle error */ }


            firestore.collection("wasteUsers").document(customerId)
                .update("status", "inactive")
                .addOnSuccessListener {  }
                .addOnFailureListener {  }


        }
    }

    fun deleteRequest(requestId:String,customerId: String){
        viewModelScope.launch {
            firestore.collection("wasteRequests").document(requestId).delete()
            firestore.collection("wasteUsers").document(customerId)
                .update("status", "inactive")
        }
    }


}
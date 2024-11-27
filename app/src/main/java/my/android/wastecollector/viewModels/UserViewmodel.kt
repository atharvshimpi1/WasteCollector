package my.android.wastecollector.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import my.android.wastecollector.model.MUser

class UserViewModel : ViewModel() {
    private val _muser = MutableStateFlow<MUser?>(null)
    val muser: StateFlow<MUser?> = _muser


    private val db = FirebaseFirestore.getInstance()

    fun getUserDetails(userId: String,onSuccess:()->Unit={}) {
        viewModelScope.launch {

            db.collection("wasteUsers").document(userId).addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    val mUser = snapshot.toObject(MUser::class.java)
                    _muser.value=mUser

                }
            }

        }
    }
    fun clearUserData() {
        _muser.value = null
    }

}
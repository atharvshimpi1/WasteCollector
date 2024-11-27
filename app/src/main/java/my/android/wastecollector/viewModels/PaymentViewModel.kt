package my.android.wastecollector.viewModels

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.razorpay.Checkout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.json.JSONObject

class PaymentViewModel : ViewModel() {

    private val _paymentSuccess = MutableStateFlow(false)
    val paymentSuccess: StateFlow<Boolean> = _paymentSuccess



    fun startPayment(activity: Activity,
                     amount: Int,
                     customerName: String,
                     customerEmail: String,
                     customerContact: String) {


        val checkout = Checkout()
        checkout.setKeyID("rzp_live_eVJmNCIbbOkLBc")
        try {
            val options = createPaymentOptions(amount, customerName, customerEmail, customerContact)
            checkout.open(activity, options)
        } catch (e: Exception) {
            showToast(activity, "Error in payment: ${e.message}")
            Log.e(TAG, "Error starting payment", e)
        }
    }

    private fun createPaymentOptions(
        amount: Int,
        customerName: String,
        customerEmail: String,
        customerContact: String
    ): JSONObject {
        return JSONObject().apply {
            put("name", "Waste Collector")
            put("description", "Booking Charges")
            put("theme.color", "#3399cc")
            put("currency", "INR")
            put("amount", (amount * 100).toString())
            put("prefill", JSONObject().apply {
                put("name", customerName)
                put("email", customerEmail)
                put("contact", customerContact)
            })
        }
    }

    fun onPaymentSuccess(razorpayPaymentId: String?) {
        _paymentSuccess.value = true
        Log.d(TAG, "Payment Successful: $razorpayPaymentId")
    }

    fun onPaymentError(code: Int, response: String?) {
        Log.e(TAG, "Payment error $code: $response")
    }

    fun resetPaymentSuccess() {
        _paymentSuccess.value = false
    }

    private fun showToast(activity: Activity, message: String) {
        Log.e(TAG, message)
        activity.runOnUiThread {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }

    }

    companion object {
        private const val TAG = "PaymentViewModel"
    }
}
package my.android.wastecollector

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.razorpay.PaymentResultListener
import my.android.wastecollector.navigation.Navigation
import my.android.wastecollector.ui.theme.WasteCollectorTheme
import my.android.wastecollector.viewModels.AuthViewmodel
import my.android.wastecollector.viewModels.BookingViewModel
import my.android.wastecollector.viewModels.PaymentViewModel
import my.android.wastecollector.viewModels.UserViewModel

class MainActivity : ComponentActivity(), PaymentResultListener {
    private lateinit var paymentViewModel: PaymentViewModel
    private lateinit var authViewmodel: AuthViewmodel
    private lateinit var userViewModel: UserViewModel
    private lateinit var bookingViewModel: BookingViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewmodel = ViewModelProvider(this)[AuthViewmodel::class.java]
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        paymentViewModel = ViewModelProvider(this)[PaymentViewModel::class.java]
        bookingViewModel = ViewModelProvider(this)[BookingViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            WasteCollectorTheme(darkTheme = false, dynamicColor = false) {
                    val navController = rememberNavController()
                    Navigation(navController = navController, paymentViewModel = paymentViewModel,
                        authViewmodel = authViewmodel, userViewModel =userViewModel,
                        bookingViewModel =  bookingViewModel )
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        paymentViewModel.onPaymentSuccess(razorpayPaymentId)
        Toast.makeText(this, "Payment successful, pickup is scheduled", Toast.LENGTH_SHORT).show()
    }

    override fun onPaymentError(code: Int, response: String?) {
        paymentViewModel.onPaymentError(code, response)
        Toast.makeText(this, "Error in payment: $response", Toast.LENGTH_LONG).show()
    }
}
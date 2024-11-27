package my.android.wastecollector.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import my.android.wastecollector.navigation.WasteCollectorScreens
import my.android.wastecollector.ui.theme.BgColor
import my.android.wastecollector.viewModels.BookingViewModel
import my.android.wastecollector.viewModels.PaymentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingConfirmationScreen(bookingViewModel: BookingViewModel, navController: NavController, paymentViewModel: PaymentViewModel) {
    val bookingData = bookingViewModel.bookingData

    var paymentAmount by remember { mutableStateOf("0") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Waste Collection Details",
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Handle more options */ }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BgColor,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                )
            )
        },
        containerColor = BgColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .background(BgColor)
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)

        ) {
            bookingData.value?.selectedDateSlot?.let { DateSection(date = it) }
            Spacer(modifier = Modifier.height(24.dp))
            bookingData.value?.selectedTimeSlot?.let { DetailItem("Time Slot", it) }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            bookingData.value?.selectedWasteType?.let { DetailItem("Type", it) }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            bookingData.value?.paymentMethod?.let {
                paymentAmount = getPaymentAmount(it)
                DetailItem("Payment Type", "$it (₹$paymentAmount)" )
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            DetailItem("Status", "Pending")
            Spacer(modifier = Modifier.height(24.dp))
            ConfirmButton(navController, paymentAmount =paymentAmount , paymentViewModel = paymentViewModel )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun DateSection(date:String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFF40C4FF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.DateRange, contentDescription = null, Modifier.size(30.dp))
        }
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                date,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                "Date",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium)
        Text(value, color = Color.Gray)
    }
}

@Composable
fun ConfirmButton(navController: NavController,paymentAmount:String, paymentViewModel: PaymentViewModel) {
    val context = LocalContext.current
    Button(
        onClick = {
            val activity = context as? Activity

            activity?.let {
                val moneyValue = paymentAmount.toIntOrNull()
                    ?: 0
                if (moneyValue > 0) {
                    paymentViewModel.startPayment(activity, moneyValue, customerContact = "", customerEmail = "", customerName = "")
                } else {
                    Toast.makeText(activity, "Amount is invalid", Toast.LENGTH_SHORT).show()
                }
            }
            navController.navigate(WasteCollectorScreens.HomeScreen.name) },

        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF40C4FF)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text("Confirm Collection", color = Color.White)
    }
}
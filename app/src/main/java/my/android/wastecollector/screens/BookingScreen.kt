package my.android.wastecollector.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import my.android.wastecollector.R
import my.android.wastecollector.model.WasteRequest
import my.android.wastecollector.navigation.WasteCollectorScreens
import my.android.wastecollector.screens.screenComponents.HomeScreenCard
import my.android.wastecollector.ui.theme.BgColor
import my.android.wastecollector.ui.theme.ButtonColor
import my.android.wastecollector.ui.theme.TextFieldColor
import my.android.wastecollector.viewModels.CustomerSideViewmodel
import my.android.wastecollector.viewModels.PaymentViewModel
import my.android.wastecollector.viewModels.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BookingScreen(navController: NavController, customerSideViewmodel: CustomerSideViewmodel,
                  userViewModel: UserViewModel, paymentViewModel: PaymentViewModel) {

    val context= LocalContext.current
    var paymentAmount by remember { mutableStateOf("0") }
    var paymentType by remember { mutableStateOf("") }
    var timeSlot by remember { mutableStateOf("") }
    var dateSlot by remember { mutableStateOf("") }
    var wasteType by remember { mutableStateOf("") }


    val paymentSuccess by paymentViewModel.paymentSuccess.collectAsState()


    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val request=WasteRequest()

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }

    var areaInput by remember { mutableStateOf("") }

    var addressInput by remember { mutableStateOf("") }

    val user = userViewModel.muser.collectAsState()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                getCurrentLocation(fusedLocationClient) { location ->
                    currentLocation = location
                }

            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        delay(3000)
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation(fusedLocationClient) { location ->
                currentLocation = location
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(paymentSuccess) {
        if (paymentSuccess) {
            request.area = areaInput.trim()
            request.customerId = user.value!!.id
            request.customerName = user.value!!.name
            request.customerNumber = user.value!!.phoneNumber
            request.customerAddress = addressInput
            request.money = paymentAmount.toInt()
            request.selectedTimeSlot = timeSlot
            request.selectedDateSlot = dateSlot
            request.paymentMethod = paymentType
            request.selectedWasteType = wasteType
            request.status = "pending"
            request.lat = currentLocation?.latitude.toString()
            request.long = currentLocation?.longitude.toString()

            customerSideViewmodel.sendBookingRequest(request)

            navController.navigate(WasteCollectorScreens.HomeScreen.name) {
                popUpTo(WasteCollectorScreens.BookingScreen.name) { inclusive = true }
            }

            paymentViewModel.resetPaymentSuccess()
        }
    }

    Scaffold(
        topBar = { BookingScreenTopBar(navController = navController)},
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(BgColor)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item { HomeScreenCard() }
            item { SectionHeader(text = "Schedule Pickup") }
            item {
                DateSelectionSection(
                    selectedDateSlot = request.selectedDateSlot,
                    onDateSelected = {request.selectedDateSlot=it
                    dateSlot = it}
                )
            }
            item {
                TimeSlotSection(
                    selectedTimeSlot = request.selectedTimeSlot,
                    onTimeSlotSelected = {request.selectedTimeSlot=it
                    timeSlot = it}
                )
            }
            item { SectionHeader(text = "Select Waste Type") }
            item {
                WasteCardSection(
                    selectedWasteType = request.selectedWasteType,
                    onWasteTypeSelected = { request.selectedWasteType=it
                    wasteType = it}
                )
            }
            item {
                Column(Modifier.fillMaxWidth()) {
                    SectionHeader("Area")
                    OutlinedTextField(
                        value = areaInput,
                        onValueChange = {areaInput=it
                        },
                        placeholder = { Text("Area") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = TextFieldColor,
                            unfocusedContainerColor = TextFieldColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }
            item {
                Column(Modifier.fillMaxWidth()) {
                    SectionHeader("Address")
                    OutlinedTextField(
                        value = addressInput,
                        onValueChange = {addressInput=it
                        },
                        placeholder = { Text("Address") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = TextFieldColor,
                            unfocusedContainerColor = TextFieldColor
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

            }

            item {
                PaymentSection(
                    onPaymentMethodSelected = { money,paymentMethod->
                        request.money=money
                        request.paymentMethod=paymentMethod
                        paymentAmount = money.toString()
                        paymentType = paymentMethod
                    }
                )
            }


            item {
                ScheduleButton() {
                    val activity = context as? Activity
                    request.area = areaInput.trim()
                    request.customerId = user.value!!.id
                    request.customerName = user.value!!.name
                    request.customerNumber = user.value!!.phoneNumber
                    request.customerAddress = addressInput
                    request.status = "pending"
                    request.lat = currentLocation?.latitude.toString()
                    request.long = currentLocation?.longitude.toString()

                    if (request.paymentMethod.isNotEmpty() && request.area.isNotEmpty() && request.customerAddress.isNotEmpty() &&
                        request.selectedDateSlot.isNotEmpty() && request.selectedTimeSlot.isNotEmpty() &&
                        request.selectedWasteType.isNotEmpty() && request.lat.isNotEmpty() && request.long.isNotEmpty()) {

                        val moneyValue = paymentAmount.toIntOrNull() ?: 0
                        if (moneyValue > 0) {
                            activity?.let {
                                paymentViewModel.startPayment(activity, moneyValue,
                                    customerEmail = user.value!!.email,
                                    customerContact = user.value!!.phoneNumber,
                                    customerName = user.value!!.name
                                )
                                 if (paymentSuccess) {
                                        customerSideViewmodel.sendBookingRequest(request)
                                        navController.navigate(WasteCollectorScreens.HomeScreen.name) {
                                            popUpTo(WasteCollectorScreens.BookingScreen.name) { inclusive = true }
                                        }
                                        paymentViewModel.resetPaymentSuccess()
                                    }
                            }
                        } else {
                            Toast.makeText(activity, "Amount is invalid", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Fill all Details", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            item { SectionHeader(text = "Upcoming Pickups") }
            items(getUpcomingPickups()) { pickup ->
                UpcomingPickupItem(pickup)
            }
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(vertical = 10.dp)
    )
}

@Composable
fun DateSelectionSection(selectedDateSlot: String?, onDateSelected: (String) -> Unit) {
    var dateText by remember { mutableStateOf(selectedDateSlot ?: "Select Date") }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth()) {
        SectionSubHeader(text = "Date Picker")

        OutlinedTextField(
            value = dateText,
            onValueChange = { },
            readOnly = true,
            label = { Text("Select Date") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Select date",
                    modifier = Modifier.clickable { showDatePicker = true }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
                .padding(bottom = 16.dp)
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDateSelected = { dateMillis ->
                if (dateMillis != null) {
                    val selectedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(dateMillis))
                    dateText = selectedDate
                    onDateSelected(selectedDate)
                }
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TimeSlotSection(selectedTimeSlot: String?, onTimeSlotSelected: (String) -> Unit) {
    val timeSlots = listOf("7:00 am", "12:00 pm", "3:00 pm", "6:00 pm")
    var timeInput by remember {
        mutableStateOf("")
    }

    Column(Modifier.fillMaxWidth()) {
        SectionSubHeader(text = "Time Slot Selector")
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
        ) {
            timeSlots.forEach { time ->
                InputChip(
                    selected = time == timeInput,
                    onClick = {
                        timeInput=time
                        onTimeSlotSelected(time) },
                    label = { Text(time) },
                    modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)
                )
            }
        }
    }
}

@Composable
fun SectionSubHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun UpcomingPickupItem(pickup: UpcomingPickup, ) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = pickup.imgRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = pickup.title, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${pickup.date} at ${pickup.time}")
            }
        }
    }
}

@Composable
fun ScheduleButton(onClick:()->Unit) {
    Button(
        onClick = {
            onClick()
        },
        colors = ButtonDefaults.buttonColors(containerColor = ButtonColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(text = "Schedule Pickup", color = Color.White)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentSection( onPaymentMethodSelected: (Int,String) -> Unit) {
    var paymentMethod by remember {
        mutableStateOf("")
    }
    var expanded by remember { mutableStateOf(false) }
    val paymentMethods = listOf("One Time", "Daily", "Weekly", "Monthly")
    var paymentAmount by remember { mutableStateOf(0) }

    Column(Modifier.fillMaxWidth()) {
        SectionHeader("Payment Type")
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value =  if (paymentMethod.trim().isEmpty())
                    "Select payment type" else paymentMethod,
                onValueChange = { },
                readOnly = true,
                colors = TextFieldDefaults.colors(focusedContainerColor = TextFieldColor, unfocusedContainerColor = TextFieldColor),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = TextFieldColor
            ) {
                paymentMethods.forEach { method ->
                    DropdownMenuItem(
                        text = { Text(method) },
                        onClick = {
                            paymentMethod=method

                            expanded = false
                            paymentAmount = when (method) {
                                "One-Time" -> 200
                                "Daily" -> 4000
                                "Weekly" -> 1250
                                "Monthly" -> 1000
                                else -> 0
                            }
                            onPaymentMethodSelected(paymentAmount,paymentMethod)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Amount: ₹$paymentAmount",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun areaSection(area: String, onAreaChanged: (String) -> Unit) {
    var textInput by remember {
        mutableStateOf("")
    }
    Column(Modifier.fillMaxWidth()) {
        SectionHeader("Area")
        OutlinedTextField(
            value = textInput,
            onValueChange = {textInput=it
                            },
            placeholder = { Text("Area") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = TextFieldColor,
                unfocusedContainerColor = TextFieldColor
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun AdditionalServicesSection(
    selectedServices: List<String>,
    onServiceToggled: (String, Boolean) -> Unit
) {
    val services = listOf("Hazardous Waste Collection", "Bulk Waste Collection", "Recycling Education")

    Column(Modifier.fillMaxWidth()) {
        SectionHeader("Additional Services")
        services.forEach { service ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = service in selectedServices,
                    onCheckedChange = { isChecked -> onServiceToggled(service, isChecked) }
                )
                Text(text = service)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WasteCardSection(selectedWasteType: String?, onWasteTypeSelected: (String) -> Unit) {
    var wasteInput by remember {
        mutableStateOf("")
    }
    val wasteTypes = listOf(
        Triple("Organic", "Organic Waste", R.drawable.ic_organic),
        Triple("Plastic", "Plastic Waste", R.drawable.ic_plastic),
        Triple("Paper", "Paper Waste", R.drawable.ic_papers),
        Triple("Other", "Other, mix", R.drawable.ic_bin)
    )
    Column(Modifier.fillMaxWidth()) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            wasteTypes.forEach { (title, subTitle, imgRes) ->
                WasteTypeCard(
                    title = title,
                    subTitle = subTitle,
                    imgRes = imgRes,
                    isSelected = title == wasteInput,
                    onSelect = { wasteInput = title
                        onWasteTypeSelected(title) }
                )
            }
        }
    }
}

@Composable
fun WasteTypeCard(title: String, subTitle: String, imgRes: Int, isSelected: Boolean, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .padding(4.dp)
            .clickable(onClick = onSelect),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painter = painterResource(id = imgRes), contentDescription = null, modifier = Modifier.size(50.dp))
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = title, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Text(text = subTitle, textAlign = TextAlign.Center)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreenTopBar(navController: NavController){
    TopAppBar(title = { Text(text = "Waste Collector",
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()) },

        navigationIcon = { IconButton(onClick = { navController.navigateUp()} ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription ="Back")
        }
        },
        actions = { IconButton(onClick = { navController.navigate(WasteCollectorScreens.NotificationScreen.name) }) {
            Icon(imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notification")
        }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

fun getPaymentAmount(method: String): String {
    return when (method) {
        "One Time" -> "20"
        "Monthly" -> "500"
        "Quarterly" -> "1250"
        "Yearly" -> "4000"
        else -> "0"
    }
}

private fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationReceived: (LatLng) -> Unit
) {
    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                onLocationReceived(LatLng(it.latitude, it.longitude))
            }
        }
    } catch (e: SecurityException) {
        // Handle the security exception if permissions are not granted
        e.printStackTrace()
    }
}




data class UpcomingPickup(val imgRes: Int, val title: String, val date: String, val time: String)

fun getUpcomingPickups(): List<UpcomingPickup> = listOf(
    UpcomingPickup(R.drawable.bg2, "Pickup 1", "14/09/24", "8:00 am"),
    UpcomingPickup(R.drawable.dustbin2, "Pickup 2", "15/09/24", "9:00 am"),
    UpcomingPickup(R.drawable.bg2, "Pickup 3", "16/09/24", "7:00 am")
)
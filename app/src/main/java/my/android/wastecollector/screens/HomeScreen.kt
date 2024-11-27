package my.android.wastecollector.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import my.android.wastecollector.R
import my.android.wastecollector.navigation.WasteCollectorScreens
import my.android.wastecollector.screens.screenComponents.BottomBar
import my.android.wastecollector.screens.screenComponents.HomeScreenCard
import my.android.wastecollector.screens.screenComponents.ReviewDialog
import my.android.wastecollector.screens.screenComponents.TopBar
import my.android.wastecollector.screens.screenComponents.cButton
import my.android.wastecollector.screens.screenComponents.csRequestCard
import my.android.wastecollector.ui.theme.BgColor
import my.android.wastecollector.ui.theme.ButtonColor
import my.android.wastecollector.viewModels.CustomerSideViewmodel
import my.android.wastecollector.viewModels.UserViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    customerSideViewmodel: CustomerSideViewmodel,
    userViewModel: UserViewModel
) {
    val userProfile = userViewModel.muser.collectAsState()
    val pendingRequest = customerSideViewmodel.pendingRequests.collectAsState()
    val acceptedRequest = customerSideViewmodel.acceptedRequest.collectAsState()
    var showReviewDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            userProfile.value?.id?.let { userViewModel.getUserDetails(it) }
            delay(2000)
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Waste Collector",
                onNotificationClick = {
                    navController.navigate(WasteCollectorScreens.NotificationScreen.name)
                }
            )
        },
        bottomBar = { BottomBar(navController) },
        containerColor = BgColor
    ) { innerPadding ->
        if (userProfile.value == null) {
            LinearProgressIndicator()
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .background(BgColor)
            ) {
                HomeScreenCard()
                Spacer(modifier = Modifier.height(20.dp))
                when (userProfile.value!!.status) {
                    "inactive" -> {
                        SchedulePickupSection(navController = navController)
                    }
                    "pending" -> {
                        customerSideViewmodel.fetchPendingRideRequests(userProfile.value!!.id)
                        if (pendingRequest.value != null) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    cButton("Delete Request") {
                                        customerSideViewmodel.deleteRequest(
                                            pendingRequest.value!!.id,
                                            pendingRequest.value!!.customerId
                                        )
                                    }

                                    cButton("Mark all as Done") {
                                        showReviewDialog = true
                                        customerSideViewmodel.completeRideRequest(
                                            pendingRequest.value!!.id,
                                            pendingRequest.value!!.customerId
                                        )
                                    }
                                }
                                csRequestCard(request = pendingRequest.value!!) {
                                    showReviewDialog = true
                                    customerSideViewmodel.completeRideRequest(
                                        pendingRequest.value!!.id,
                                        pendingRequest.value!!.customerId
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (showReviewDialog) {
                ReviewDialog(
                    onDismiss = { showReviewDialog = false },
                    onSubmit = { rating, reviewText ->
                        customerSideViewmodel.submitRating(
                            wasteRequestId = pendingRequest.value!!.id,
                            rating = rating,
                            review = reviewText
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun SchedulePickupSection(navController: NavController) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            "Schedule Your Pickup",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Scheduling your waste collection is quick and easy. " +
                    "Select a date and time that suits you, and our team will handle the rest." +
                    " Reliable and timely pickup, just a few taps away.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        ScheduleOptions(onButtonClick = { navController.navigate(WasteCollectorScreens.BookingScreen.name) })
        Button(
            onClick = { navController.navigate(WasteCollectorScreens.BookingScreen.name) },
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonColor,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)

        ) {
            Text("Schedule Pickup")
        }
    }
}

@Composable
fun ScheduleOptions(onButtonClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScheduleButton(
                icon = Icons.Default.DateRange,
                text = "Choose Date",
                modifier = Modifier.weight(1f),
                onButtonClick = onButtonClick
            )
            ScheduleButton(
                imgRes =  R.drawable.ic_history,
                text = "Select Time",
                modifier = Modifier.weight(1f),
                onButtonClick = onButtonClick
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ScheduleButton(
                icon = Icons.Outlined.LocationOn,
                text = "Location",
                modifier = Modifier.weight(1f),
                onButtonClick = onButtonClick
            )
            ScheduleButton(
                icon = Icons.Default.Add,
                text = "Add Notes",
                modifier = Modifier.weight(1f),
                onButtonClick = onButtonClick
            )
        }
    }
}

@Composable
fun ScheduleButton(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onButtonClick: ()-> Unit,
    imgRes: Int ? = null,
    text: String,
) {
    val offWhite = Color(0xFFFFFFFF)

    OutlinedButton(
        onClick = { onButtonClick() },
        modifier = modifier.height(56.dp),
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = offWhite,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            imgRes?.let { painterResource(id = it) }
                ?.let { Image(painter = it, contentDescription = null) }

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}
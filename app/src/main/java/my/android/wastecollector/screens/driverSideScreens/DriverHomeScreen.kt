package my.android.wastecollector.screens.driverSideScreens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.delay
import my.android.wastecollector.R
import my.android.wastecollector.model.LatLong
import my.android.wastecollector.model.LocationData
import my.android.wastecollector.model.WasteRequest
import my.android.wastecollector.navigation.WasteCollectorScreens
import my.android.wastecollector.screens.SectionHeader
import my.android.wastecollector.screens.screenComponents.DriverBottomBar
import my.android.wastecollector.screens.screenComponents.SearchBar
import my.android.wastecollector.screens.screenComponents.TopBar
import my.android.wastecollector.screens.screenComponents.cButton
import my.android.wastecollector.screens.screenComponents.dsRequestCard
import my.android.wastecollector.ui.theme.BgColor
import my.android.wastecollector.ui.theme.ButtonColor
import my.android.wastecollector.ui.theme.TextFieldColor
import my.android.wastecollector.viewModels.DriverSideViewmodel
import my.android.wastecollector.viewModels.GoogleMapView
import my.android.wastecollector.viewModels.UserViewModel

@Composable
fun DriverHomeScreen(navController: NavController,userViewModel: UserViewModel,driverSideViewmodel: DriverSideViewmodel) {
    val searchQuery= remember { mutableStateOf("") }
    val pendingList by driverSideViewmodel.pendingRequests.collectAsState()
    val userProfile by userViewModel.muser.collectAsState()
    var areaInput by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {
        while (true) {
            userViewModel.getUserDetails(userProfile!!.id)
            delay(2000)
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Waste Collector",
                onNotificationClick = { navController.navigate(WasteCollectorScreens.NotificationScreen.name) }
            )
        },
        bottomBar = { DriverBottomBar(navController) },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(3.dp)
                .background(BgColor)
        ) {
            SearchBar(
                searchQuery =searchQuery ,
                placeholder = "Search for Pickup Requests",
                onSearchQueryChange = {}
            )
            Spacer(modifier = Modifier.height(16.dp))
            MapView(requestList = pendingList,areaInput)

            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(text = "Pickup Request")
            Spacer(modifier = Modifier.height(8.dp))
                when(userProfile!!.status){
                    "inactive"->{
                        Column (verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                            SectionHeader("Pickup Area")
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
                            cButton("Look for Pickup") {
                                if (areaInput.trim().isNotEmpty()){

                                    driverSideViewmodel.updateBusy(userProfile!!.id)
                                    driverSideViewmodel.updateArea(userProfile!!.id,areaInput.trim())
                                }

                            }

                    }}
                    "busy"->{
                        driverSideViewmodel.fetchPendingRideRequests(userProfile!!.driverArea)
                        Column(modifier = Modifier.fillMaxWidth(),horizontalAlignment = Alignment.CenterHorizontally) {
                            cButton("Stop Pickup") {
                                areaInput=""
                                driverSideViewmodel.updateInactive(userProfile!!.id)
                                driverSideViewmodel.updateArea(userProfile!!.id,"")
                            }


                        }
                        if (pendingList.isEmpty()) {
                            NoRequestFoundMessage()
                        } else {
                            requestList(requestList = pendingList) {}

                        }



                    }
                }



        }
    }
}

data class PickupRequestData(
    val imgRes: Int,
    val address: String,
    val requestNumber: String
)



@Composable
fun PickupRequestCard(
    imgRes: Int,
    address: String,
    requestNumber: String,
    onNavigateClick: () -> Unit,
    onMarkCompleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = imgRes),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .padding(end = 16.dp)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Pickup Request #$requestNumber", style = MaterialTheme.typography.bodyMedium)
                Text(text = address, style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onNavigateClick,
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonColor),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Navigate", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onMarkCompleteClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF428526)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Mark as Complete", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}


@Composable
private fun MapView(requestList: List<WasteRequest>,area:String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Extract locations from requestList
        var userLocations by remember { mutableStateOf<List<LocationData>>(emptyList()) }

        LaunchedEffect(requestList) {
            userLocations = requestList.mapNotNull { request ->
                // Ensure lat and long are valid before mapping to LatLong
                val lat = request.lat.toDoubleOrNull()
                val long = request.long.toDoubleOrNull()
                val areaIn=request.area


                if (lat != null && long != null && areaIn==area) {
                    LocationData(
                        status = request.status,
                        name = request.customerName,
                        customer = true, // Assuming customer as true for this case
                        uid = request.customerId,
                        latLong = LatLong(latitude = lat, longitude = long)
                    )
                } else {
                    null // Skip if lat or long is invalid
                }
            }

        }


        // Pass the location data to GoogleMapView
        GoogleMapView(locationDataList = userLocations)
    }
}


@Composable
fun requestList(
    requestList: List<WasteRequest>,
    onClick: () -> Unit={}
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = requestList,

            ) { request ->
            dsRequestCard(
                request = request,
                onClick = {}
            )
        }
    }
}

@Composable
fun NoRequestFoundMessage() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "No Pickup Found",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}




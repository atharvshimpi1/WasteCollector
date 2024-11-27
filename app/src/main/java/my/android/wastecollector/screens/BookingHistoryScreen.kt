package my.android.wastecollector.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import my.android.wastecollector.R
import my.android.wastecollector.model.WasteRequest
import my.android.wastecollector.screens.screenComponents.BottomBar
import my.android.wastecollector.screens.screenComponents.SearchBar
import my.android.wastecollector.ui.theme.BgColor
import my.android.wastecollector.viewModels.CustomerSideViewmodel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryScreen(
    navController: NavController,
    customerSideViewModel: CustomerSideViewmodel
) {
    val userId = FirebaseAuth.getInstance().uid

    LaunchedEffect(Unit) {
        customerSideViewModel.fetchCompletedRequests("$userId")
    }

    val completedRequests by customerSideViewModel.completedRequest.collectAsState()
    val searchQuery = rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Collection History",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgColor)
            )
        },
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgColor)
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            SearchBar(
                searchQuery = searchQuery,
                placeholder = "Search for your collection",
                onSearchQueryChange = {
                    // Implement search filtering logic here
                }
            )
            Spacer(modifier = Modifier.height(20.dp))

            if (completedRequests.isEmpty()) {
                EmptyBookingHistoryScreen()
            } else {
                BookingHistoryContent(completedRequests)
            }
        }
    }
}

@Composable
fun EmptyBookingHistoryScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_history) ,
                contentDescription = "No History",
                modifier = Modifier.size(100.dp),
                tint = Color.Gray.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Collection History",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your completed waste collections will appear here.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BookingHistoryContent(completedRequests: List<WasteRequest>) {
    LazyColumn {
        item { SectionHeader(text = "Recent Collection") }

        items(completedRequests.take(2)) { request ->
            CollectionItem(
                completedRequest = request,
                imgRes = getWasteTypeIcon(request.selectedWasteType),
                title = request.selectedWasteType
            )
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionHeader(text = "Past Collections")
        }

        items(completedRequests.drop(2)) { request ->
            CollectionItem(
                completedRequest = request,
                imgRes = getWasteTypeIcon(request.selectedWasteType),
                title = request.selectedWasteType
            )
        }
    }
}

@Composable
fun CollectionItem(
    completedRequest: WasteRequest,
    imgRes: Int,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imgRes),
                    contentDescription = "Waste Icon",
                    modifier = Modifier.size(34.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Collected on ${completedRequest.selectedDateSlot}",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Text(
                text = completedRequest.selectedTimeSlot,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

fun getWasteTypeIcon(wasteType: String): Int {
    return when (wasteType.lowercase()) {
        "Paper" -> R.drawable.ic_recycle
        "Organic" -> R.drawable.ic_organic
        "Plastic" -> R.drawable.ic_plastic
        else -> R.drawable.ic_bin
    }
}
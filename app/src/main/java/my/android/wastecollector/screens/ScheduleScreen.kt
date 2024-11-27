package my.android.wastecollector.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import my.android.wastecollector.R
import my.android.wastecollector.screens.screenComponents.BottomBar
import my.android.wastecollector.ui.theme.BgColor
import my.android.wastecollector.ui.theme.ButtonColor

@Composable
fun ScheduleScreen(navController: NavController) {
    Scaffold(
        topBar = { ScheduleTopAppBar(navController) },
        bottomBar = { BottomBar(navController) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BgColor)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { DetailItem(R.drawable.ic_calendar, "Date", "14/09/2024") }
            item { DetailItem(R.drawable.ic_schedule, "Time Slot", "08:00 - 10:00") }
            item { DetailItem(R.drawable.ic_recycle, "Type", "Recyclable") }

            item { StatusSection() }
            item { AdditionalInformationSection() }

            item {
                Button(
                    onClick = { /* TODO: Handle edit details */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ButtonColor)
                ) {
                    Text("Edit Details", color = Color.White)
                }
            }

            item {
                OutlinedButton(
                    onClick = { /* TODO: Handle cancel collection */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Text("Cancel Collection")
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun DetailItem(iconResId: Int, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleTopAppBar(navController: NavController) {
    TopAppBar(
        title = {
            Text(
                "Waste Collection Details",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
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
            IconButton(onClick = { /* TODO: Handle edit */ }) {
                Icon(Icons.Filled.Edit, contentDescription = "Edit")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = BgColor,
            titleContentColor = Color.Black,
            navigationIconContentColor = Color.Black,
            actionIconContentColor = Color.Black
        )
    )
}

@Composable
fun StatusSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text("Status", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Status", fontSize = 14.sp, color = Color.Gray)
                Text("Pending", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_schedule),
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun AdditionalInformationSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text("Additional Information", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(
            "Collection Location: 123 Ranchi main road , Ranchi, 834001. Notes: Please ensure " +
                    "bins are placed at the curb by 07:30 AM. Separate recyclables from general waste.",
            modifier = Modifier.padding(top = 8.dp),
            fontSize = 14.sp
        )
    }
}


@Preview(showBackground = true)
@Composable
fun BookingDetailScreenPreview() {
    MaterialTheme {
        ScheduleScreen(navController = rememberNavController())
    }
}
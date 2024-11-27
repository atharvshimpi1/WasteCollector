package my.android.wastecollector.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import my.android.wastecollector.R
import my.android.wastecollector.navigation.WasteCollectorScreens
import my.android.wastecollector.screens.screenComponents.BottomBar
import my.android.wastecollector.ui.theme.BgColor
import my.android.wastecollector.ui.theme.BtColor
import my.android.wastecollector.ui.theme.ButtonColor
import my.android.wastecollector.viewModels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, userViewModel: UserViewModel) {
    val muser = userViewModel.muser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BgColor,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(BgColor)
        ) {
            item {
                muser.value?.let { ProfileHeader(name = it.name) }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                muser.value?.let { PersonalDetails(it.email, it.phoneNumber) }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                muser.value?.let { Location(address = it.address) }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                LogOutButton {
                    FirebaseAuth.getInstance().signOut()
                    userViewModel.clearUserData()
                    navController.navigate(WasteCollectorScreens.SplashScreen.name) {
                        popUpTo(WasteCollectorScreens.ProfileScreen.name) { inclusive = true }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.pfp),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(BgColor),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "@${name}", color = Color.Gray)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
    )
}

@Composable
fun DetailItem(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    showArrow: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(BtColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = Color.Gray, fontSize = 14.sp)
            Text(text = value, fontWeight = FontWeight.Medium, fontSize = 16.sp)
        }
        if (showArrow) {
            Text(text = ">", fontWeight = FontWeight.Bold, color = Color.Gray)
        }
    }
}

@Composable
fun PersonalDetails(email: String, mobileNumber: String) {
    SectionTitle(title = "Personal Details")
    DetailItem(
        icon = { Icon(Icons.Default.Email, contentDescription = "Email", tint = Color.Black) },
        label = "Email",
        value = email
    )
    DetailItem(
        icon = { Icon(Icons.Default.Phone, contentDescription = "Phone", tint = Color.Black) },
        label = "Phone",
        value = mobileNumber
    )
}

@Composable
fun Location(address:String) {
    SectionTitle(title = "Location")
    DetailItem(
        icon = { Icon(Icons.Default.LocationOn, contentDescription = "Address", tint = Color.Black) },
        label = "Address",
        value = address
    )
}

@Composable
fun LogOutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ButtonColor)
    ) {
        Text(text = "Log Out", color = Color.White, fontWeight = FontWeight.Bold)
    }
}
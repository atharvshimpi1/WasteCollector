package my.android.wastecollector.screens.driverSideScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import my.android.wastecollector.navigation.WasteCollectorScreens
import my.android.wastecollector.screens.LogOutButton
import my.android.wastecollector.screens.PersonalDetails
import my.android.wastecollector.screens.ProfileHeader
import my.android.wastecollector.screens.screenComponents.DriverBottomBar
import my.android.wastecollector.ui.theme.BgColor
import my.android.wastecollector.viewModels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
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
                }
            )
        },
        bottomBar = {
            DriverBottomBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgColor)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            muser.value?.let {
                ProfileHeader(name = it.name)
            } ?: Text("No user data available", fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            muser.value?.let { user ->
                PersonalDetails(
                    email = user.email ,
                    mobileNumber = user.phoneNumber
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

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

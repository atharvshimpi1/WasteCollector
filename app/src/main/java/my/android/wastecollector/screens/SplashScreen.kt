package my.android.wastecollector.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import my.android.wastecollector.R
import my.android.wastecollector.navigation.WasteCollectorScreens
import my.android.wastecollector.ui.theme.BgColor
import my.android.wastecollector.ui.theme.ButtonColor
import my.android.wastecollector.viewModels.UserViewModel


@Composable
fun SplashScreen(navController: NavController,userViewModel:UserViewModel) {


    val auth= FirebaseAuth.getInstance()
    val muser by userViewModel.muser.collectAsState()


    LaunchedEffect(Unit) {
        delay(2000)
        delay(300)
        if (auth.currentUser==null){
            navController.navigate(WasteCollectorScreens.LoginScreen.name) {
                popUpTo(WasteCollectorScreens.SplashScreen.name) { inclusive = true }

            }}else
        {
            userViewModel.getUserDetails(auth.currentUser!!.uid)


        }


    }
    LaunchedEffect(muser) {

        if (muser!=null){
            if (muser!!.customer){
                navController.navigate(WasteCollectorScreens.HomeScreen.name) {
                    popUpTo(WasteCollectorScreens.SplashScreen.name) { inclusive = true }

                }

            }else{
                navController.navigate(WasteCollectorScreens.DriverHomeScreen.name) {
                    popUpTo(WasteCollectorScreens.SplashScreen.name) { inclusive = true }

                }

            }

        }

    }
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        val density = LocalDensity.current.density
        val paddingTopBottomPx = maxHeight * 0.15f
        val paddingTopBottomDp = paddingTopBottomPx / density
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth().height(5.dp), color = Color.Black)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, paddingTopBottomDp),
        ) {



            Spacer(modifier = Modifier.height(16.dp))
            Column (modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, paddingTopBottomDp),
                verticalArrangement = Arrangement.Center){

            Image(
                painter = painterResource(id = R.drawable.bg2),
                contentDescription = "People recycling",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Join us in making the world a cleaner place by managing your waste efficiently. Our app helps you schedule waste pickups, track recycling efforts, and get reminders.",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Our app provides a comprehensive solution for waste management. You can easily schedule pickups for different types of waste, track your recycling efforts, and get reminders for waste collection.",
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )
                Button(
                    onClick = {

                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonColor,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(text = "Get Started")
                }
            }
        } }

    }

}



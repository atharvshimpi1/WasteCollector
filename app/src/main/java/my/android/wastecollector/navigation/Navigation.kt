package my.android.wastecollector.navigation


import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import my.android.wastecollector.screens.BookingConfirmationScreen
import my.android.wastecollector.screens.BookingHistoryScreen
import my.android.wastecollector.screens.BookingScreen
import my.android.wastecollector.screens.HomeScreen
import my.android.wastecollector.screens.LocationScreen
import my.android.wastecollector.screens.LoginScreen
import my.android.wastecollector.screens.NotificationScreen
import my.android.wastecollector.screens.OtpScreen
import my.android.wastecollector.screens.ProfileScreen
import my.android.wastecollector.screens.ScheduleScreen
import my.android.wastecollector.screens.SplashScreen
import my.android.wastecollector.screens.driverSideScreens.DriverHomeScreen
import my.android.wastecollector.screens.driverSideScreens.DriverProfileScreen
import my.android.wastecollector.viewModels.AuthViewmodel
import my.android.wastecollector.viewModels.BookingViewModel
import my.android.wastecollector.viewModels.CustomerSideViewmodel
import my.android.wastecollector.viewModels.DriverSideViewmodel
import my.android.wastecollector.viewModels.PaymentViewModel
import my.android.wastecollector.viewModels.UserViewModel


@Composable
fun Navigation(navController: NavHostController, paymentViewModel: PaymentViewModel,
               authViewmodel: AuthViewmodel, userViewModel: UserViewModel,
               bookingViewModel: BookingViewModel){
    val customerSideViewmodel= viewModel<CustomerSideViewmodel>()
    val driverSideViewmodel= viewModel<DriverSideViewmodel>()
    NavHost(navController = navController,
        startDestination = WasteCollectorScreens.SplashScreen.name ){

        composable(route = WasteCollectorScreens.LoginScreen.name) {
            LoginScreen(navController = navController, authViewmodel = authViewmodel)
        }

        composable(route = WasteCollectorScreens.OTPScreen.name) {
            OtpScreen(navController = navController,authViewmodel=authViewmodel)
        }

        composable(route = WasteCollectorScreens.HomeScreen.name) {
            HomeScreen(navController = navController, customerSideViewmodel = customerSideViewmodel, userViewModel = userViewModel)
        }

        composable(route = WasteCollectorScreens.ProfileScreen.name) {
            ProfileScreen(navController = navController, userViewModel = userViewModel)
        }
        composable(route = WasteCollectorScreens.SplashScreen.name) {
            SplashScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(route = WasteCollectorScreens.LocationScreen.name) {
            LocationScreen(navController = navController)
        }

        composable(route = WasteCollectorScreens.ScheduleScreen.name) {
            ScheduleScreen(navController = navController)
        }

        composable(route = WasteCollectorScreens.BookingHistoryScreen.name) {
            BookingHistoryScreen(navController = navController, customerSideViewModel = customerSideViewmodel)
        }

        composable(route = WasteCollectorScreens.BookingConfirmationScreen.name) {
            BookingConfirmationScreen(bookingViewModel = bookingViewModel, navController =  navController,
                paymentViewModel = paymentViewModel)
        }


        composable(route = WasteCollectorScreens.BookingScreen.name) {
            BookingScreen(navController = navController, customerSideViewmodel = customerSideViewmodel,
                userViewModel=userViewModel, paymentViewModel =  paymentViewModel)
        }

        composable(route = WasteCollectorScreens.NotificationScreen.name) {
            NotificationScreen(navController = navController)
        }

        composable(route = WasteCollectorScreens.DriverHomeScreen.name) {
            DriverHomeScreen(navController = navController, driverSideViewmodel = driverSideViewmodel, userViewModel = userViewModel)
        }
        composable(route = WasteCollectorScreens.DriverProfileScreen.name) {
            DriverProfileScreen(navController = navController, userViewModel = userViewModel)
        }
    }
}
package my.android.wastecollector.screens


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import my.android.wastecollector.model.MUser
import my.android.wastecollector.navigation.WasteCollectorScreens
import my.android.wastecollector.ui.theme.BgColor
import my.android.wastecollector.ui.theme.ButtonColor
import my.android.wastecollector.ui.theme.TextFieldColor
import my.android.wastecollector.viewModels.AuthState
import my.android.wastecollector.viewModels.AuthViewmodel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(navController: NavController, authViewmodel: AuthViewmodel) {
    var otp by remember { mutableStateOf("") }
    var showBottomSheet by remember { mutableStateOf(false) }

    val authState by authViewmodel.authState.collectAsState()
    var userid by remember { mutableStateOf("") }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.CodeSent -> { /* handle CodeSent state */ }
            is AuthState.NewUser -> {
                userid = (authState as AuthState.NewUser).userId
                showBottomSheet = true
            }
            is AuthState.ExistingUser -> {
                navController.navigate(WasteCollectorScreens.SplashScreen.name) {
                    popUpTo(WasteCollectorScreens.OTPScreen.name) { inclusive = true }
                }
                Toast.makeText(navController.context, "Welcome back!", Toast.LENGTH_SHORT).show()
            }
            is AuthState.ProfileCompleted -> {
                Toast.makeText(navController.context, "Profile completed successfully", Toast.LENGTH_SHORT).show()
                showBottomSheet = false
                navController.navigate(WasteCollectorScreens.SplashScreen.name) {
                    popUpTo(WasteCollectorScreens.OTPScreen.name) { inclusive = true }
                }
            }
            is AuthState.Error -> {
                Toast.makeText(navController.context, "Error: ${(authState as AuthState.Error).message}", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "OTP Verification",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgColor)
                .padding(innerPadding)
        ) {
            if (authViewmodel.loading.value) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Enter OTP",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                )

                Text(
                    text = "A One-Time Password has been sent to your registered mobile number." +
                            " Please enter the OTP in the fields below to proceed.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TextField(
                    value = otp,
                    onValueChange = { otp = it },
                    placeholder = { Text(text = "Enter the OTP here") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = TextFieldColor,
                        unfocusedContainerColor = TextFieldColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Row(Modifier.fillMaxWidth()) {
                    Text(
                        text = "Didn't receive the OTP? ",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "Resend",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .clickable {
                                // Logic to resend OTP goes here
                            }
                    )
                }

                Button(
                    onClick = {
                        when (authState) {
                            is AuthState.CodeSent -> {
                                if (otp.isNotEmpty()) {
                                    authViewmodel.verifyCode(
                                        (authState as AuthState.CodeSent).verificationId,
                                        otp
                                    )
                                } else {
                                    Toast.makeText(
                                        navController.context,
                                        "Please enter the OTP",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            is AuthState.NewUser -> {
                                userid = (authState as AuthState.NewUser).userId
                                showBottomSheet = true
                            }

                            is AuthState.ExistingUser -> {
                                navController.navigate(WasteCollectorScreens.SplashScreen.name) {
                                    popUpTo(WasteCollectorScreens.OTPScreen.name) {
                                        inclusive = true
                                    }
                                }
                                Toast.makeText(
                                    navController.context,
                                    "Welcome back!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            is AuthState.ProfileCompleted -> {
                                Toast.makeText(
                                    navController.context,
                                    "Profile completed successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                                showBottomSheet = false
                            }

                            is AuthState.Error -> {
                                Toast.makeText(
                                    navController.context,
                                    "Error: ${(authState as AuthState.Error).message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {
                                Toast.makeText(
                                    navController.context,
                                    "Unknown Error",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonColor,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text(text = "Submit")
                }

                Text(
                    text = "Your OTP is confidential. Do not share it with anyone. " +
                            "If you need assistance, please contact our support team.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
        if (showBottomSheet) {
            BottomSheetContent(
                onDismiss = { showBottomSheet = false },
                authViewmodel = authViewmodel,
                userid = userid,
                padding = innerPadding
            )
        }

    }
}

@Composable
fun BottomSheetContent(onDismiss: () -> Unit, authViewmodel: AuthViewmodel,
                       userid: String, padding:PaddingValues) {
    var customer by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var aadhaarNumber by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var licenseNumber by remember { mutableStateOf("") }



    Column(
        modifier = Modifier
            .fillMaxWidth().fillMaxHeight()
            .padding(padding)
            .background(Color.White, shape = MaterialTheme.shapes.medium)
            .clip(shape = MaterialTheme.shapes.medium)
            .verticalScroll(rememberScrollState()), // Enable scrolling
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Complete Your Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Fields
        TextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Enter Your Name") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "Select User Type", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))

        // Radio Buttons for User Type Selection
        Row(modifier = Modifier.padding(vertical = 5.dp)) {
            RadioButton(
                selected = customer,
                onClick = { customer = true }
            )
            Text(text = "Customer", modifier = Modifier.padding(start = 2.dp).align(Alignment.CenterVertically))

            Spacer(modifier = Modifier.width(16.dp))

            RadioButton(
                selected = !customer,
                onClick = { customer=false }
            )
            Text(text = "Collector", modifier = Modifier.padding(start = 2.dp).align(Alignment.CenterVertically))
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Enter Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = phoneNumber,
            onValueChange = { phoneNumber = it },
            placeholder = { Text("Enter Phone Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Gray
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (customer) {
            TextField(
                value = address,
                onValueChange = { address = it },
                placeholder = { Text("Enter Your Address") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(10.dp))

        }
        TextField(
            value = aadhaarNumber,
            onValueChange = { aadhaarNumber = it },
            placeholder = { Text("Enter Your Aadhaar Number") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next,
                keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = Color.Gray
            )
        )

        if (!customer)
        {
            TextField(
                value = licenseNumber,
                onValueChange = { licenseNumber = it },
                placeholder = { Text("Enter Your License Number") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = Color.Gray
                )
            )        }
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                // Generate timestamp
                val timestamp = System.currentTimeMillis()

                // Create a SimpleDateFormat instance with the desired format
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                // Convert the timestamp to Date
                val date = Date(timestamp)

                // Format the date into the desired string format
                val formattedDate = sdf.format(date)// Or format to a readable date
                // Handle submission of data here (e.g., save to database, send to server, etc.)
                authViewmodel.completeUserProfile(
                    MUser(
                        id = userid,
                        phoneNumber = phoneNumber,
                        name = name,
                        email = email,
                        createdAt = formattedDate,
                        customer = customer,
                        profileCompleted = true,
                        aadhaarNumber = aadhaarNumber,
                        address = address), userid)

                // For now, just dismiss the bottom sheet

            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonColor)
        ) {
            Text(text = "Done", color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}





package my.android.wastecollector.screens.screenComponents

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import my.android.wastecollector.ui.theme.ButtonColor

@Composable
fun cButton(title:String,onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = ButtonColor),
        modifier = Modifier.padding(top = 20.dp)
    ) {
        Text(text = title, color = Color.White)
    }
}
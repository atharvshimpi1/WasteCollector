package my.android.wastecollector.screens.screenComponents

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LoadingIndicator(isLoading: Boolean) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(56.dp)
            )
        }
    }
}

// Example usage
@Composable
fun MainScreen() {
    val isLoading by remember { mutableStateOf(true) } // Replace with actual loading state

    Surface {
        LoadingIndicator(isLoading)
        if (!isLoading) {
            // Your main content goes here
            Text(text = "Data Loaded")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun LoadingIndicatorPreview() {
    MainScreen()

}

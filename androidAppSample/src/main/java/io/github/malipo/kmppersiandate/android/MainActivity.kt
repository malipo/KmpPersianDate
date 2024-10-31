package io.github.malipo.kmppersiandate.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.malipo.kmppersiandate.android.theme.title_Style
import io.github.malipo.kmppersiandate.PersianDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {

                        DateView(PersianDate().getFullDatetimeWithMonthName("2025-03-21T18:01:41Z"))
                        DateView(PersianDate().getFullDatetimeWithMonthNumber("2025-03-21T18:01:41Z"))
                        DateView(PersianDate().daysAgo("2024-10-26T12:01:41Z"))

                        val persianDate = PersianDate(formatPattern = "2024-10-29T11:32:41Z")
                        DateView(persianDate.getShYear().toString())


                    }
                }
            }
        }
    }
}

@Composable
fun DateView(text: String) {
    Text(
        text = text, style = title_Style, modifier = Modifier.fillMaxWidth().padding(8.dp)
    )
}

@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        DateView("Hello, Android!")
    }
}

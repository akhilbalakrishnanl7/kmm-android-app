package com.litmus7.kmmandroid

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.litmus7.kmmandroid.ui.theme.KmmAndroidTheme
import com.litmus7.kmmlibrary.shared.SpaceXSDK
import com.litmus7.kmmlibrary.shared.cache.DatabaseDriverFactory
import com.litmus7.kmmlibrary.shared.entity.RocketLaunch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val sdk = SpaceXSDK(DatabaseDriverFactory(this))
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var rocketLists: List<RocketLaunch>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadLaunches(false)

        setContent {
            KmmAndroidTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SmallTopAppBar()
                    rocketLists?.let { RocketListPage(rocketLists = it) }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    private fun loadLaunches(forceReload: Boolean) {
        coroutineScope.launch {
            kotlin.runCatching {
                sdk.getLaunches(forceReload)
            }.onSuccess {
                rocketLists = it
            }.onFailure {
                Toast.makeText(this@MainActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBar() {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("SpaceX Launches")
                }
            )
        },
    ) {}
}

@Composable
fun RocketListPage(rocketLists: List<RocketLaunch>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp)
    ) {
        items(rocketLists) { rocketData ->
            RocketListItem(rocketData)
        }
    }
}

@Composable
fun RocketListItem(rocketData: RocketLaunch) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(185.dp)
            .padding(10.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Text(text = "Launch Name: ${rocketData.missionName}", color = Color.Black)
            if (rocketData.launchSuccess == true) {
                Text(text = "Successful", color = Color.Green)
            } else {
                Text(text = "Unsuccessful", color = Color.Red)
            }
            Text(text = "Launch Year: ${rocketData.launchYear}", color = Color.Black)
            Text(
                text = "Launch details: ${rocketData.details}",
                color = Color.Black
            )
        }
    }
}
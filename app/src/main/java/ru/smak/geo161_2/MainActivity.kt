package ru.smak.geo161_2

import android.Manifest
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.smak.geo161_2.ui.theme.Geo1612Theme

class MainActivity : ComponentActivity() {

    private lateinit var requester: ActivityResultLauncher<Array<String>>
    private val mvm by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requester = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ){
            if (!it.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)){
                android.os.Process.killProcess(android.os.Process.myPid())
            } else {
                mvm.startLocationUpdates()
            }
        }

        requester.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )

        setContent {
            Geo1612Theme {
                val loc by mvm.location.collectAsState()
                loc?.let{
                    LocationInfo(loc = it)    
                }                    
            }
        }
    }
}

@Composable
fun LocationInfo(
    loc: Location, 
    modifier: Modifier = Modifier,
){
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)){
            Text(text = "Широта ${loc.latitude}")
            Text(text = "Долгота ${loc.longitude}")
        }
    }
}
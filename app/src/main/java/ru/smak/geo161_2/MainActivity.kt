package ru.smak.geo161_2

import android.Manifest
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.smak.geo161_2.ui.theme.Geo1612Theme
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

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
                LocationList(mvm, Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun LocationList(
    mvm: MainViewModel,
    modifier: Modifier = Modifier,
){
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        reverseLayout = true,
        contentPadding = PaddingValues(8.dp)
    ) {
        items(mvm.locations){
            LocationInfo(loc = it)
        }
    }
}

@Composable
fun LocationInfo(
    loc: Location, 
    modifier: Modifier = Modifier,
){
    val dt = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())
        .format(
            //ZonedDateTime.of(
                LocalDateTime.ofEpochSecond(
                    loc.time / 1000,
                    0,
                    ZoneOffset.UTC
                ),
            //    ZoneId.systemDefault()
            //)
        )
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)){
            Text(text = stringResource(R.string.txt_lat, loc.latitude))
            Text(text = stringResource(R.string.txt_lon, loc.longitude))
            Text(text = stringResource(
                R.string.txt_time,
                dt
            ))
        }
    }
}
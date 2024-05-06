package com.example.locationtracker

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locationtracker.ui.theme.LocationTrackerTheme



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel : LocationViewModel = viewModel()
            LocationTrackerTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background

                ){
                    MyLocationApp(viewModel)
                }
            }
        }
    }
}
@Composable
fun MyLocationApp(viewModel :LocationViewModel,){
    val context = LocalContext.current
    val myLocationUtils = MyLocationUtils(context)
    DisplayLocation(myLocationUtils = myLocationUtils,viewModel,
                    context = context )

}
@Composable
fun DisplayLocation(
    myLocationUtils: MyLocationUtils,
    viewModel :LocationViewModel,
    context: Context
){
    val location = viewModel.location.value
    val address = location?.let{
        myLocationUtils.requestGeocodeLocation(location)
    }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions: Map<String, Boolean> -> // Specify the output type explicitly
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
                ) {
                // ok can access location
                myLocationUtils.requestLocationUpdates(viewModel=viewModel )
            } else {
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            context as MainActivity,
                            Manifest.permission.ACCESS_COARSE_LOCATION)

                if (rationaleRequired) {
                    Toast.makeText(context, "This feature requires location permission", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Please enable location permission from Android settings", Toast.LENGTH_LONG).show()
                }
            }
        }
    )





    Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

    ){
        if(location != null){
            Text("Location, \n Lat: ${location.latitude} & Long: ${location.longitude} \n $address",
                fontSize = 20.sp)
        }
       Text("Location not Available")
        Button(onClick = {
            if(myLocationUtils.hasLocationPermission(context)){
                myLocationUtils.requestLocationUpdates(viewModel)
            }else{
                // request location permission
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION

                    )
                )
            }

        }) {
            Text("Get Location")
        }
}
}


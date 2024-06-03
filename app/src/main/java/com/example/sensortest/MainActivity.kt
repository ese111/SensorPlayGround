package com.example.sensortest

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.OrientationEventListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.sensortest.ui.theme.SensorTestTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.coroutines.resumeWithException
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SensorTestTheme {
                // A surface container using the 'background' color from the theme
                val lifecycleOwner = LocalLifecycleOwner.current

                val sensorManager = remember { getSystemService(Context.SENSOR_SERVICE) as SensorManager }
                val sensorAccelerator = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
                val angle = remember { mutableDoubleStateOf(0.0) }
                val sensorEventListener = remember {
                    object : SensorEventListener {
                        override fun onSensorChanged(event: SensorEvent?) {
                            event?.let {
                                val x = event.values[0]
                                val y = event.values[1]
                                val z = event.values[2]

                                val anglexy = atan2(x, y) / (PI/180)

                                if (anglexy >= 0) {
                                    angle.doubleValue = anglexy
                                    Log.println(Log.INFO, "anglexy", anglexy.toString())
                                } else {
                                    angle.doubleValue = (360 - abs(anglexy))
                                    Log.println(Log.INFO, "anglexy", (360 - abs(anglexy)).toString())
                                }
                            }
                        }

                        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

                        }

                    }
                }

                DisposableEffect(lifecycleOwner) {
                    // Create an observer that triggers our remembered callbacks
                    // for sending analytics events
                    val observer = LifecycleEventObserver { _, event ->
                        when(event) {
                            Lifecycle.Event.ON_RESUME -> {
                                sensorManager.registerListener(sensorEventListener, sensorAccelerator, SensorManager.SENSOR_DELAY_NORMAL)
                            }
                            Lifecycle.Event.ON_PAUSE,
                            Lifecycle.Event.ON_STOP,
                            Lifecycle.Event.ON_DESTROY -> {
                                sensorManager.unregisterListener(sensorEventListener)
                            }
                            else-> {}
                        }
                    }

                    // Add the observer to the lifecycle
                    lifecycleOwner.lifecycle.addObserver(observer)

                    // When the effect leaves the Composition, remove the observer
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }



                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(angle.doubleValue.toString())
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SensorTestTheme {
        Greeting("Android")
    }
}
package com.example.sensortest

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.sensortest.ui.theme.SensorTestTheme
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        setContent {
            SensorTestTheme {
                // A surface container using the 'background' color from the theme

                val lifecycleOwner = LocalLifecycleOwner.current
                val context = LocalContext.current

                var orientationState by remember { mutableStateOf("") }

                DisposableEffect(key1 = lifecycleOwner) {
                    val listener = object : SensorEventListener {
                        override fun onSensorChanged(event: SensorEvent?) {
                            event?.let { sensorEvent ->
                                val x = sensorEvent.values[0]
                                val y = sensorEvent.values[1]
                                val z = sensorEvent.values[2]
                                val r = sqrt(x.pow(2) + y.pow(2) + z.pow(2))

                                Log.d("MainActivity", "onSensorChanged: x: $x, y: $y, z: $z, R: $r")
                                val xrAngle = (90 - acos(x / r) * 180 / PI).toFloat()
                                val yrAngle = (90 - acos(y / r) * 180 / PI).toFloat()

                                orientationState = String.format(
                                    "x-rotation: %.1f\u00B0 \n y-rotation: %.1f\u00B0", xrAngle, yrAngle)
                            }
                        }

                        override fun onAccuracyChanged(
                            sensor: Sensor?,
                            accuracy: Int
                        ) {

                        }

                    }
                    val observer = LifecycleEventObserver { _, event ->
                        when (event) {
                            Lifecycle.Event.ON_RESUME -> {
                                sensorManager.registerListener(
                                    listener,
                                    sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                                    SensorManager.SENSOR_DELAY_FASTEST
                                )

                            }
                            Lifecycle.Event.ON_PAUSE -> {
                                sensorManager.unregisterListener(listener)
                            }

                            else -> {

                            }
                        }
                    }

                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting(orientationState)
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
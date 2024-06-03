package com.example.sensortest

interface SensorHandlerCallback {
    fun onChanged(
        axisX: Float,
        axisY:Float,
        axisZ: Float,
        angleXY: Float,
        angleXZ: Float,
        angleYZ: Float,
        absAngleXY: Float,
    )
}
package com.example.sensortest

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

class SensorHandler(
    private val context: Context,
    private val sensorAccelerator: Sensor,
    private val sensorEventListener: SensorEventListener,
    private val sensorManager: SensorManager,
    private val callback: SensorHandlerCallback
) {

    /**
     * 상위 액티비티의 onResume() 핸들러에서 호출해야 하는 함수
     */
    fun onResume() {
        if (sensorManager != null && sensorAccelerator != null && sensorEventListener != null) {
            sensorManager.registerListener(sensorEventListener, sensorAccelerator, DETECT_SPEED)
        }
    }

    /**
     * 상위 액티비티의 onPause() 핸들러에서 호출해야 하는 함수
     */
    fun onPause() {
        if (sensorManager != null && sensorAccelerator != null && sensorEventListener != null) {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    /**
     * 상위 액티비티의 onDestroy() 핸들러에서 호출해야 하는 함수
     */
    fun onDestroy() {
        if (sensorManager != null && sensorAccelerator != null && sensorEventListener != null) {
            sensorManager.unregisterListener(sensorEventListener)

//            sensorAccelerator = null;
//            sensorManager = null;
//            sensorEventListener = null;
//            callback = null;
        }
    }

    /**
     * 지정한 각도를 0 ~ 360도 기준으로 환산하여 계산한다.
     *
     * 각도값이 0을 포함하여 양수일 경우 동일한 각도가 반환되고,
     * 각도값이 음수일 경우 360도를 기준으로 해당 각도값의 절대값을 뺀 각도가 반환된다.
     *
     * @param angle 센서에 의한 원본 각도값
     * @return 0 ~ 360도 기준의 각도값
     */
    fun calcAbsoluteAngle(angle: Float): Float {
        if (angle >= 0) {
            return angle
        }
        else {
            return (360 - abs(angle))
        }
    }

    /**
     * 지정한 센서 각도를 판단하여 적절한 회전 모드를 결정한다.
     *
     * @param angle 센서 각도값 또는 360도 기준의 각도값
     * @return 회전 모드
     *                 정방향 세로모드일 경우 "portrait"
     *                 역방향 세로모드일 경우 "portrait_reverse"
     *                 정방향 가로모드일 경우 "landscape"
     *                 역방향 가로모드일 경우 "landscape_reverse"
     */
    fun determineOrientationModeByAngle(angle: Float): String {
        val absAngle = calcAbsoluteAngle(angle)

        // PORTRAIT
        if ((0 <= absAngle && absAngle < 45) || (315 <= absAngle && absAngle < 360)) {
            return "portrait";
        }
        // LANDSCAPE
        else if (45 <= absAngle && absAngle < 135) {
            return "landscape";
        }
        // PORTRAIT (REVERSE)
        else if (135 <= absAngle && absAngle < 225) {
            return "portrait_reverse";
        }
        // LANDSCAPE (REVERSE)
        else { // 225 <= absAngle && absAngle < 315
            return "landscape_reverse";
        }
    }

    companion object {
        private const val DETECT_SPEED = SensorManager.SENSOR_DELAY_NORMAL
    }
}
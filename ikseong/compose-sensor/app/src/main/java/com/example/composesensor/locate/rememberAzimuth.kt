package com.example.composesensor.locate

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun rememberAzimuth(): Float {
    val context = LocalContext.current
    // SensorManager 및 센서 객체 가져오기
    val sensorManager =
        remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer =
        remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) } // 가속도계 센서
    val magnetometer =
        remember { sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) } // 자기장 센서

    var azimuthDeg by remember { mutableFloatStateOf(0f) }  // 방위각 상태 값 (0~360)

    DisposableEffect(Unit) {
        // 센서 측정값을 담을 배열
        val accelerometerReading = FloatArray(3)
        val magnetometerReading = FloatArray(3)
        val rotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)
        // 센서 이벤트 리스너 정의
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        // 가속도계 측정값 복사
                        accelerometerReading[0] = event.values[0]
                        accelerometerReading[1] = event.values[1]
                        accelerometerReading[2] = event.values[2]
                    }

                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        // 자기장 센서 측정값 복사
                        magnetometerReading[0] = event.values[0]
                        magnetometerReading[1] = event.values[1]
                        magnetometerReading[2] = event.values[2]
                    }
                }
                // 두 센서 값으로부터 회전 행렬과 방위각 계산
                val success = SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    accelerometerReading,
                    magnetometerReading
                )
                if (success) {
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    // orientationAngles[0] 이 방위각(rad)이며, [1] Pitch, [2] Roll입니다.
                    val azimuthRad = orientationAngles[0]
                    var azimuthDegrees = Math.toDegrees(azimuthRad.toDouble()).toFloat()
                    if (azimuthDegrees < 0) {
                        azimuthDegrees += 360f  // 음수 값을 0~360도로 변환
                    }
                    azimuthDeg = azimuthDegrees  // 상태 업데이트
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) { /* 생략 */
            }
        }
        // 센서 리스너 등록 (실행 속도: NORMAL)
        sensorManager.registerListener(
            sensorListener,
            accelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager.registerListener(
            sensorListener,
            magnetometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        // 컴포저블이 사라질 때 센서 해제
        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    return azimuthDeg  // 현재 방위각 값을 반환 (수시로 업데이트됨)
}

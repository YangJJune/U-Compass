package com.ikseong.ucompass.ui.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlin.math.round

/**
 * 기기의 방향을 감지하고 관리하는 유틸리티 클래스
 */
object DeviceOrientationUtil {
    private const val TAG = "DeviceOrientationUtil"
    private const val SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME // 빠른 업데이트를 위한 게임 모드 딜레이

    // 센서 평활화를 위한 저주파 필터 계수
    private const val ALPHA = 0.05f

    /**
     * 현재 기기의 방향 각도를 Composable 함수에서 관찰 가능한 State로 제공
     */
    @Composable
    fun rememberDeviceOrientation(): State<Float> {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val azimuthState = remember { mutableFloatStateOf(0f) }
        val history = remember { mutableListOf<Float>() }

        DisposableEffect(lifecycleOwner) {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

            // 센서 값을 저장할 배열
            val accelerometerReading = FloatArray(3)
            val magnetometerReading = FloatArray(3)

            // 회전 행렬과 방향 각도를 계산하기 위한 배열
            val rotationMatrix = FloatArray(9)
            val orientationAngles = FloatArray(3)

            // 필터링된 센서 값
            val filteredAccReading = FloatArray(3)
            val filteredMagReading = FloatArray(3)

            // 센서 이벤트 리스너
            val sensorEventListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    when (event.sensor.type) {
                        Sensor.TYPE_ACCELEROMETER -> {
                            // 저주파 필터 적용
                            filteredAccReading[0] =
                                ALPHA * event.values[0] + (1 - ALPHA) * filteredAccReading[0]
                            filteredAccReading[1] =
                                ALPHA * event.values[1] + (1 - ALPHA) * filteredAccReading[1]
                            filteredAccReading[2] =
                                ALPHA * event.values[2] + (1 - ALPHA) * filteredAccReading[2]

                            System.arraycopy(filteredAccReading, 0, accelerometerReading, 0, 3)
                        }

                        Sensor.TYPE_MAGNETIC_FIELD -> {
                            // 저주파 필터 적용
                            filteredMagReading[0] =
                                ALPHA * event.values[0] + (1 - ALPHA) * filteredMagReading[0]
                            filteredMagReading[1] =
                                ALPHA * event.values[1] + (1 - ALPHA) * filteredMagReading[1]
                            filteredMagReading[2] =
                                ALPHA * event.values[2] + (1 - ALPHA) * filteredMagReading[2]

                            System.arraycopy(filteredMagReading, 0, magnetometerReading, 0, 3)
                        }
                    }

                    // 두 센서 데이터가 모두 있을 때 방향 계산
                    if (accelerometerReading[0] != 0f && magnetometerReading[0] != 0f) {
                        updateOrientation(
                            accelerometerReading,
                            magnetometerReading,
                            rotationMatrix,
                            orientationAngles,
                            azimuthState,
                            history
                        )
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
                    // 정확도 변경 시 필요한 처리
                }
            }

            // 라이프사이클 옵저버
            val lifecycleObserver = LifecycleEventObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> {
                        // 센서 리스너 등록
                        sensorManager.registerListener(
                            sensorEventListener,
                            accelerometer,
                            SENSOR_DELAY
                        )
                        sensorManager.registerListener(
                            sensorEventListener,
                            magnetometer,
                            SENSOR_DELAY
                        )
                        Log.d(TAG, "센서 리스너 등록됨")
                    }

                    Lifecycle.Event.ON_PAUSE -> {
                        // 센서 리스너 해제
                        sensorManager.unregisterListener(sensorEventListener)
                        Log.d(TAG, "센서 리스너 해제됨")
                    }

                    else -> { /* no-op */
                    }
                }
            }

            // 라이프사이클 옵저버 등록
            lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

            // DisposableEffect가 종료될 때 정리
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
                sensorManager.unregisterListener(sensorEventListener)
            }
        }

        return azimuthState
    }

    /**
     * 가속도계와 자기장 센서 데이터로부터 기기의 방향 각도 계산
     */
    private fun updateOrientation(
        accelerometerReading: FloatArray,
        magnetometerReading: FloatArray,
        rotationMatrix: FloatArray,
        orientationAngles: FloatArray,
        azimuthState: MutableState<Float>,
        history: MutableList<Float>
    ) {
        // 회전 행렬 계산
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )

        // 방향 각도 계산
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        // 라디안을 도(degree)로 변환 (0~360도)
        val azimuthInRadians = orientationAngles[0]
        val azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()
        val azimuth = (azimuthInDegrees + 360) % 360

        // 값을 반올림하여 미세한 변화로 인한 과도한 업데이트 방지
        val roundedAzimuth = round(azimuth * 10) / 10
        Log.d(TAG, "원본 방위각: $roundedAzimuth")
        // 상태 업데이트
        if (history.size <= 10) {
            // 최초값인 경우 바로 리턴하고 리스트에 추가
            history.add(roundedAzimuth)
            azimuthState.value = roundedAzimuth
            Log.d(TAG, "방위각: ${azimuthState.value}")
            return
        }

        // 10개를 초과하면 가장 오래된 데이터 제거
        history.removeAt(0)

        // 최근 10개 데이터의 평균 계산
        val averageAzimuth = history.average().toFloat()
        Log.d(TAG, "최근 10개 방위각: $history")
        Log.d(TAG, "평균 방위각: $averageAzimuth")

        // 기존 값과의 차이를 확인하여 변화가 충분할 때만 업데이트
        if (kotlin.math.abs(azimuthState.value - averageAzimuth) >= 3f) {
            azimuthState.value = averageAzimuth
        }
    }
}
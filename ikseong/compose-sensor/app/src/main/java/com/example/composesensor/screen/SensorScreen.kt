package com.example.composesensor.screen

import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService


@Composable
fun SensorRoute(
    padding: PaddingValues = PaddingValues()
) {
    val sensorManager =
        getSystemService(LocalContext.current, SensorManager::class.java) as SensorManager

    val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
    SensorScreen(
        modifier = Modifier.padding(padding),
        deviceSensors = deviceSensors
    )

}

@Composable
fun SensorScreen(
    modifier: Modifier = Modifier,
    deviceSensors: List<Sensor> = listOf()
) {
    val lazyState = rememberLazyListState()
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        state = lazyState
    ) {
        items(deviceSensors.size) { index ->
            SensorText(
                sensor = deviceSensors[index],
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun SensorText(sensor: Sensor, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(10.dp)
    ) {
        Text(
            text = sensor.name,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = sensor.type.toString(),
        )
    }
}

@Preview
@Composable
private fun SensorScreenPreview() {
    SensorScreen()
}
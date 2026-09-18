package com.example.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

data class CompassData(
    val currentHeading: Float = 0f,     // Phone heading in degrees (0 = North)
    val qiblaBearing: Float = 135f,     // Kaaba direction from True North
    val needleAngle: Float = 0f,        // Angle of Kaaba relative to top of phone
    val isFacingQibla: Boolean = false, // Within +/- 3 degrees
    val accuracy: Int = SensorManager.SENSOR_STATUS_ACCURACY_HIGH
)

class CompassSensorManager(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val _compassData = MutableStateFlow(CompassData())
    val compassData: StateFlow<CompassData> = _compassData.asStateFlow()

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private val gravityValues = FloatArray(3)
    private val magneticValues = FloatArray(3)
    private var hasGravity = false
    private var hasMagnetic = false

    private var targetQiblaAngle: Float = 136.0f // default for Egypt/Cairo or Makkah angle
    private var smoothedHeading: Float = 0f
    private val smoothingFactor = 0.15f // Low pass filter factor

    fun setQiblaAngle(angle: Float) {
        targetQiblaAngle = angle
        updateNeedle(smoothedHeading)
    }

    fun startListening() {
        if (rotationVectorSensor != null) {
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            accelerometerSensor?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
            magneticFieldSensor?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ROTATION_VECTOR -> {
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                SensorManager.getOrientation(rotationMatrix, orientationAngles)
                val azimuthInRadians = orientationAngles[0]
                val azimuthInDegrees = ((Math.toDegrees(azimuthInRadians.toDouble()) + 360) % 360).toFloat()
                applySmoothedHeading(azimuthInDegrees)
            }
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, gravityValues, 0, 3)
                hasGravity = true
                if (hasMagnetic) computeOrientationFromAccelMag()
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, magneticValues, 0, 3)
                hasMagnetic = true
                if (hasGravity) computeOrientationFromAccelMag()
            }
        }
    }

    private fun computeOrientationFromAccelMag() {
        if (SensorManager.getRotationMatrix(rotationMatrix, null, gravityValues, magneticValues)) {
            SensorManager.getOrientation(rotationMatrix, orientationAngles)
            val azimuthInRadians = orientationAngles[0]
            val azimuthInDegrees = ((Math.toDegrees(azimuthInRadians.toDouble()) + 360) % 360).toFloat()
            applySmoothedHeading(azimuthInDegrees)
        }
    }

    private fun applySmoothedHeading(rawHeading: Float) {
        // Handle angle wrap around for smooth interpolation (e.g. 359 to 1)
        var diff = rawHeading - smoothedHeading
        while (diff < -180) diff += 360
        while (diff > 180) diff -= 360

        smoothedHeading = (smoothedHeading + diff * smoothingFactor + 360) % 360
        updateNeedle(smoothedHeading)
    }

    private fun updateNeedle(heading: Float) {
        val needle = (targetQiblaAngle - heading + 360) % 360
        // Check if phone points directly at the Kaaba within 4 degrees
        val diffFromQibla = abs((targetQiblaAngle - heading + 540) % 360 - 180)
        val facing = diffFromQibla <= 4.0f

        _compassData.value = _compassData.value.copy(
            currentHeading = heading,
            qiblaBearing = targetQiblaAngle,
            needleAngle = needle,
            isFacingQibla = facing
        )
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        _compassData.value = _compassData.value.copy(accuracy = accuracy)
    }
}

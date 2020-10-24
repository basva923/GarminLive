package com.github.basva923.garminphoneactivity.model

import com.github.basva923.garminphoneactivity.settings.Settings
import kotlin.math.max

enum class PropertyType {
    CURRENT,
    AVERAGE,
    AVERAGE_15S,
    AVERAGE_30S,
    MAX
}

enum class LiveTrackProperty(val quantity: String, val unit: String, val precision: Int) {
    SPEED("speed", "km/h", 2),
    DISTANCE("distance", "km", 2),
    ALTITUDE("altitude", "m", 0),
    CADENCE("cadence", "cpm", 0),
    HEART_RATE("heart rate", "bpm", 0),
    HEART_RATE_ZONE("heart rate zone", "bpm", 0),
    LATITUDE("latitude", "°", 6),
    LONGITUDE("longitude", "°", 6),
    POWER("power", "W", 0),
    POWER_ZONE("power zone", "W", 0),
    TIME("time", "s", 0)
}

class LiveTrackInfo(private val track: Track) {
    private val properties = mutableMapOf<LiveTrackProperty, Double>()
    private val avgProperties = mutableMapOf<LiveTrackProperty, Double>()
    private val maxProperties = mutableMapOf<LiveTrackProperty, Double>()
    private val avg15SProperties = mutableMapOf<LiveTrackProperty, FixedSizeSet<Double>>()
    private val avg30SProperties = mutableMapOf<LiveTrackProperty, FixedSizeSet<Double>>()


    // https://www.trainingpeaks.com/blog/power-training-levels/
    val powerZoneFinder = ZoneFinder(
        arrayOf(
            Pair(0, 56),
            Pair(56, 76),
            Pair(76, 91),
            Pair(91, 106),
            Pair(106, 120),
            Pair(121, 1000)
        ), Settings.ftp
    )

    val hrZoneFinder = ZoneFinder(
        arrayOf(
            Pair(0, 69),
            Pair(69, 84),
            Pair(84, 95),
            Pair(95, 106),
            Pair(106, 1000)
        ), Settings.ftp
    )

    init {
        update()
    }

    fun getValueAsString(
        property: LiveTrackProperty,
        propertyType: PropertyType = PropertyType.CURRENT
    ): String {
        val value = getValue(propertyType, property)

        return String.format("%.${property.precision}f", value)
    }

    fun getValue(
        propertyType: PropertyType,
        property: LiveTrackProperty
    ): Double {
        return when (propertyType) {
            PropertyType.CURRENT -> {
                properties[property]!!
            }
            PropertyType.AVERAGE -> {
                avgProperties[property]!! / properties[LiveTrackProperty.TIME]!!
            }
            PropertyType.MAX -> {
                maxProperties[property]!!
            }
            PropertyType.AVERAGE_15S -> {
                avg15SProperties[property]!!.average()
            }
            PropertyType.AVERAGE_30S -> avg30SProperties[property]!!.average()
        }
    }

    fun getAvg30SProp(property: LiveTrackProperty): FixedSizeSet<Double> {
        return avg30SProperties[property]!!
    }


    fun update() {
        powerZoneFinder.unitValue = Settings.ftp
        hrZoneFinder.unitValue = Settings.ftpHeartRate

        properties[LiveTrackProperty.TIME] =
            (track.getLastSample()?.millisSinceStart?.toDouble() ?: 0.0) / 1000.0
        properties[LiveTrackProperty.SPEED] = (track.getLastSample()?.speed ?: 0.0) * 3.6
        properties[LiveTrackProperty.DISTANCE] = (track.getLastSample()?.distance ?: 0.0) / 1000
        properties[LiveTrackProperty.ALTITUDE] = track.getLastSample()?.altitude ?: 0.0
        properties[LiveTrackProperty.CADENCE] = track.getLastSample()?.cadence?.toDouble() ?: 0.0
        properties[LiveTrackProperty.HEART_RATE] =
            (track.getLastSample()?.heartRate ?: 0).toDouble()
        properties[LiveTrackProperty.HEART_RATE_ZONE] =
            hrZoneFinder.valueToZone(properties[LiveTrackProperty.HEART_RATE]!!.toInt()).toDouble()

        properties[LiveTrackProperty.LATITUDE] = track.getLastSample()?.latitude ?: 0.0
        properties[LiveTrackProperty.LONGITUDE] = track.getLastSample()?.longitude ?: 0.0
        properties[LiveTrackProperty.POWER] = PowerCalculation().calculateCurrentPower(
            track,
            Settings.cyclingPosition,
            Settings.trackSurface,
            Settings.totalMass,
            0.08
        )
        properties[LiveTrackProperty.POWER_ZONE] =
            powerZoneFinder.valueToZone(properties[LiveTrackProperty.POWER]!!.toInt()).toDouble()

        updateAvg()
        updateMax()
        update15SAvg()
        update30SAvg()
    }

    fun getLocation(): Pair<Double, Double> {
        return Pair(
            properties[LiveTrackProperty.LATITUDE]!!,
            properties[LiveTrackProperty.LONGITUDE]!!
        )
    }

    private fun updateAvg() {
        for (prop in properties.entries) {
            if (prop.key !in avgProperties.keys) {
                avgProperties[prop.key] = 0.0
            }

            avgProperties[prop.key] = avgProperties[prop.key]!!.plus(prop.value)
        }
    }

    private fun updateMax() {
        for (prop in properties.entries) {
            if (prop.key !in maxProperties.keys) {
                maxProperties[prop.key] = 0.0
            }

            maxProperties[prop.key] = max(maxProperties[prop.key]!!, prop.value)
        }
    }

    private fun update15SAvg() {
        for (prop in properties.entries) {
            if (prop.key !in avg15SProperties.keys) {
                avg15SProperties[prop.key] = FixedSizeSet(15)
                avg15SProperties[prop.key]!!.addAll((0..14).map { 0.0 })
            }

            avg15SProperties[prop.key]!!.add(prop.value)
        }
    }

    private fun update30SAvg() {
        for (prop in properties.entries) {
            if (prop.key !in avg30SProperties.keys) {
                avg30SProperties[prop.key] = FixedSizeSet(30)
                avg30SProperties[prop.key]!!.addAll((0..30).map { 0.0 })
            }

            avg30SProperties[prop.key]!!.add(prop.value)
        }
    }

    fun hasLocation(): Boolean {
        return properties[LiveTrackProperty.LATITUDE] != null && properties[LiveTrackProperty.LONGITUDE] != null
    }
}
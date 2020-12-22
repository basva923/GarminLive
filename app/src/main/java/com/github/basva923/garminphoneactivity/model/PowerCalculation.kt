package com.github.basva923.garminphoneactivity.model

import android.util.Log
import com.github.basva923.garminphoneactivity.settings.Settings
import kotlin.math.*

enum class PowerAlgorithm {
    REAL,
    ELITE_TRAVEL_FLUID_YELLOW,
    GARMIN
}

class PowerCalculation {
    val GRAV_CONST = 9.81
    val TAG = "PowerCalculation"
    val ACCELERATION_WINDOW_SIZE = 5
    val GRADE_WINDOW_SIZE = 20

    fun calculateCurrentPower(
        track: Track,
        position: CyclingPosition,
        surface: TrackSurface,
        totalMass: Int,
        bikeLoss: Double
    ): Double {
        when (Settings.powerAlgorithm) {
            PowerAlgorithm.REAL -> {
                return calcRealPower(track, surface, totalMass, position, bikeLoss)
            }
            PowerAlgorithm.ELITE_TRAVEL_FLUID_YELLOW -> {
                return calcEliteTravelFluidYellowPower(
                    track,
                    bikeLoss
                )
            }
            PowerAlgorithm.GARMIN -> {
                if (track.samples.size < 1)
                    return 0.0
                return track.getLastSample()!!.power
            }
        }

    }

    fun calcEliteTravelFluidYellowPower(
        track: Track,
        bikeLoss: Double
    ): Double {
        val trackSize = track.samples.size
        if (trackSize < 10)
            return 0.0

        val smoothedCurrentSpeed =
            track.samples.subList(trackSize - 5, trackSize).sumByDouble { it.speed } / 5

        val airDensity = 1.225 * exp(-0.00011856 * 0)

        val rollingPower = TrackSurface.ASPHALT.rollCoefficient * 80 * GRAV_CONST * 1
        val windPower =
            0.5 * airDensity * smoothedCurrentSpeed * smoothedCurrentSpeed * CyclingPosition.DROPS.aeroCoefficient
        val power =
            (rollingPower + windPower) * smoothedCurrentSpeed / (1 - bikeLoss)

        val lastSample = track.getLastSample()!!
        if (lastSample.cadence != null && lastSample.cadence < 30)
            return 0.0
        if (power < 0)
            return 0.0

        return power
    }

    fun calcRealPower(
        track: Track,
        surface: TrackSurface,
        totalMass: Int,
        position: CyclingPosition,
        bikeLoss: Double
    ): Double {
        val trackSize = track.samples.size
        if (trackSize < 10)
            return 0.0


        val acceleration = calcAcceleration(track)
        val grade = calcGrade(track)
        val smoothedCurrentSpeed =
            track.samples.subList(trackSize - 5, trackSize).sumByDouble { it.speed } / 5

        val airDensity = 1.225 * exp(-0.00011856 * track.getLastSample()!!.altitude)

        val rollingPower = surface.rollCoefficient * totalMass * GRAV_CONST * cos(atan(grade))
        val windPower =
            0.5 * airDensity * smoothedCurrentSpeed * smoothedCurrentSpeed * position.aeroCoefficient
        val gravityPower = totalMass * GRAV_CONST * sin(atan(grade))
        val accelerationPower = totalMass * acceleration

        val power =
            (rollingPower + windPower + gravityPower + accelerationPower) * smoothedCurrentSpeed / (1 - bikeLoss)


        Log.d(TAG, "Grav: $grade ==> ${gravityPower * smoothedCurrentSpeed} ==> $power")
        Log.d(
            TAG,
            "Acceleration: $acceleration ==> ${accelerationPower * smoothedCurrentSpeed} ==> $power"
        )

        val lastSample = track.getLastSample()!!
        if (lastSample.cadence != null && lastSample.cadence < 30)
            return 0.0
        if (power < 0)
            return 0.0

        return power
    }

    private fun calcAcceleration(track: Track): Double {
        val trackSize = track.samples.size
        if (trackSize < ACCELERATION_WINDOW_SIZE)
            return 0.0
        val times =
            track.samples.subList(trackSize - ACCELERATION_WINDOW_SIZE, trackSize)
                .map { it.millisSinceStart / 1000.0 }
                .toDoubleArray()
        val speeds =
            track.samples.subList(trackSize - ACCELERATION_WINDOW_SIZE, trackSize).map { it.speed }
                .toDoubleArray()
        return bestFitSlope(times, speeds)
    }

    private fun calcGrade(track: Track): Double {
        val trackSize = track.samples.size
        if (trackSize < GRADE_WINDOW_SIZE)
            return 0.0
        val distances =
            track.samples.subList(trackSize - GRADE_WINDOW_SIZE, trackSize).map { it.distance }
                .toDoubleArray()
        val speeds =
            track.samples.subList(trackSize - GRADE_WINDOW_SIZE, trackSize).map { it.speed }
                .toDoubleArray()
        return bestFitSlope(distances, speeds)
    }

    private fun averageSpeedInWindow(track: Track, centerFromEnd: Int, windowSize: Int): Double {
        val start = track.samples.size - centerFromEnd - (windowSize / 2)
        if (start < 0) {
            return 0.0
        }

        val end = min(start + windowSize, track.samples.size)

        var sum = 0.0
        for (i in start until end) {
            sum += track.samples[i].speed
        }
        return sum / (end - start)
    }

    private fun bestFitSlope(xs: DoubleArray, ys: DoubleArray): Double {
        val xsAvg = xs.average()

        val div = (xsAvg * xsAvg) - elementProduct(xs, xs).average()
        if (div == 0.0)
            return 0.0

        return (xsAvg * ys.average() - elementProduct(xs, ys).average()) / div
    }

    private fun elementProduct(xs: DoubleArray, ys: DoubleArray): Array<Double> {
        return Array(xs.size) { i -> xs[i] * ys[i] }
    }

    private fun averageAltitudeInWindow(track: Track, centerFromEnd: Int, windowSize: Int): Double {
        val start = track.samples.size - centerFromEnd - (windowSize / 2)
        if (start < 0) {
            return 0.0
        }

        val end = min(start + windowSize, track.samples.size)

        var sum = 0.0
        for (i in start until end) {
            sum += track.samples[i].altitude
        }
        return sum / (end - start)
    }

    private fun distanceAt(track: Track, indexFromEnd: Int): Double {
        val start = track.samples.size - indexFromEnd
        if (start < 0) {
            return 0.0
        }
        return track.samples[start].distance
    }

    private fun timeAt(track: Track, indexFromEnd: Int): Int {
        val start = track.samples.size - indexFromEnd
        if (start < 0) {
            return 0
        }
        return track.samples[start].millisSinceStart / 1000
    }
}

fun main() {
//    println(PowerCalculation().calcEliteTravelFluidYellowPower(16.6))
//    println(PowerCalculation().calcEliteTravelFluidYellowPower(15.279))
}
package com.github.basva923.garminphoneactivity.model

class ZoneFinder(private val zonesPercents: Array<Pair<Int, Int>>, var unitValue: Int) {

    fun valueToZone(value: Int): Int {
        for (nr in zonesPercents.indices) {
            val zoneStart = zonesPercents[nr].first * unitValue / 100
            val zoneEnd = zonesPercents[nr].second * unitValue / 100
            if (value in zoneStart until zoneEnd) {
                return nr + 1
            }
        }
        return 0
    }

    fun valueToZone(value: Double): Int {
        return valueToZone(value.toInt())
    }
}
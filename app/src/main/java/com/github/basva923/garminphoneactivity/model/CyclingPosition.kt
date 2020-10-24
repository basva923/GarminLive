package com.github.basva923.garminphoneactivity.model

enum class CyclingPosition(val aeroCoefficient: Double) {
    TOPS(0.408),
    HOODS(0.324),
    DROPS(0.307),
    AERO_BARS(0.2914)
}

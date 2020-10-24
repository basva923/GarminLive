package com.github.basva923.garminphoneactivity.model

enum class TrackSurface(val rollCoefficient: Double) {
    CONCRETE(0.0020),
    ASPHALT(0.0050),
    GRAVEL(0.0060),
    GRASS(0.0070),
    OFF_ROAD(0.0200),
    SAND(0.0300)
}

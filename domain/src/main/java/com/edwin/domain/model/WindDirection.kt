package com.edwin.domain.model

import kotlin.math.round

enum class WindDirection(val direction: String) {
    NORTH("N"),
    NORTH_EAST("NE"),
    EAST("E"),
    SOUTH_EAST("SE"),
    SOUTH("S"),
    SOUTH_WEST("SW"),
    WEST("W"),
    NORTH_WEST("NW");

    companion object {
        fun degreeToDirection(degree: Int): WindDirection {
            val numberOfDirections = values().size
            val degreesPerDirection = 360f / numberOfDirections
            val index = round(degree / degreesPerDirection) % numberOfDirections
            return values()[index.toInt()]
        }
    }
}
package com.chhemsronglong.together.util

import android.location.Location
import com.chhemsronglong.together.DataModel.Post

object CalculateDistance {

    fun createLocation(longTitude : Double, latitude : Double) : Location{
        var location =   Location("point")
        location.latitude = latitude
        location.longitude = longTitude
        return  location
    }

    fun getDistanceKm(it: Post, withLocation : Location): Float{
        val locationB = Location("point B")

        locationB.latitude = it.latitute
        locationB.longitude = it.longitude

       // showVLog(it.locationString)

        return withLocation.distanceTo(locationB) / 1000
    }

}


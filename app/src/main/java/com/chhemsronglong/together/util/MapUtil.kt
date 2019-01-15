package com.chhemsronglong.together.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.io.IOException


// Get Address from Google MAP by Location
fun Any.getAddress(latLng: LatLng,context : Context): Triple<String, Address?,String> {
    // 1
    val geocoder = Geocoder(context)
    val addresses: List<Address>?
    var address: Address? = null
    var addressText = ""
    var placeName = ""

    try {
        // 2
        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        // 3
        if (null != addresses && !addresses.isEmpty()) {
            address = addresses[0]
            addressText = address.getAddressLine(0)
            placeName = address.featureName
        }
    } catch (e: IOException) {
        Log.e("MapsActivity", e.localizedMessage)
    }

    return Triple(addressText,address,placeName)
}
package com.chhemsronglong.together.HomeTabFragement

//import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.chhemsronglong.together.ActivitiePosting.PostingActivity
import com.chhemsronglong.together.HomeTabbar.HomeTabFragment
import com.chhemsronglong.together.ProfileTab.ProfileTabFragment
import com.chhemsronglong.together.R
import com.chhemsronglong.together.util.getAddress
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_buttom_navigation.*
import kotlinx.android.synthetic.main.activity_posting.*
import org.jetbrains.anko.toast
import showVLog

class ButtomNavigationActivity : AppCompatActivity() {

    private val TAG_FRAGMENT_PROFILE = "fragment_one"
    private val TAG_FRAGMENT_HOME = "fragment_two"
    private val TAG_FRAGMENT_MESSAGE = "fragment_three"

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    private lateinit var lastLocation: Location
    private var doubleBackToExitPressedOnce = false

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
             //   message.setText(R.string.title_home)
                var fragment = supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_HOME)
                if (fragment == null) {
                    fragment = HomeTabFragment()
                }
                openFragment(fragment,TAG_FRAGMENT_HOME)

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_profile -> {
               // message.setText(R.string.title_profile)
                var fragment = supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_PROFILE)
                if (fragment == null) {
                    fragment = ProfileTabFragment()
                }
                openFragment(fragment,TAG_FRAGMENT_PROFILE)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
               // message.setText(R.string.title_notifications)
                var fragment = supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_MESSAGE)
                if (fragment == null) {
                    fragment = MessageTabFragement()
                }
                openFragment(fragment,TAG_FRAGMENT_MESSAGE)
//                var messageTabFragment = MessageTabFragement()
//                openFragment(messageTabFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buttom_navigation)

        println("selected : ${navigation.selectedItemId}")
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = R.id.navigation_home

        setupLocationRequest()

    }

    fun setupLocationRequest(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                showVLog("ocation change")

                if (lastLocation != p0.lastLocation) {
                    lastLocation = p0.lastLocation
                    var fragment = supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_HOME) as HomeTabFragment
                    fragment?.let {
                        sharedLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                        it.objectUpdated(lastLocation)
                    }
                }
              //  placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))

            }
        }
        createLocationRequest()
//        checkIfPermissionNeeded()

    }

    fun checkIfPermissionNeeded() {
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    ButtomNavigationActivity.LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            showVLog("location change")
//            showVLog(location.toString())
            location?.let {
                lastLocation = location

                var fragment  = supportFragmentManager.findFragmentByTag(TAG_FRAGMENT_HOME) as HomeTabFragment
                fragment?.let{
                    sharedLocation = LatLng(location.latitude, location.longitude)
                    it.objectUpdated(location)
            }

        }

        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            if (grantResults.size >= 0){
                showVLog("premission grated")
                checkIfPermissionNeeded()
            }else{
                showVLog("permission deny")
            }
        }else if(requestCode == REQUEST_CHECK_SETTINGS){
            showVLog("location request failed")
        }
    }


    private fun createLocationRequest() {
        // 1
        locationRequest = LocationRequest()
        // 2
        locationRequest.interval = 10000
        // 3
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        // 4
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        // 5
        task.addOnSuccessListener {
            locationUpdateState = true
            checkIfPermissionNeeded()
        }
        task.addOnFailureListener { e ->
            // 6
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this@ButtomNavigationActivity,
                            ButtomNavigationActivity.REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    private fun openFragment(fragment: Fragment, fragementTag : String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment,fragementTag)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    // prevent back pressed.
    // pressed twice to quite the finish.
    //https://stackoverflow.com/questions/3226495/how-to-exit-from-the-application-and-show-the-home-screen
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish()
            return
        }

        this.doubleBackToExitPressedOnce = true
        toast("Please click BACK again to exit")

        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val PLACE_PICKER_REQUEST = 3
        private const val REQUEST_CHECK_SETTINGS = 2

        var sharedLocation : LatLng? = null

            fun start (context : Context){
                var intent = Intent(context,ButtomNavigationActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent)

        }
    }

}

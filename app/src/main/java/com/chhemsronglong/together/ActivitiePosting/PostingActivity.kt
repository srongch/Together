package com.chhemsronglong.together.ActivitiePosting

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.chhemsronglong.together.BaseActivity
import com.chhemsronglong.together.DataModel.Post
import com.chhemsronglong.together.DatePickerFragment
import com.chhemsronglong.together.MainActivity.MainActivity
import com.chhemsronglong.together.R
import com.chhemsronglong.together.TimePickerFragment
import com.chhemsronglong.together.util.getAddress
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import convertDateFormat
import convertTimeFormat
import kotlinx.android.synthetic.main.activity_post_detail1.*
import kotlinx.android.synthetic.main.activity_posting.*
import kotlinx.android.synthetic.main.activity_posting_main.*
import org.jetbrains.anko.toast
import showVLog
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PostingActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    // Get a reference to the database service
    private lateinit var database :  DatabaseReference
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    // 1
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    private lateinit var lastLocation: Location
    private var selectedLocation : LatLng? = null

    private  var saveMarket: Marker? = null
    private var imageUpload : Bitmap ? = null
    private var imageString : String? = null
    private  var locationName : String = ""
    private  var locality : String = ""

    var activitySelectedType = 0

    // [START storage_field_declaration]
    lateinit var storage: FirebaseStorage
    // [END storage_field_declaration]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posting_main)

        setupOptionButton()

        closeButton.setOnClickListener {
            onBackPressed()
        }

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        delete_button.visibility = View.GONE
        progressBar = progress_bar
        hideProgressDialog()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()

        //change button click
        mapButton.setOnClickListener{
            loadPlacePicker()
        }

        // date calender click
        dateButton.setOnClickListener {
            showDatePickerDialog()
        }

        // time calender click
        timeButton.setOnClickListener {
            showTimePickerDialog()
        }

        // image button click
        imageView.setOnClickListener {
            showPictureDialog()
        }

        // small delete button on image button click
        delete_button.setOnClickListener {
            resetImage()
        }

        // post button click
        addButton.setOnClickListener {
            posting()
        }

        // set date
        setInitDate()

        //create location request.
        createLocationRequest()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
            }
        }

    }


    private  fun setInitDate(){
        var timeStamp = System.currentTimeMillis()
        date_input_editor.setText(convertDateFormat(timeStamp))
        time_input_editor.setText(convertTimeFormat(timeStamp))

    }


    private fun loadPlacePicker() {
        val builder = PlacePicker.IntentBuilder()

        if (selectedLocation != null) builder.setLatLngBounds(LatLngBounds(selectedLocation,selectedLocation))

        try {
            startActivityForResult(builder.build(this@PostingActivity), PLACE_PICKER_REQUEST)
        } catch (e: GooglePlayServicesRepairableException) {
            e.printStackTrace()
        } catch (e: GooglePlayServicesNotAvailableException) {
            e.printStackTrace()
        }
    }


  private fun showTimePickerDialog() {

        var timePicker = TimePickerFragment()
        timePicker.onTimeSelected { timeString ->
            time_input_editor.setText(timeString)
            }
        timePicker.show(supportFragmentManager, "timePicker")
    }

   private fun showDatePickerDialog() {

        var datePicker = DatePickerFragment()
        datePicker.onDateSelected {
            date_input_editor.setText(it)
        }
        datePicker.show(supportFragmentManager, "datePicker")
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        // permission request code
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                locationUpdateState = true
                startLocationUpdates()
            }
            return
        }

        // google place result
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                val place = PlacePicker.getPlace(this, data)
                var addressText = place.name.toString()
                addressText += "\n" + place.address.toString()
                locationName = place.name.toString()
//                locality = place.address.
                placeMarkerOnMap(place.latLng)
            }
            return
        }

        // get image from gallery
        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data
                try
                {
                    imageUpload = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    Toast.makeText(this@PostingActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
                    imageView!!.setImageBitmap(imageUpload)
                    delete_button.visibility = View.VISIBLE

                }
                catch (e: IOException) {
                    e.printStackTrace()
                  //  Toast.makeText(this@MainActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }

            }

        }

        // get image from Camera
        else if (requestCode == CAMERA)
        {
            if (data != null) return

            imageUpload = data!!.extras!!.get("data") as Bitmap
            imageView!!.setImageBitmap(imageUpload)
            Toast.makeText(this@PostingActivity, "Image Saved!", Toast.LENGTH_SHORT).show()
            delete_button.visibility = View.VISIBLE
        }
    }

    // delet image
    private fun resetImage(){
        delete_button.visibility = View.GONE
        imageView.setImageResource(R.mipmap.postin_add_image)
        imageUpload = null

    }


    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> dispatchTakePictureIntent()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)
    }

    // show take picture
    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }


    // Handle Firebase Onetime Posting

    private fun posting(){

        if (selectedLocation == null){
            toast("Please select location")
            return
        }

        title_input_editor.text!!.isEmpty().let { check ->
            if (check) {
            title_input_editor.error = "input title"
            return
            }
        }

        person_input_editor.text!!.isEmpty().let { check ->
            if (check) {
                person_input_editor.error = "Please input number"
                return
            }
        }

        note_input_editor.text!!.isEmpty().let { check ->
            if (check) {
                note_input_editor.error = "Please input notes"
                return
            }
        }

        showProgressDialog()
        if (imageUpload != null){
            //upload image than
            uploadImage()
        }else{
            saveToDatabase(database,null)
        }

    }

    fun saveToDatabase(firebaseData: DatabaseReference,imageString : String?) {

        val formatter = SimpleDateFormat("dd-MM-yyyy HH:SS", Locale.ENGLISH)
        val dateInString = date_input_editor.text.toString() + " "+time_input_editor.text.toString()
        val date = formatter.parse(dateInString)

        val cal = Calendar.getInstance()
        cal.time  = date

        database.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)

        var key = firebaseData.child("posts").push().key
        showVLog(FirebaseAuth.getInstance().currentUser!!.uid)
        var post = Post(
                activitySelectedType,
                selectedLocation?.latitude!!,
                selectedLocation?.longitude!!,
                location_text4.text.toString(),
                locality.toLowerCase(),
                locationName,
                title_input_editor.text.toString(),
                person_input_editor.text.toString(),
                cal.timeInMillis,
                date_input_editor.text.toString(),
                time_input_editor.text.toString(),
                note_input_editor.text.toString(),
                imageString,
                key!!,
                FirebaseAuth.getInstance().currentUser?.uid,
                MainActivity.getSharedUser().name,
                MainActivity.getSharedUser().profile!!

        )

        showProgressDialog()
        firebaseData.child("posts").child(key).setValue(post)
                .addOnCanceledListener {  hideProgressDialog()}
                .addOnCompleteListener {
                    toast("completed")
                hideProgressDialog()
                donePosting()}

        }

    fun donePosting(){
        val returnIntent = Intent()
        returnIntent.putExtra("result", "done")
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }


    fun uploadImage(){
        // Create a storage reference from our app
        val storageRef = storage.reference

        // Create a random number for image name
        val imagePath= UUID.randomUUID().toString()+ ".jpg"
        val mountainsRef = storageRef.child(imagePath)

        val baos = ByteArrayOutputStream()
        imageUpload?.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val data = baos.toByteArray()

        var uploadTask = mountainsRef.putBytes(data)

        val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation mountainsRef.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                showVLog("Uri: " + downloadUri)
                saveToDatabase(database,downloadUri.toString());
                hideProgressDialog()
            } else {
                // Handle failures
                // ...
                toast("Image uploaded failed.");
                hideProgressDialog()
            }
        }

    }

    


    // set activite type button selected and deselected when click
    protected fun setupOptionButton() {


        dining_button.setOnClickListener {
            if (!dining_button.isSelected){
                dining_button.isSelected = !dining_button.isSelected
                setImageToButton(0)
            }
        }

        movie_button.setOnClickListener {
            if (!movie_button.isSelected){
                movie_button.isSelected = !movie_button.isSelected
                setImageToButton(1)
            }
        }

        trip_button.setOnClickListener {
            if (!trip_button.isSelected){
                trip_button.isSelected = !trip_button.isSelected
                setImageToButton(2)
            }
        }

        sport_button.setOnClickListener {
            if (!sport_button.isSelected){
                sport_button.isSelected = !sport_button.isSelected
                setImageToButton(3)
            }
        }

        event_button.setOnClickListener {
            if (!event_button.isSelected){
                event_button.isSelected = !event_button.isSelected
                setImageToButton(4)
            }
        }

        //initail button click to dinning
        setImageToButton(0)

    }

    // set image button based on status
    private fun setImageToButton(buttonIndex: Int){

        activitySelectedType = buttonIndex
        dining_button.setImageResource(if (buttonIndex == 0) R.mipmap.filter_dining_color else R.mipmap.filter_dining_bw)
        movie_button.setImageResource( if (buttonIndex == 1) R.mipmap.filter_movie_color else R.mipmap.filter_movie_bw)
        trip_button.setImageResource(if (buttonIndex == 2) R.mipmap.filter_trip_color else R.mipmap.filter_travel_bw)
        sport_button.setImageResource(if (buttonIndex == 3) R.mipmap.filter_sport_color else R.mipmap.filter_sport_bw)
        event_button.setImageResource(if (buttonIndex == 4) R.mipmap.filter_event_color else R.mipmap.filter_event_bw)

        if (buttonIndex != 0) dining_button.isSelected = false
        if (buttonIndex != 1) movie_button.isSelected = false
        if (buttonIndex != 2) trip_button.isSelected = false
        if (buttonIndex != 3) sport_button.isSelected = false
        if (buttonIndex != 4) event_button.isSelected = false

    }


    //MAP Handling
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.getUiSettings().setZoomControlsEnabled(true)
        mMap.setOnMarkerClickListener(this)
        setUpMap()
    }

    private fun setUpMap() {

        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            // 3
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            }
        }

        mMap.isMyLocationEnabled = true


    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState) {
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        //1
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        //2
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        startLocationUpdates()
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
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            // 6
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this@PostingActivity,
                            REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        return false
    }

    private fun placeMarkerOnMap(location: LatLng) {
        // 1
        val markerOptions = MarkerOptions().position(location)
        // 2
        val titleStr = getAddress(location,this)  // add these two lines
        location_text4.text = titleStr.first
        locality = titleStr.second?.locality!!

//        markerOptions.draggable(true)
        selectedLocation = location

        if (saveMarket == null){
            saveMarket = mMap.addMarker(markerOptions)
        }else{
            saveMarket?.title = titleStr.first
            saveMarket?.position = location
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))

    }



    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val PLACE_PICKER_REQUEST = 3
        private const val REQUEST_CHECK_SETTINGS = 2
        private const val GALLERY = 4
        private const val CAMERA = 5

        fun start (context : Context){
            var intent = Intent(context, PostingActivity::class.java)
            context.startActivity(intent)
        }

    }







}

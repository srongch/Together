package com.chhemsronglong.together.Common

import android.graphics.Bitmap
import android.net.Uri
import com.chhemsronglong.together.Firebase.database
import com.chhemsronglong.together.Firebase.storage
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.*


fun uploadImage(path : String,bitmap: Bitmap,callback: (status: Boolean,downloadUrl : String?)-> Unit) {
    //do stuff
    val storageRef = FirebaseStorage.getInstance().reference
    val imagePath= UUID.randomUUID().toString()+ ".jpg"
    val pictureRef = storageRef.child("$path/$imagePath")

// Create a reference to 'images/imagePath.jpg'
    val pictureImagesRef = storageRef.child("images/$imagePath")

// While the file names are the same, the references point to different files
    pictureRef.name == pictureImagesRef.name    // true
    pictureRef.path == pictureImagesRef.path    // false

    // Get the data from an ImageView as bytesbitmap

    val baos = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 50, baos)
    val data = baos.toByteArray()

    var uploadTask = pictureRef.putBytes(data)

    val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
        if (!task.isSuccessful) {
            task.exception?.let {
                throw it
            }
        }
        return@Continuation pictureRef.downloadUrl
    }).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val downloadUri = task.result
            callback(true,downloadUri.toString())
        } else {
            // Handle failures
            // ...
            callback(false,null)
        }
    }
}







package com.chhemsronglong.together.DataModel

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.Exclude

data class UserModel(
        var name: String = "",
        var email: String = "",
        var profile: String = defaultProfilePic,
        var userId: String = "",
        var devideToken : String = "",
        @get:Exclude val id: String = ""
        ){
        companion object {
                private const val defaultProfilePic = "https://firebasestorage.googleapis.com/v0/b/together-1540688691564.appspot.com/o/default%2Fdefault_profile_pic.png?alt=media&token=85db238e-2db0-4590-9cd3-be582c8fd204"
                private  var sharedUser : UserModel? = null
                fun setSharedUser(userModel : UserModel) {
                        sharedUser = userModel
                }

                fun getSharedUser() : UserModel{
                        return sharedUser!!
                }


        }
}




import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.graphics.Color
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.chhemsronglong.together.R
import java.util.*
import com.bumptech.glide.request.RequestOptions
import java.text.SimpleDateFormat


fun Any.showVLog(log: String) = Log.v(this::class.java.simpleName, log)

fun Any.showELog(log: String) = Log.e(this::class.java.simpleName, log)

fun Any.showDLog(log: String) = Log.d(this::class.java.simpleName, log)

fun Any.showILog(log: String) = Log.i(this::class.java.simpleName, log)

fun Any.showWLog(log: String) = Log.w(this::class.java.simpleName, log)

fun <T> LiveData<T>.reobserve(owner: LifecycleOwner, observer: Observer<T>) {
    removeObserver(observer)
    observe(owner, observer)
}

//https://code.luasoftware.com/tutorials/android/android-livedata-observe-once-only-kotlin/
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun bindImageFromUrl(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(view.context)
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)

    }
}

fun bindImageFromUrlHideWhenEror(view: ImageView, imageUrl: String?) {
    if (!imageUrl.isNullOrEmpty()) {
        view.visibility = View.VISIBLE
        Glide.with(view.context)
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
    }else{
        view.visibility = View.GONE
    }
}

fun bindImageFromUrlWithPlaceHolder(view: ImageView, imageUrl: String?, placeHolder : Int) {
    if (!imageUrl.isNullOrEmpty()) {
        val options  = RequestOptions()
                .placeholder(placeHolder)
                .error(placeHolder)

        Glide.with(view.context)
                .load(imageUrl)
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
    }else{
        view.setImageResource(placeHolder)
    }
}

fun convertTimeFormat(time: Long?): String {
    if (time != null){
        val format = SimpleDateFormat("HH:MM")
        val netDate = Date(time)
        return format.format(time)
    }else{
        return  "00:00"
    }
}

fun convertDateFormat(time: Long?): String {
    if (time != null){
        val format = SimpleDateFormat("dd-MM-yyyy")
        val netDate = Date(time)
        return format.format(time)
    }else{
        return  "00.00.0000"
    }
}

fun isSameDay (time1 : Long, time2 : Long) : Boolean {
    val fmt = SimpleDateFormat("yyyyMMdd")
    return fmt.format(Date(time1)) == fmt.format(Date(time2))
}


//Activity name by type
val activityByType: (type : Int) -> String = {
    when( it % 5){
    0 ->   "DINNING"
    1 -> "MOVIE"
    2 -> "TRIP"
    3 -> "SPORT"
    else ->  "EVENT"
} }


//Color by type
val colorByType: (type : Int) -> String = {
    when( it % 5){
        0 ->    "#FF7956"
        1 -> "#FDB62F"
        2 -> "#366DB6"
        3 -> "#3A3A58"
        else ->  "#54B265"
    } }



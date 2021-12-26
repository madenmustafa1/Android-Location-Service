package your package

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.lang.Exception
import java.lang.UnsupportedOperationException
import java.text.SimpleDateFormat
import java.util.*

class LocationServiceK : Service() {
    private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)

            try {
                if (locationResult != null && locationResult.lastLocation != null) {
                    val latitude = locationResult.lastLocation.latitude
                    val longitude = locationResult.lastLocation.longitude
                    //println("Location KService: $latitude $longitude")
                }
            } catch (e: Exception) {
                println("Service Error 81: " + e
            }
        }
    }


    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    @SuppressLint("MissingPermission")
    private fun startLocationService() {
        val channelId = "location_notification_channel"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val resultIntent = Intent()
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            resultIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(
            applicationContext,
            channelId
        )
        builder.setSmallIcon("your app icon")
        builder.setContentTitle("Location service")
        builder.setDefaults(NotificationCompat.DEFAULT_ALL)
        builder.setContentText("Running")
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(false)
        builder.priority = NotificationCompat.PRIORITY_MAX
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                val notificationChannel = NotificationChannel(
                    channelId,
                    "Location Service",
                    NotificationManager.IMPORTANCE_HIGH
                )
                notificationChannel.description = "This channel is used by service"
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }
        val locationRequest = LocationRequest()
        locationRequest.interval = 60000
        locationRequest.fastestInterval = 48000
        //locationRequest.interval = 4000 -- 60000
        //locationRequest.fastestInterval = 2000 -- 48000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        //PRIORITY_HIGH_ACCURACY

         LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        startForeground(LocationServiceConstants.LOCATION_SERVICE_ID, builder.build())
    }

    private fun stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this)
            .removeLocationUpdates(locationCallback)
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.action
            if (action != null) {
                if (action == LocationServiceConstants.ACTION_START_LOCATION_SERVICE) {
                    startLocationService()
                } else if (action == LocationServiceConstants.ACTION_STOP_LOCATION_SERVICE) {
                    stopLocationService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}

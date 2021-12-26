package your package;

class MainActivity : AppCompatActivity(){
 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
      

        stopLocationService()
    }

    override fun onDestroy() {
        super.onDestroy()
        startLocationService()
    }

    private fun isLocationServiceRunning(): Boolean {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        if (activityManager != null) {
            for (service in activityManager.getRunningServices(Int.MAX_VALUE)) {
                if (LocationServiceK::class.java.name == service.service.className) {
                    if (service.foreground) {
                        return true
                    }
                }
            }
            return false
        }
        return false
    }

    private fun startLocationService() {
        if (!isLocationServiceRunning()) {
            val intent = Intent(
                applicationContext,
                LocationServiceK::class.java
            )
            intent.action = "startLocationService"
            startService(intent)
            Toast.makeText(applicationContext, "LocationService Start", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopLocationService() {
        if (isLocationServiceRunning()) {
            val intent = Intent(
                applicationContext,
                LocationServiceK::class.java
            )
            intent.action = "stopLocationService";
            startService(intent)
            Toast.makeText(applicationContext, "Location service stop", Toast.LENGTH_LONG).show()
        }
    }
}

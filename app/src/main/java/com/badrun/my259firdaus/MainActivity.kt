package com.badrun.my259firdaus


import android.Manifest
import android.app.ActivityManager
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.badrun.my259firdaus.activity.LoginActivity
import com.badrun.my259firdaus.activity.WelcomeActivity
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.fragment.AkunFragment
import com.badrun.my259firdaus.fragment.HomeFragment
import com.badrun.my259firdaus.fragment.NewsFragment
import com.badrun.my259firdaus.fragment.QuranFragment
import com.badrun.my259firdaus.helper.PrayerService
import com.badrun.my259firdaus.helper.SharedNewsViewModel
import com.badrun.my259firdaus.helper.SharedPref
import com.badrun.my259firdaus.model.ResponseGuru
import com.badrun.my259firdaus.model.ResponseOrtu
import com.badrun.my259firdaus.model.ResponseSantri
import com.badrun.my259firdaus.model.ResponseTamu
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100

    }

    private var dataString = ""

    private lateinit var bundle: Bundle

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    val sharedViewModelNews by viewModels<SharedNewsViewModel>()
    private val homeFragment = HomeFragment()
    private val quranFragment = QuranFragment()
    private val akunFragment = AkunFragment()
    private val newsFragment = NewsFragment()

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var buttonNav : BottomNavigationView
    private val fm = supportFragmentManager
    private var active : Fragment = homeFragment

    private lateinit var menu: Menu
    private lateinit var menuItem: MenuItem

    private lateinit var s : SharedPref


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        s = SharedPref(this)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        val sharedPrefs = this.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isNotifEnabled = sharedPrefs.getBoolean("NotificationEnabled", true)
        // Cek jika service sudah berjalan, jangan jalankan lagi
        if (!isServiceRunning(PrayerService::class.java) && isNotifEnabled) {
            togglePrayerService(true)
        }

        if (!s.getStatusLogin()){
            val i = Intent(this,WelcomeActivity::class.java)
            startActivity(i)
            finish()
        } else {
            initGetUSer()
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    p0 ?: return
                    for (location in p0.locations) {
                        location?.let {
                            processLocation(it)
                        }
                    }
                }
            }

            swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
            swipeRefreshLayout.setOnRefreshListener {
                ambilLokasi()
            }
            bundle = Bundle()
            ambilLokasi()
            createNotificationChannel(this)
        }

    }

    override fun getResources(): Resources {
        val res = super.getResources()
        val config = res.configuration
        val metrics = res.displayMetrics
        val newConfig = Configuration(config)

        // Reset the font scale to default (1.0)
        newConfig.fontScale = 1.0f

        return Resources(assets, metrics, newConfig)
    }

    private fun processLocation(location: Location) {
        val latitude = location.latitude
        val longitude = location.longitude
        dataString = "$latitude,$longitude"
        bundle.putString("KEY_DATA_STRING", dataString)

        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        val address = addresses?.get(0)
        val subLocality = address?.locality ?: "Tidak Diketahui"
        val locality = address?.subAdminArea ?: "Tidak Diketahui"
        bundle.putString("KEY_SUB_LOCALITY", subLocality)
        bundle.putString("KEY_LOCALITY", locality)

        setUpBottomNav()
        swipeRefreshLayout.isRefreshing = false
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 600000 // 10 menit
            fastestInterval = 300000 // 5 menit
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = activityManager.getRunningServices(Integer.MAX_VALUE)
        for (serviceInfo in services) {
            if (serviceClass.name == serviceInfo.service.className) {
                return true
            }
        }
        return false
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "Shalat Notification"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("notif_adzan", channelName, importance)
            val notificationManager = context. getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun checkPermissions(): Boolean {
        val locationPermissionGranted = (
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                )
        val notificationPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        return locationPermissionGranted && notificationPermissionGranted
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun ambilLokasi() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(this, "Lokasi tidak ditemukan", Toast.LENGTH_LONG).show()
                    } else {
                        processLocation(location)
                    }
                }
            } else {
                showLocationSettingsDialog()
            }
        } else {
            requestPermission()
        }
    }

    private fun requestPermission() {
        val permissionsToRequest = mutableListOf<String>()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED })) {
                ambilLokasi()
            } else {
                // Izin tidak diberikan, beri tahu pengguna
                Toast.makeText(this, "Izin diperlukan untuk menjalankan aplikasi ini", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun togglePrayerService(start: Boolean) {
        val serviceIntent = Intent(this, PrayerService::class.java)
        if (start) {
            if (hasScheduleExactAlarmPermission()) {
                startService(serviceIntent)
            } else {
                requestScheduleExactAlarmPermission()
            }
        } else {
            // Menghentikan layanan dan membatalkan alarm
            serviceIntent.action = PrayerService.ACTION_CANCEL_ALARMS
            startService(serviceIntent)
            stopService(serviceIntent)
        }
    }

    private fun hasScheduleExactAlarmPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED
        }
        return true  // Perizinan tidak diperlukan untuk versi Android di bawah S
    }

    private fun requestScheduleExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(this, "Izin untuk menyetel alarm tidak diberikan. Mohon aktifkan izin.", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                return
            }
        }
    }

    private fun showLocationSettingsDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("GPS Ponsel Tidak Aktif")
        dialogBuilder.setMessage("Silakan aktifkan GPS untuk menggunakan aplikasi ini.")
        dialogBuilder.setPositiveButton("Pengaturan") { _, _ ->
            // Buka pengaturan aplikasi untuk mengaktifkan izin lokasi
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
        dialogBuilder.setNegativeButton("Batal") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun bottomNavActive(value:Fragment,item:Int = 0){
        fm.beginTransaction().replace(R.id.container, value).commit()
        menuItem = menu.getItem(item)
        menuItem.isChecked = true
        active = value
    }
    private fun setUpBottomNav(){

        homeFragment.arguments = bundle
        fm.beginTransaction().replace(R.id.container,homeFragment).commit()

        buttonNav = findViewById(R.id.nav_view_container)
        menu = buttonNav.menu
        menuItem = menu.getItem(0)
        menuItem.isChecked = true

        buttonNav.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.navigation_home ->{
                    bottomNavActive(homeFragment)
                }
                R.id.navigation_news -> {
                    bottomNavActive(newsFragment,1)
                }
                R.id.navigation_quran -> {
                    bottomNavActive(quranFragment,2)
                }
                R.id.navigation_akun -> {
                    bottomNavActive(akunFragment,3)
                }
            }
            false }
    }

    private fun initGetUSer(){
       val user = s.getUser()

        if (user == null) {
            Toast.makeText(this, "Session expired, please login again", Toast.LENGTH_SHORT).show()

            s.deleteShared()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            return
        }

        when(user!!.role){
            2 -> {
               ApiConfig.create(this).getGuru(user.email).enqueue(object : Callback<ResponseGuru> {
                   override fun onResponse(call: Call<ResponseGuru>,response: Response<ResponseGuru>) {

                       if (response.isSuccessful){
                           val res = response.body()
                           if (res?.data != null){
                               s.setGuru(res.data)
                           }
                       }
                   }

                   override fun onFailure(call: Call<ResponseGuru>, t: Throwable) {
                       Log.e("bdr", "get data Guru Gagal ", )
                   }

               })
            }
            3 -> {
                ApiConfig.create(this).getSantri(user.email).enqueue(object : Callback<ResponseSantri> {
                    override fun onResponse(call: Call<ResponseSantri>,response: Response<ResponseSantri>) {

                        if (response.isSuccessful){
                            val res = response.body()
                            if (res?.data != null){
                                s.setSantri(res.data)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseSantri>, t: Throwable) {
                        Log.e("bdr", "get data Santri Gagal ", )
                    }

                })
            }
            4 -> {
                ApiConfig.create(this).getOrtu(user.email).enqueue(object : Callback<ResponseOrtu> {
                    override fun onResponse(call: Call<ResponseOrtu>,response: Response<ResponseOrtu>) {

                        if (response.isSuccessful){
                            val res = response.body()
                            if (res?.data != null){
                                s.setOrangtua(res.data)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseOrtu>, t: Throwable) {
                        Log.e("bdr", "get data Orangtua Gagal ", )
                    }

                })
            }
            5 -> {
                ApiConfig.create(this).getTamu(user.email).enqueue(object : Callback<ResponseTamu> {
                    override fun onResponse(call: Call<ResponseTamu>,response: Response<ResponseTamu>) {

                        if (response.isSuccessful){
                            val res = response.body()
                            if (res?.data != null){
                                s.setTamu(res.data)
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseTamu>, t: Throwable) {
                        Log.e("bdr", "get data User Gagal", )
                    }

                })
            }
        }
    }

}

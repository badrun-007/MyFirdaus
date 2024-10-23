package com.badrun.my259firdaus.fragment


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.activity.ExamActivity
import com.badrun.my259firdaus.activity.KalenderActivity
import com.badrun.my259firdaus.activity.NotificationActivity
import com.badrun.my259firdaus.activity.PembayaranDetailActivity
import com.badrun.my259firdaus.activity.PendaftaranActivity
import com.badrun.my259firdaus.activity.PerpustakaanActivity
import com.badrun.my259firdaus.activity.ProfilPesantrenActivity
import com.badrun.my259firdaus.activity.ScanTestActivity
import com.badrun.my259firdaus.adapter.AdapterNews
import com.badrun.my259firdaus.adapter.AdapterNewsSearch
import com.badrun.my259firdaus.adapter.AdapterSlider
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.database.NewsEntity
import com.badrun.my259firdaus.database.NewsViewModel
import com.badrun.my259firdaus.database.NewsViewModelFactory
import com.badrun.my259firdaus.database.NotifDatabase
import com.badrun.my259firdaus.helper.DialogUtils
import com.badrun.my259firdaus.helper.NotificationRepository
import com.badrun.my259firdaus.helper.NotificationViewModel
import com.badrun.my259firdaus.helper.SharedNewsViewModel
import com.badrun.my259firdaus.helper.SharedPref
import com.badrun.my259firdaus.helper.SharedSholat
import com.badrun.my259firdaus.helper.SharedSlide
import com.badrun.my259firdaus.model.JadwalSholat
import com.badrun.my259firdaus.model.ResponseJadwalSholat
import com.badrun.my259firdaus.model.ResponseSlide
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {


    //Late Init
    private var dataLokasi: String? = null
    private var dataKecamatan : String? = null
    private var dataKota : String? = null
    private lateinit var btnFormulir : CardView
    private lateinit var btnTransaksi : CardView
    private lateinit var btnPerpus : CardView
    private lateinit var btnProfil : CardView
    private lateinit var textBtn4 : TextView
    private lateinit var imgBtn4 : ImageView

    private lateinit var vpSlider : ViewPager
    private lateinit var rvNews : RecyclerView
    private lateinit var mainAdapter : AdapterNews
    private lateinit var mainAdapterSearh : AdapterNewsSearch
    private lateinit var shubuh : TextView
    private lateinit var dzhur : TextView
    private lateinit var ashar : TextView
    private lateinit var maghrib : TextView
    private lateinit var isya : TextView
    private lateinit var imsak : TextView
    private lateinit var shubuhText : TextView
    private lateinit var dzhurText : TextView
    private lateinit var asharText : TextView
    private lateinit var maghribText : TextView
    private lateinit var isyaText : TextView
    private lateinit var imsakText : TextView
    private lateinit var kalenderH : TextView
    private lateinit var kalenderM : TextView
    private lateinit var lokasi : TextView
    private lateinit var pb :ProgressBar
    private lateinit var time : TextView
    private var tabName: String? = null
    private val handler = Handler(Looper.getMainLooper())
    private val arrSlide = ArrayList<String>()

    private lateinit var s : SharedSholat
    private lateinit var sl : SharedSlide
    private lateinit var sp : SharedPref
    private lateinit var runnable: Runnable

    private lateinit var dataJadwalSholat : JadwalSholat

    private lateinit var notificationRepository: NotificationRepository
    private val notificationViewModel: NotificationViewModel by viewModels()
    private lateinit var tvJlmlhNotif: TextView
    private lateinit var bulletNotif : RelativeLayout
    private lateinit var imgNotif : ImageView

    private lateinit var searchBar : SearchBar
    private lateinit var searchView : SearchView
    private lateinit var rvSearch : RecyclerView
    private lateinit var newsList: MutableList<NewsEntity>
    private var originalList: List<NewsEntity> = mutableListOf()
    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = NotifDatabase.getDatabase(requireContext())
        notificationRepository = NotificationRepository(database.notificationDao())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        dataLokasi = arguments?.getString("KEY_DATA_STRING", "")
        dataKecamatan = arguments?.getString("KEY_SUB_LOCALITY")
        dataKota = arguments?.getString("KEY_LOCALITY")

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        rvNews = view.findViewById(R.id.rv_news)
        vpSlider = view.findViewById(R.id.vp_slider)
        shubuh = view.findViewById(R.id.shubuhTimeValue)
        dzhur = view.findViewById(R.id.dzhuhurTimeValue)
        ashar = view.findViewById(R.id.asharTimeValue)
        maghrib = view.findViewById(R.id.magribTimeValue)
        isya = view.findViewById(R.id.isyaTimeValue)
        imsak = view.findViewById(R.id.imsakTimeValue)
        shubuhText = view.findViewById(R.id.shubuhTime)
        dzhurText = view.findViewById(R.id.dzhuhurTime)
        asharText = view.findViewById(R.id.asharTime)
        maghribText = view.findViewById(R.id.magribTime)
        isyaText = view.findViewById(R.id.isyaTime)
        imsakText = view.findViewById(R.id.imsakTime)
        kalenderH = view.findViewById(R.id.hijriDate)
        kalenderM = view.findViewById(R.id.dateText)
        lokasi = view.findViewById(R.id.tv_location)
        time = view.findViewById(R.id.tv_realtime)
        pb = view.findViewById(R.id.pb_home)
        s = SharedSholat(requireContext())
        sl = SharedSlide(requireActivity())
        btnFormulir = view.findViewById(R.id.btn_psbsatu)
        btnTransaksi = view.findViewById(R.id.btn_psbempat)
        btnProfil = view.findViewById(R.id.btn_psbdua)
        textBtn4 = view.findViewById(R.id.text_btnIcon4)
        imgBtn4 = view.findViewById(R.id.img_btnIcon4)
        btnPerpus = view.findViewById(R.id.btn_psbtiga)
        tvJlmlhNotif = view.findViewById(R.id.tv_jlmlh_notif)
        bulletNotif = view.findViewById(R.id.bullet_notif)
        imgNotif = view.findViewById(R.id.img_notif)
        searchBar = view.findViewById(R.id.searchBar)
        searchView = view.findViewById(R.id.searchView)
        rvSearch = view.findViewById(R.id.recyclerViewSearch)

        initIconMenu()

        btnFormulir.setOnClickListener {
            val i = Intent(requireContext(),PendaftaranActivity::class.java)
            startActivity(i)
        }

        btnProfil.setOnClickListener {
            val i = Intent(requireContext(), ExamActivity::class.java)
            startActivity(i)
        }

        imgNotif.setOnClickListener {
            val i = Intent(requireContext(),NotificationActivity::class.java)
            startActivity(i)
        }

        val configuration = resources.configuration

        // Define the desired font size in SP
        var desiredFontSizeSp = 10f // Example size
        var fontSizeTime = 10f
        var fontSizeDate = 10f
        var fontSizeAdzan = 10f

        when (configuration.fontScale ){
            0.9f -> {
                desiredFontSizeSp = 10f
                fontSizeTime = 43f
                fontSizeDate = 12.5f
                fontSizeAdzan = 10f
            }
            1.0f -> {
                desiredFontSizeSp = 10f
                fontSizeTime = 43f
                fontSizeDate = 12.5f
                fontSizeAdzan = 10f
            }
            1.15f -> {
                desiredFontSizeSp = 8.5f
                fontSizeTime = 48f
                fontSizeDate = 12f
                fontSizeAdzan = 9f
            }
            1.35f -> {
                desiredFontSizeSp = 7.5f
                fontSizeTime = 48f
                fontSizeDate = 11f
                fontSizeAdzan = 8f
            }
            1.5f -> {
                desiredFontSizeSp = 7f
                fontSizeTime = 48f
                fontSizeDate = 11f
                fontSizeAdzan = 7f
            }
            1.6f -> {
                desiredFontSizeSp = 6f
                fontSizeTime = 48f
                fontSizeDate = 10f
                fontSizeAdzan = 6.5f
            }
        }

        lokasi.textSize = desiredFontSizeSp
        kalenderH.textSize = desiredFontSizeSp
        time.textSize = fontSizeTime
        kalenderM.textSize = fontSizeDate
        shubuhText.textSize = fontSizeAdzan
        dzhurText.textSize = fontSizeAdzan
        asharText.textSize = fontSizeAdzan
        maghribText.textSize = fontSizeAdzan
        isyaText.textSize = fontSizeAdzan
        imsakText.textSize = fontSizeAdzan
        shubuh.textSize = fontSizeAdzan
        dzhur.textSize = fontSizeAdzan
        ashar.textSize = fontSizeAdzan
        maghrib.textSize = fontSizeAdzan
        isya.textSize = fontSizeAdzan
        imsak.textSize = fontSizeAdzan

        observeUnreadNotifications()
        initJadwalSholat()
        getNewsss()
        getSlide()
        startClock()
        setUpRecyclerHome()

        // Inflate the layout for this fragment
        setupSearch()

        setUpRvSearch()

        return view
    }

    private fun setupSearch() {
        searchView.setupWithSearchBar(searchBar)
        searchView.editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()

                // Cek apakah ada teks di dalam SearchView
                if (query.isEmpty()) {
                    // Sembunyikan RecyclerView jika tidak ada teks
                    rvSearch.visibility = View.GONE
                } else {
                    // Tampilkan RecyclerView jika ada teks
                    rvSearch.visibility = View.VISIBLE

                    // Batalkan pencarian sebelumnya jika ada
                    searchRunnable?.let { searchHandler.removeCallbacks(it) }

                    // Buat runnable baru untuk menjalankan pencarian dengan jeda waktu
                    searchRunnable = Runnable {
                        performSearch(query)
                    }

                    // Jadwalkan runnable untuk dijalankan setelah 500ms
                    searchHandler.postDelayed(searchRunnable!!, 500)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Called as and when the text is being changed
            }
        })
    }

    private fun performSearch(query: String) {
        val filteredList = originalList.filter { news ->
            news.judul!!.contains(query, ignoreCase = true) || news.deskripsi!!.contains(query, ignoreCase = true)
        }
        showDataSearch(filteredList)
    }

    fun showDataSearch(data: List<NewsEntity>) {
        if (data.isNotEmpty()) {
            mainAdapterSearh.showDataSearch(data)
        }
    }

    private fun observeUnreadNotifications() {
        notificationViewModel.unreadCount.observe(viewLifecycleOwner, Observer { count ->
            updateNotificationBadge(count)
        })
        loadUnreadNotifications()
    }

    private fun loadUnreadNotifications() {
        lifecycleScope.launch {
            val unreadCount = withContext(Dispatchers.IO) {
                notificationRepository.getUnreadNotifications().size
            }
            notificationViewModel.setUnreadCount(unreadCount)
        }
    }

    private fun updateNotificationBadge(count: Int) {
        if (count > 0) {
            tvJlmlhNotif.text = count.toString()
            bulletNotif.visibility = View.VISIBLE
        } else {
            bulletNotif.visibility = View.GONE
        }
    }

    private fun initIconMenu(){
        sp = SharedPref(requireActivity())
        val user = sp.getUser()

        when(user!!.role){
            2 ->{
                textBtn4.text = getString(R.string.testing)
                imgBtn4.setImageResource(R.drawable.icon_test)
                btnTransaksi.setOnClickListener {
                    val i = Intent(requireContext(),ScanTestActivity::class.java)
                    startActivity(i)
                }
            }
            else ->{
                btnTransaksi.setOnClickListener {
                    val i = Intent(requireContext(),PembayaranDetailActivity::class.java)
                    startActivity(i)
                }
            }
        }

        btnPerpus.setOnClickListener {
            val i = Intent(requireContext(),PerpustakaanActivity::class.java)
            startActivity(i)
        }
    }


    private fun getNewsss(){

        val factory: NewsViewModelFactory = NewsViewModelFactory.getInstance(requireActivity())
        val viewModel: NewsViewModel by viewModels {
            factory
        }
        val sharedViewModel by viewModels<SharedNewsViewModel>(ownerProducer = { requireActivity() })

        viewModel.getHeadlineNews().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {
                    is com.badrun.my259firdaus.remote.Result.Loading -> {
                        pb.visibility = View.VISIBLE
                    }
                    is com.badrun.my259firdaus.remote.Result.Success -> {
                        pb.visibility = View.GONE
                        val newsData = result.data
                        showData(newsData)
                        sharedViewModel.setHeadlineNews(result.data)
                        originalList = result.data
                    }
                    is com.badrun.my259firdaus.remote.Result.Error -> {
                        pb.visibility = View.GONE
                        Toast.makeText(
                            context,
                            "Terjadi kesalahan" + result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
            }
        }


    }

    private fun filterResults(query: String) {
        val filteredResults = newsList.filter { it.judul!!.contains(query, ignoreCase = true) }
        showData(filteredResults)
    }

    fun showData(data: List<NewsEntity>) {
        if (data.isNotEmpty()) {
            mainAdapter.showData(data)
        }
    }

    private fun setUpRecyclerHome(){
        mainAdapter = AdapterNews(requireActivity(),arrayListOf())
        rvNews.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mainAdapter
        }
    }

    private fun setUpRvSearch(){
        mainAdapterSearh = AdapterNewsSearch(requireActivity(),arrayListOf())
        rvSearch.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mainAdapterSearh
        }
    }

    private fun getSlide(){
        ApiConfig.create(requireContext()).getSlide().enqueue(object : Callback<ResponseSlide>{
            override fun onResponse(call: Call<ResponseSlide>, response: Response<ResponseSlide>) {
                if (response.isSuccessful){
                    val res = response.body()!!
                    if (res.code == 1){
                        val slide = res.slide
                        sl.deleteSharedSlide()
                        sl.setSlide(slide)

                        displaySlide()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseSlide>, t: Throwable) {
                if (isAdded) return DialogUtils.showServerConnectionFailedDialog(requireActivity())
            }

        })
    }


    private fun displaySlide(){
        val data = sl.getSlide()
        arrSlide.clear()
        arrSlide.add(data!!.image1)
        arrSlide.add(data.image2)
        arrSlide.add(data.image3)

        val adapterSlider = AdapterSlider(arrSlide,activity)
        vpSlider.adapter = adapterSlider
        adapterSlider.startAutoSlider(vpSlider)
    }


    private fun initJadwalSholat(){
        // Memisahkan latitude dan longitude -7.0591149 , 107.7628315
        val coordinates = dataLokasi?.split(",") ?: emptyList()
        val latitude = coordinates.getOrNull(0) ?: ""
        val longitude = coordinates.getOrNull(1) ?: ""
        val kalender = Calendar.getInstance()
        val tahun = kalender.get(Calendar.YEAR)
        val bulan = kalender.get(Calendar.MONTH) + 1
        val hari = kalender.get(Calendar.DAY_OF_MONTH)

        val apiS = "http://api.aladhan.com/v1/"
        //Imsak,Shubuh,Sunrise,Dhuhr,Asr,Maghrib,Sunset,Isha,Midnight = -1,-1,-1,1,3,2,2,4,0
//        ApiConfig.create(requireContext(),apiS).getJSholat(latitude,longitude,5,"-1,-1,-1,1,3,2,2,4,0",bulan,tahun)
        ApiConfig.create(requireContext(),apiS).getJSholatM(tahun,bulan,latitude,longitude,20,"20,null,18").enqueue(object:Callback<ResponseJadwalSholat>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ResponseJadwalSholat>,
                response: Response<ResponseJadwalSholat>
            ) {
                if (response.isSuccessful){
                    val res = response.body()!!
                    if (res.code == 200){
                        val jadwalSho = res.data!![hari-1]
                        s.deleteShared()
                        s.setJadwal(jadwalSho)
                        dataJadwalSholat = jadwalSho
                        displayJadwalSholat(dataJadwalSholat)
                    }
                } else {
                    val data = s.getJadwal()
                    displayJadwalSholat(data!!)
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<ResponseJadwalSholat>, t: Throwable) {
                kalenderH.text = "...., 00 .... ....."
                val data = s.getJadwal()
                displayJadwalSholat(data!!)
                if (isAdded) return DialogUtils.showServerConnectionFailedDialog(requireActivity())
            }

        })
    }

    private fun displayJadwalSholat (jadwalSho : JadwalSholat){
        val kalender = Calendar.getInstance()
        val dayOfWeek = kalender.get(Calendar.DAY_OF_WEEK)

        val dayName = when (dayOfWeek) {
            Calendar.SUNDAY -> "Minggu"
            Calendar.MONDAY -> "Senin"
            Calendar.TUESDAY -> "Selasa"
            Calendar.WEDNESDAY -> "Rabu"
            Calendar.THURSDAY -> "Kamis"
            Calendar.FRIDAY -> "Jumat"
            Calendar.SATURDAY -> "Sabtu"
            else -> ""
        }

        val waktu = jadwalSho.timings

        imsak.text = waktu.Imsak.substring(0,5)
        shubuh.text = waktu.Fajr.substring(0,5)
        dzhur.text = waktu.Dhuhr.substring(0,5)
        ashar.text = waktu.Asr.substring(0,5)
        maghrib.text = waktu.Maghrib.substring(0,5)
        isya.text = waktu.Isha.substring(0,5)


        val kalen = jadwalSho.date.hijri
        val mount = kalen.month.en
        val year = kalen.year
        val day = kalen.day

        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
        val currentDate = dateFormat.format(kalender.time)

        val cleanedKecamatan = dataKecamatan?.replace("Kecamatan ", "Kec. ") ?: ""
        lokasi.text = "$cleanedKecamatan ,\n$dataKota"
        kalenderM.text = "$dayName, $currentDate"
        kalenderH.text = "$day $mount $year H"

    }

    private fun startClock() {
        runnable = object : Runnable {
            override fun run() {
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                val currentTime = sdf.format(System.currentTimeMillis())
                time.text = currentTime
                handler.postDelayed(this, 1000) // Update per second
            }
        }
        handler.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        startClock()
        observeUnreadNotifications()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

}
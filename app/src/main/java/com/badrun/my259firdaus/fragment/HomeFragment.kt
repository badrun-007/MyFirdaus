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
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuAdapter
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.activity.ExamActivity
import com.badrun.my259firdaus.activity.IklanActivity
import com.badrun.my259firdaus.activity.KalenderActivity
import com.badrun.my259firdaus.activity.NotificationActivity
import com.badrun.my259firdaus.activity.PembayaranDetailActivity
import com.badrun.my259firdaus.activity.PendaftaranActivity
import com.badrun.my259firdaus.activity.PerpustakaanActivity
import com.badrun.my259firdaus.activity.ProfilPesantrenActivity
import com.badrun.my259firdaus.activity.ScanTestActivity
import com.badrun.my259firdaus.activity.TentangKamiActivity
import com.badrun.my259firdaus.activity.daring.DaringGuruActivity
import com.badrun.my259firdaus.activity.daring.DaringSantriActivity
import com.badrun.my259firdaus.adapter.AdapterNews
import com.badrun.my259firdaus.adapter.AdapterNewsSearch
import com.badrun.my259firdaus.adapter.AdapterSlider
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.database.NewsEntity
import com.badrun.my259firdaus.database.NewsViewModel
import com.badrun.my259firdaus.database.NewsViewModelFactory
import com.badrun.my259firdaus.database.NotifDatabase
import com.badrun.my259firdaus.databinding.LayoutBottomMenuBinding
import com.badrun.my259firdaus.helper.DialogUtils
import com.badrun.my259firdaus.helper.NotificationRepository
import com.badrun.my259firdaus.helper.NotificationViewModel
import com.badrun.my259firdaus.helper.SharedNewsViewModel
import com.badrun.my259firdaus.helper.SharedPref
import com.badrun.my259firdaus.helper.SharedSholat
import com.badrun.my259firdaus.helper.SharedSlide
import com.badrun.my259firdaus.model.JadwalSholat
import com.badrun.my259firdaus.model.ResponseCheck
import com.badrun.my259firdaus.model.ResponseJadwalSholat
import com.badrun.my259firdaus.model.ResponseSlide
import com.badrun.my259firdaus.model.User
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.ObjectUtils.Null
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
    private lateinit var btnMoreMenu : CardView
    private lateinit var btnPerpus : CardView
    private lateinit var btnProfil : CardView
    private lateinit var textBtn4 : TextView
    private lateinit var imgBtn4 : ImageView

    private var isClockRunning = false

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

    private var bottomSheetDialog: BottomSheetDialog? = null

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
        sp = SharedPref(requireActivity())

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
        btnMoreMenu = view.findViewById(R.id.btn_psbempat)
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

        val tamu = sp.getTamu()
        val user = sp.getUser()
        if (user != null && user.role != 2) {
            if (user.kelas.isNullOrEmpty() && (tamu == null || tamu.kelas.isNullOrEmpty())) {
                showSelectClassDialog(user)
            }
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

        btnMoreMenu.setOnClickListener {
            btnMoreMenu.isEnabled = false  // Nonaktifkan tombol sementara
            showBottomMenu()
            btnMoreMenu.postDelayed({ btnMoreMenu.isEnabled = true }, 500) // Aktifkan kembali setelah 500ms
        }

        btnPerpus.setOnClickListener {
            val i = Intent(requireContext(),PerpustakaanActivity::class.java)
            startActivity(i)
        }

        btnFormulir.setOnClickListener {
            val i = Intent(requireContext(),PendaftaranActivity::class.java)
            startActivity(i)
        }

        btnProfil.setOnClickListener {
            val i = Intent(requireContext(), ProfilPesantrenActivity::class.java)
            startActivity(i)
        }
    }

    private fun showBottomMenu() {

        if (bottomSheetDialog?.isShowing == true) return

        bottomSheetDialog = BottomSheetDialog(requireContext())

        val binding = LayoutBottomMenuBinding.inflate(layoutInflater)
        val user = sp.getUser()

        with(binding) {
            btnExamMts.setOnClickListener {
                startActivity(Intent(requireContext(), ExamActivity::class.java))
                bottomSheetDialog?.dismiss()
            }

            btnSupportPengembang.setOnClickListener {
                startActivity(Intent(requireContext(), IklanActivity::class.java))
                bottomSheetDialog?.dismiss()
            }

            btnTransaksiCard.setOnClickListener {
                startActivity(Intent(requireContext(), PembayaranDetailActivity::class.java))
                bottomSheetDialog?.dismiss()
            }

            btnJadwalPelajaran.setOnClickListener { showCustomDialog() }
            btnLaporanAkademik.setOnClickListener { showCustomDialog() }
            btnBiayaAkademik.setOnClickListener { showCustomDialog() }
            btnQuizPesantren.setOnClickListener { showCustomDialog() }
            btnStreamLensa.setOnClickListener { showCustomDialog() }
            btnRuangAlumni.setOnClickListener { showCustomDialog() }

            btnTentangPengembang.setOnClickListener {
                startActivity(Intent(requireContext(), TentangKamiActivity::class.java))
                bottomSheetDialog?.dismiss()
            }

            if (user!!.role == 2) {
                cardDaring.visibility = View.VISIBLE
                cardTestPpdb.visibility = View.VISIBLE
                cardLapAkademik.visibility = View.GONE
                btnTestPpdb.setOnClickListener {
                    startActivity(Intent(requireContext(), ScanTestActivity::class.java))
                    bottomSheetDialog?.dismiss()
                }
                btnDaringAkademik.setOnClickListener{
                    startActivity(Intent(requireContext(), DaringGuruActivity::class.java))
                    bottomSheetDialog?.dismiss()
                }
            } else {
                val tamu = sp.getTamu()
                if (user.kelas != null || tamu!!.kelas != null){
                    cardDaring.visibility = View.VISIBLE
                    btnDaringAkademik.setOnClickListener{
                        startActivity(Intent(requireContext(), DaringSantriActivity::class.java))
                        bottomSheetDialog?.dismiss()
                    }
                }
                cardTestPpdb.visibility = View.GONE
                cardLapAkademik.visibility = View.VISIBLE
            }
        }

        bottomSheetDialog?.setContentView(binding.root)
        bottomSheetDialog?.show()
    }

    private fun showSelectClassDialog(user: User) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_update_user, null)
        val edtNama = dialogView.findViewById<EditText>(R.id.edtNamaLengkap)
        val spinnerKelas = dialogView.findViewById<Spinner>(R.id.spinnerKelas)

        val kelasList = listOf("7A", "7B", "7C", "8A", "8B", "9A", "9B", "9C")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kelasList)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerKelas.adapter = spinnerAdapter

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Lengkapi Data")
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("Simpan", null)
            .create()

        dialog.setOnShowListener {
            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                val namaLengkap = edtNama.text.toString().trim()
                val kelas = spinnerKelas.selectedItem.toString()

                if (namaLengkap.isEmpty()) {
                    edtNama.error = "Nama tidak boleh kosong"
                    return@setOnClickListener
                }

                // Simpan ke SharedPref
                user.nama = namaLengkap
                user.kelas = kelas
                sp.setUser(user)

                // Kirim ke API
                val userData = mapOf(
                    "name" to namaLengkap,
                    "email" to user.email,
                    "role" to user.role.toString(),
                    "kelas" to kelas
                )

                updateUser(user.user_id, userData)
                Toast.makeText(requireContext(), "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        dialog.show()
    }


    private fun updateUser(userId: Int, userData: Map<String, String>) {
        // Panggil API menggunakan Retrofit
        ApiConfig.create(requireContext()).updateUser(userId, userData).enqueue(object : Callback<ResponseCheck> {
            override fun onResponse(call: Call<ResponseCheck>, response: Response<ResponseCheck>) {
                if (response.isSuccessful) {
                    val res = response.body()
                    Toast.makeText(context, "Berhasil update data", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Gagal update buka ulang aplikasi", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseCheck>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }


    private fun showCustomDialog() {
        // Inflate the custom layout
        val inflater = LayoutInflater.from(requireContext())
        val dialogView: View = inflater.inflate(R.layout.dialog_fitur, null)

        // Create the dialog builder
        val dialogBuilder = android.app.AlertDialog.Builder(requireContext())
        dialogBuilder.setView(dialogView)

        val dialogMessage = dialogView.findViewById<TextView>(R.id.dialogMessage)
        val dialogButton = dialogView.findViewById<Button>(R.id.dialogButtonError)

        val alertDialog = dialogBuilder.create()
        dialogButton.setOnClickListener {
            // Dismiss the dialog
            alertDialog.dismiss()
        }
        alertDialog.show()
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
                if (!isAdded) return

                if (response.isSuccessful) {
                    val res = response.body()
                    if (res != null && res.code == 1) {
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
        arrSlide.clear()
        val data = sl.getSlide() ?: return
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
                    val res = response.body() ?: return
                    if (res.code == 200){
                        val jadwalSho = res.data!![hari-1]
                        s.deleteShared()
                        s.setJadwal(jadwalSho)
                        dataJadwalSholat = jadwalSho
                        displayJadwalSholat(dataJadwalSholat)
                    }
                } else {
                    val data = s.getJadwal()
                    if (data != null) {
                        displayJadwalSholat(data)
                    } else {
                        tampilkanJadwalKosong()
                    }
                }

            }

            @SuppressLint("SetTextI18n")
            override fun onFailure(call: Call<ResponseJadwalSholat>, t: Throwable) {
                kalenderH.text = "...., 00 .... ....."
                val data = s.getJadwal()
                if (data != null) {
                    displayJadwalSholat(data)
                } else {
                    tampilkanJadwalKosong()
                }

                if (isAdded) {
                    DialogUtils.showServerConnectionFailedDialog(requireActivity())
                }
            }


        })
    }

    private fun displayJadwalSholat(jadwalSho: JadwalSholat?) {
        if (jadwalSho == null || jadwalSho.timings == null) {
            tampilkanJadwalKosong()
            return
        }

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

        val waktu = jadwalSho.timings!!

        fun potong5(str: String?): String {
            return if (!str.isNullOrBlank() && str.length >= 5) {
                str.substring(0, 5)
            } else {
                "--:--"
            }
        }

        imsak.text   = potong5(waktu.Imsak)
        shubuh.text  = potong5(waktu.Fajr)
        dzhur.text   = potong5(waktu.Dhuhr)
        ashar.text   = potong5(waktu.Asr)
        maghrib.text = potong5(waktu.Maghrib)
        isya.text    = potong5(waktu.Isha)

        val kalen = jadwalSho.date?.hijri
        val mount = kalen?.month?.en ?: "-"
        val year  = kalen?.year ?: "-"
        val day   = kalen?.day ?: "-"

        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
        val currentDate = dateFormat.format(kalender.time)

        val cleanedKecamatan = dataKecamatan?.replace("Kecamatan ", "Kec. ") ?: ""
        lokasi.text = "$cleanedKecamatan ,\n$dataKota"
        kalenderM.text = "$dayName, \n$currentDate"
        kalenderH.text = "$day $mount $year H"
    }


    @SuppressLint("SetTextI18n")
    private fun tampilkanJadwalKosong() {
        imsak.text = "--:--"
        shubuh.text = "--:--"
        dzhur.text = "--:--"
        ashar.text = "--:--"
        maghrib.text = "--:--"
        isya.text = "--:--"

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

        val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
        val currentDate = dateFormat.format(kalender.time)

        kalenderM.text = "$dayName,\n$currentDate"
        kalenderH.text = "...., 00 .... ....."
    }


    private fun startClock() {
        if (isClockRunning) return

        runnable = object : Runnable {
            override fun run() {
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                sdf.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
                time.text = sdf.format(System.currentTimeMillis())
                handler.postDelayed(this, 1000)
            }
        }

        isClockRunning = true
        handler.post(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
    }

    override fun onResume() {
        super.onResume()
        startClock()
        observeUnreadNotifications()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
        isClockRunning = false
    }

}
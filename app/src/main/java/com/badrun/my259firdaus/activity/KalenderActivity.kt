package com.badrun.my259firdaus.activity

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.adapter.CalendarAdapter
import com.badrun.my259firdaus.adapter.InfoAdapter
import com.badrun.my259firdaus.helper.MonthYearPickerDialogFragment
import com.badrun.my259firdaus.model.KalenderPendidikan
import com.badrun.my259firdaus.model.calendarModel
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class KalenderActivity : AppCompatActivity() {

    private val DAYS_COUNT = 42
    private val calendarList = ArrayList<calendarModel>()
    private val calendar = Calendar.getInstance()
    private var tahun : Int = -1
    private var monthOfYear : Int = -1
    private var adapter : CalendarAdapter = CalendarAdapter(calendarList)

    private lateinit var month  : TextView
    private lateinit var year : TextView
    private lateinit var recyclerView : RecyclerView

    private lateinit var infoRecyclerView: RecyclerView
    private lateinit var infoAdapter: InfoAdapter
    private val infoList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kalender)

        initBtnBack()

        month = findViewById(R.id.month)
        year = findViewById(R.id.year)
        recyclerView = findViewById(R.id.recyclerView)

        infoRecyclerView = findViewById(R.id.recyclerViewInfo)
        infoAdapter = InfoAdapter(infoList)
        infoRecyclerView.layoutManager = LinearLayoutManager(this)
        infoRecyclerView.adapter = infoAdapter

        loadCalendar()

        month.setOnClickListener {
            showMonthYearPicker()
        }
        year.setOnClickListener {
            showMonthYearPicker()
        }



        recyclerView.layoutManager = GridLayoutManager(applicationContext, 7)
        recyclerView.adapter = adapter

    }

    private fun initBtnBack(){
        val toolbar: Toolbar = findViewById(R.id.toolbarDetails)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            //kondisi ketika tombol navigasi di klik
            onBackPressed()
        }
    }

    private fun showMonthYearPicker(){
        val calendar = Calendar.getInstance()
        val dialogFragment = MonthYearPickerDialogFragment.getInstance(calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR))
        dialogFragment.show(supportFragmentManager, null)

        dialogFragment.setOnDateSetListener { year, month ->
            tahun = year
            monthOfYear = month
            loadCalendar()
        }
    }


    private fun loadCalendar() {
        val cells = ArrayList<calendarModel>()

        if (tahun != -1 && monthOfYear != -1) {
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.YEAR, tahun)
        } else {
            tahun = calendar.get(Calendar.YEAR)
            monthOfYear = calendar.get(Calendar.MONTH)
        }

        val sdf = SimpleDateFormat("MMMM, yyyy", Locale("in", "ID"))
        val dateToday = sdf.format(calendar.time).split(",")
        month.text = dateToday[0]
        year.text = dateToday[1]

        // CalendarToday
        val calendarCompare: Calendar = Calendar.getInstance()
        calendarCompare.set(Calendar.MONTH, monthOfYear)
        calendarCompare.set(Calendar.YEAR, tahun)

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - 1

        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        val dateSdf = SimpleDateFormat("dd-MM-yyyy", Locale("in", "ID"))
        val eventSdf = SimpleDateFormat("yyyy-MM-dd", Locale("in", "ID"))

        // Fetch events from API
        val events = fetchEvents() ?: listOf() // If fetchEvents() returns null, use an empty list

        // Clear the infoList before adding new information
        infoList.clear()

        while (cells.size < DAYS_COUNT) {
            val isFriday = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
            var color: String? = if (isFriday) "merah" else null
            var info: String? = null

            // Check if current date is within any event range
            for (event in events) {
                val eventStartDate = eventSdf.parse(event.startDate)
                val eventEndDate = eventSdf.parse(event.endDate)

                if (eventStartDate <= calendar.time && calendar.time <= eventEndDate) {
                    color = event.status
                    if (event.status == "hijau") {
                        info = event.info
                        infoList.add(event.info)
                    }
                    break
                }
            }

            cells.add(calendarModel(
                calendar.get(Calendar.DATE),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.YEAR),
                calendarCompare,
                color.toString()
            ))

            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        infoAdapter.notifyDataSetChanged()

        calendar.set(Calendar.MONTH, monthOfYear)
        calendar.set(Calendar.YEAR, tahun)

        calendarList.clear()
        calendarList.addAll(cells)
        adapter.notifyDataSetChanged()
    }


    private fun fetchEvents(): List<KalenderPendidikan>? {
        val json = """
        [
            {"id": 1, "startDate": "2024-07-22", "endDate": "2024-07-24", "info": "Masa Ta'aruf Santri", "status": "hijau"},
            {"id": 2, "startDate": "2024-07-27", "endDate": "2024-07-27", "info": "Kegiatan Belajar Mengajar", "status": "hijau"},
            {"id": 3, "startDate": "2024-08-09", "endDate": "2024-08-09", "info": "Muhadloroh", "status": "hijau"},
            {"id": 4, "startDate": "2024-08-10", "endDate": "2024-08-17", "info": "Libur Sekolah, Yeayy!", "status": "merah"}
        ]
    """

        val gson = Gson()
        return try {
            val eventType = object : TypeToken<List<KalenderPendidikan>>() {}.type
            gson.fromJson(json, eventType)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }

}
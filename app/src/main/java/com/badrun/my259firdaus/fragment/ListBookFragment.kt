package com.badrun.my259firdaus.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Spinner
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.adapter.AdapterBuku
import com.badrun.my259firdaus.helper.BookViewModel
import com.badrun.my259firdaus.helper.GridSpacingItemDecoration
import com.badrun.my259firdaus.model.Buku
import java.util.Locale


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ListBookFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListBookFragment : Fragment() {

    private lateinit var bookViewModel: BookViewModel
    private lateinit var booksAdapter: AdapterBuku
    private lateinit var search: EditText
    private lateinit var drawerLayoutSatu: DrawerLayout
    private lateinit var filterDrawerSatu: RelativeLayout
    private lateinit var btnFilter: Button
    private lateinit var penulisSpinner: Spinner
    private lateinit var kategoriSpinner: Spinner
    private lateinit var penerbitSpinner: Spinner
    private lateinit var tahunterbitSpinner: Spinner
    private lateinit var jenisBuku: Spinner
    private lateinit var buttonFilter: Button
    private var originalList: List<Buku> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list_book, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookViewModel = ViewModelProvider(requireActivity()).get(BookViewModel::class.java)
        drawerLayoutSatu = view.findViewById(R.id.drawer_layoutsatu)
        filterDrawerSatu = view.findViewById(R.id.filter_drawersatu)
        btnFilter = view.findViewById(R.id.filter_buttonsatu)
        penulisSpinner = view.findViewById(R.id.penulis_Spinner)
        kategoriSpinner = view.findViewById(R.id.kategori_Spinner)
        penerbitSpinner = view.findViewById(R.id.penerbit_Spinner)
        tahunterbitSpinner = view.findViewById(R.id.tahunterbit_Spinner)
        jenisBuku = view.findViewById(R.id.jenis_Spinner)
        buttonFilter = view.findViewById(R.id.filter)

        booksAdapter = AdapterBuku(listOf(), requireActivity())
        search = view.findViewById(R.id.search_bar)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val layoutManager = GridLayoutManager(context, 2)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = booksAdapter

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.spacing)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels, true))

        btnFilter.setOnClickListener {
            toggleFilterDrawer()
        }
        // Observasi perubahan pada ViewModel
        bookViewModel.books.observe(viewLifecycleOwner, Observer { books ->
            books?.let {
                // Simpan originalList
                originalList = it
                setupSpinners()
                booksAdapter.updateBooks(it)
            }
        })

        // Tambahkan TextWatcher untuk melakukan pencarian
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString().trim().toLowerCase(Locale.getDefault())

                // Lakukan filtering berdasarkan originalList
                val filteredList = originalList.filter { item ->
                    item.judul_buku.toLowerCase(Locale.getDefault()).contains(searchText)
                }

                // Update RecyclerView dengan filteredList
                booksAdapter.updateBooks(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        buttonFilter.setOnClickListener {
            applyFilters()
            drawerLayoutSatu.closeDrawer(GravityCompat.END)
        }


    }

    private fun applyFilters() {
        val kategoriFilter = kategoriSpinner.selectedItem?.toString() ?: ""
        val penulisFilter = penulisSpinner.selectedItem?.toString() ?: ""
        val penerbitFilter = penerbitSpinner.selectedItem?.toString() ?: ""
        val tahunTerbitFilter = tahunterbitSpinner.selectedItem?.toString() ?: ""
        val jenisBukuFilter = jenisBuku.selectedItem?.toString() ?: ""

        val filteredList = originalList.filter { buku ->
            val isKategoriMatched = kategoriFilter.isBlank() || buku.genre_buku.equals(kategoriFilter, ignoreCase = true)
            val isPenulisMatched = penulisFilter.isBlank() || buku.penulis.equals(penulisFilter, ignoreCase = true)
            val isPenerbitMatched = penerbitFilter.isBlank() || buku.penerbit.equals(penerbitFilter, ignoreCase = true)
            val isTahunTerbitMatched = tahunTerbitFilter.isBlank() || buku.tahun_terbit.equals(tahunTerbitFilter, ignoreCase = true)
            val isJenisBukuMatched = jenisBukuFilter.isBlank() || buku.jenis.equals(jenisBukuFilter, ignoreCase = true)

            isKategoriMatched && isPenulisMatched && isPenerbitMatched && isTahunTerbitMatched && isJenisBukuMatched
        }

        booksAdapter.updateBooks(filteredList)
    }

    private fun setupSpinners() {
        // Mengisi Spinner Kategori
        val kategoriList = mutableListOf<String>()
        kategoriList.add("") // String kosong untuk tidak memfilter
        kategoriList.addAll(originalList.map { it.genre_buku }.distinct())
        val kategoriAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, kategoriList)
        kategoriAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        kategoriSpinner.adapter = kategoriAdapter

        // Mengisi Spinner Penulis
        val penulisList = mutableListOf<String>()
        penulisList.add("") // String kosong untuk tidak memfilter
        penulisList.addAll(originalList.map { it.penulis }.distinct())
        val penulisAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, penulisList)
        penulisAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        penulisSpinner.adapter = penulisAdapter

        // Mengisi Spinner Penerbit
        val penerbitList = mutableListOf<String>()
        penerbitList.add("") // String kosong untuk tidak memfilter
        penerbitList.addAll(originalList.map { it.penerbit }.distinct())
        val penerbitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, penerbitList)
        penerbitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        penerbitSpinner.adapter = penerbitAdapter

        // Mengisi Spinner Tahun Terbit
        val tahunTerbitList = mutableListOf<String>()
        tahunTerbitList.add("") // String kosong untuk tidak memfilter
        tahunTerbitList.addAll(originalList.map { it.tahun_terbit }.distinct())
        val tahunTerbitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tahunTerbitList)
        tahunTerbitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        tahunterbitSpinner.adapter = tahunTerbitAdapter

        // Mengisi Spinner Jenis Buku
        val jenisBukuList = mutableListOf<String>()
        jenisBukuList.add("") // String kosong untuk tidak memfilter
        jenisBukuList.addAll(originalList.map { it.jenis }.distinct())
        val jenisBukuAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, jenisBukuList)
        jenisBukuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        jenisBuku.adapter = jenisBukuAdapter
    }

    private fun toggleFilterDrawer() {
        if (drawerLayoutSatu.isDrawerOpen(GravityCompat.END)) {
            drawerLayoutSatu.closeDrawer(GravityCompat.END)
        } else {
            drawerLayoutSatu.openDrawer(GravityCompat.END)
        }
    }

    override fun onResume() {
        super.onResume()
        drawerLayoutSatu.closeDrawer(GravityCompat.END)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListBookFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListBookFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
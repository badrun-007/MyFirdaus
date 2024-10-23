package com.badrun.my259firdaus.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.adapter.AdapterBuku
import com.badrun.my259firdaus.helper.BookViewModel
import com.badrun.my259firdaus.model.Buku
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BookmarkFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookmarkFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var bookViewModel: BookViewModel
    private lateinit var booksAdapter: AdapterBuku
    private lateinit var sharedPreferences: SharedPreferences

    var originalList: List<Buku> = mutableListOf()
    private lateinit var search : EditText
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var filterDrawer: RelativeLayout
    private lateinit var btnFilter : Button

    private lateinit var penulisSpinner : Spinner
    private lateinit var kategoriSpinner : Spinner
    private lateinit var penerbitSpinner : Spinner
    private lateinit var tahunterbitSpinner : Spinner
    private lateinit var jenisBuku : Spinner
    private lateinit var buttonFilter : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookViewModel = ViewModelProvider(requireActivity())[BookViewModel::class.java]
        sharedPreferences = requireActivity().getSharedPreferences("bookmarks", Context.MODE_PRIVATE)
        search = view.findViewById(R.id.search_bar)
        drawerLayout = view.findViewById(R.id.drawer_layoutdua)
        filterDrawer = view.findViewById(R.id.filter_drawerdua)
        btnFilter = view.findViewById(R.id.filter_buttondua)
        penulisSpinner = view.findViewById(R.id.penulis_Spinnerdua)
        kategoriSpinner = view.findViewById(R.id.kategori_Spinnerdua)
        penerbitSpinner = view.findViewById(R.id.penerbit_Spinnerdua)
        tahunterbitSpinner = view.findViewById(R.id.tahunterbit_Spinnerdua)
        jenisBuku = view.findViewById(R.id.jenis_Spinnerdua)
        buttonFilter = view.findViewById(R.id.filterdua)

        booksAdapter = AdapterBuku(listOf(), requireActivity())
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_bookmark)
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = booksAdapter

        // Menambahkan observer untuk memuat ulang data saat fragment kembali aktif
        bookViewModel.books.observe(viewLifecycleOwner, Observer { books ->
            books?.let {
                originalList = getBookmarkedBooks(it)
                setupSpinners()
                booksAdapter.updateBooks(originalList)
            }
        })

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Tidak perlu diimplementasikan untuk pencarian instan
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Metode ini dipanggil saat teks berubah, Anda bisa menangani pencarian di sini
                val searchText = s.toString().trim().toLowerCase(Locale.getDefault())

                // Lakukan filtering berdasarkan originalList
                val filteredList = originalList.filter { item ->
                    item.judul_buku.toLowerCase(Locale.getDefault()).contains(searchText)
                }

                // Update RecyclerView dengan filteredList
                booksAdapter.updateBooks(filteredList)
            }

            override fun afterTextChanged(s: Editable?) {
                // Tidak perlu diimplementasikan untuk pencarian instan
            }
        })

        buttonFilter.setOnClickListener {
            applyFilters()
            drawerLayout.closeDrawer(GravityCompat.END)
        }

        btnFilter.setOnClickListener {
            toggleFilterDrawer()
        }

    }

    override fun onResume() {
        super.onResume()
        refreshBookmarkedBooks()
    }

    private fun refreshBookmarkedBooks() {
        val allBooks = bookViewModel.books.value ?: emptyList()
        val bookmarkedBooks = getBookmarkedBooks(allBooks)
        booksAdapter.updateBooks(bookmarkedBooks)
    }

    private fun getBookmarkedBooks(allBooks: List<Buku>): List<Buku> {
        val bookmarkedBooks = mutableListOf<Buku>()
        val bookmarks = sharedPreferences.all

        for (book in allBooks) {
            if (bookmarks.containsKey(book.id.toString()) && bookmarks[book.id.toString()] as Boolean) {
                bookmarkedBooks.add(book)
            }
        }

        return bookmarkedBooks
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
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END)
        } else {
            drawerLayout.openDrawer(GravityCompat.END)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BookmarkFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BookmarkFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
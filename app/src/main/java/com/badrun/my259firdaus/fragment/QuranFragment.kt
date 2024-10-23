package com.badrun.my259firdaus.fragment


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.adapter.AdapterSurat
import com.badrun.my259firdaus.database.*
import com.badrun.my259firdaus.model.SuratEntity
import com.badrun.my259firdaus.remote.Result


class QuranFragment : Fragment() {

    private lateinit var rvSurat : RecyclerView
    private lateinit var mainAdapter : AdapterSurat
    private lateinit var load : ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_quran, container, false)

        rvSurat = view.findViewById(R.id.rv_list_surat)
        load = view.findViewById(R.id.pbSurat)

        setUpRecycler()
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getSurah()

    }

    private fun getSurah() {

        val factory: SuratViewModelFactory = SuratViewModelFactory.getInstance(requireActivity())
        val viewModel: SuratViewModel by viewModels {
            factory
        }
//        val dataAda = SuratDatabase.getInstance(requireContext()).isSuratTableNotEmpty()
//        Log.e("BDR", "getSurah: $dataAda")

        viewModel.getHeadlineSurat().observe(viewLifecycleOwner) { result ->
            if (result != null) {
                when (result) {

                    is Result.Error -> {
                        load.visibility = View.GONE
                        Toast.makeText(
                            context,
                            "Terjadi kesalahan" + result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Result.Loading -> {
                        load.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        load.visibility = View.GONE
                        val suratData = result.data
                        showData(suratData)

                    }
                }
            }
        }

    }


    private fun setUpRecycler(){
        mainAdapter = AdapterSurat(requireActivity(),arrayListOf())
        rvSurat.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mainAdapter
        }
    }

    fun showData(data: List<SuratEntity>) {
        if (data.isNotEmpty()) {
            mainAdapter.showData(data)
        }
    }



}
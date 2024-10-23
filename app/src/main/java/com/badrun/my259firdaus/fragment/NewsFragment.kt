package com.badrun.my259firdaus.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.activity.ArtikelActivity
import com.badrun.my259firdaus.activity.InfopesantrenActivity
import com.badrun.my259firdaus.activity.LainnyaActivity
import com.badrun.my259firdaus.adapter.AdapterNewsFrag
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.database.NewsEntity
import com.badrun.my259firdaus.database.NewsViewModel
import com.badrun.my259firdaus.database.NewsViewModelFactory
import com.badrun.my259firdaus.helper.SharedNewsViewModel
import com.badrun.my259firdaus.model.News
import com.badrun.my259firdaus.model.ResponseNews
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFragment : Fragment() {

    private lateinit var rvInfoPesantren : RecyclerView
    private lateinit var rvArtikel :RecyclerView
    private lateinit var rvLainnya : RecyclerView
    private lateinit var textInfo : TextView
    private lateinit var textArtikel : TextView
    private lateinit var textLainnya : TextView
    private lateinit var load : ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_news, container, false)

        rvInfoPesantren = view.findViewById(R.id.rv_news_infopesantren)
        rvArtikel = view.findViewById(R.id.rv_news_artikel)
        rvLainnya = view.findViewById(R.id.rv_news_infolainnya)
        textInfo = view.findViewById(R.id.selengkapnyainfo)
        textArtikel = view.findViewById(R.id.selengkapnyaartikel)
        textLainnya = view.findViewById(R.id.selengkapnyalainnya)
        load = view.findViewById(R.id.pb_asd)


        getNewsss()

        return view
    }



    private fun getNewsss(){
        val sharedViewModel by viewModels<SharedNewsViewModel>(ownerProducer = { requireActivity() })
        sharedViewModel.headlineNews.observe(viewLifecycleOwner) { list ->
            val arrayList = ArrayList(list)
            setUpRecycler(arrayList)
            initTextListener(arrayList)
        }
    }

    private fun initTextListener(data : ArrayList<NewsEntity>?){
        textInfo.setOnClickListener {
            val i = Intent(context, InfopesantrenActivity::class.java )
            i.putParcelableArrayListExtra("news",data)
            startActivity(i)
        }

        textArtikel.setOnClickListener {
            val i = Intent(context, ArtikelActivity::class.java)
            i.putParcelableArrayListExtra("artikel",data)
            startActivity(i)
        }

        textLainnya.setOnClickListener {
            val i = Intent(context, LainnyaActivity::class.java)
            i.putParcelableArrayListExtra("lainnya",data)
            startActivity(i)
        }
    }

    private fun setUpRecycler(data : List<NewsEntity>){

        val infoAdapter = AdapterNewsFrag(requireActivity(), arrayListOf())
        val artikelAdapter = AdapterNewsFrag(requireActivity(), arrayListOf())
        val lainnyaAdapter = AdapterNewsFrag(requireActivity(), arrayListOf())

        // filter data dan set adapter untuk setiap RecyclerView
        val dataInfo = data.filter { it.kategori == "1" }
        val dataArtikel = data.filter { it.kategori == "2" }
        val dataLainnya = data.filter { it.kategori == "3" }

        infoAdapter.showData(dataInfo as List<NewsEntity>)
        infoAdapter.setMaxItemCount(2)
        artikelAdapter.showData(dataArtikel as List<NewsEntity>)
        artikelAdapter.setMaxItemCount(2)
        lainnyaAdapter.showData(dataLainnya as List<NewsEntity>)
        lainnyaAdapter.setMaxItemCount(2)

        rvInfoPesantren.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = infoAdapter

        }
        rvArtikel.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = artikelAdapter

        }
        rvLainnya.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = lainnyaAdapter

        }
    }


}
package com.badrun.my259firdaus.fragment


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.badrun.my259firdaus.MainActivity
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.activity.LoginActivity
import com.badrun.my259firdaus.activity.ProfileActivity
import com.badrun.my259firdaus.api.ApiConfig
import com.badrun.my259firdaus.helper.SharedPref
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AkunFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var keluar : TextView
    private lateinit var namaUser : TextView
    private lateinit var kelas : TextView
    private lateinit var pp : ImageView
    private lateinit var notif : Switch
    private lateinit var editProfile : RelativeLayout
    private lateinit var ubahPassword : RelativeLayout
    private lateinit var bantuan : RelativeLayout
    private lateinit var tentangKami : RelativeLayout

    private val PICK_IMAGE_REQUEST = 1
    private lateinit var s : SharedPref

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
        val view =  inflater.inflate(R.layout.fragment_akun, container, false)

        keluar = view.findViewById(R.id.keluars)
        namaUser = view.findViewById(R.id.userName)
        kelas = view.findViewById(R.id.tv_kelas)
        pp = view.findViewById(R.id.profile_image)
        editProfile = view.findViewById(R.id.btn_editProfile)
        notif = view.findViewById(R.id.swt_notif)

        initUserHeula()

        val sharedPrefs = requireActivity().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        val isNotifEnabled = sharedPrefs.getBoolean("NotificationEnabled", true)
        notif.isChecked = isNotifEnabled

        notif.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPrefs.edit()
            editor.putBoolean("NotificationEnabled", isChecked)
            editor.apply()
            (activity as? MainActivity)?.togglePrayerService(isChecked)
        }

        keluar.setOnClickListener {
           showExitConfirmationDialog()
        }

        editProfile.setOnClickListener {
//            val intent = Intent(requireContext(), ProfileActivity::class.java)
//            startActivity(intent)
            showCustomDialog()
        }


        return view
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Konfirmasi Keluar Akun")
            .setMessage("Apakah Anda yakin ingin Logout?")
            .setPositiveButton("Ya") { _: DialogInterface, _: Int ->
                // Tutup aplikasi jika pengguna mengonfirmasi
                FirebaseAuth.getInstance().signOut()
                s.setStatusLogin(false)
                s.deleteShared()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Tidak") { dialogInterface: DialogInterface, _: Int ->
                // Biarkan aplikasi tetap terbuka jika pengguna membatalkan
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun initUserHeula(){
        s = SharedPref(requireActivity())
        val user = s.getUser()

        when(user!!.role){
            2 ->{
                val guru = s.getUser()
                namaUser.text = guru!!.nama
                kelas.text = guru.email

                val linkPP = "http://${ApiConfig.iplink.ip}/storage/profile/" + guru.image
                initPhotoProfile(linkPP)
            }
            3 ->{
                val santri = s.getUser()
                namaUser.text = santri!!.nama
                kelas.text = santri.email

                val linkPP = "http://${ApiConfig.iplink.ip}/storage/psb/" + santri.image
                initPhotoProfile(linkPP)
            }
            4 ->{
                val orangtua = s.getUser()
                namaUser.text = orangtua!!.nama

                val linkPP = "http://${ApiConfig.iplink.ip}/storage/profile/" + orangtua.image
                initPhotoProfile(linkPP)
            }
            5 ->{
                val tamu = s.getUser()
                namaUser.text = tamu!!.nama

                val linkPP = "http://${ApiConfig.iplink.ip}/storage/profile/" + tamu.image
                initPhotoProfile(linkPP)
            }
            6 ->{
                val tamu = s.getUser()
                namaUser.text = tamu!!.nama

                val linkPP = "http://${ApiConfig.iplink.ip}/storage/profile/" + tamu.image
                initPhotoProfile(linkPP)
            }
        }
    }

    private fun initPhotoProfile(link:String){

        Glide.with(this)
            .load(link)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.progress_animation)
            .error(R.drawable.rg)
            .into(pp)

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

    private fun initEditProfile(){

    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters

        fun nTC() : AkunFragment{
            return AkunFragment()
        }

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
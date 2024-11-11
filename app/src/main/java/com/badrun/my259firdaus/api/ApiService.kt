package com.badrun.my259firdaus.api

import com.badrun.my259firdaus.model.*
import kotlinx.coroutines.Deferred
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.util.Date

interface ApiService {

    @GET("news")
    fun getNews():Call<ResponseNews>

    @GET("buku")
    fun getBuku():Call<ResponseBook>

    @GET
    fun downloadBuku(@Url fileUrl: String): Deferred<ResponseBody>

    @GET("infopsb")
    fun getInfoPsb():Call<ResponseInfoPendaftaran>

    @FormUrlEncoded
    @POST("datapsb")
    fun getDataPsb(@Field("order_id")orderid:String):Call<ResponsePsb>

    @GET("slide")
    fun getSlide():Call<ResponseSlide>

    @FormUrlEncoded
    @POST("porto")
    fun setPorto(@Field("id_santri")idSantri:Int,@Field("baca_quran")bacaQuran:Int,@Field("nulis_quran")nulisQuran:Int,@Field("pengetahuan_umum")pengetahuan:Int,@Field("hafalan_quran")hafalan:Int,@Field("ekstrakurikuler")ekstrakurikuler:String):Call<ResponsePorto>

    @FormUrlEncoded
    @POST("register")
    fun register(@Field("name")nama:String,@Field("email")email:String,@Field("password")password:String,@Field("role")role:Int, @Field("jenis")jenis:String):Call<ResponsModel>

    @FormUrlEncoded
    @POST("login")
    fun login(@Field("email")email:String,@Field("password")password:String):Call<ResponseUser>

    @GET("calendar")
    fun getJSholat(@Query("latitude")kota:String, @Query("longitude")negara:String, @Query("method")mothod:Int, @Query("tune")tune:String, @Query("month")bulan:Int, @Query("year")year:Int):Call<ResponseJadwalSholat>

    @GET("provinces.json")
    fun getProvinsi():Call<List<Provinsi>>

    @GET("regencies/{provinceId}.json")
    fun getKabupaten(@Path("provinceId") provinceId:String):Call<List<Kabupaten>>

    @GET("districts/{regencyId}.json")
    fun getKecamatan(@Path("regencyId") kecamatan:String):Call<List<Kecamatan>>

    @GET("villages/{districtId}.json")
    fun getDesa(@Path("districtId") kecamatan:String):Call<List<Desa>>

    @GET("calendar/{year}/{month}")
    fun getJSholatM(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("method") method: Int,
        @Query("methodSettings") methodSettings: String?
    ): Call<ResponseJadwalSholat>
//    @GET("calendarByCity")
//    fun getJSholat(@Query("city")kota:String, @Query("country")negara:String, @Query("method")mothod:Int, @Query("tune")tune:String, @Query("month")bulan:Int, @Query("year")year:Int):Call<ResponseJadwalSholat>

    @GET("/api/v2/surat/")
    fun getSurah():Call<ResponseSurat>

    @GET("/api/v2/surat/{nomor}")
    fun getAyat(@Path("nomor") nomorSurat: Int):Call<ResponseAyat>

    @FormUrlEncoded
    @POST("data-user")
    fun getSantri(@Field("email")email:String):Call<ResponseSantri>

    @FormUrlEncoded
    @POST("data-user")
    fun getGuru(@Field("email")email:String):Call<ResponseGuru>

    @FormUrlEncoded
    @POST("data-user")
    fun getOrtu(@Field("email")email:String):Call<ResponseOrtu>

    @FormUrlEncoded
    @POST("data-user")
    fun getTamu(@Field("email")email:String):Call<ResponseTamu>

    @POST("coba")
    fun charge(@Body request: MidtransRequest): Call<ResponseMidtrans>

    @POST("coba")
    fun chargePerpus(@Body request: PerpusMidtrans): Call<ResponseMidtrans>

    @POST("coba")
    fun chargeDenda(@Body request: DendaMidtrans): Call<ResponseMidtrans>

    @FormUrlEncoded
    @POST("check-buku")
    fun checkBuku(@Field("id_user")idUser: Int, @Field("isbn")isbn: String) : Call<ResponseCheck>

    @FormUrlEncoded
    @POST("check-pinjam")
    fun checkPinjamBuku(@Field("id_user")idUser: Int) : Call<ResponseCheck>

    @FormUrlEncoded
    @POST("notif-buku")
    fun insertNotifikasiBuku(@Field("id_user")idUser: Int, @Field("judul_buku")judul:String, @Field("isbn")isbn:String, @Field("hp")hp:String) : Call<ResponseCheck>

    @FormUrlEncoded
    @POST("reservasi")
    fun setReservasi(@Field("isbn")isbn:String,@Field("id_buku")idBuku:String, @Field("id_user")idUser:Int,@Field("tgl_pengembalian")tgl_pengembalian:String,@Field("cover")cover:String, @Field("nama_user")namaUser:String, @Field("judul_buku")judulBuku:String, @Field("penulis")penulis:String, @Field("waktu_peminjaman")waktuPeminjaman:Int, @Field("hp")hp:String):Call<ResponseCheck>

    @FormUrlEncoded
    @POST("cek-reservasi")
    fun checkReservasi(@Field("id_user")idUser: Int, @Field("id_buku")idBuku: String) : Call<ResponseCheck>

    @FormUrlEncoded
    @POST("perpanjang")
    fun perpanjangPeminjaman(@Field("id")idPeminjaman: Int, @Field("perpanjangan")perpanjangan: Int) : Call<ResponseCheck>

    @FormUrlEncoded
    @POST("buku-stok")
    fun getJadwalBuku(@Field("id_buku")idBuku:String) : Call<ResponseJadwalBuku>

    @FormUrlEncoded
    @POST("codebuku")
    fun checkCodeBuku(@Field("isbn")idBuku: String): Call<ResponseCodeBuku>

    @FormUrlEncoded
    @POST("list-pinjam")
    fun listPinjamBuku(@Field("id_user")idUser:Int) : Call<ResponsePeminjaman>

    @FormUrlEncoded
    @POST("pinjam-buku")
    fun pinjamBuku(@Field("id_buku")idBuku:String, @Field("id_user")idUser:Int, @Field("isbn")isbn:String, @Field("cover")cover:String, @Field("nama_user")namaUser:String, @Field("judul_buku")judulBuku:String, @Field("penulis")penulis:String, @Field("waktu_peminjaman")waktuPeminjaman:Int,@Field("hp")hp:String) : Call<ResponseMinjam>

    @FormUrlEncoded
    @POST("waduh")
    fun waduh(@Field("email")email:String):Call<ResponseUser>

    @GET("status/{transactionId}")
    fun getTransactionDetails(
        @Path("transactionId") transactionId: String
    ): Call<ResponseTransactionDetail>

    @Multipart
    @POST("psb")
    fun setPsb(
        @Part("user_id") userId: RequestBody,
        @Part("nama_santri") namaSantri: RequestBody,
        @Part("nik_santri") nikSantri: RequestBody,
        @Part("nisn_santri") nisnSantri: RequestBody,
        @Part("tempatlahir_santri") tempatLahirSantri: RequestBody,
        @Part("tanggallahir_santri") tanggalLahirSantri: RequestBody,
        @Part("anakke_santri") anakke: RequestBody,
        @Part("saudarake_santri") saudara: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("alamat") alamat: RequestBody,
        @Part("sekolah_asal") sekolahAsal: RequestBody,
        @Part("alamat_sekolah_asal") alamatSekolahAsal: RequestBody,
        @Part("nama_ayah") namaAyah: RequestBody,
        @Part("nik_ayah") nikAyah: RequestBody,
        @Part("tempatlahir_ayah") tempalLahirAyah: RequestBody,
        @Part("tanggallahir_ayah") tanggalLahirAyah: RequestBody,
        @Part("nomor_hp_ayah") nomorHpAyah: RequestBody,
        @Part("pendidikan_ayah") pendidikanAyah: RequestBody,
        @Part("pekerjaan_ayah") pekerjaanAyah: RequestBody,
        @Part("penghasilan_ayah") penghasilanAyah: RequestBody,
        @Part("alamat_ayah") alamatAyah: RequestBody,
        @Part("nama_ibu") namaIbu: RequestBody,
        @Part("nik_ibu") nikIbu: RequestBody,
        @Part("tempatlahir_ibu") tempalLahirIbu: RequestBody,
        @Part("tanggallahir_ibu") tanggalLahirIbu: RequestBody,
        @Part("nomor_hp_ibu") nomorHpIbu: RequestBody,
        @Part("pendidikan_ibu") pendidikanIbu: RequestBody,
        @Part("pekerjaan_ibu") pekerjaanIbu: RequestBody,
        @Part("penghasilan_ibu") penghasilanIbu: RequestBody,
        @Part("alamat_ibu") alamatIbu: RequestBody,
        @Part("prestasi") prestasiSantri: RequestBody,
        @Part("jenis_prestasi") jenisPrestasi: RequestBody,
        @Part("penyelenggara") prestasiPenyelenggara: RequestBody,
        @Part("tingkat") prestasiTingkat: RequestBody,
        @Part("juara") prestasiJuara: RequestBody,
        @Part("tanggal_kegiatan") prestasiTanggal: RequestBody,
        @Part foto: MultipartBody.Part,
        @Part prestasi : MultipartBody.Part
    ): Call<ResponsePsb>

    @FormUrlEncoded
    @POST("get-payment")
    fun getPayment(@Field("order_id")orderId: String) : Call<ResponseCheckPayment>

    @Multipart
    @POST("daftar-ulang")
    fun setDaftarUlang(
        @Part("user_id") userId: RequestBody,
        @Part kk: MultipartBody.Part,
        @Part akte: MultipartBody.Part,
        @Part ijazah: MultipartBody.Part,
        @Part kip: MultipartBody.Part
    ): Call<ResponseDaftarUlang>

    @FormUrlEncoded
    @POST("login-exam")
    fun loginExam(@Field("email")email: String,@Field("password")pass:String) : Call<ResponseLoginExam>

    @FormUrlEncoded
    @POST("set-exam")
    fun setExam(
        @Field("schedule_id")schedule_id:Int,
        @Field("token")token:String,
        @Field("student_id")student_id:Int,
        @Field("matpel_id")matpel_id:Int,
        @Field("kelas")kelas:String,
        @Field("transaksi_id")transaksi_id:Int): Call<ResponseSetToken>

    @FormUrlEncoded
    @POST("get-exam")
    fun getExam(
        @Field("class_level")class_id:Int,
        @Field("transaction_id")transaction_id:Int): Call<QuestionResponse>

    @POST("set-answer") // Sesuaikan dengan endpoint API Anda
    fun answerStore(@Body request: AnswerRequest): Call<ResponseBody>

}
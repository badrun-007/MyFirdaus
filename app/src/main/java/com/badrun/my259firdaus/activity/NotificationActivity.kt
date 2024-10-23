package com.badrun.my259firdaus.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.badrun.my259firdaus.R
import com.badrun.my259firdaus.adapter.NotificationAdapter
import com.badrun.my259firdaus.database.NotifDatabase
import com.badrun.my259firdaus.helper.NotificationRepository
import com.badrun.my259firdaus.helper.NotificationViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter
    private lateinit var notificationRepository: NotificationRepository
    private val notificationViewModel: NotificationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        val database = NotifDatabase.getDatabase(this)
        notificationRepository = NotificationRepository(database.notificationDao())

        recyclerView = findViewById(R.id.rv_list_notif)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        lifecycleScope.launch {
            // Mark all notifications as read
            withContext(Dispatchers.IO) {
                notificationRepository.markAllAsRead()
            }
            // Load notifications
            loadNotifications()
        }

        initBtnBack()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        notificationViewModel.setUnreadCount(0)
    }

    private fun loadNotifications() {
        lifecycleScope.launch {
            val notifications = withContext(Dispatchers.IO) {
                notificationRepository.getAllNotifications()
            }
            adapter = NotificationAdapter(notifications)
            recyclerView.adapter = adapter
        }
    }

    private fun initBtnBack() {
        val toolbar: Toolbar = findViewById(R.id.toolbarAnggota)
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
}
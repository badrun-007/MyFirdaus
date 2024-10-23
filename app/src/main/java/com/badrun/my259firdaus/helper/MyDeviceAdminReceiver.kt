package com.badrun.my259firdaus.helper

import android.app.ActivityOptions
import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.UserManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MyDeviceAdminReceiver : DeviceAdminReceiver() {
    private val KIOSK_PACKAGE = "com.badrun.my259firdaus"

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)

        val dpm = getManager(context)
        val admin = getWho(context)
        dpm.setLockTaskFeatures(admin, DevicePolicyManager.LOCK_TASK_FEATURE_NONE)
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        // Kirim siaran untuk memberi tahu aktivitas tentang penonaktifan
        val intent = Intent("ACTION_ADMIN_DISABLED")
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    override fun onLockTaskModeEntering(context: Context, intent: Intent, pkg: String) {
        val dpm = getManager(context)
        val admin = getWho(context)

        dpm.addUserRestriction(admin, UserManager.DISALLOW_CREATE_WINDOWS)
    }
}

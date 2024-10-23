package com.badrun.my259firdaus.helper

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            if (it.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                // Dapatkan nama paket aplikasi yang sedang aktif
                val packageName = it.packageName?.toString()
                // Logika untuk mendeteksi aplikasi dalam mode floating
                checkIfFloatingApp(packageName)
            }
        }
    }

    private fun checkIfFloatingApp(packageName: String?) {
        // Implementasikan logika untuk memeriksa apakah aplikasi lain berada dalam mode floating
        if (packageName == "com.android.chrome" || packageName == "com.opera.browser" || packageName == "com.google.android.googlequicksearchbox") {
            Log.e("BDR", "checkIfFloatingApp: ada float app: $packageName")
        } else {
            Log.e("BDR", "checkIfFloatingApp: tidak ada float app: $packageName")
        }
    }

    override fun onInterrupt() {
        // Tangani interupsi
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
        serviceInfo = info
        Log.e("BDR", "Accessibility Service Connected")
    }
}
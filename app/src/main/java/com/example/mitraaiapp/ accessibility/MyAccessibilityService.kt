package com.example.mitraaiapp.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class MyAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
        // Service is connected; set up if needed
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        // Forward the event & rootInActiveWindow to the orchestrator logic
        AccessibilityOrchestrator.handleEvent(this, event)
    }

    override fun onInterrupt() {
        // Called when the service is interrupted, e.g., disabled or phone locked
    }
}
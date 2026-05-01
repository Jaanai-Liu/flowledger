package com.flowledger.app.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.service.notification.NotificationListenerService

object NotificationPermissionHelper {

    fun isListenerEnabled(context: Context): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            "enabled_notification_listeners"
        ) ?: return false

        val componentName = ComponentName(
            context,
            com.flowledger.app.service.NotificationMonitorService::class.java
        )
        return enabledListeners.contains(componentName.flattenToString())
    }

    fun openNotificationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        context.startActivity(intent)
    }
}

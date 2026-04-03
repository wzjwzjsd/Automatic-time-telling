package com.example.hourlychime.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.hourlychime.service.ChimeForegroundService

class BootReceiver : BroadcastReceiver() {

    private val TAG = "BootReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || 
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            
            Log.d(TAG, "开机启动广播收到")
            
            // 检查是否启用了报时功能
            val prefs = context.getSharedPreferences("hourly_chime_prefs", Context.MODE_PRIVATE)
            val isEnabled = prefs.getBoolean("chime_enabled", false)
            
            if (isEnabled) {
                Log.d(TAG, "报时功能已启用，自动启动服务")
                
                // 启动前台服务
                val serviceIntent = Intent(context, ChimeForegroundService::class.java)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
                
                // 重新设置闹钟（如果需要）
                // 这里可以添加重新设置 AlarmManager 的逻辑
            } else {
                Log.d(TAG, "报时功能未启用，不自动启动")
            }
        }
    }
}

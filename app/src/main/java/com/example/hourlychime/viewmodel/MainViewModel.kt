package com.example.hourlychime.viewmodel

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.hourlychime.receiver.HourlyChimeReceiver
import com.example.hourlychime.service.ChimeForegroundService
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val _isChimeEnabled = MutableLiveData(false)
    val isChimeEnabled: LiveData<Boolean> = _isChimeEnabled

    private val _lastChimeTime = MutableLiveData<String?>()
    val lastChimeTime: LiveData<String?> = _lastChimeTime

    private var textToSpeech: TextToSpeech? = null
    private var isTTSReady = false

    private val TAG = "MainViewModel"

    init {
        // 初始化 TTS
        textToSpeech = TextToSpeech(getApplication(), this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale.CHINA)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "中文语音包不可用")
                isTTSReady = false
            } else {
                isTTSReady = true
                Log.d(TAG, "TTS 初始化成功")
            }
        } else {
            Log.e(TAG, "TTS 初始化失败")
            isTTSReady = false
        }
    }

    fun enableChime(context: Context) {
        viewModelScope.launch {
            // 检查精确闹钟权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (!alarmManager.canScheduleExactAlarms()) {
                    // 需要请求精确闹钟权限
                    Log.w(TAG, "需要请求精确闹钟权限")
                }
            }

            // 启动前台服务
            val serviceIntent = Intent(context, ChimeForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }

            // 设置整点闹钟
            scheduleHourlyAlarm(context, true)
            
            _isChimeEnabled.value = true
            Log.d(TAG, "整点报时已启用")
        }
    }

    fun disableChime(context: Context) {
        viewModelScope.launch {
            // 取消闹钟
            scheduleHourlyAlarm(context, false)
            
            // 停止前台服务
            val serviceIntent = Intent(context, ChimeForegroundService::class.java)
            context.stopService(serviceIntent)
            
            _isChimeEnabled.value = false
            Log.d(TAG, "整点报时已禁用")
        }
    }

    private fun scheduleHourlyAlarm(context: Context, enable: Boolean) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HourlyChimeReceiver::class.java)
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, pendingIntentFlags)

        if (enable) {
            // 计算下一个整点时间
            val calendar = Calendar.getInstance().apply {
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.HOUR_OF_DAY, 1)
            }

            // 设置重复闹钟，每小时触发一次
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_HOUR,
                pendingIntent
            )
            Log.d(TAG, "已设置整点闹钟，下次触发：${calendar.time}")
        } else {
            // 取消闹钟
            alarmManager.cancel(pendingIntent)
            Log.d(TAG, "已取消整点闹钟")
        }
    }

    fun testChime(context: Context) {
        viewModelScope.launch {
            speakCurrentTime(context)
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            _lastChimeTime.value = "测试报时：$currentTime"
        }
    }

    private fun speakCurrentTime(context: Context) {
        if (!isTTSReady) {
            Log.e(TAG, "TTS 未准备好")
            return
        }

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timeText = if (minute == 0) {
            "现在是${hour}点整"
        } else {
            "现在是${hour}点${minute}分"
        }

        Log.d(TAG, "播报时间：$timeText")
        textToSpeech?.speak(timeText, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun requestIgnoreBatteryOptimization(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
            val intent = Intent().apply {
                action = android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
        }
    }

    suspend fun checkPermissionsAndStatus(context: Context) {
        // 检查是否已启用（从 SharedPreferences 读取）
        val prefs = context.getSharedPreferences("hourly_chime_prefs", Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean("chime_enabled", false)
        _isChimeEnabled.value = enabled
        
        // 读取上次报时时间
        val lastTime = prefs.getString("last_chime_time", null)
        _lastChimeTime.value = lastTime
    }

    fun saveLastChimeTime(context: Context, time: String) {
        _lastChimeTime.value = time
        val prefs = context.getSharedPreferences("hourly_chime_prefs", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("last_chime_time", time)
            apply()
        }
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.shutdown()
    }
}

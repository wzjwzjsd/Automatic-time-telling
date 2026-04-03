package com.example.hourlychime.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*

class HourlyChimeReceiver : BroadcastReceiver() {

    private val TAG = "HourlyChimeReceiver"
    private var textToSpeech: TextToSpeech? = null

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "收到整点报时广播")

        // 初始化 TTS
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.CHINA)
                if (result == TextToSpeech.LANG_MISSING_DATA || 
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "中文语音包不可用")
                } else {
                    speakCurrentTime(context)
                }
            } else {
                Log.e(TAG, "TTS 初始化失败")
            }
        }

        // 保存报时记录
        saveChimeRecord(context)
    }

    private fun speakCurrentTime(context: Context) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        
        val timeText = "现在是${hour}点整"
        
        Log.d(TAG, "播报时间：$timeText")
        textToSpeech?.speak(timeText, TextToSpeech.QUEUE_FLUSH, null, null)
        
        // 延迟关闭 TTS
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            textToSpeech?.shutdown()
        }, 3000)
    }

    private fun saveChimeRecord(context: Context) {
        val prefs = context.getSharedPreferences("hourly_chime_prefs", Context.MODE_PRIVATE)
        val currentTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            .format(Date())
        
        prefs.edit().apply {
            putString("last_chime_time", currentTime)
            apply()
        }
        
        Log.d(TAG, "已保存报时记录：$currentTime")
    }
}

package com.example.hourlychime

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.hourlychime.databinding.ActivityMainBinding
import com.example.hourlychime.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 观察报时开关状态
        viewModel.isChimeEnabled.observe(this) { enabled ->
            binding.switchChime.isChecked = enabled
            updateUI(enabled)
        }

        // 观察最后报时时间
        viewModel.lastChimeTime.observe(this) { time ->
            binding.textLastChime.text = "上次报时：${time ?: "暂无"}"
        }

        // 开关监听
        binding.switchChime.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.enableChime(this)
            } else {
                viewModel.disableChime(this)
            }
        }

        // 请求电池优化忽略按钮
        binding.btnRequestBatteryOptimization.setOnClickListener {
            viewModel.requestIgnoreBatteryOptimization(this)
        }

        // 测试报时按钮
        binding.btnTestChime.setOnClickListener {
            viewModel.testChime(this)
        }

        // 检查权限和状态
        lifecycleScope.launch {
            viewModel.checkPermissionsAndStatus(this@MainActivity)
        }
    }

    private fun updateUI(enabled: Boolean) {
        binding.textStatus.text = if (enabled) {
            "整点报时已启用 ✓"
        } else {
            "整点报时已禁用 ✗"
        }
        binding.textStatus.setTextColor(
            getColor(if (enabled) android.R.color.holo_green_dark else android.R.color.holo_red_dark)
        )
    }
}

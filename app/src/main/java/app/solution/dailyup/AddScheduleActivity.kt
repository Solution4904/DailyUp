package app.solution.dailyup

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.solution.dailyup.databinding.ActivityAddscheduleBinding

class AddScheduleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddscheduleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddscheduleBinding.inflate(layoutInflater)

        enableEdgeToEdge()

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setButtonsEvent()
    }

    private fun setButtonsEvent() {
        binding.btnConfirm.setOnClickListener { finish() }
        binding.btnCancel.setOnClickListener { finish() }
    }
}
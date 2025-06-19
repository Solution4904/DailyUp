package app.solution.dailyup

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.solution.dailyup.databinding.ActivityAddscheduleBinding
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.utility.ConstKeys
import java.util.UUID
import kotlin.apply

class AddScheduleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddscheduleBinding
    private lateinit var scheduleModel: ScheduleModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddscheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setLoad()
        setButtonsEvent()
    }

    private fun setLoad() {
        if (intent.hasExtra(ConstKeys.SCHEDULE_ID)
            || intent.hasExtra(ConstKeys.SCHEDULE_TITLE)
            || intent.hasExtra(ConstKeys.SCHEDULE_DEC)
            || intent.hasExtra(ConstKeys.SCHEDULE_ICON)
        ) {
            scheduleModel = ScheduleModel(
                id = intent.getStringExtra(ConstKeys.SCHEDULE_ID).toString(),
                title = intent.getStringExtra(ConstKeys.SCHEDULE_TITLE).toString(),
                dec = intent.getStringExtra(ConstKeys.SCHEDULE_DEC).toString(),
                iconResId = intent.getStringExtra(ConstKeys.SCHEDULE_ICON).toString()
            )

            binding.etTitle.setText(scheduleModel.title)
            binding.etDec.setText(scheduleModel.dec)
        }
    }

    private fun setButtonsEvent() {
        binding.btnConfirm.setOnClickListener {
            scheduleModel.title = binding.etTitle.text.toString()
            scheduleModel.dec = binding.etDec.text.toString()

            val resultIntent = Intent().apply {
                putExtra(ConstKeys.SCHEDULE_ID, scheduleModel.id)
                putExtra(ConstKeys.SCHEDULE_TITLE, scheduleModel.title)
                putExtra(ConstKeys.SCHEDULE_DEC, scheduleModel.dec)
                putExtra(ConstKeys.SCHEDULE_ICON, scheduleModel.iconResId)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.btnCancel.setOnClickListener { finish() }
    }
}
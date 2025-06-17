package app.solution.dailyup

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.solution.dailyup.databinding.ActivityAddscheduleBinding
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.utility.ConstKeys
import java.util.UUID

class AddScheduleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddscheduleBinding
    private var schedule: ScheduleModel? = null

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
        intent.getStringExtra(ConstKeys.INTENT_EXTRA)?.let {
            MyAppication.localDataManager.getData(ConstKeys.SCHEDULE_LIST)?.let {
                schedule = MyAppication.localDataManager.deserialization(it, ScheduleModel::class.java)

                binding.etTitle.setText(schedule?.title)
                binding.etDec.setText(schedule?.dec)
            }
        }
    }

    private fun setButtonsEvent() {
        binding.btnConfirm.setOnClickListener {
            val schedule = schedule ?: ScheduleModel(
                id = UUID.randomUUID().toString(),
                title = binding.etTitle.text.toString(),
                dec = binding.etDec.text.toString(),
                iconResId = 0
            )

            val data = MyAppication.localDataManager.serialization(schedule)
            MyAppication.localDataManager.setData("${ConstKeys.SCHEDULE_LIST}_${schedule.id}", data)

            finish()
        }

        binding.btnCancel.setOnClickListener { finish() }
    }
}
package app.solution.dailyup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.solution.dailyup.databinding.ActivityAddscheduleBinding
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.utility.ConstKeys
import com.bumptech.glide.Glide
import java.util.UUID
import kotlin.apply

class AddScheduleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddscheduleBinding
    private lateinit var scheduleModel: ScheduleModel
    private lateinit var intentLauncher: ActivityResultLauncher<Intent>

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

        initData()
        initField()

        setIntentLauncher()
        setButtonsEvent()
    }

    private fun initData() {
        val _id = if (intent.hasExtra(ConstKeys.SCHEDULE_ID)) intent.getStringExtra(ConstKeys.SCHEDULE_ID).toString() else UUID.randomUUID().toString()
        val _title = if (intent.hasExtra(ConstKeys.SCHEDULE_TITLE)) intent.getStringExtra(ConstKeys.SCHEDULE_TITLE) else ""
        val _dec = if (intent.hasExtra(ConstKeys.SCHEDULE_DEC)) intent.getStringExtra(ConstKeys.SCHEDULE_DEC) else ""
        val _icon = if (intent.hasExtra(ConstKeys.SCHEDULE_ICON)) intent.getStringExtra(ConstKeys.SCHEDULE_ICON) else ""

        scheduleModel = ScheduleModel(
            id = _id,
            title = _title,
            dec = _dec,
            iconResId = _icon,
        )
    }

    private fun initField() {
        binding.etTitle.setText(scheduleModel.title)
        binding.etDec.setText(scheduleModel.dec)
    }

    private fun setButtonsEvent() {
        binding.ibtnIcon.setOnClickListener { popupIconList() }

        binding.btnConfirm.setOnClickListener { save() }

        binding.btnCancel.setOnClickListener { cancel() }
    }

    private fun setIntentLauncher() {
        intentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    val fileName = it.getStringExtra(ConstKeys.SCHEDULE_ICON)

                    Glide.with(baseContext)
                        .load(fileName)
                        .into(binding.ibtnIcon)
                }
            }
        }
    }

    private fun popupIconList() {
        val fragment = ScheduleIconSelectorBottomSheet(
            onItemClick = { resId ->
                binding.ibtnIcon.apply {
                    setImageResource(resId)
                    tag = resId
                }
            }
        )
        fragment.show(supportFragmentManager, fragment.tag)
    }

    private fun save() {
        scheduleModel.apply {
            title = binding.etTitle.text.toString()
            dec = binding.etDec.text.toString()
            iconResId = binding.ibtnIcon.tag.toString()
        }

        val resultIntent = Intent().apply {
            putExtra(ConstKeys.SCHEDULE_ID, scheduleModel.id)
            putExtra(ConstKeys.SCHEDULE_TITLE, scheduleModel.title)
            putExtra(ConstKeys.SCHEDULE_DEC, scheduleModel.dec)
            putExtra(ConstKeys.SCHEDULE_ICON, scheduleModel.iconResId)
        }
        setResult(RESULT_OK, resultIntent)

        finish()
    }

    private fun cancel() {
        finish()
    }
}
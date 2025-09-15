package app.solution.dailyup

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import app.solution.dailyup.databinding.ActivityAddscheduleBinding
import app.solution.dailyup.utility.ConstKeys
import app.solution.dailyup.utility.ScheduleTypeEnum
import app.solution.dailyup.utility.TraceLog
import app.solution.dailyup.viewmodel.ScheduleViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.UUID

class AddScheduleActivity : AppCompatActivity() {
    //    Variable
    private lateinit var binding: ActivityAddscheduleBinding
    private val viewModel: ScheduleViewModel by viewModels()
    //    private lateinit var selectIconResultLauncher: ActivityResultLauncher<Intent>


    //    LifeCycle
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_addschedule)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initData()

        setButtonsEvent()
    }

    //    Function
    private fun initData() {
        with(intent)
        {
            if (hasExtra(ConstKeys.SCHEDULE_ID)) {
                viewModel.type.value = ScheduleTypeEnum.convert(getStringExtra(ConstKeys.SCHEDULE_TYPE).toString())
                viewModel.id.value = getStringExtra(ConstKeys.SCHEDULE_ID).toString()
                viewModel.title.value = getStringExtra(ConstKeys.SCHEDULE_TITLE).toString()
                viewModel.dec.value = getStringExtra(ConstKeys.SCHEDULE_DEC).toString()
                viewModel.date.value = getStringExtra(ConstKeys.SCHEDULE_DATE).toString()
                viewModel.iconResId.value = getIntExtra(ConstKeys.SCHEDULE_ICONNAME, viewModel.iconResId.value ?: R.drawable.ic_schedule_default)
                viewModel.processMaxValue.value = getIntExtra(ConstKeys.SCHEDULE_MAXVALUE, viewModel.processMaxValue.value ?: 1)
                viewModel.processValueStep.value = getIntExtra(ConstKeys.SCHEDULE_VALUESTEP, viewModel.processValueStep.value ?: 1)
                viewModel.processValue.value = getIntExtra(ConstKeys.SCHEDULE_VALUE, viewModel.processValue.value ?: 0)

            }
        }

        TraceLog(message = "initData -> ${viewModel.scheduleModels.value}")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setButtonsEvent() {
        binding.apply {
            etDate.setOnClickListener {
                popupDatePicker()
            }
            ibtnIcon.setOnClickListener {
                popupIconList()
            }

            btnType.setOnClickListener {
                popupTypeList()
            }

            btnConfirm.setOnClickListener {
                save()
            }

            btnCancel.setOnClickListener {
                cancel()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun popupDatePicker() {
        val selectedDay = if (!viewModel.date.value.isNullOrEmpty()) {
            LocalDate.parse(viewModel.date.value).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        } else {
            MaterialDatePicker.todayInUtcMilliseconds()
        }

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("일정 날짜 선택")
            .setSelection(selectedDay)
            .build()
        datePicker.show(supportFragmentManager, "datePicker")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = Date(selection)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formattedDate = sdf.format(selectedDate)

            viewModel.date.value = formattedDate

            binding.etDate.setText(viewModel.date.value)
        }
    }

    private fun popupTypeList() {
        val scheduleTypes = resources.getStringArray(R.array.schedule_type_array)

        MaterialAlertDialogBuilder(this@AddScheduleActivity)
            .setTitle("완료 방식")
            .setItems(scheduleTypes) { dialog, which ->
                viewModel.type.value = ScheduleTypeEnum.convert(which)
            }.show()
    }

    private fun popupIconList() {
        val fragment = ScheduleIconSelectorBottomSheet(
            onItemClick = { resId ->
                viewModel.iconResId.value = resId
            }
        )

        fragment.show(supportFragmentManager, fragment.tag)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun save() {
        val id = viewModel.id.value?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
        val date = viewModel.date.value ?: LocalDate.now().toString()
        val title = binding.etTitle.text.toString()
        val dec = binding.etDec.text.toString()
        val iconResId = viewModel.iconResId.value ?: R.drawable.ic_schedule_default
        val type = viewModel.type.value ?: ScheduleTypeEnum.NORMAL
        val processMaxValue = binding.etCountingMax.text.toString().toIntOrNull() ?: 1
        val processValueStep = binding.etCountingValueStep.text.toString().toIntOrNull() ?: 1
        val processValue = viewModel.processValue.value ?: 0

        val resultIntent = Intent().apply {
            with(viewModel) {
                putExtra(ConstKeys.SCHEDULE_ID, id)
                putExtra(ConstKeys.SCHEDULE_DATE, date)
                putExtra(ConstKeys.SCHEDULE_TITLE, title)
                putExtra(ConstKeys.SCHEDULE_DEC, dec)
                putExtra(ConstKeys.SCHEDULE_ICONNAME, iconResId)
                putExtra(ConstKeys.SCHEDULE_TYPE, type.toString())
                putExtra(ConstKeys.SCHEDULE_MAXVALUE, processMaxValue)
                putExtra(ConstKeys.SCHEDULE_VALUESTEP, processValueStep)
                putExtra(ConstKeys.SCHEDULE_VALUE, processValue)
            }
        }
        setResult(RESULT_OK, resultIntent)

        val logText = StringBuilder()
        val bundle = resultIntent.extras
        if (bundle != null) {
            for (key in bundle.keySet()) {
                val value = bundle[key]
                logText.append("\n$key : $value")
            }
        }
        TraceLog(message = "save -> $logText")

        finish()
    }

    private fun cancel() {
        finish()
    }
}
package app.solution.dailyup

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
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
    private lateinit var scheduleViewModel: ScheduleViewModel
    //    private lateinit var selectIconResultLauncher: ActivityResultLauncher<Intent>


    //    LifeCycle
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleViewModel = ViewModelProvider(this)[ScheduleViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_addschedule)
        binding.lifecycleOwner = this
        binding.viewModel = this.scheduleViewModel

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
                scheduleViewModel.type.value = ScheduleTypeEnum.convert(getStringExtra(ConstKeys.SCHEDULE_TYPE).toString())
                scheduleViewModel.id.value = getStringExtra(ConstKeys.SCHEDULE_ID).toString()
                scheduleViewModel.title.value = getStringExtra(ConstKeys.SCHEDULE_TITLE).toString()
                scheduleViewModel.dec.value = getStringExtra(ConstKeys.SCHEDULE_DEC).toString()
                scheduleViewModel.iconResId.value = getIntExtra(ConstKeys.SCHEDULE_ICONNAME, scheduleViewModel.iconResId.value ?: R.drawable.ic_schedule_default)
                scheduleViewModel.processMaxValue.value = getIntExtra(ConstKeys.SCHEDULE_MAXVALUE, scheduleViewModel.processMaxValue.value ?: 1)
                scheduleViewModel.processValueStep.value = getIntExtra(ConstKeys.SCHEDULE_VALUESTEP, scheduleViewModel.processValueStep.value ?: 1)
                scheduleViewModel.processValue.value = getIntExtra(ConstKeys.SCHEDULE_VALUE, scheduleViewModel.processValue.value ?: 0)

            }
        }

        TraceLog(message = "initData -> ${scheduleViewModel.scheduleModels.value}")
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
        val selectedDay = if (!scheduleViewModel.date.value.isNullOrEmpty()) {
            LocalDate.parse(scheduleViewModel.date.value).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
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

            scheduleViewModel.date.value = formattedDate

            binding.etDate.setText(scheduleViewModel.date.value)
        }
    }

    private fun popupTypeList() {
        val scheduleTypes = resources.getStringArray(R.array.schedule_type_array)

        MaterialAlertDialogBuilder(this@AddScheduleActivity)
            .setTitle("완료 방식")
            .setItems(scheduleTypes) { dialog, which ->
                scheduleViewModel.type.value = ScheduleTypeEnum.convert(which)
            }.show()
    }

    private fun popupIconList() {
        val fragment = ScheduleIconSelectorBottomSheet(
            onItemClick = { resId ->
                scheduleViewModel.iconResId.value = resId
            }
        )

        fragment.show(supportFragmentManager, fragment.tag)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun save() {
        val id = scheduleViewModel.id.value?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
        val date = LocalDate.now().toString()
        val title = binding.etTitle.text.toString()
        val dec = binding.etDec.text.toString()
        val iconResId = scheduleViewModel.iconResId.value ?: R.drawable.ic_schedule_default
        val type = scheduleViewModel.type.value ?: ScheduleTypeEnum.NORMAL
        val processMaxValue = binding.etCountingMax.text.toString().toIntOrNull() ?: 1
        val processValueStep = binding.etCountingValueStep.text.toString().toIntOrNull() ?: 1
        val processValue = scheduleViewModel.processValue.value ?: 0

        val resultIntent = Intent().apply {
            with(scheduleViewModel) {
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
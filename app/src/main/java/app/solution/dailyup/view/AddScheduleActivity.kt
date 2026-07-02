package app.solution.dailyup.view

import android.content.Intent
import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import app.solution.dailyup.BaseActivity
import app.solution.dailyup.R
import app.solution.dailyup.view.ScheduleIconSelectorBottomSheet
import app.solution.dailyup.databinding.ActivityAddscheduleBinding
import app.solution.dailyup.event.AddScheduleUiEvent
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.utility.ConstKeys
import app.solution.dailyup.utility.RepeatTypeEnum
import app.solution.dailyup.utility.ScheduleTypeEnum
import app.solution.dailyup.utility.TraceLog
import app.solution.dailyup.viewmodel.AddScheduleViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale

class AddScheduleActivity : BaseActivity<ActivityAddscheduleBinding>(R.layout.activity_addschedule) {
    //    Variable
    private val viewModel: AddScheduleViewModel by viewModels()

    //    LifeCycle
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun init() {
        binding.viewModel = viewModel

        initIntentData()

        observeEvent()

        supportTwoWayBinding()
    }

    /**
     * Check intent data
     * 일정 편집으로 들어왔는지 확인 후 ViewModel에 데이터 세팅 호출.
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun initIntentData() {
        val scheduleModel = intent.getParcelableExtra(ConstKeys.SCHEDULE_MODEL, ScheduleModel::class.java) ?: return

        viewModel.setData(scheduleModel)
    }

    private fun supportTwoWayBinding() {
        binding.npHour.minValue = 0
        binding.npHour.maxValue = 23
        binding.npMinute.minValue = 0
        binding.npMinute.maxValue = 59

        binding.npHour.value = viewModel.hour.value ?: LocalTime.now().hour
        binding.npMinute.value = viewModel.minute.value ?: LocalTime.now().minute

        binding.etProgressMaxValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                val value = p0.toString().toIntOrNull() ?: 0
                viewModel.setProgressMaxValue(value.toString())
            }
        })

        binding.etProgressStepValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                val value = p0.toString().toIntOrNull() ?: 0
                viewModel.setProgressStepValue(value.toString())
            }
        })

        binding.npHour.setOnValueChangedListener { _, _, newVal ->
            viewModel.hour.value = newVal
        }

        binding.npMinute.setOnValueChangedListener { _, _, newVal ->
            viewModel.minute.value = newVal
        }

        when (viewModel.repeat.value) {
            RepeatTypeEnum.ONCE -> binding.rbRepeatOnce
            RepeatTypeEnum.WEEKLY -> binding.rbRepeatWeekly
            RepeatTypeEnum.MONTHLY -> binding.rbRepeatMonthly
        }.isChecked = true

        binding.rgRepeat.setOnCheckedChangeListener { _, checkedId ->
            viewModel.repeat.value = when (checkedId) {
                R.id.rb_repeat_once -> RepeatTypeEnum.ONCE
                R.id.rb_repeat_weekly -> RepeatTypeEnum.WEEKLY
                R.id.rb_repeat_monthly -> RepeatTypeEnum.MONTHLY
                else -> RepeatTypeEnum.ONCE
            }
        }
    }

    /**
     * Observe event
     * 이벤트 관찰
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeEvent() {
        lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is AddScheduleUiEvent.ShowDatePicker -> popupDatePicker()
                    is AddScheduleUiEvent.ShowIconPicker -> popupIconList()
                    is AddScheduleUiEvent.ShowTypePicker -> popupTypeList()
                    is AddScheduleUiEvent.ScheduleSave -> scheduleSave(event.scheduleModel)
                    is AddScheduleUiEvent.ScheduleCancel -> scheduleCancel()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleSave(scheduleModel: ScheduleModel) {
        val resultIntent = Intent().apply {
            putExtra(ConstKeys.SCHEDULE_MODEL, scheduleModel)
        }

        TraceLog(message = "scheduleSave -> $scheduleModel")

        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun scheduleCancel() = finish()

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

            viewModel.setDate(formattedDate)
        }
    }

    private fun popupTypeList() {
        val scheduleTypes = resources.getStringArray(R.array.schedule_type_array)

        MaterialAlertDialogBuilder(this@AddScheduleActivity)
            .setTitle("선택")
            .setItems(scheduleTypes) { dialog, which ->
                viewModel.setType(ScheduleTypeEnum.convertToType(which))
            }.show()
    }

    private fun popupIconList() {
        val fragment = ScheduleIconSelectorBottomSheet(
            onItemClick = { resId ->
                viewModel.setIconResId(resId)
            }
        )

        fragment.show(supportFragmentManager, fragment.tag)
    }
}
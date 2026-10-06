package app.solution.dailyup.view

import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.IntentCompat
import androidx.lifecycle.lifecycleScope
import app.solution.dailyup.BaseActivity
import app.solution.dailyup.R
import app.solution.dailyup.databinding.ActivityAddscheduleBinding
import app.solution.dailyup.event.AddScheduleUiEvent
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.utility.ConstKeys
import app.solution.dailyup.utility.RepeatTypeEnum
import app.solution.dailyup.utility.ScheduleTypeEnum
import app.solution.dailyup.utility.TraceLog
import app.solution.dailyup.viewmodel.AddScheduleViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset


class AddScheduleActivity : BaseActivity<ActivityAddscheduleBinding>(R.layout.activity_addschedule) {
    //    Variable
    private val viewModel: AddScheduleViewModel by viewModels()

    //    LifeCycle
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
    private fun initIntentData() {
        val scheduleModel = IntentCompat.getParcelableExtra(
            intent,
            ConstKeys.SCHEDULE_MODEL,
            ScheduleModel::class.java
        )

        if (scheduleModel != null) {
            viewModel.setData(scheduleModel)
            return
        }

        intent.getStringExtra(ConstKeys.SCHEDULE_DATE)?.let {
            viewModel.setDate(it)
        }
    }

    private fun supportTwoWayBinding() {
        binding.npHour.minValue = 0
        binding.npHour.maxValue = 23
        binding.npMinute.minValue = 0
        binding.npMinute.maxValue = 59

        binding.npHour.value = viewModel.hour.value ?: 0
        binding.npMinute.value = viewModel.minute.value ?: 0

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

    private fun scheduleSave(scheduleModel: ScheduleModel) {
        val resultIntent = Intent().apply {
            putExtra(ConstKeys.SCHEDULE_MODEL, scheduleModel)
        }

        TraceLog(message = "scheduleSave -> $scheduleModel")

        setResult(RESULT_OK, resultIntent)
        finish()
    }

    private fun scheduleCancel() = finish()

    private fun popupDatePicker() {
        val date = viewModel.date.value?.takeIf {
            it.isNotEmpty()
        }?.let {
            LocalDate.parse(it)
        } ?: LocalDate.now()

        val today = LocalDate.now()
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()

        val constraints = CalendarConstraints.Builder()
            .setStart(today)
            .setValidator(DateValidatorPointForward.from(today))
            .build()

        val selectedDay = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("일정 날짜 선택")
            .setSelection(selectedDay)
            .setCalendarConstraints(constraints)
            .build()
        datePicker.show(supportFragmentManager, "datePicker")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val selectedDate = Instant.ofEpochMilli(selection)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()

            if (selectedDate.isBefore(LocalDate.now())) {
                Toast.makeText(baseContext, "오늘보다 이전의 일정은 등록할 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@addOnPositiveButtonClickListener
            }

            viewModel.setDate(selectedDate.toString())
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
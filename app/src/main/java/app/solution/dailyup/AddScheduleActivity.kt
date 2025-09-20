package app.solution.dailyup

import android.os.Build
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import app.solution.dailyup.databinding.ActivityAddscheduleBinding
import app.solution.dailyup.event.AddScheduleUiEvent
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.utility.ConstKeys
import app.solution.dailyup.utility.ScheduleTypeEnum
import app.solution.dailyup.viewmodel.AddScheduleViewModel
import app.solution.dailyup.viewmodel.ScheduleViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import java.util.UUID

class AddScheduleActivity : BaseActivity<ActivityAddscheduleBinding>(R.layout.activity_addschedule) {
    //    Variable
    private val viewModel: AddScheduleViewModel by viewModels()
    private val scheduleViewModel: ScheduleViewModel by viewModels()
    //    private lateinit var selectIconResultLauncher: ActivityResultLauncher<Intent>

    //    LifeCycle
    /*@RequiresApi(Build.VERSION_CODES.O)
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
    }*/

    @RequiresApi(Build.VERSION_CODES.O)
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
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initIntentData() {
        val scheduleModel = ScheduleModel(
            id = (intent.getStringExtra(ConstKeys.SCHEDULE_ID) ?: UUID.randomUUID()).toString(),
            date = (intent.getStringExtra(ConstKeys.SCHEDULE_DATE) ?: LocalDate.now()).toString(),
            title = (intent.getStringExtra(ConstKeys.SCHEDULE_TITLE) ?: "").toString(),
            dec = (intent.getStringExtra(ConstKeys.SCHEDULE_DEC) ?: "").toString(),
            iconResId = intent.getIntExtra(ConstKeys.SCHEDULE_ICONNAME, R.drawable.ic_schedule_default),
            type = ScheduleTypeEnum.convertToType(intent.getStringExtra(ConstKeys.SCHEDULE_TYPE).toString()),
            progressMaxValue = intent.getIntExtra(ConstKeys.SCHEDULE_MAXVALUE, 1),
            progressStepValue = intent.getIntExtra(ConstKeys.SCHEDULE_VALUESTEP, 1),
            progressValue = intent.getIntExtra(ConstKeys.SCHEDULE_VALUE, 0)
        )

        viewModel.setData(scheduleModel)
    }

    private fun supportTwoWayBinding() {
        binding.etProgressMaxValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                viewModel.setProgressMaxValue(p0.toString())
            }
        })

        binding.etProgressStepValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                viewModel.setProgressStepValue(p0.toString())
            }
        })
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

    //    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleSave(scheduleModel: ScheduleModel) {
        scheduleViewModel.upsertSchedule(scheduleModel)

        finish()
    }

    private fun scheduleCancel() = finish()

    /*private fun initData() {
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
    }*/

    /*@RequiresApi(Build.VERSION_CODES.O)
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
    }*/

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

//            `binding`.etDate.setText(viewModel.date.value)
        }
    }

    private fun popupTypeList() {
        val scheduleTypes = resources.getStringArray(R.array.schedule_type_array)

        MaterialAlertDialogBuilder(this@AddScheduleActivity)
            .setTitle("완료 방식")
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

    /*@RequiresApi(Build.VERSION_CODES.O)
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
    }*/
}
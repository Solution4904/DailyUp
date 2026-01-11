package app.solution.dailyup.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.solution.dailyup.event.AddScheduleUiEvent
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.utility.ScheduleTypeEnum
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.UUID

class AddScheduleViewModel : ViewModel() {
    private val _uiEvent = MutableSharedFlow<AddScheduleUiEvent>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val uiEvent = _uiEvent.asSharedFlow()

    private val _id = MutableLiveData<String>(UUID.randomUUID().toString())
    val id: LiveData<String> = _id

    @RequiresApi(Build.VERSION_CODES.O)
    val date = MutableLiveData<String>("")
    val title = MutableLiveData<String>("")
    val dec = MutableLiveData<String>("")
    val iconResId = MutableLiveData<Int?>(0)
    val type = MutableLiveData<ScheduleTypeEnum>(ScheduleTypeEnum.NORMAL)

    private val _progressMaxValue = MutableLiveData<Int?>(1)
    val progressMaxValue: LiveData<Int?> = _progressMaxValue

    private val _progressStepValue = MutableLiveData<Int?>(1)
    val progressStepValue: LiveData<Int?> = _progressStepValue

    private val _progressValue = MutableLiveData<Int?>(0)
    val progressValue = _progressValue


    @RequiresApi(Build.VERSION_CODES.O)
    fun setData(param: ScheduleModel) {
        _id.value = param.id
        date.value = param.date
        title.value = param.title
        dec.value = param.dec
        iconResId.value = param.iconResId
        type.value = param.type
        _progressMaxValue.value = param.progressMaxValue
        _progressStepValue.value = param.progressStepValue
        _progressValue.value = param.progressValue
    }

    fun setIconResId(resId: Int) {
        iconResId.value = resId
    }

    fun setType(param: ScheduleTypeEnum) {
        type.value = param
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setDate(param: String) {
        date.value = param
    }

    fun setProgressMaxValue(param: String) {
        _progressMaxValue.value = param.toInt()
    }

    fun setProgressStepValue(param: String) {
        _progressStepValue.value = param.toInt()
    }

    /*@RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleSave() {
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

    fun onDateClicked() {
        viewModelScope.launch {
            _uiEvent.emit(AddScheduleUiEvent.ShowDatePicker)
        }
    }

    fun onIconClicked() {
        viewModelScope.launch {
            _uiEvent.emit(AddScheduleUiEvent.ShowIconPicker)
        }
    }

    fun onTypeClicked() {
        viewModelScope.launch {
            _uiEvent.emit(AddScheduleUiEvent.ShowTypePicker)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onConfirmClicked() {
        val data = ScheduleModel(
            id = _id.value!!,
            date = date.value!!,
            title = title.value!!,
            dec = dec.value!!,
            iconResId = iconResId.value,
            type = type.value!!,
            progressMaxValue = progressMaxValue.value!!,
            progressStepValue = progressStepValue.value!!,
            progressValue = progressValue.value!!,
        )
        viewModelScope.launch {
            _uiEvent.emit(
                AddScheduleUiEvent.ScheduleSave(data)
            )
        }
    }

    fun onCancelClicked() {
        viewModelScope.launch {
            _uiEvent.emit(AddScheduleUiEvent.ScheduleCancel)
        }
    }
}
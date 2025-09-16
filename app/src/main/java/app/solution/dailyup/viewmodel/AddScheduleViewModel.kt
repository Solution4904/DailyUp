package app.solution.dailyup.viewmodel

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

class AddScheduleViewModel : ViewModel() {
    private val _uiEvent = MutableSharedFlow<AddScheduleUiEvent>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val uiEvent = _uiEvent.asSharedFlow()

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private val _dec = MutableLiveData<String>()
    val dec: LiveData<String> = _dec

    private val _iconResId = MutableLiveData<Int>()
    val iconResId: LiveData<Int> = _iconResId

    private val _type = MutableLiveData<ScheduleTypeEnum>()
    val type: LiveData<ScheduleTypeEnum> = _type

    private val _processMaxValue = MutableLiveData<Int>()
    val processMaxValue: LiveData<Int> = _processMaxValue

    private val _processValueStep = MutableLiveData<Int>()
    val processValueStep: LiveData<Int> = _processValueStep


    fun setData(scheduleModel: ScheduleModel?) {

    }

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

    fun onConfirmClicked() {
        viewModelScope.launch {
            _uiEvent.emit(AddScheduleUiEvent.ScheduleSave)
        }
    }

    fun onCancelClicked() {
        viewModelScope.launch {
            _uiEvent.emit(AddScheduleUiEvent.ScheduleCancel)
        }
    }
}
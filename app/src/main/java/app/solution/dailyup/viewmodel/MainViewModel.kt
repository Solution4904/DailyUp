package app.solution.dailyup.viewmodel

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.solution.dailyup.event.NavigationEvent
import app.solution.dailyup.R
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.event.MainUiEvent
import app.solution.dailyup.utility.ConstKeys
import app.solution.dailyup.utility.ScheduleTypeEnum
import app.solution.dailyup.utility.TraceLog
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainViewModel : ViewModel() {
    @RequiresApi(Build.VERSION_CODES.O)
    private val _currentCalendar = MutableLiveData<LocalDate>(LocalDate.now())

    @RequiresApi(Build.VERSION_CODES.O)
    val currentCalendar: LiveData<LocalDate> = _currentCalendar

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    private val _scheduleModel = MutableLiveData<ScheduleModel>()
    val scheduleModel: LiveData<ScheduleModel> = _scheduleModel

    @RequiresApi(Build.VERSION_CODES.O)
    private var currentWeek: LocalDate = LocalDate.now()

    private val _uiEvent = MutableSharedFlow<MainUiEvent>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val uiEvent = _uiEvent.asSharedFlow()


    /**
     * Receive schedule data with intent
     * 일정 추가 화면에서 데이터 받아오기
     * @param intent
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun receiveScheduleDataWithIntent(intent: Intent) {
        _scheduleModel.value = ScheduleModel(
            id = intent.getStringExtra(ConstKeys.SCHEDULE_ID).toString(),
            title = intent.getStringExtra(ConstKeys.SCHEDULE_TITLE).toString(),
            date = intent.getStringExtra(ConstKeys.SCHEDULE_DATE).toString(),
            dec = intent.getStringExtra(ConstKeys.SCHEDULE_DEC).toString(),
            iconResId = intent.getIntExtra(ConstKeys.SCHEDULE_ICONNAME, R.drawable.ic_schedule_default),
            type = ScheduleTypeEnum.convert(intent.getStringExtra(ConstKeys.SCHEDULE_TYPE).toString()),
            processMaxValue = intent.getIntExtra(ConstKeys.SCHEDULE_MAXVALUE, 1),
            processValueStep = intent.getIntExtra(ConstKeys.SCHEDULE_VALUESTEP, 1),
            processValue = intent.getIntExtra(ConstKeys.SCHEDULE_VALUE, 0)
        )

        TraceLog(message = "receiveScheduleDataWithIntent -> $scheduleModel")
    }

    fun onScheduleCompleteClick(scheduleModel: ScheduleModel) {
        viewModelScope.launch {
            _uiEvent.emit(MainUiEvent.ScheduleComplete(scheduleModel))
        }
    }

    fun onScheduleIncreaseProcessClick(scheduleModel: ScheduleModel) {
        viewModelScope.launch {
            _uiEvent.emit(MainUiEvent.ScheduleIncreaseProcess(scheduleModel))
        }
    }

    /**
     * On edit schedule click
     * 일정 수정 화면으로 이동
     * @param scheduleModel 수정 대상 ScheduleModel
     */
    fun onEditScheduleClick(scheduleModel: ScheduleModel) {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.MoveToEditScheduleActivity(scheduleModel))
        }
    }

    /**
     * On schedule item lonk clicked
     * 일정 제거 확인 다이얼로그 팝업
     */
    fun onScheduleDeleteDialog(scheduleModel: ScheduleModel) {
        viewModelScope.launch {
            _uiEvent.emit(MainUiEvent.ShowDeleteScheduleDialog(scheduleModel))
        }
    }

    /**
     * On move to another week
     * 주간 달력 주차 이동
     * @param request 이동할 주차(+1, -1)
     */
    fun onMoveToAnotherWeek(request: Int) {
        currentWeek = currentWeek.plusWeeks(request.toLong())

        _currentCalendar.value = currentWeek

        TraceLog(message = "onMoveToAnotherWeek -> ${_currentCalendar.value}")
    }

    /**
     * On move add schedule click
     * 일정 추가 화면으로 이동
     */
    fun onMoveAddScheduleClick() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.MoveToAddScheduleActivity)
        }
    }

    /**
     * On move chart click
     * 통계 화면으로 이동
     */
    fun onMoveChartClick() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.MoveToChartActivity)
        }
    }

    /**
     * On move setting click
     * 옵션 화면으로 이동
     */
    fun onMoveSettingClick() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.MoveToSettingActivity)
        }
    }
}
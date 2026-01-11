package app.solution.dailyup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.solution.dailyup.R
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.utility.LocalDataManager
import app.solution.dailyup.utility.ScheduleTypeEnum
import app.solution.dailyup.utility.TraceLog

class ScheduleViewModel : ViewModel() {
    // # Variable
    val type = MutableLiveData<ScheduleTypeEnum>(ScheduleTypeEnum.NORMAL)
    val date = MutableLiveData<String>("")
    val id = MutableLiveData<String>("")
    val title = MutableLiveData<String>("")
    val dec = MutableLiveData<String>("")
    val iconResId = MutableLiveData<Int>(R.drawable.ic_schedule_default)
    val processMaxValue = MutableLiveData<Int>(1)
    val processValueStep = MutableLiveData<Int>(1)
    val processValue = MutableLiveData<Int>(0)

    private val _scheduleModels = MutableLiveData<List<ScheduleModel>>(emptyList())
    val scheduleModels: LiveData<List<ScheduleModel>> = _scheduleModels

    private var scheduleDatas: MutableList<ScheduleModel> = mutableListOf()

    // # LifeCycle
    init {
        scheduleDatas = LocalDataManager.getSchedules().toMutableList()

        TraceLog(message = "ScheduleViewModel 생성")
    }

    override fun onCleared() {
        super.onCleared()

        TraceLog(message = "ScheduleViewModel 파괴")
    }

    // # Function
    fun loadSchedules(date: String = "") {
        _scheduleModels.value = if (date.isEmpty()) {
            scheduleDatas
        } else {
            scheduleDatas.filter { it.date == date }
        }

        /*val lodedData = LocalDataManager.getSchedules()

        if (date == "") {
            _scheduleModels.value = lodedData
        } else {
            _scheduleModels.value = lodedData.filter { it.date == date }
        }*/

        TraceLog(message = "Schedule 로드 -> \nrequest date : $date\nsize : ${_scheduleModels.value?.size}\n${_scheduleModels.value}")
    }

    fun upsertSchedule(scheduleModel: ScheduleModel) {
        val index = scheduleDatas.indexOfFirst { it.id == scheduleModel.id }

        // 수정
        if (index != -1) {
            scheduleDatas[index] = scheduleModel

            TraceLog(message = "Schedule 수정 -> $scheduleModel")
        }
        // 추가
        else {
            scheduleDatas.add(scheduleModel)

            TraceLog(message = "Schedule 추가 -> $scheduleModel")
        }
        saveSchedules()

//        val currentDate = _scheduleModels.value?.firstOrNull { it.date.isNotEmpty() }?.date ?: ""
        loadSchedules(scheduleModel.date)

        /*val resultScheduleModel = _scheduleModels.value?.find { it.id == scheduleModel.id }

        if (resultScheduleModel != null) {
            editSchedule(scheduleModel)
        } else {
            addSchedule(scheduleModel)
        }*/
    }

    /*private fun editSchedule(scheduleModel: ScheduleModel) {
        _scheduleModels.value = _scheduleModels.value?.map { model ->
            if (model.id == scheduleModel.id) {
                scheduleModel
            } else {
                model
            }
        }

        if (_scheduleModels.value == null) return
        LocalDataManager.saveSchedules(_scheduleModels.value!!)

        TraceLog(message = "Schedule 수정 -> $scheduleModel")
    }

    private fun addSchedule(scheduleModel: ScheduleModel) {
        _scheduleModels.value?.let { datas ->

            val newDatas = datas + scheduleModel
            _scheduleModels.value = newDatas

            LocalDataManager.saveSchedules(newDatas)

//            TraceLog(message = "현재 데이터 -> $datas\n전달받은 데이터-> $scheduleModel\n새로 저장할 데이터 -> $newDatas")
//            TraceLog(message = "Schedule 추가 -> ${_scheduleModels.value}")
            TraceLog(message = "Schedule 추가 -> $scheduleModel")
        }
    }*/

    fun deleteSchedule(scheduleModel: ScheduleModel) {
        scheduleDatas.removeAll { it.id == scheduleModel.id }

        saveSchedules()

        /*_scheduleModels.value = _scheduleModels.value?.filter { it.id != scheduleModel.id }
        LocalDataManager.saveSchedules(_scheduleModels.value!!)*/

        TraceLog(message = "Schedule 삭제 -> $scheduleModel")
    }

    fun saveSchedules() {
        LocalDataManager.saveSchedules(scheduleDatas)

        TraceLog(message = "scheduleDatas 저장")
    }
}
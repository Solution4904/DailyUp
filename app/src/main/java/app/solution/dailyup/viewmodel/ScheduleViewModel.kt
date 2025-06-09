package app.solution.dailyup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.solution.dailyup.model.ScheduleModel

class ScheduleViewModel : ViewModel() {
    private val _datas = MutableLiveData<List<ScheduleModel>>(listOf())
    val datas: LiveData<List<ScheduleModel>> = _datas

    fun addSchedule(scheduleModel: ScheduleModel) {
        _datas.value = _datas.value?.plus(scheduleModel)
    }
}
package app.solution.dailyup.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.solution.dailyup.R
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.utility.ScheduleTypeEnum
import java.util.UUID

class ScheduleViewModel : ViewModel() {
    val scheduleModel = MutableLiveData<ScheduleModel>(ScheduleModel())



    fun getType() = scheduleModel.value?.type ?: ScheduleTypeEnum.NORMAL
    fun setType(p: ScheduleTypeEnum) {
        scheduleModel.value = scheduleModel.value?.copy(type = p)
    }

    fun getId() = scheduleModel.value?.id ?: UUID.randomUUID().toString()
    fun setId(p: String) {
        scheduleModel.value = scheduleModel.value?.copy(id = p)
    }

    fun getTitle() = scheduleModel.value?.title ?: ""
    fun setTitle(p: String) {
        scheduleModel.value = scheduleModel.value?.copy(title = p)
    }

    fun getDec() = scheduleModel.value?.dec ?: ""
    fun setDec(p: String) {
        scheduleModel.value = scheduleModel.value?.copy(dec = p)
    }

    fun getIconName() = scheduleModel.value?.iconResId ?: R.drawable.ic_schedule_default
    fun setIconName(p: Int) {
        scheduleModel.value = scheduleModel.value?.copy(iconResId = p)
    }

    fun getMaxValue() = scheduleModel.value?.maxValue ?: 1
    fun setMaxValue(p: Int) {
        scheduleModel.value = scheduleModel.value?.copy(maxValue = p)
    }

    fun getValueStep() = scheduleModel.value?.valueStep ?: 1
    fun setValueStep(p: Int) {
        scheduleModel.value = scheduleModel.value?.copy(valueStep = p)
    }

    fun getValue() = scheduleModel.value?.value ?: 0
    fun setValue(p: Int) {
        scheduleModel.value = scheduleModel.value?.copy(value = p)
    }
}
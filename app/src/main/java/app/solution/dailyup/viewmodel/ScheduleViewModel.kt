package app.solution.dailyup.viewmodel

import androidx.annotation.DrawableRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.solution.dailyup.R
import app.solution.dailyup.utility.ScheduleTypeEnum

class ScheduleViewModel : ViewModel() {
    val type = MutableLiveData(ScheduleTypeEnum.NORMAL)
    val id = MutableLiveData("")
    val title = MutableLiveData("")
    val dec = MutableLiveData("")
    @DrawableRes val iconResId = MutableLiveData<Int>(R.drawable.ic_schedule_default)
    val maxValue = MutableLiveData(1)
    val valueStep = MutableLiveData(1)
    val value = MutableLiveData(0)



    var temp : Int = 1
}
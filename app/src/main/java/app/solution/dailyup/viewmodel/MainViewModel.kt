package app.solution.dailyup.viewmodel

import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {


    /*fun setData(data : ScheduleModel) {
        if (scheduleList.find { it.id == scheduleModel.id } == null) {
            scheduleList.add(scheduleModel)
            Log.d("TAG", "새로 추가")
        } else {
            val targetIndex = scheduleList.indexOfFirst { it.id == scheduleModel.id }
            if (targetIndex < 0) return@registerForActivityResult

            viewModel.scheduleList[targetIndex] = scheduleModel
            Log.d("TAG", "수정 등록")
        }

        MyAppication.localDataManager.setData(ConstKeys.SCHEDULE_LIST, MyAppication.localDataManager.serialization(viewModel.scheduleList).toString())
    }

    fun onIconClick(position: Int) {
        Log.d("", "loadScheduleList: $position")
        val data = viewModel.scheduleList[position]
        Log.d("", "loadScheduleList: $data")
        val changeValue = data.value?.plus((data.valueStep ?: 1))
        Log.d("", "loadScheduleList: $changeValue")
        viewModel.scheduleList[position] = viewModel.scheduleList[position].copy(value = changeValue)
        adapter.notifyItemChanged(position)
    }

    fun clearScheduleList() {
        MyAppication.localDataManager.getData(ConstKeys.SCHEDULE_LIST)?.let {
            Log.d("TAG", "loadScheduleList: $it")
            viewModel.scheduleList.clear()
            viewModel.scheduleList.addAll(MyAppication.localDataManager.deserialization(it))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshScheduleList() {
        MyAppication.localDataManager.getData(ConstKeys.SCHEDULE_LIST)?.let {
            Log.d("TAG", "refreshScheduleList: $it")

            viewModel.scheduleList.clear()
            viewModel.scheduleList.addAll(MyAppication.localDataManager.deserialization(it))
        }
    }*/

//    fun getType() = scheduleModel.value?.type ?: ScheduleTypeEnum.NORMAL
//    fun setType(p: ScheduleTypeEnum) {
//        scheduleModel.value = scheduleModel.value?.copy(type = p)
//    }
//
//    fun getId() = scheduleModel.value?.id ?: UUID.randomUUID().toString()
//    fun setId(p: String) {
//        scheduleModel.value = scheduleModel.value?.copy(id = p)
//    }
//
//    fun getTitle() = scheduleModel.value?.title ?: ""
//    fun setTitle(p: String) {
//        scheduleModel.value = scheduleModel.value?.copy(title = p)
//    }
//
//    fun getDec() = scheduleModel.value?.dec ?: ""
//    fun setDec(p: String) {
//        scheduleModel.value = scheduleModel.value?.copy(dec = p)
//    }
//
//    fun getIconName() = scheduleModel.value?.iconResId ?: R.drawable.ic_schedule_default
//    fun setIconName(p: Int) {
//        scheduleModel.value = scheduleModel.value?.copy(iconResId = p)
//    }
//
//    fun getMaxValue() = scheduleModel.value?.maxValue ?: 1
//    fun setMaxValue(p: Int) {
//        scheduleModel.value = scheduleModel.value?.copy(maxValue = p)
//    }
//
//    fun getValueStep() = scheduleModel.value?.valueStep ?: 1
//    fun setValueStep(p: Int) {
//        scheduleModel.value = scheduleModel.value?.copy(valueStep = p)
//    }
//
//    fun getValue() = scheduleModel.value?.value ?: 0
//    fun setValue(p: Int) {
//        scheduleModel.value = scheduleModel.value?.copy(value = p)
//    }
}
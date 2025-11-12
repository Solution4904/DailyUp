package app.solution.dailyup.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import app.solution.dailyup.AppNavigator
import app.solution.dailyup.BaseActivity
import app.solution.dailyup.R
import app.solution.dailyup.adapter.CalendarAdapter
import app.solution.dailyup.adapter.ScheduleAdapter
import app.solution.dailyup.databinding.ActivityMainBinding
import app.solution.dailyup.event.MainUiEvent
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.utility.ConstKeys
import app.solution.dailyup.utility.ScheduleTypeEnum
import app.solution.dailyup.utility.TraceLog
import app.solution.dailyup.viewmodel.MainViewModel
import app.solution.dailyup.viewmodel.ScheduleViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    //    Variable
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var calendarAdapter: CalendarAdapter

    private val scheduleViewModel: ScheduleViewModel by viewModels()
    private val viewModel: MainViewModel by viewModels()

    private val appNavigator = AppNavigator()

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>


    //    LifeCycle
    override fun onResume() {
        super.onResume()

        // TODO: 각 리스트 최신화
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun init() {
        binding.viewModel = viewModel

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    val resultData = ScheduleModel(
                        type = ScheduleTypeEnum.convertToType(it.getStringExtra(ConstKeys.SCHEDULE_TYPE).toString()),
                        id = it.getStringExtra(ConstKeys.SCHEDULE_ID).toString(),
                        title = it.getStringExtra(ConstKeys.SCHEDULE_TITLE).toString(),
                        dec = it.getStringExtra(ConstKeys.SCHEDULE_DEC).toString(),
                        date = it.getStringExtra(ConstKeys.SCHEDULE_DATE).toString(),
                        iconResId = it.getIntExtra(ConstKeys.SCHEDULE_ICONNAME, -1).takeIf { it != -1 },
                        progressMaxValue = it.getIntExtra(ConstKeys.SCHEDULE_MAXVALUE, -1).takeIf { it != -1 },
                        progressStepValue = it.getIntExtra(ConstKeys.SCHEDULE_VALUESTEP, -1).takeIf { it != -1 },
                        progressValue = it.getIntExtra(ConstKeys.SCHEDULE_VALUE, -1).takeIf { it != -1 }
                    )

                    scheduleViewModel.upsertSchedule(resultData)
                    scheduleViewModel.loadSchedules()
                }
            }
        }

        setScheduleRecyclerViewAdapter()
        setCalendarRecyclerViewAdapter()

        observeViewModel()
        observeNavigation()
        observeEvent()
    }

    /**
     * Observe event
     * 이벤트 관찰
     */
    private fun observeEvent() {
        lifecycleScope.launch {
            viewModel.uiEvent.collect { event ->
                when (event) {
                    is MainUiEvent.ShowDeleteScheduleDialog -> popupScheduleItemDialog(event.scheduleModel)
                    is MainUiEvent.ScheduleComplete -> scheduleViewModel.upsertSchedule(event.scheduleModel)
                    is MainUiEvent.ScheduleIncreaseProcess -> scheduleViewModel.upsertSchedule(event.scheduleModel)
                }
            }
        }
    }

    /**
     * Observe view model
     * 데이터 변경 관찰
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeViewModel() {
        viewModel.currentCalendar.observe(this) { date ->
            calendarAdapter.updateDates(date)

            scheduleViewModel.loadSchedules(date.toString())
            // TODO: 날짜가 바뀔 때마다 로드를 하는 게 아닌 로드로 모든 데이터를 가져온 뒤 필터링으로 표시하는 방식으로 개선 필요 
        }

        viewModel.currentDate.observe(this) { date ->
            calendarAdapter.updateDates(date)

            scheduleViewModel.loadSchedules(date.toString())
        }

        viewModel.scheduleModel.observe(this) { scheduleModel ->
            scheduleViewModel.upsertSchedule(scheduleModel)
        }
    }

    /**
     * Observe navigation
     * 화면 이동 관리
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeNavigation() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.navigationEvents.collectLatest { event ->
                    appNavigator.navigate(this@MainActivity, event, activityResultLauncher)
                }
            }
        }
    }

    //    Function
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("NotifyDataSetChanged")
    private fun setScheduleRecyclerViewAdapter() {
        binding.layoutRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        scheduleAdapter = ScheduleAdapter(
            scheduleList = mutableListOf(),
            onItemClick = { position ->
                viewModel.onEditScheduleClick(scheduleViewModel.scheduleModels.value!![position])
            },
            onIconClickForNormalType = { position ->
                scheduleViewModel.scheduleModels.value?.let { scheduleModels ->
                    viewModel.onScheduleCompleteClick(scheduleModels[position].copy(isCompleted = true))
                }
            },
            onIconClickForCountingType = { position ->
                scheduleViewModel.scheduleModels.value?.let { scheduleModels ->
                    val targetScheduleModel = scheduleModels[position]

                    if (targetScheduleModel.progressMaxValue!! <= targetScheduleModel.progressValue!!) return@let

                    val calculatedValue = targetScheduleModel.progressValue.plus(targetScheduleModel.progressStepValue!!)
                    val value =
                        if (calculatedValue > targetScheduleModel.progressMaxValue) targetScheduleModel.progressMaxValue
                        else calculatedValue

                    viewModel.onScheduleIncreaseProcessClick(scheduleModels[position].copy(progressValue = value))
                }
            },
            onItemLongClick = { position ->
                viewModel.onScheduleDeleteDialog(scheduleViewModel.scheduleModels.value!![position])
            },
        )
        binding.layoutRecyclerview.adapter = scheduleAdapter

        scheduleViewModel.scheduleModels.observe(this) { list ->
            scheduleAdapter.updateList(list)

            TraceLog(message = "scheduleModels observe -> $list")
        }

        scheduleViewModel.loadSchedules(LocalDate.now().toString())
    }

    private fun popupScheduleItemDialog(scheduleModel: ScheduleModel) {
        scheduleViewModel.scheduleModels.value?.let { scheduleModels ->
            AlertDialog.Builder(this@MainActivity).apply {
                setTitle("제거 확인")
                setMessage("선택하신 스케줄을 제거하시겠습니까?")
                setPositiveButton("제거") { _, _ ->
                    scheduleViewModel.deleteSchedule(scheduleModel)
                }
                setNegativeButton("취소") { _, _ -> }
            }.show()
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setCalendarRecyclerViewAdapter() {
        binding.viewCalendar.layoutManager = GridLayoutManager(this@MainActivity, 7)

        calendarAdapter = CalendarAdapter(
            onDateClickEvent = { currentDate ->
                viewModel.onDateSelected(currentDate)
            },
            onUpdateDateEvent = { updateDate ->
                binding.tvYearAndMonth.text = "${updateDate.year}년 ${updateDate.monthValue}월"
            }
        )
        binding.viewCalendar.adapter = calendarAdapter

        calendarAdapter.notifyDataSetChanged()

        val dateNow = LocalDate.now()
        binding.tvYearAndMonth.text = "${dateNow.year}년 ${dateNow.monthValue}월"
    }
}
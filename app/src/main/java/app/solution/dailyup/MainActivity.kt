package app.solution.dailyup

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import app.solution.dailyup.adapter.CalendarAdapter
import app.solution.dailyup.adapter.ScheduleAdapter
import app.solution.dailyup.databinding.ActivityMainBinding
import app.solution.dailyup.event.MainUiEvent
import app.solution.dailyup.event.NavigationEvent
import app.solution.dailyup.model.ScheduleModel
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

    @RequiresApi(Build.VERSION_CODES.O)
    private val addScheduleResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { resultIntent ->
                viewModel.receiveScheduleDataWithIntent(resultIntent)
            }
        }
    }


    //    LifeCycle
    @RequiresApi(Build.VERSION_CODES.O)
    override fun init() {
        binding.viewModel = viewModel

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
                    when (event) {
                        is NavigationEvent.MoveToAddScheduleActivity -> {
                            appNavigator.navigate(this@MainActivity, event, addScheduleResultLauncher)
                        }

                        is NavigationEvent.MoveToEditScheduleActivity -> {
                            val eventWithData = NavigationEvent.MoveToEditScheduleActivity(event.scheduleModel)
                            appNavigator.navigate(this@MainActivity, eventWithData, addScheduleResultLauncher)
                        }

                        else -> {
                            appNavigator.navigate(this@MainActivity, event)
                        }
                    }
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

                    if (targetScheduleModel.processMaxValue!! <= targetScheduleModel.processValue!!) return@let

                    val calculatedValue = targetScheduleModel.processValue.plus(targetScheduleModel.processValueStep!!)
                    val value =
                        if (calculatedValue > targetScheduleModel.processMaxValue) targetScheduleModel.processMaxValue
                        else calculatedValue

                    viewModel.onScheduleIncreaseProcessClick(scheduleModels[position].copy(processValue = value))
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
                scheduleViewModel.loadSchedules(currentDate.toString())
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
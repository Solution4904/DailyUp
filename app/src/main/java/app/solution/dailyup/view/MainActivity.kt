package app.solution.dailyup.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.withStarted
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import app.solution.dailyup.BaseActivity
import app.solution.dailyup.R
import app.solution.dailyup.adapter.CalendarAdapter
import app.solution.dailyup.adapter.ScheduleAdapter
import app.solution.dailyup.databinding.ActivityMainBinding
import app.solution.dailyup.event.MainUiEvent
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.navigation.AppNavigator
import app.solution.dailyup.utility.ConstKeys
import app.solution.dailyup.utility.LocalDataManager
import app.solution.dailyup.utility.RepeatTypeEnum
import app.solution.dailyup.utility.ScheduleAlarmScheduler
import app.solution.dailyup.utility.ScheduleTypeEnum
import app.solution.dailyup.utility.TraceLog
import app.solution.dailyup.viewmodel.MainViewModel
import app.solution.dailyup.viewmodel.ScheduleViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    //    Variable
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var calendarAdapter: CalendarAdapter

    private val scheduleViewModel: ScheduleViewModel by viewModels()
    private val viewModel: MainViewModel by viewModels()

    private val appNavigator = AppNavigator()

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var notificationPermissionResultLauncher: ActivityResultLauncher<String>


    //    LifeCycle
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent)
        handleNotificationIntent(intent)
    }

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
                        hour = it.getIntExtra(ConstKeys.SCHEDULE_HOUR, LocalTime.now().hour),
                        minute = it.getIntExtra(ConstKeys.SCHEDULE_MINUTE, LocalTime.now().minute),
                        repeat = RepeatTypeEnum.convertToType(it.getStringExtra(ConstKeys.SCHEDULE_REPEAT).toString()),
                    )

                    scheduleViewModel.upsertSchedule(resultData)
                    ScheduleAlarmScheduler.add(this, resultData)

                    viewModel.onDateSelected(LocalDate.parse(resultData.date))
                    TraceLog(message = "registerForActivityResult -> $resultData")
                }
            }
        }

        notificationPermissionResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            //  권한 허용
            if (isGranted) return@registerForActivityResult

            //  권한 거부
            showNotificationPermissionDenindDialog()
        }


        setScheduleRecyclerViewAdapter()
        setCalendarRecyclerViewAdapter()

        observeViewModel()
        observeNavigation()
        observeEvent()

        checkNotificationPermission()

        lifecycleScope.launch {
            lifecycle.withStarted {
                handleNotificationIntent(intent)
            }
        }
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
    private fun observeViewModel() {
        /*viewModel.currentCalendar.observe(this) { date ->
            calendarAdapter.updateDates(date)

            scheduleViewModel.loadSchedules(date.toString())
        }*/

        viewModel.currentDate.observe(this) { date ->
            calendarAdapter.updateDates(date)
            scheduleViewModel.loadSchedules(date.toString())
        }

        /*viewModel.scheduleModel.observe(this) { scheduleModel ->
            scheduleViewModel.upsertSchedule(scheduleModel)
        }*/
    }

    /**
     * Observe navigation
     * 화면 이동 관리
     */
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
    @SuppressLint("NotifyDataSetChanged")
    private fun setScheduleRecyclerViewAdapter() {
        binding.layoutRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        scheduleAdapter = ScheduleAdapter(
            occurrence = mutableListOf(),
            onItemClick = { occurrence ->
                viewModel.onEditScheduleClick(occurrence.source)
            },
            onIconClickForNormalType = { occurrence ->
                val updated = occurrence.progress.copy(isComplete = true)

                LocalDataManager.upsertProgress(updated)

                scheduleViewModel.loadSchedules(occurrence.date.toString())
            },
            onIconClickForCountingType = { occurrence ->
                val max = occurrence.source.progressMaxValue
                val current = occurrence.progress.progressValue

                if (max != null && current < max) {
                    val step = occurrence.source.progressStepValue ?: 1
                    //  todo : ???
                    val next = (current + step).coerceAtMost(max)
                    LocalDataManager.upsertProgress(occurrence.progress.copy(progressValue = next))
                    scheduleViewModel.loadSchedules(occurrence.date.toString())
                }
            },
            /*onIconClickForNormalType = { position ->
                scheduleViewModel.scheduleModels.value?.let { scheduleModels ->
                    viewModel.onScheduleCompleteClick(scheduleModels[position].copy(isCompleted = true))
                }
            },*/
            /*onIconClickForCountingType = { position ->
                scheduleViewModel.scheduleModels.value?.let { scheduleModels ->
                    val targetScheduleModel = scheduleModels[position]

                    if (targetScheduleModel.progressMaxValue!! <= targetScheduleModel.progressValue!!) return@let

                    val calculatedValue = targetScheduleModel.progressValue.plus(targetScheduleModel.progressStepValue!!)
                    val value =
                        if (calculatedValue > targetScheduleModel.progressMaxValue) targetScheduleModel.progressMaxValue
                        else calculatedValue

                    viewModel.onScheduleIncreaseProcessClick(scheduleModels[position].copy(progressValue = value))
                }
            },*/
            onItemLongClick = { occurrence ->
                viewModel.onScheduleDeleteDialog(occurrence.source)
            },
        )
        binding.layoutRecyclerview.adapter = scheduleAdapter

        scheduleViewModel.occurrences.observe(this) { list ->
            scheduleAdapter.updateList(list)

            binding.layoutEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
//            binding.layoutRecyclerview.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE

            TraceLog(message = "scheduleViewModel observe -> $list")
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
                    ScheduleAlarmScheduler.cancel(this@MainActivity, scheduleModel)
                }
                setNegativeButton("취소") { _, _ -> }
            }.show()
        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
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

    //  알림 권한 요청
    private fun showNotificationPermissionDenindDialog() {
        AlertDialog.Builder(this)
            .setTitle("알림 권한이 차단되어 있습니다.")
            .setMessage("일정 알림을 위해 '설정'에서 알림 권한을 허용해주세요.")
            .setPositiveButton("설정") { _, _ ->
                startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                })
            }
            .setNegativeButton("취소") { _, _ -> finishAffinity() }
            .setCancelable(false)
            .show()
    }

    //  알림 권한 확인
    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ActivityCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                notificationPermissionResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun handleNotificationIntent(intent: Intent) {
        intent ?: return
        if (!intent.getBooleanExtra(ConstKeys.FROM_NOTIFICATION, false)) return
        val scheduleId = intent.getStringExtra(ConstKeys.SCHEDULE_ID) ?: return

        intent.removeExtra(ConstKeys.FROM_NOTIFICATION)
        intent.removeExtra(ConstKeys.SCHEDULE_ID)

        val target = scheduleViewModel.findScheduleById(scheduleId)
        if (target == null) {
            Toast.makeText(this, "해당 일정을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.onDateSelected(LocalDate.parse(target.date))
        viewModel.onEditScheduleClick(target)
    }
}
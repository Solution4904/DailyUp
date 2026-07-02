package app.solution.dailyup.view

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import app.solution.dailyup.R
import app.solution.dailyup.adapter.ChartPagerAdapter
import app.solution.dailyup.data.scheduleRepository
import app.solution.dailyup.databinding.ActivityChartBinding
import app.solution.dailyup.model.ChartPageItem
import app.solution.dailyup.model.ScheduleAchievedBox
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.model.ScheduleProgressModel
import app.solution.dailyup.utility.CalendarUtil
import app.solution.dailyup.utility.TimePeriod
import app.solution.dailyup.utility.occurrencesIn
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import java.time.LocalDate

class ChartActivity : AppCompatActivity() {
    //  # Variable
    private lateinit var binding: ActivityChartBinding


    //  # LifeCycle
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        lifecycleScope.launch {
            setupPager()
        }
    }

    //  # Functions
    //  ViewPager 세팅
    private suspend fun setupPager() {
        val schedules = scheduleRepository.getSchedules()
        val progressMap = scheduleRepository.getProgressMap()

        val items = listOf(
            //  전체 성취율
            ChartPageItem(
                label = getString(R.string.chart_total_label),
                indicatorColor = ContextCompat.getColor(this, R.color.chart_total),
                box = calculateAchievement(TimePeriod.TOTAL, schedules, progressMap)
            ),
            //  월간 성취율
            ChartPageItem(
                label = getString(R.string.chart_monthly_label),
                indicatorColor = ContextCompat.getColor(this, R.color.chart_monthly),
                box = calculateAchievement(TimePeriod.MONTH, schedules, progressMap)
            ),
            //  주간 성취율
            ChartPageItem(
                label = getString(R.string.chart_weekly_label),
                indicatorColor = ContextCompat.getColor(this, R.color.chart_weekly),
                box = calculateAchievement(TimePeriod.WEEK, schedules, progressMap)
            ),
            //  일간 성취율
            ChartPageItem(
                label = getString(R.string.chart_daily_label),
                indicatorColor = ContextCompat.getColor(this, R.color.chart_daily),
                box = calculateAchievement(TimePeriod.DAY, schedules, progressMap)
            ),
        )

        binding.vpChart.adapter = ChartPagerAdapter(items)

        TabLayoutMediator(binding.tlChart, binding.vpChart) { tab, position ->
            tab.text = items[position].label
        }.attach()
    }

    //  일정 성취율 계산
    private fun calculateAchievement(timePeriod: TimePeriod, schedules: List<ScheduleModel>, progressMap: Map<String, ScheduleProgressModel>): ScheduleAchievedBox {
        val today = LocalDate.now()

        val range: ClosedRange<LocalDate> = when (timePeriod) {
            TimePeriod.TOTAL -> {
                val earliest = schedules
                    .mapNotNull { runCatching { LocalDate.parse(it.date) }.getOrNull() }
                    .minOrNull() ?: today
                earliest..today
            }

            else -> {
                //  todo : ???
                val (start, end) = CalendarUtil().getTheCurrentDays(today, timePeriod)
                start..end
            }
        }

        //  todo : ???
        val pairs: List<Pair<ScheduleModel, ScheduleProgressModel>> = schedules.flatMap { schedule ->
            schedule.occurrencesIn(range).map { date ->
                schedule to (progressMap["${schedule.id}@$date"]
                    ?: ScheduleProgressModel(schedule.id, date.toString()))
            }
        }


        val achieved = pairs.count { (schedule, progress) ->
            val max = schedule.progressMaxValue
            progress.isComplete || (max != null && progress.progressValue == max)
        }

        val total = pairs.size
        val rate = if (total == 0) {
            0
        } else {
            (achieved * 100) / total
        }

        return ScheduleAchievedBox(total = total, achieved = achieved, rate = rate)
    }
}
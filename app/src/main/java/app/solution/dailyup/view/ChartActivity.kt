package app.solution.dailyup.view

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.solution.dailyup.R
import app.solution.dailyup.adapter.ChartPagerAdapter
import app.solution.dailyup.databinding.ActivityChartBinding
import app.solution.dailyup.model.ChartPageItem
import app.solution.dailyup.model.ScheduleAchievedBox
import app.solution.dailyup.utility.LocalDataManager
import app.solution.dailyup.utility.TimePeriod
import com.google.android.material.tabs.TabLayoutMediator
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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupPager()
    }

    //  # Functions
    private fun setupPager() {
        val today = LocalDate.now()
        val items = listOf(
            buildPageItem(today, TimePeriod.TOTAL, R.string.chart_total_label, R.color.chart_total),
            buildPageItem(today, TimePeriod.MONTH, R.string.chart_monthly_label, R.color.chart_monthly),
            buildPageItem(today, TimePeriod.WEEK, R.string.chart_weekly_label, R.color.chart_weekly),
            buildPageItem(today, TimePeriod.DAY, R.string.chart_daily_label, R.color.chart_daily),
        )

        binding.vpChart.adapter = ChartPagerAdapter(items)

        TabLayoutMediator(binding.tabChart, binding.vpChart) { tab, position ->
            tab.text = items[position].label
        }.attach()
    }

    private fun buildPageItem(day: LocalDate, period: TimePeriod, @StringRes labelRes: Int, @ColorRes colorRes: Int): ChartPageItem {
        return ChartPageItem(
            label = getString(labelRes),
            indicatorColor = ContextCompat.getColor(this, colorRes),
            box = calculateAchievement(day, period)
        )
    }

    private fun calculateAchievement(day: LocalDate, timePeriod: TimePeriod): ScheduleAchievedBox {
        val schedules = LocalDataManager.getSchedulesForPeriod(day, timePeriod)
        val achieved = schedules.count {
            (it.progressMaxValue != null && it.progressValue == it.progressMaxValue) || it.isCompleted
        }
        val rate = if (schedules.isEmpty())
            0
        else
            (achieved * 100) / schedules.size

        return ScheduleAchievedBox(
            total = schedules.size,
            achieved = achieved,
            rate = rate,
        )
    }
}
package app.solution.dailyup

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import app.solution.dailyup.databinding.ActivityChartBinding
import app.solution.dailyup.utility.LocalDataManager
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

        setProgressBar()
    }


    //  # Functions
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setProgressBar() {
        calculateAchievement(LocalDate.now(), TimePeriod.DAY).let {
            binding.apply {
                progressbarDaily.progress = it.rate.toInt()
                tvDaily.text = "${it.achieved} / ${it.total}"
            }
        }

        calculateAchievement(LocalDate.now(), TimePeriod.WEEK).let {
            binding.apply {
                progressbarWeekly.progress = it.rate.toInt()
                tvWeekly.text = "${it.achieved} / ${it.total}"
            }
        }

        calculateAchievement(LocalDate.now(), TimePeriod.MONTH).let {
            binding.apply {
                progressbarMonthly.progress = it.rate.toInt()
                tvMonthly.text = "${it.achieved} / ${it.total}"
            }
        }
    }

    /**
     * 특정 날짜의 하루, 주간, 월간 성취율을 반환.
     *
     * @param day 날짜
     * @param timePeriod 기간 (DAY, WEEK, MONTH)
     * @return 성취율
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateAchievement(day: LocalDate, timePeriod: TimePeriod): ScheduleAchievedBox {
        val schedules = LocalDataManager.getSchedulesForPeriod(day, timePeriod)
        val scheduleAchieved = schedules.filter {
            it.progressValue == it.progressMaxValue || it.isCompleted
        }

        return ScheduleAchievedBox(
            total = schedules.size,
            achieved = scheduleAchieved.size,
            rate = (scheduleAchieved.size.toDouble() / schedules.size.toDouble()) * 100
        )
    }
}

data class ScheduleAchievedBox(
    val total: Int,
    val achieved: Int,
    val rate: Double,
)
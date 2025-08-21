package app.solution.dailyup

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
    private fun setProgressBar() {
        binding.progressbarDaily.progress = 50
        binding.tvDaily.text = "5 / 10"

        binding.progressbarWeekly.progress = 50
        binding.tvWeekly.text = "5 / 10"

        binding.progressbarMonthly.progress = 50
        binding.tvMonthly.text = "5 / 10"
    }

    /**
     * 특정 날짜의 하루, 주간, 월간 성취율을 반환.
     *
     * @param day 날짜
     * @param timePeriod 기간 (DAY, WEEK, MONTH)
     * @return 성취율
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun calulateAchievement(day: LocalDate, timePeriod: TimePeriod): Float {
        val schedules = LocalDataManager.getSchedulesForPeriod(day, timePeriod)
        val achivementScheduls = schedules.filter {
            it.processValue != null && it.processValue > 0
        }

        // TODO:백분율로 계산해서 리턴시켜야 함

        return 0.0f
    }
}
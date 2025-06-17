package app.solution.dailyup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import app.solution.dailyup.adapter.ScheduleAdapter
import app.solution.dailyup.databinding.ActivityMainBinding
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.utility.ConstKeys

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val testList = mutableListOf<ScheduleModel>()
        for (i in 1..50) {
            testList.add(ScheduleModel("$i", "title", "dec", 0))
        }

        val adapter = ScheduleAdapter(
            testList,
            onIconClick = {
                Log.d("TAG", "icon click")
            },
            onItemClick = { schedule ->
                Log.d("TAG", "clicked data : \n${schedule}")

                startActivity(
                    Intent(this@MainActivity, AddScheduleActivity::class.java).apply {
                        putExtra(ConstKeys.INTENT_EXTRA, "${ConstKeys.SCHEDULE_LIST}_${schedule.id}")
                    }
                )
            })

        binding.layoutRecyclerview.layoutManager = LinearLayoutManager(this)
        binding.layoutRecyclerview.adapter = adapter

        addButtonsEvent()
    }

    fun addButtonsEvent() {
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddScheduleActivity::class.java))
        }
    }
}
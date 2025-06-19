package app.solution.dailyup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import app.solution.dailyup.adapter.ScheduleAdapter
import app.solution.dailyup.databinding.ActivityMainBinding
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.utility.ConstKeys

class MainActivity : AppCompatActivity() {
    //    Variable
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ScheduleAdapter
    private val scheduleList = mutableListOf<ScheduleModel>()

    //    LifeCycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        addButtonsEvent()
        loadScheduleList()
    }

    override fun onStart() {
        super.onStart()

        refreshScheduleList()
    }

    //    Function
    private fun addButtonsEvent() {
        val intent = Intent(this@MainActivity, AddScheduleActivity::class.java)
        var intentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    val scheduleModel = ScheduleModel(
                        id = it.getStringExtra(ConstKeys.SCHEDULE_ID).toString(),
                        title = it.getStringExtra(ConstKeys.SCHEDULE_TITLE).toString(),
                        dec = it.getStringExtra(ConstKeys.SCHEDULE_DEC).toString(),
                        iconResId = it.getStringExtra(ConstKeys.SCHEDULE_ICON).toString()
                    )

                    scheduleList.add(scheduleModel)
                    MyAppication.localDataManager.setData(ConstKeys.SCHEDULE_LIST, MyAppication.localDataManager.serialization(scheduleList).toString())
                }
            }

        }

        binding.btnAdd.setOnClickListener { intentLauncher.launch(intent) }
    }

    private fun loadScheduleList() {
        MyAppication.localDataManager.getData(ConstKeys.SCHEDULE_LIST)?.let {
            Log.d("TAG", "loadScheduleList: $it")

            scheduleList.clear()
            scheduleList.addAll(MyAppication.localDataManager.deserialization(it))

            adapter = ScheduleAdapter(
                scheduleList,
                onIconClick = {
                    Log.d("TAG", "icon click")
                },
                onItemClick = { schedule ->
                    Log.d("TAG", "clicked data : \n${schedule}")

                    startActivity(
                        Intent(this@MainActivity, AddScheduleActivity::class.java).apply {
                            putExtra(ConstKeys.SCHEDULE_ID, schedule.id)
                            putExtra(ConstKeys.SCHEDULE_TITLE, schedule.title)
                            putExtra(ConstKeys.SCHEDULE_DEC, schedule.dec)
                            putExtra(ConstKeys.SCHEDULE_ICON, schedule.iconResId)
                        }
                    )
                })

            binding.layoutRecyclerview.layoutManager = LinearLayoutManager(this)
            binding.layoutRecyclerview.adapter = adapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshScheduleList() {
        MyAppication.localDataManager.getData(ConstKeys.SCHEDULE_LIST)?.let {
            Log.d("TAG", "refreshScheduleList: $it")

            scheduleList.clear()
            scheduleList.addAll(MyAppication.localDataManager.deserialization(it))
            adapter.notifyDataSetChanged()
        }
    }
}
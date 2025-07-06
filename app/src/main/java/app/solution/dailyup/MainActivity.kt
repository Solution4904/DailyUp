package app.solution.dailyup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import app.solution.dailyup.adapter.ScheduleAdapter
import app.solution.dailyup.databinding.ActivityMainBinding
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.utility.ConstKeys
import app.solution.dailyup.utility.ScheduleTypeEnum

class MainActivity : AppCompatActivity() {
    //    Variable
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ScheduleAdapter
    private lateinit var intentLauncher: ActivityResultLauncher<Intent>
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
        setIntentLauncher()

        loadScheduleList()
    }

    override fun onStart() {
        super.onStart()

        refreshScheduleList()
    }

    //    Function
    private fun addButtonsEvent() {
        binding.btnAdd.setOnClickListener {
            intentLauncher.launch(Intent(this@MainActivity, AddScheduleActivity::class.java))
        }
    }

    private fun setIntentLauncher() {
        intentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    val scheduleModel = ScheduleModel(
                        id = it.getStringExtra(ConstKeys.SCHEDULE_ID).toString(),
                        title = it.getStringExtra(ConstKeys.SCHEDULE_TITLE).toString(),
                        dec = it.getStringExtra(ConstKeys.SCHEDULE_DEC).toString(),
                        iconResId = it.getIntExtra(ConstKeys.SCHEDULE_ICONNAME, R.drawable.ic_schedule_default),
                        type = ScheduleTypeEnum.convert(it.getStringExtra(ConstKeys.SCHEDULE_TYPE).toString()),
                        maxValue = it.getIntExtra(ConstKeys.SCHEDULE_MAXVALUE, 1),
                        valueStep = it.getIntExtra(ConstKeys.SCHEDULE_VALUESTEP, 1),
                        value = it.getIntExtra(ConstKeys.SCHEDULE_VALUE, 0)
                    )

                    var data = scheduleList.find { it.id == scheduleModel.id }
                    if (data == null) {
                        scheduleList.add(scheduleModel)

                        Log.d("TAG", "새로 추가")
                    } else {
                        val index = scheduleList.indexOfFirst { it.id == scheduleModel.id }
                        if (index < 0) return@let

                        scheduleList[index] = scheduleModel.copy(
                            title = scheduleModel.title,
                            dec = scheduleModel.dec,
                            iconResId = scheduleModel.iconResId,
                            type = scheduleModel.type,
                            maxValue = scheduleModel.maxValue,
                            valueStep = scheduleModel.valueStep,
                            value = scheduleModel.value,
                        )

                        Log.d("TAG", "수정 등록")
                    }

                    MyAppication.localDataManager.setData(ConstKeys.SCHEDULE_LIST, MyAppication.localDataManager.serialization(scheduleList).toString())
                }
            }
        }
    }

    private fun loadScheduleList() {
        MyAppication.localDataManager.getData(ConstKeys.SCHEDULE_LIST)?.let {
            Log.d("TAG", "loadScheduleList: $it")

            scheduleList.clear()
            scheduleList.addAll(MyAppication.localDataManager.deserialization(it))

            adapter = ScheduleAdapter(
                scheduleList,
                onIconClick = { item ->

                },
                onItemClick = { item ->
                    intentLauncher.launch(Intent(this@MainActivity, AddScheduleActivity::class.java).apply {
                        putExtra(ConstKeys.SCHEDULE_ID, item.id)
                        putExtra(ConstKeys.SCHEDULE_TITLE, item.title)
                        putExtra(ConstKeys.SCHEDULE_DEC, item.dec)
                        putExtra(ConstKeys.SCHEDULE_ICONNAME, item.iconResId)
                        putExtra(ConstKeys.SCHEDULE_TYPE, item.type.name)
                        putExtra(ConstKeys.SCHEDULE_MAXVALUE, item.maxValue)
                        putExtra(ConstKeys.SCHEDULE_VALUESTEP, item.valueStep)
                        putExtra(ConstKeys.SCHEDULE_VALUE, item.value)
                    })
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
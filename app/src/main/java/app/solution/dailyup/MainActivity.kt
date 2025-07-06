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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import app.solution.dailyup.adapter.ScheduleAdapter
import app.solution.dailyup.databinding.ActivityMainBinding
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.utility.ConstKeys
import app.solution.dailyup.utility.ScheduleTypeEnum
import app.solution.dailyup.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    //    Variable
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ScheduleAdapter
    private lateinit var intentLauncher: ActivityResultLauncher<Intent>

    private lateinit var viewModel: MainViewModel

    //    LifeCycle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
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
                result.data?.let { resultIntent ->
                    val scheduleModel = ScheduleModel(
                        id = resultIntent.getStringExtra(ConstKeys.SCHEDULE_ID).toString(),
                        title = resultIntent.getStringExtra(ConstKeys.SCHEDULE_TITLE).toString(),
                        dec = resultIntent.getStringExtra(ConstKeys.SCHEDULE_DEC).toString(),
                        iconResId = resultIntent.getIntExtra(ConstKeys.SCHEDULE_ICONNAME, R.drawable.ic_schedule_default),
                        type = ScheduleTypeEnum.convert(resultIntent.getStringExtra(ConstKeys.SCHEDULE_TYPE).toString()),
                        maxValue = resultIntent.getIntExtra(ConstKeys.SCHEDULE_MAXVALUE, 1),
                        valueStep = resultIntent.getIntExtra(ConstKeys.SCHEDULE_VALUESTEP, 1),
                        value = resultIntent.getIntExtra(ConstKeys.SCHEDULE_VALUE, 0)
                    )

                    if (viewModel.scheduleList.find { it.id == scheduleModel.id } == null) {
                        viewModel.scheduleList.add(scheduleModel)
                        Log.d("TAG", "새로 추가")
                    } else {
                        val targetIndex = viewModel.scheduleList.indexOfFirst { it.id == scheduleModel.id }
                        if (targetIndex < 0) return@registerForActivityResult

                        viewModel.scheduleList[targetIndex] = scheduleModel
                        Log.d("TAG", "수정 등록")
                    }

                    MyAppication.localDataManager.setData(ConstKeys.SCHEDULE_LIST, MyAppication.localDataManager.serialization(viewModel.scheduleList).toString())
                }
            }
        }
    }

    private fun loadScheduleList() {
        MyAppication.localDataManager.getData(ConstKeys.SCHEDULE_LIST)?.let {
            Log.d("TAG", "loadScheduleList: $it")

            viewModel.scheduleList.clear()
            viewModel.scheduleList.addAll(MyAppication.localDataManager.deserialization(it))

            adapter = ScheduleAdapter(
                viewModel.scheduleList,
                onIconClick = { position ->
                    Log.d("", "loadScheduleList: $position")
                    val data = viewModel.scheduleList[position]
                    Log.d("", "loadScheduleList: $data")
                    val changeValue = data.value?.plus((data.valueStep ?: 1))
                    Log.d("", "loadScheduleList: $changeValue")
                    viewModel.scheduleList[position] = viewModel.scheduleList[position].copy(value = changeValue)
                    adapter.notifyItemChanged(position)
                },
                onItemClick = { position ->
                    intentLauncher.launch(Intent(this@MainActivity, AddScheduleActivity::class.java).apply {
                        putExtra(ConstKeys.SCHEDULE_ID, viewModel.scheduleList[position].id)
                        putExtra(ConstKeys.SCHEDULE_TITLE, viewModel.scheduleList[position].title)
                        putExtra(ConstKeys.SCHEDULE_DEC, viewModel.scheduleList[position].dec)
                        putExtra(ConstKeys.SCHEDULE_ICONNAME, viewModel.scheduleList[position].iconResId)
                        putExtra(ConstKeys.SCHEDULE_TYPE, viewModel.scheduleList[position].type)
                        putExtra(ConstKeys.SCHEDULE_MAXVALUE, viewModel.scheduleList[position].maxValue)
                        putExtra(ConstKeys.SCHEDULE_VALUESTEP, viewModel.scheduleList[position].valueStep)
                        putExtra(ConstKeys.SCHEDULE_VALUE, viewModel.scheduleList[position].value)
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

            viewModel.scheduleList.clear()
            viewModel.scheduleList.addAll(MyAppication.localDataManager.deserialization(it))
            adapter.notifyDataSetChanged()
        }
    }
}
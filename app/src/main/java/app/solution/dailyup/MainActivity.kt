package app.solution.dailyup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import app.solution.dailyup.adapter.ScheduleAdapter
import app.solution.dailyup.databinding.ActivityMainBinding
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.utility.ConstKeys
import app.solution.dailyup.utility.ScheduleTypeEnum
import app.solution.dailyup.utility.TraceLog
import app.solution.dailyup.viewmodel.ScheduleViewModel

class MainActivity : AppCompatActivity() {
    //    Variable
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ScheduleAdapter
    private lateinit var addScheduleResultLauncher: ActivityResultLauncher<Intent>

    private val scheduleViewModel: ScheduleViewModel by viewModels()


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
        setAdapter()
        setAddScheduleResultLauncher()

//        loadScheduleList()
    }

    /*override fun onStart() {
        super.onStart()

        refreshScheduleList()
    }*/

    //    Function
    private fun addButtonsEvent() {
        binding.btnAdd.setOnClickListener {
            addScheduleResultLauncher.launch(Intent(this@MainActivity, AddScheduleActivity::class.java))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setAdapter() {
        binding.layoutRecyclerview.layoutManager = LinearLayoutManager(this)

        adapter = ScheduleAdapter(
            scheduleList = mutableListOf(),
            onItemClick = { position ->
                sendScheduleDataWithIntent(position)
            },
            onIconClick = { position ->
                increaseScheduleProgress(position)
            },
            onItemLongClick = { position ->
                popupScheduleItemDialog(position)
            }
        )
        binding.layoutRecyclerview.adapter = adapter

        scheduleViewModel.scheduleModels.observe(this) { list ->
            TraceLog(message = "scheduleModels observe -> $list")

            adapter.updateList(list)
        }

        scheduleViewModel.loadSchedules()
    }

    private fun popupScheduleItemDialog(position: Int) {
        scheduleViewModel.scheduleModels.value?.let { scheduleModels ->
            val targetScheduleModel = scheduleModels[position]

            AlertDialog.Builder(this@MainActivity).apply {
                setTitle("제거 확인")
                setMessage("선택하신 스케줄을 제거하시겠습니까?")
                setPositiveButton("제거") { _, _ ->
                    scheduleViewModel.deleteSchedule(targetScheduleModel)
                }
                setNegativeButton("취소") { _, _ -> }
            }.show()
        }
    }

    private fun increaseScheduleProgress(position: Int) {
        scheduleViewModel.scheduleModels.value?.let { scheduleModels ->
            val targetScheduleModel = scheduleModels[position]

            if (targetScheduleModel.processMaxValue!! <= targetScheduleModel.processValue!!) return

            val calculatedValue = targetScheduleModel.processValue?.plus(targetScheduleModel.processValueStep!!)
            val value =
                if (calculatedValue!! > targetScheduleModel.processMaxValue!!) targetScheduleModel.processMaxValue
                else calculatedValue

            val resultScheduleModel = targetScheduleModel.copy(processValue = value)
            scheduleViewModel.upsertSchedule(resultScheduleModel)
        }
    }

    private fun sendScheduleDataWithIntent(position: Int) {
        scheduleViewModel.scheduleModels.value?.let { scheduleModels ->
            addScheduleResultLauncher.launch(Intent(this@MainActivity, AddScheduleActivity::class.java).apply {
                with(scheduleModels[position]) {
                    putExtra(ConstKeys.SCHEDULE_ID, id)
                    putExtra(ConstKeys.SCHEDULE_TITLE, title)
                    putExtra(ConstKeys.SCHEDULE_DEC, dec)
                    putExtra(ConstKeys.SCHEDULE_ICONNAME, iconResId)
                    putExtra(ConstKeys.SCHEDULE_TYPE, type.toString())
                    putExtra(ConstKeys.SCHEDULE_MAXVALUE, processMaxValue)
                    putExtra(ConstKeys.SCHEDULE_VALUESTEP, processValueStep)
                    putExtra(ConstKeys.SCHEDULE_VALUE, processValue)
                }
            })
        }
    }

    private fun receiveScheduleDataWithIntent(intent: Intent) {
        val scheduleModel = ScheduleModel(
            id = intent.getStringExtra(ConstKeys.SCHEDULE_ID).toString(),
            title = intent.getStringExtra(ConstKeys.SCHEDULE_TITLE).toString(),
            dec = intent.getStringExtra(ConstKeys.SCHEDULE_DEC).toString(),
            iconResId = intent.getIntExtra(ConstKeys.SCHEDULE_ICONNAME, R.drawable.ic_schedule_default),
            type = ScheduleTypeEnum.convert(intent.getStringExtra(ConstKeys.SCHEDULE_TYPE).toString()),
            processMaxValue = intent.getIntExtra(ConstKeys.SCHEDULE_MAXVALUE, 1),
            processValueStep = intent.getIntExtra(ConstKeys.SCHEDULE_VALUESTEP, 1),
            processValue = intent.getIntExtra(ConstKeys.SCHEDULE_VALUE, 0)
        )

        scheduleViewModel.upsertSchedule(scheduleModel)

        TraceLog(message = "receiveScheduleDataWithIntent -> $scheduleModel")
    }

    private fun setAddScheduleResultLauncher() {
        addScheduleResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let { resultIntent ->
                    receiveScheduleDataWithIntent(resultIntent)
                }
            }
        }
    }

    /*    private fun loadScheduleList() {
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
        }*/

    /*@SuppressLint("NotifyDataSetChanged")
    private fun refreshScheduleList() {
        MyAppication.localDataManager.getData(ConstKeys.SCHEDULE_LIST)?.let {
            Log.d("TAG", "refreshScheduleList: $it")

            viewModel.scheduleList.clear()
            viewModel.scheduleList.addAll(MyAppication.localDataManager.deserialization(it))
            adapter.notifyDataSetChanged()
        }
    }*/
}
package app.solution.dailyup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import app.solution.dailyup.databinding.ActivityAddscheduleBinding
import app.solution.dailyup.utility.ConstKeys
import app.solution.dailyup.utility.ScheduleTypeEnum
import app.solution.dailyup.viewmodel.ScheduleViewModel
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.apply

class AddScheduleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddscheduleBinding
    private lateinit var viewModel: ScheduleViewModel
    private lateinit var intentLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[ScheduleViewModel::class.java]


        binding = DataBindingUtil.setContentView(this, R.layout.activity_addschedule)
        binding.lifecycleOwner = this
        binding.viewModel = this.viewModel

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initData()

        setIntentLauncher()
        setButtonsEvent()
    }

    private fun initData() {
        if (intent.hasExtra(ConstKeys.SCHEDULE_ID)) {
            with(intent)
            {
                viewModel.setType(ScheduleTypeEnum.convert(getStringExtra(ConstKeys.SCHEDULE_TYPE).toString()))
                viewModel.setId(getStringExtra((ConstKeys.SCHEDULE_ID)).toString())
                viewModel.setTitle(getStringExtra(ConstKeys.SCHEDULE_TITLE).toString())
                viewModel.setDec(getStringExtra(ConstKeys.SCHEDULE_DEC).toString())
                viewModel.setIconName(getIntExtra(ConstKeys.SCHEDULE_ICONNAME, viewModel.getIconName()))
                viewModel.setMaxValue(getIntExtra(ConstKeys.SCHEDULE_MAXVALUE, viewModel.getMaxValue()))
                viewModel.setValueStep(getIntExtra(ConstKeys.SCHEDULE_VALUESTEP, viewModel.getValueStep()))
                viewModel.setValue(getIntExtra(ConstKeys.SCHEDULE_VALUE, viewModel.getValue()))
            }

            Log.d("TAG", "initData: ${viewModel.scheduleModel.value}")
        }
    }

    private fun setButtonsEvent() {
        binding.ibtnIcon.setOnClickListener { popupIconList() }

        binding.btnType.setOnClickListener { popupTypeList() }

        binding.btnConfirm.setOnClickListener { save() }

        binding.btnCancel.setOnClickListener { cancel() }
    }

    private fun setIntentLauncher() {
        intentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.let {
                    val fileName = it.getStringExtra(ConstKeys.SCHEDULE_ICONNAME)

                    Glide.with(baseContext)
                        .load(fileName)
                        .into(binding.ibtnIcon)
                }
            }
        }
    }

    private fun popupTypeList() {
        val types = resources.getStringArray(R.array.schedule_type_array)

        MaterialAlertDialogBuilder(this@AddScheduleActivity)
            .setTitle("완료 방식")
            .setItems(types) { dialog, which ->
                viewModel.scheduleModel.value?.let {
                    viewModel.setType(ScheduleTypeEnum.convert(which))
                }
            }.show()
    }

    private fun popupIconList() {
        val fragment = ScheduleIconSelectorBottomSheet(
            onItemClick = { resId ->
                binding.ibtnIcon.apply {
                    setImageResource(resId)
                    tag = resId
                }
            }
        )
        fragment.show(supportFragmentManager, fragment.tag)
    }

    private fun save() {
        viewModel.apply {
            setTitle(binding.etTitle.text.toString())
            setDec(binding.etDec.text.toString())

            if (viewModel.getType() == ScheduleTypeEnum.COUNTING) {
                setMaxValue(binding.etMaxValue.text.toString().toInt())
                setValueStep(binding.etValueStep.text.toString().toInt())
            } else {
                setIconName(binding.ibtnIcon.tag as Int)
            }
        }

        val resultIntent = Intent().apply {
            with(viewModel) {
                putExtra(ConstKeys.SCHEDULE_ID, getId())
                putExtra(ConstKeys.SCHEDULE_TITLE, getTitle())
                putExtra(ConstKeys.SCHEDULE_DEC, getDec())
                putExtra(ConstKeys.SCHEDULE_ICONNAME, getIconName())
                putExtra(ConstKeys.SCHEDULE_TYPE, getType().name)
                putExtra(ConstKeys.SCHEDULE_MAXVALUE, getMaxValue())
                putExtra(ConstKeys.SCHEDULE_VALUESTEP, getValueStep())
                putExtra(ConstKeys.SCHEDULE_VALUE, getValue())
            }
        }
        setResult(RESULT_OK, resultIntent)

        finish()
    }

    private fun cancel() {
        finish()
    }
}
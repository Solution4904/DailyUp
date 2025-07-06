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
                viewModel.type.value = (ScheduleTypeEnum.convert(getStringExtra(ConstKeys.SCHEDULE_TYPE).toString()))
                viewModel.id.value = (getStringExtra((ConstKeys.SCHEDULE_ID)).toString())
                viewModel.title.value = (getStringExtra(ConstKeys.SCHEDULE_TITLE).toString())
                viewModel.dec.value = (getStringExtra(ConstKeys.SCHEDULE_DEC).toString())
                viewModel.iconResId.value = (getIntExtra(ConstKeys.SCHEDULE_ICONNAME, viewModel.iconResId.value ?: R.drawable.ic_schedule_default))
                viewModel.maxValue.value = (getIntExtra(ConstKeys.SCHEDULE_MAXVALUE, viewModel.maxValue.value ?: 1))
                viewModel.valueStep.value= (getIntExtra(ConstKeys.SCHEDULE_VALUESTEP, viewModel.valueStep.value ?: 1))
                viewModel.value.value = (getIntExtra(ConstKeys.SCHEDULE_VALUE, viewModel.value.value ?: 0))
            }

            Log.d("TAG", "initData: ${viewModel.value}")
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
                viewModel.value.let {
                    viewModel.type.value = ScheduleTypeEnum.convert(which)
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
            title.value = (binding.etTitle.text.toString())
            dec.value = (binding.etDec.text.toString())

            if (viewModel.type.value == ScheduleTypeEnum.COUNTING) {
                maxValue.value = (binding.etMaxValue.text.toString().toInt())
                valueStep.value = (binding.etValueStep.text.toString().toInt())
            } else {
                iconResId.value = (binding.ibtnIcon.tag as Int)
            }
        }

        val resultIntent = Intent().apply {
            with(viewModel) {
                putExtra(ConstKeys.SCHEDULE_ID, id.value)
                putExtra(ConstKeys.SCHEDULE_TITLE, title.value)
                putExtra(ConstKeys.SCHEDULE_DEC, dec.value)
                putExtra(ConstKeys.SCHEDULE_ICONNAME, iconResId.value ?: R.drawable.ic_schedule_default)
                putExtra(ConstKeys.SCHEDULE_TYPE, type.value?.name ?: ScheduleTypeEnum.NORMAL)
                putExtra(ConstKeys.SCHEDULE_MAXVALUE, maxValue.value ?: 1)
                putExtra(ConstKeys.SCHEDULE_VALUESTEP, valueStep.value ?: 1)
                putExtra(ConstKeys.SCHEDULE_VALUE, value.value ?: 0)
            }
        }
        setResult(RESULT_OK, resultIntent)

        finish()
    }

    private fun cancel() {
        finish()
    }
}
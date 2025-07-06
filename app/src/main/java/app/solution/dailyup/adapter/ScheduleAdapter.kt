package app.solution.dailyup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.solution.dailyup.databinding.ScheduleViewCountingBinding
import app.solution.dailyup.databinding.ScheduleViewNormalBinding
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.utility.ScheduleTypeEnum

class ScheduleAdapter(
    private val scheduleModels: MutableList<ScheduleModel>,
    private val onIconClick: (ScheduleModel) -> Unit,
    private val onItemClick: (ScheduleModel) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ScheduleNormalViewHolder(private val binding: ScheduleViewNormalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(schedule: ScheduleModel) {
            binding.schedule = schedule
            binding.layoutRoot.setOnClickListener { onItemClick(schedule) }

            binding.btnIcon.setOnClickListener { onIconClick(schedule) }
        }
    }

    inner class ScheduleCountingViewHolder(private val binding: ScheduleViewCountingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(schedule: ScheduleModel) {
            binding.scheduleModel = schedule
            binding.layoutRoot.setOnClickListener { onItemClick(schedule) }

            binding.pbIcon.setOnClickListener { onIconClick(schedule) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ScheduleTypeEnum.NORMAL.ordinal -> ScheduleNormalViewHolder(ScheduleViewNormalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            ScheduleTypeEnum.COUNTING.ordinal -> ScheduleCountingViewHolder(ScheduleViewCountingBinding.inflate(LayoutInflater.from(parent.context), parent, false))

            else -> ScheduleNormalViewHolder(ScheduleViewNormalBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ScheduleNormalViewHolder -> holder.bind(scheduleModels[position])
            is ScheduleCountingViewHolder -> holder.bind(scheduleModels[position])
        }
    }

    override fun getItemViewType(position: Int) = scheduleModels[position]?.type?.ordinal ?: 0
    override fun getItemCount(): Int = scheduleModels.size
}
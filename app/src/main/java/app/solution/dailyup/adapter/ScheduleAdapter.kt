package app.solution.dailyup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.solution.dailyup.databinding.ScheduleViewBinding
import app.solution.dailyup.model.ScheduleModel

class ScheduleAdapter(
    private val scheduleModels: List<ScheduleModel>,
    private val onIconClick: () -> Unit,
    private val onItemClick: (ScheduleModel) -> Unit
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {
    inner class ScheduleViewHolder(private val binding: ScheduleViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(schedule: ScheduleModel) {
            binding.schedule = schedule
            binding.btnIcon.setOnClickListener { onIconClick() }
            binding.layoutRoot.setOnClickListener { onItemClick(schedule) }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ScheduleViewBinding.inflate(inflater, parent, false)
        return ScheduleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) = holder.bind(scheduleModels[position])
    override fun getItemCount(): Int = scheduleModels.size
}

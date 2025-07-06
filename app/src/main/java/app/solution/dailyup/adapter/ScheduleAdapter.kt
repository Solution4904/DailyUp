package app.solution.dailyup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.solution.dailyup.databinding.ScheduleViewCountingBinding
import app.solution.dailyup.databinding.ScheduleViewNormalBinding
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.utility.ScheduleTypeEnum

class ScheduleAdapter(
    private val list: List<ScheduleModel>,
    private val onIconClick: (Int) -> Unit,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ScheduleNormalViewHolder(private val binding: ScheduleViewNormalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.schedule = list[adapterPosition]
            binding.layoutRoot.setOnClickListener { onItemClick(adapterPosition) }

            binding.btnIcon.setOnClickListener { onIconClick(adapterPosition) }
        }
    }

    inner class ScheduleCountingViewHolder(private val binding: ScheduleViewCountingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.scheduleModel = list[adapterPosition]
            binding.layoutRoot.setOnClickListener { onItemClick(adapterPosition) }

            binding.pbIcon.setOnClickListener { onIconClick(adapterPosition) }
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
            is ScheduleNormalViewHolder -> holder.bind()
            is ScheduleCountingViewHolder -> holder.bind()
        }
    }

    override fun getItemViewType(position: Int) = list[position].type.ordinal
    override fun getItemCount(): Int = list.size
}
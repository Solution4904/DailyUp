package app.solution.dailyup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.solution.dailyup.databinding.ScheduleViewCountingBinding
import app.solution.dailyup.databinding.ScheduleViewNormalBinding
import app.solution.dailyup.model.ScheduleOccurrence
import app.solution.dailyup.utility.ScheduleTypeEnum

class ScheduleAdapter(
    private val onIconClickForNormalType: (ScheduleOccurrence) -> Unit,
    private val onIconClickForCountingType: (ScheduleOccurrence) -> Unit,
    private val onItemClick: (ScheduleOccurrence) -> Unit,
    private val onItemLongClick: (ScheduleOccurrence) -> Unit,
) : ListAdapter<ScheduleOccurrence, RecyclerView.ViewHolder>(DiffCallback) {
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ScheduleOccurrence>() {
            override fun areItemsTheSame(old: ScheduleOccurrence, new: ScheduleOccurrence) =
                old.source.id == new.source.id && old.date == new.date

            override fun areContentsTheSame(old: ScheduleOccurrence, new: ScheduleOccurrence) =
                old == new
        }
    }

    inner class ScheduleNormalViewHolder(private val binding: ScheduleViewNormalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = getItem(position)

            binding.occurrence = item

            binding.layoutRoot.setOnClickListener {
                onItemClick(item)
            }

            binding.layoutRoot.setOnLongClickListener {
                onItemLongClick(item)
                true
            }

            binding.btnIcon.setOnClickListener {
                onIconClickForNormalType(item)
            }
        }
    }

    inner class ScheduleCountingViewHolder(private val binding: ScheduleViewCountingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = getItem(position)

            binding.occurrence = item

            binding.layoutRoot.setOnClickListener {
                onItemClick(item)
            }

            binding.layoutRoot.setOnLongClickListener {
                onItemLongClick(item)
                true
            }

            binding.pbIcon.setOnClickListener {
                onIconClickForCountingType(item)
            }

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
            is ScheduleNormalViewHolder -> holder.bind(position)
            is ScheduleCountingViewHolder -> holder.bind(position)
        }
    }

    override fun getItemViewType(position: Int) = getItem(position).source.type.ordinal
}
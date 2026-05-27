package app.solution.dailyup.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.adapters.ViewBindingAdapter.setOnLongClickListener
import androidx.recyclerview.widget.RecyclerView
import app.solution.dailyup.BR.occurrence
import app.solution.dailyup.databinding.ScheduleViewCountingBinding
import app.solution.dailyup.databinding.ScheduleViewNormalBinding
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.model.ScheduleOccurrence
import app.solution.dailyup.utility.ScheduleTypeEnum

class ScheduleAdapter(
    private val occurrence: MutableList<ScheduleOccurrence>,
    private val onIconClickForNormalType: (ScheduleOccurrence) -> Unit,
    private val onIconClickForCountingType: (ScheduleOccurrence) -> Unit,
    private val onItemClick: (ScheduleOccurrence) -> Unit,
    private val onItemLongClick: (ScheduleOccurrence) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newScheduleList: List<ScheduleOccurrence>) {
        occurrence.clear()
        occurrence.addAll(newScheduleList)
        notifyDataSetChanged()
    }

    inner class ScheduleNormalViewHolder(private val binding: ScheduleViewNormalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val item = occurrence[position]

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
            val item = occurrence[position]

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

    override fun getItemViewType(position: Int) = occurrence[position].source.type.ordinal
    override fun getItemCount(): Int = occurrence.size
}
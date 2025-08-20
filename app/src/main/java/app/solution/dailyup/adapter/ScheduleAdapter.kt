package app.solution.dailyup.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.solution.dailyup.databinding.ScheduleViewCountingBinding
import app.solution.dailyup.databinding.ScheduleViewNormalBinding
import app.solution.dailyup.model.ScheduleModel
import app.solution.dailyup.utility.ScheduleTypeEnum

class ScheduleAdapter(
    private val scheduleList: MutableList<ScheduleModel>,
    private val onIconClick: (Int) -> Unit,
    private val onItemClick: (Int) -> Unit,
    private val onItemLongClick: (Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newScheduleList: List<ScheduleModel>) {
        scheduleList.clear()
        scheduleList.addAll(newScheduleList)
        notifyDataSetChanged()
    }

    inner class ScheduleNormalViewHolder(private val binding: ScheduleViewNormalBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.scheduleModel = scheduleList[position]
            binding.layoutRoot.apply {
                setOnClickListener {
                    onItemClick(position)
                }

                setOnLongClickListener {
                    onItemLongClick(position)
                    true
                }
            }

            binding.btnIcon.apply {
                setOnClickListener {
                    onIconClick(position)
                }
            }
        }
    }

    inner class ScheduleCountingViewHolder(private val binding: ScheduleViewCountingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.scheduleModel = scheduleList[position]
            binding.layoutRoot.apply {
                setOnClickListener {
                    onItemClick(position)
                }

                setOnLongClickListener {
                    onItemLongClick(position)
                    true
                }
            }

            binding.pbIcon.apply {
                setOnClickListener { onIconClick(position) }
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

    override fun getItemViewType(position: Int) = scheduleList[position].type.ordinal
    override fun getItemCount(): Int = scheduleList.size
}
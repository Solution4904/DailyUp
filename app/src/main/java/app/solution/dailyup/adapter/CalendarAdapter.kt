package app.solution.dailyup.adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import app.solution.dailyup.CalendarUtil
import app.solution.dailyup.databinding.HorizontalCalendarItemBinding
import app.solution.dailyup.utility.TraceLog
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class CalendarAdapter(
    private val onItemClickListener: (date: LocalDate) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.DateViewHolder>() {
    private val weekDates: List<LocalDate> = CalendarUtil().getWeeklyDates()
    private var selectedPosition: Int = -1

    inner class DateViewHolder(private val binding: HorizontalCalendarItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val date = weekDates[position]
                val day = date.dayOfMonth   // 날짜
                val week = date.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN))   // 요일

                tvDay.text = day.toString()
                tvWeek.text = week.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        TraceLog(message = "weekDatas : \n$weekDates")

        return DateViewHolder(HorizontalCalendarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CalendarAdapter.DateViewHolder, position: Int) {
        holder.apply {
            bind(position)

            if (position == selectedPosition) {
                holder.itemView.setBackgroundColor(Color.BLUE)
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            }

            holder.itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun getItemCount() = weekDates.size
}
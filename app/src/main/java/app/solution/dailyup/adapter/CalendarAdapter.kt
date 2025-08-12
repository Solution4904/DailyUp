package app.solution.dailyup.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import app.solution.dailyup.CalendarUtil
import app.solution.dailyup.databinding.HorizontalCalendarItemBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class CalendarAdapter(
    private val onItemClickListener: (date: LocalDate) -> Unit,
    private val onUpdateDateEvent: (date: LocalDate) -> Unit,
) : RecyclerView.Adapter<CalendarAdapter.DateViewHolder>() {
    //    private val weekDates: List<LocalDate> = CalendarUtil().getWeeklyDates()
    private var weekDates: List<LocalDate> = CalendarUtil().getWeeklyDates()
    private var weekDate: LocalDate = LocalDate.now()
    private var selectedPosition: Int

    init {
        selectedPosition = (weekDate.dayOfWeek.value % 7)
    }

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
        return DateViewHolder(HorizontalCalendarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CalendarAdapter.DateViewHolder, position: Int) {
        holder.apply {
            bind(position)

            if (weekDate.get((WeekFields.ISO).weekOfWeekBasedYear()) == LocalDate.now().get((WeekFields.ISO).weekOfWeekBasedYear())
            ) {
                if (position == selectedPosition) {
                    itemView.setBackgroundColor(Color.BLUE)
                } else {
                    itemView.setBackgroundColor(Color.TRANSPARENT)
                }
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }

            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
            }
        }
    }

    override fun getItemCount() = weekDates.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateDates(newWeek: Int) {
        weekDate = weekDate.plusWeeks(newWeek.toLong())
        weekDates = CalendarUtil().getWeeklyDates(weekDate)

        onUpdateDateEvent(weekDate)

        notifyDataSetChanged()
    }
}
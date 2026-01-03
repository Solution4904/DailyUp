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
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class CalendarAdapter(
    private val onDateClickEvent: (date: LocalDate) -> Unit,
    private val onUpdateDateEvent: (date: LocalDate) -> Unit,
) : RecyclerView.Adapter<CalendarAdapter.DateViewHolder>() {
    private var weekDates: List<LocalDate> = CalendarUtil().getWeeklyDates()
    private var weekDate: LocalDate
    private var selectedPosition: Int
    private var selectedDate: LocalDate

    init {
        weekDate = LocalDate.now()

        selectedPosition = (weekDate.dayOfWeek.value % 7)
        selectedDate = weekDates[selectedPosition]
    }

    inner class DateViewHolder(private val binding: HorizontalCalendarItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val date = weekDates[position]
                val day = date.dayOfMonth   // 날짜
                val week = date.format(DateTimeFormatter.ofPattern("E", Locale.KOREAN))   // 요일

                val checkDate = weekDates.find { it == selectedDate }
                if (checkDate != null) {
                    if (position == selectedPosition) {
                        layoutRoot.setBackgroundColor(Color.BLUE)
                    } else {
                        layoutRoot.setBackgroundColor(Color.TRANSPARENT)
                    }
                } else {
                    layoutRoot.setBackgroundColor(Color.TRANSPARENT)
                }

                tvDay.text = day.toString()
                tvWeek.text = week.toString()

                layoutRoot.setOnClickListener {
                    selectedDate = date
                    val previousPosition = selectedPosition
                    selectedPosition = adapterPosition
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                    onDateClickEvent(weekDates[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        return DateViewHolder(HorizontalCalendarItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CalendarAdapter.DateViewHolder, position: Int) {
        holder.apply {
            bind(position)
        }
    }

    override fun getItemCount() = weekDates.size

//    @SuppressLint("NotifyDataSetChanged")
//    fun updateDates(newWeek: Int) {
//        weekDate = weekDate.plusWeeks(newWeek.toLong())
//        weekDates = CalendarUtil().getWeeklyDates(weekDate)
//
//        onUpdateDateEvent(weekDate)
//
//        notifyDataSetChanged()
//    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDates(date: LocalDate) {
        weekDates = CalendarUtil().getWeeklyDates(date)

        onUpdateDateEvent(weekDate)

        notifyDataSetChanged()
    }
}
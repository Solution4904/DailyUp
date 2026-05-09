package app.solution.dailyup.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.solution.dailyup.databinding.HorizontalCalendarItemBinding
import app.solution.dailyup.utility.CalendarUtil
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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

                val isSelected = (weekDates.find { it == selectedDate } != null) && (position == selectedPosition)
                if (isSelected) {
                    layoutRoot.setBackgroundColor(Color.BLACK)
                    tvWeek.setTextColor(Color.WHITE)
                    tvDay.setTextColor(Color.WHITE)
                } else {
                    layoutRoot.setBackgroundColor(Color.TRANSPARENT)
                    tvWeek.setTextColor(Color.BLACK)
                    tvDay.setTextColor(Color.BLACK)
                }

                tvWeek.text = week.toString()
                tvDay.text = day.toString()

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
        weekDate = date
        weekDates = CalendarUtil().getWeeklyDates(date)

        selectedDate = weekDates[selectedPosition]

        onUpdateDateEvent(weekDate)
        notifyDataSetChanged()
    }
}
package app.solution.dailyup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.solution.dailyup.R
import app.solution.dailyup.databinding.ItemChartPageBinding
import app.solution.dailyup.model.ChartPageItem

class ChartPagerAdapter(private val items: List<ChartPageItem>) : RecyclerView.Adapter<ChartPagerAdapter.ChartPageViewHolder>() {
    inner class ChartPageViewHolder(private val binding: ItemChartPageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChartPageItem) {
            binding.item = item
            binding.executePendingBindings()

            binding.pbChart.setIndicatorColor(item.indicatorColor)
            binding.pbChart.setProgressCompat(item.box.rate, true)
            binding.tvProgress.text = binding.root.context.getString(
                R.string.chart_progress_format,
                item.box.achieved,
                item.box.total,
                item.box.rate
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartPageViewHolder {
        return ChartPageViewHolder(ItemChartPageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ChartPageViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
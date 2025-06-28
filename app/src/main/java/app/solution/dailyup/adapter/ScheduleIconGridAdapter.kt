package app.solution.dailyup.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import app.solution.dailyup.R


class ScheduleIconGridAdapter(
    private val context: Context,
    private val resName: Array<String>,
) : BaseAdapter() {
    //    총 항목 갯수
    override fun getCount() = resName.size

    //    position번 째 항목
    override fun getItem(position: Int) = resName[position]

    //    position번 째 항목의 id
    override fun getItemId(position: Int) = position.toLong()

    //    position번 째 항목의 뷰
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(R.layout.schedule_grid_item, parent, false)
        val imageView = view.findViewById<ImageView>(R.id.iv_icon)

        val resId = context.resources.getIdentifier(resName[position], "drawable", context.packageName)
        imageView.setImageResource(resId)

        return view
    }
}
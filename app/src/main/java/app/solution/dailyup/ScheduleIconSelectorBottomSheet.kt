package app.solution.dailyup

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import app.solution.dailyup.adapter.ScheduleIconGridAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ScheduleIconSelectorBottomSheet(
    private val onItemClick: (Int) -> Unit
) : BottomSheetDialogFragment() {
    private lateinit var iconList: Array<String>


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        iconList = resources.getStringArray(R.array.schedule_icon_name_array)


        return inflater.inflate(R.layout.activity_schedule_icon_selector_bottom_sheet, container, false)
    }

    @SuppressLint("DiscouragedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gridview = view.findViewById<GridView>(R.id.gv_list)
        val adapter = ScheduleIconGridAdapter(view.context, iconList)
        gridview.adapter = adapter
        gridview.setOnItemClickListener { parent, view, position, id ->
            val resId = view.resources.getIdentifier(iconList[position], "drawable", view.rootView.context.packageName)
            onItemClick.invoke(resId)

            val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            BottomSheetBehavior.from(bottomSheet!!).apply {
                dismiss()
            }
        }
    }
}
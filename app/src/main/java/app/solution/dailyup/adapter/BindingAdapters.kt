package app.solution.dailyup.adapter

import android.annotation.SuppressLint
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import app.solution.dailyup.R
import app.solution.dailyup.model.ScheduleModel
import com.bumptech.glide.Glide

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("scheduleIcon")
    fun loadIconImage(view: ImageButton, iconResId: Int?) {
        if (iconResId != 0) {
            Glide.with(view.context)
                .load(iconResId)
                .into(view)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @JvmStatic
    @BindingAdapter("scheduleUpdate")
    fun scheduleUpdate(view: ImageButton, scheduleModel: ScheduleModel) {
        val iconResId = if (scheduleModel.isCompleted || scheduleModel.processMaxValue == scheduleModel.processValue) {
            R.drawable.ic_check
        } else {
            scheduleModel.iconResId
        }

        Glide.with(view.context)
            .load(iconResId)
            .into(view)
    }
}
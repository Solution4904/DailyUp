package app.solution.dailyup.adapter

import android.annotation.SuppressLint
import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import app.solution.dailyup.R
import app.solution.dailyup.model.ScheduleOccurrence
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
    fun scheduleUpdate(view: ImageButton, occurrence: ScheduleOccurrence) {
        val source = occurrence.source
        val progress = occurrence.progress
        val isDone = progress.isComplete
                || (source.progressMaxValue != null
                && progress.progressValue == source.progressMaxValue)
        val iconResId = if (isDone) {
            R.drawable.ic_check
        } else {
            source.iconResId
        }

        Glide
            .with(view.context)
            .load(iconResId)
            .into(view)

        /*val iconResId = if (scheduleModel.isCompleted || scheduleModel.progressMaxValue == scheduleModel.progressValue) {
            R.drawable.ic_check
        } else {
            scheduleModel.iconResId
        }

        Glide.with(view.context)
            .load(iconResId)
            .into(view)*/
    }
}
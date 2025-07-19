package app.solution.dailyup.adapter

import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import app.solution.dailyup.utility.TraceLog
import com.bumptech.glide.Glide

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("scheduleIcon")
    fun loadIconImage(view: ImageButton, iconResId: Int?) {
        TraceLog(message = "BindingAdapters loadIconImage -> $iconResId")

        if (iconResId != 0) {
            Glide.with(view.context)
                .load(iconResId)
                .into(view)
        }
    }
}
package app.solution.dailyup.adapter

import android.widget.ImageButton
import androidx.databinding.BindingAdapter
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
}
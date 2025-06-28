package app.solution.dailyup.adapter

import android.widget.ImageButton
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("scheduleIcon")
    fun loadIconImage(view: ImageButton, iconName: String?) {
        val _resId = view.resources.getIdentifier(iconName, "drawable", view.rootView.context.packageName)

        if (_resId != 0) {
            Glide.with(view.context)
                .load(_resId)
                .into(view)
        }
    }
}
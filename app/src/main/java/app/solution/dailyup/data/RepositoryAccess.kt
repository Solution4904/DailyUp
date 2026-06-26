package app.solution.dailyup.data

import android.content.Context
import app.solution.dailyup.MyApplication

//  todo:???
val Context.scheduleRepository: ScheduleRepository
    get() = (applicationContext as MyApplication).scheduleRepository
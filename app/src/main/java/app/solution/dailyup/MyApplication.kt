package app.solution.dailyup

import android.app.Application
import app.solution.dailyup.data.LocalScheduleRepository
import app.solution.dailyup.data.ScheduleRepository
import app.solution.dailyup.utility.ConstKeys
import app.solution.dailyup.utility.LocalDataManager
import app.solution.dailyup.utility.NotificationHelper

class MyApplication : Application() {
    lateinit var scheduleRepository: ScheduleRepository
        private set

    override fun onCreate() {
        super.onCreate()
        
        //  data
        val prefs = getSharedPreferences(ConstKeys.SHARED_PREFERENCES, MODE_PRIVATE)
        scheduleRepository = LocalScheduleRepository(prefs)
//        LocalDataManager.init(this)

        //  notification
        NotificationHelper.createChannels(this)
    }
}
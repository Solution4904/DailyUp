package app.solution.dailyup

import android.app.Application
import app.solution.dailyup.utility.LocalDataManager
import app.solution.dailyup.utility.NotificationHelper

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        //  data
        LocalDataManager.init(this)

        //  notification
        NotificationHelper.createChannels(this)
    }
}
package app.solution.dailyup

import android.app.Application
import app.solution.dailyup.utility.LocalDataManager

class MyAppication : Application() {
    override fun onCreate() {
        super.onCreate()

        LocalDataManager.init(this)
    }
}
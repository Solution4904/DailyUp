package app.solution.dailyup

import android.app.Application
import app.solution.dailyup.utility.LocalDataManager

class MyAppication : Application() {
    companion object{
        lateinit var localDataManager: LocalDataManager
    }

    override fun onCreate() {
        super.onCreate()

        localDataManager = LocalDataManager(applicationContext)
    }
}
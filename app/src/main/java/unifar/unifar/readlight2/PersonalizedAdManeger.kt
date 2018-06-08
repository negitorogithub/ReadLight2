package unifar.unifar.readlight2

import android.content.Context
import android.content.SharedPreferences

class PersonalizedAdManager(private val applicationContext: Context) {

    fun updatePersonalized() {
        applicationContext.getSharedPreferences("adManager", Context.MODE_PRIVATE).edit().putBoolean("isPersonalized", true).apply()
    }

    fun updateNonPersonalized() {
        applicationContext.getSharedPreferences("adManager", Context.MODE_PRIVATE).edit().putBoolean("isPersonalized", false).apply()
    }
    fun getIsPersonalized(): Boolean {
        return applicationContext.getSharedPreferences("adManager", Context.MODE_PRIVATE).getBoolean("isPersonalized", false)
    }

}
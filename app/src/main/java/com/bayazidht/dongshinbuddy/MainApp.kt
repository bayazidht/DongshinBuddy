package com.bayazidht.dongshinbuddy

import android.app.Application
import androidx.core.graphics.toColorInt
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val mySeedColor = "#0563BC".toColorInt()
        DynamicColors.applyToActivitiesIfAvailable(
            this,
            DynamicColorsOptions.Builder()
                .setContentBasedSource(mySeedColor)
                .build()
        )
    }
}
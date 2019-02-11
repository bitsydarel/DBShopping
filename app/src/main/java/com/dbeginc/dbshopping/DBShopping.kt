package com.dbeginc.dbshopping

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.StrictMode
import android.support.text.emoji.EmojiCompat
import android.support.text.emoji.FontRequestEmojiCompatConfig
import android.support.v4.provider.FontRequest
import android.text.format.DateUtils
import com.crashlytics.android.Crashlytics
import com.dbeginc.data.CrashlyticsLogger
import com.dbeginc.dbshopping.di.Injector
import com.dbeginc.dbshopping.di.components.DaggerApplicationComponent
import com.dbeginc.dbshopping.utils.sync.DataPushJob
import com.dbeginc.dbshopping.utils.sync.UserAvailabilityJob
import com.facebook.FacebookSdk
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import io.fabric.sdk.android.Fabric
import io.reactivex.plugins.RxJavaPlugins

/**
 * Created by darel on 21.08.17.
 *
 * DBShopping Application
 */
class DBShopping: DaggerApplication() {

    companion object {
        private const val DATA_SYNC_JOB_ID = 5712
        private const val USER_AVAILABILITY_JOB_ID = 57123

        @JvmStatic val memoryCache: HashMap<String, Any> = HashMap(1)

        @JvmStatic fun scheduleDataSync(context: Context) {
            val scheduler : JobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (scheduler.getPendingJob(DATA_SYNC_JOB_ID) != null)
                    return

            } else {
                if (scheduler.allPendingJobs.find { it.id == DATA_SYNC_JOB_ID } != null)
                    return
            }

            val serviceComponent = ComponentName(context, DataPushJob::class.java)

            val jobRequirement = JobInfo.Builder(DATA_SYNC_JOB_ID, serviceComponent)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setRequiresCharging(false)
                    .setPersisted(true)
                    .setPeriodic(2 * DateUtils.HOUR_IN_MILLIS)
                    .build()

            scheduler.schedule(jobRequirement)
        }

        @JvmStatic fun scheduleUserAvailabilityCheck(context: Context) {
            val scheduler : JobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (scheduler.getPendingJob(USER_AVAILABILITY_JOB_ID) != null)
                    return

            } else {
                if (scheduler.allPendingJobs.find { it.id == USER_AVAILABILITY_JOB_ID } != null)
                    return
            }

            val serviceComponent = ComponentName(context, UserAvailabilityJob::class.java)

            val jobRequirement = JobInfo.Builder(USER_AVAILABILITY_JOB_ID, serviceComponent)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_NONE)
                    .setRequiresDeviceIdle(true)
                    .setRequiresCharging(false)
                    .setPersisted(true)
                    .setPeriodic(5 * DateUtils.HOUR_IN_MILLIS /* Every 5 hours */)
                    .build()

            scheduler.schedule(jobRequirement)
        }
    }

    override fun onCreate() {
        super.onCreate()

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }

        LeakCanary.install(this)

        AndroidThreeTen.init(this)

        Fabric.with(this, Crashlytics())

        RxJavaPlugins.setErrorHandler { CrashlyticsLogger.logError(it) }

        FacebookSdk.setApplicationId(getString(R.string.facebook_app_id))

        val fontRequest = FontRequest(
                "com.google.android.gms.fonts",
                "com.google.android.gms",
                "Noto Color Emoji Compat",
                R.array.com_google_android_gms_fonts_certs
        )

        val emojiConfiguration : EmojiCompat.Config =
                FontRequestEmojiCompatConfig(this, fontRequest)

        if (BuildConfig.DEBUG) {
            StrictMode.noteSlowCall(packageName)

            StrictMode.setThreadPolicy(
                    StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyDialog()
                            .penaltyLog()
                            .build()
            )

            StrictMode.setVmPolicy(
                    StrictMode.VmPolicy.Builder()
                            .setClassInstanceLimit(MainActivity::class.java, 2)
                            .detectAll()
                            .penaltyLog()
                            .build()
            )

            emojiConfiguration.setEmojiSpanIndicatorEnabled(true)

            emojiConfiguration.setEmojiSpanIndicatorColor(Color.YELLOW)
        }

        EmojiCompat.init(emojiConfiguration)

        Injector.init(this)
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
            DaggerApplicationComponent.builder().create(this)

}
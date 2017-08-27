package com.dbeginc.dbshopping

import android.app.Application
import com.dbeginc.dbshopping.di.application.component.DaggerApplicationComponent
import com.dbeginc.dbshopping.di.application.module.AppModule
import com.dbeginc.dbshopping.helper.ConstantHolder
import com.dbeginc.dbshopping.helper.Injector
import com.squareup.leakcanary.LeakCanary
import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by darel on 21.08.17.
 */
class DBShopping: Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

        val builder = RealmConfiguration.Builder()
                .name(ConstantHolder.DB_NAME)
                .schemaVersion(ConstantHolder.DB_VERSION)

        if (BuildConfig.DEBUG) builder.deleteRealmIfMigrationNeeded()

        val configuration = builder.build()
        Realm.setDefaultConfiguration(configuration)

        Injector.appComponent = DaggerApplicationComponent.builder()
                .appModule(AppModule(this))
                .build()


        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        //Todo : Important check if you're doing  work outside of the mainThread
        // with Looper.myLooper() == Looper.getMainLooper()
    }
}
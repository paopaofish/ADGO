package com.lgh.advertising.going.myclass;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.lgh.advertising.going.mybean.AppDescribe;
import com.lgh.advertising.going.mybean.MyAppConfig;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MyApplication extends Application {

    public static DataDao dataDao;
    public static MyAppConfig myAppConfig;
    public static AppDescribe appDescribe;
    public static MyHttpRequest myHttpRequest;

    @Override
    public void onCreate() {
        super.onCreate();

        if (dataDao == null) {
            Migration migration_1_2 = new Migration(1, 2) {
                @Override
                public void migrate(@NonNull SupportSQLiteDatabase database) {
                    database.execSQL("ALTER TABLE 'Coordinate' ADD 'comment' TEXT");
                    database.execSQL("ALTER TABLE 'Widget' ADD 'comment' TEXT");
                }
            };
            Migration migration_2_3 = new Migration(2, 3) {
                @Override
                public void migrate(@NonNull SupportSQLiteDatabase database) {
                    database.execSQL("ALTER TABLE 'MyAppConfig' ADD 'isVip' INTEGER NOT NULL DEFAULT 0");
                }
            };
            Migration migration_3_4 = new Migration(3, 4) {
                @Override
                public void migrate(@NonNull SupportSQLiteDatabase database) {
                    database.execSQL("ALTER TABLE 'Widget' ADD `noRepeat` INTEGER NOT NULL DEFAULT 0");
                }
            };
            Migration migration_4_5 = new Migration(4, 5) {
                @Override
                public void migrate(@NonNull SupportSQLiteDatabase database) {
                    database.execSQL("ALTER TABLE 'AppDescribe' RENAME 'on_off' to 'onOff'");
                }
            };
            dataDao = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, "applicationData.db").addMigrations(migration_1_2, migration_2_3, migration_3_4, migration_4_5).allowMainThreadQueries().build().dataDao();
        }

        if (myAppConfig == null) {
            myAppConfig = dataDao.getMyAppConfig();
        }

        if (myHttpRequest == null) {
            Retrofit retrofit = new Retrofit.Builder().baseUrl("https://api.github.com/").addConverterFactory(ScalarsConverterFactory.create()).addConverterFactory(GsonConverterFactory.create()).addCallAdapterFactory(RxJava3CallAdapterFactory.create()).build();
            myHttpRequest = retrofit.create(MyHttpRequest.class);
        }

        MyUncaughtExceptionHandler.getInstance(this).run();
    }
}

package pondthaitay.googlemapapi.exercises.ui.mylocattion.database;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.config.FlowManager;

@Database(name = LocationDatabase.NAME, version = LocationDatabase.VERSION)
public class LocationDatabase {

    static final String NAME = "LocationDatabase";

    static final int VERSION = 1;

    static void insertLocation(LocationModel model) {
        FlowManager.getModelAdapter(LocationModel.class).save(model);
    }

//    static void queryDataByLocation(String location){
//        SQLite.select()
//                .from(LocationModel.class)
//                .where(LocationModel)
//    }
}

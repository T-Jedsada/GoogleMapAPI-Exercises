package pondthaitay.googlemapapi.exercises.ui.mylocattion.database;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

@Database(name = LocationDatabase.NAME, version = LocationDatabase.VERSION)
public class LocationDatabase {

    static final String NAME = "LocationDatabase";
    static final int VERSION = 1;

    public void insertLocation(LocationModel model) {
        FlowManager.getModelAdapter(LocationModel.class).save(model);
    }

    public void updateLocation(LocationModel model) {
        FlowManager.getModelAdapter(LocationModel.class).update(model);
    }

    @SuppressWarnings("ConstantConditions")
    public LocationModel queryDataByLocation(String location) {
        return SQLite.select()
                .from(LocationModel.class)
                .where(LocationModel_Table.location.eq(location))
                .querySingle();
    }

    @SuppressWarnings("ConstantConditions")
    public LocationModel queryIdByLocation(String location) {
        return SQLite.select()
                .from(LocationModel.class)
                .where(LocationModel_Table.location.like(location))
                .querySingle();
    }

    public List<LocationModel> getPendingMessages() {
        return SQLite.select()
                .from(LocationModel.class)
                .orderBy(LocationModel_Table.id, true)
                .queryList();
    }
}
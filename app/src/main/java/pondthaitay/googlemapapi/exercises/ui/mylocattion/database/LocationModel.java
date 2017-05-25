package pondthaitay.googlemapapi.exercises.ui.mylocattion.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

@Table(database = LocationDatabase.class)
public class LocationModel {

    @PrimaryKey(autoincrement = true)
    public int id;
    @Column
    public String location;
    @Column
    public String jsonData;
}

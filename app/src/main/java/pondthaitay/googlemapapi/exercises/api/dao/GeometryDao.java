package pondthaitay.googlemapapi.exercises.api.dao;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel(Parcel.Serialization.BEAN)
public class GeometryDao {

    @SerializedName("location")
    private ResultLocationDao location;

    public ResultLocationDao getLocation() {
        return location;
    }

    public void setLocation(ResultLocationDao location) {
        this.location = location;
    }
}

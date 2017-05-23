package pondthaitay.googlemapapi.exercises.api.dao;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel(Parcel.Serialization.BEAN)
public class NearbySearchDao {

    @SerializedName("next_page_token")
    private String nextPageToken;
    @SerializedName("results")
    private List<ResultNearbySearchDao> list = new ArrayList<>();
    @SerializedName("status")
    private String status;

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<ResultNearbySearchDao> getList() {
        return list;
    }

    public void setList(List<ResultNearbySearchDao> list) {
        this.list = list;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

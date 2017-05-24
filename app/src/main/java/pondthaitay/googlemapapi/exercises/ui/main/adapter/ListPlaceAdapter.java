package pondthaitay.googlemapapi.exercises.ui.main.adapter;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import pondthaitay.googlemapapi.exercises.R;
import pondthaitay.googlemapapi.exercises.api.dao.NearbySearchDao;
import pondthaitay.googlemapapi.exercises.api.dao.ResultNearbySearchDao;

public class ListPlaceAdapter extends RecyclerView.Adapter<ListPlaceViewHolder> implements
        ListPlaceViewHolder.LisPlaceViewHolderListener {

    private NearbySearchDao dao;
    private LisPlaceListener listener;
    private Location myLocation;

    public interface LisPlaceListener {
        void onPlaceItemClick(ResultNearbySearchDao dao);
    }

    public void setData(NearbySearchDao data, Location myLocation) {
        this.dao = data;
        this.myLocation = myLocation;
        notifyDataSetChanged();
    }

    public void setListener(LisPlaceListener listener) {
        this.listener = listener;
    }

    @Override
    public ListPlaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ListPlaceViewHolder holder = new ListPlaceViewHolder(LayoutInflater
                .from(parent.getContext()).inflate(R.layout.place_item, parent, false));
        holder.setListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(ListPlaceViewHolder holder, int position) {
        holder.setupView(dao.getList().get(position), myLocation);
    }

    @Override
    public int getItemCount() {
        if (dao == null || dao.getList().isEmpty()) return 0;
        else return dao.getList().size();
    }

    @Override
    public void itemClick(int position) {
        if (listener != null) listener.onPlaceItemClick(dao.getList().get(position));
    }
}
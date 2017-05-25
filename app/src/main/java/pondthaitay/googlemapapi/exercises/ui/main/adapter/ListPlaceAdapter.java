package pondthaitay.googlemapapi.exercises.ui.main.adapter;

import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import pondthaitay.googlemapapi.exercises.R;
import pondthaitay.googlemapapi.exercises.api.dao.NearbySearchDao;
import pondthaitay.googlemapapi.exercises.api.dao.ResultNearbySearchDao;

public class ListPlaceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        ListPlaceViewHolder.LisPlaceViewHolderListener {

    private static final int TYPE_PLACE = 0;
    private static final int TYPE_PROGRESS = 1;

    private NearbySearchDao dao;
    private LisPlaceListener listener;
    private Location myLocation;
    private boolean isNextItemAvailable;

    public interface LisPlaceListener {
        void loadMore();

        void onPlaceItemClick(ResultNearbySearchDao dao);
    }

    public void setData(NearbySearchDao data, Location myLocation, boolean nextPage) {
        this.dao = data;
        this.myLocation = myLocation;
        this.isNextItemAvailable = nextPage;
        notifyDataSetChanged();
    }

    public NearbySearchDao getData() {
        return dao;
    }

    public void setListener(LisPlaceListener listener) {
        this.listener = listener;
    }

    public void setNextItemAvailable(boolean nextItemAvailable) {
        isNextItemAvailable = nextItemAvailable;
        notifyDataSetChanged();
    }

    public void addNewPlace(List<ResultNearbySearchDao> list, boolean nexPage) {
        isNextItemAvailable = nexPage;
        dao.getList().addAll(dao.getList().size(), list);
        notifyItemInserted(dao.getList().size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_PLACE) {
            ListPlaceViewHolder holder = new ListPlaceViewHolder(LayoutInflater
                    .from(parent.getContext()).inflate(R.layout.place_item, parent, false));
            holder.setListener(this);
            return holder;
        } else {
            return new ProgressViewHolder(LayoutInflater
                    .from(parent.getContext()).inflate(R.layout.progress_item, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_PLACE) {
            if (position < dao.getList().size())
                ((ListPlaceViewHolder) holder).setupView(dao.getList().get(position), myLocation);
        } else if (getItemViewType(position) == TYPE_PROGRESS && listener != null) {
            listener.loadMore();
        }
    }

    @Override
    public int getItemCount() {
        int count;
        if (dao == null) count = 0;
        else count = dao.getList().size();
        if (isNextItemAvailable()) count++;
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= dao.getList().size()) return TYPE_PROGRESS;
        else return TYPE_PLACE;
    }

    @Override
    public void itemClick(int position) {
        if (listener != null) listener.onPlaceItemClick(dao.getList().get(position));
    }

    private boolean isNextItemAvailable() {
        return isNextItemAvailable;
    }
}
package pondthaitay.googlemapapi.exercises.ui.main.adapter;

import android.annotation.SuppressLint;
import android.location.Location;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import pondthaitay.googlemapapi.exercises.R;
import pondthaitay.googlemapapi.exercises.api.dao.ResultNearbySearchDao;

class ListPlaceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.tv_name)
    AppCompatTextView tvName;
    @BindView(R.id.tv_distance)
    AppCompatTextView tvDistance;

    private LisPlaceViewHolderListener listener;

    interface LisPlaceViewHolderListener {
        void itemClick(int position);
    }

    ListPlaceViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
    }

    void setListener(LisPlaceViewHolderListener listener) {
        this.listener = listener;
    }

    void setupView(ResultNearbySearchDao dao, Location myLocation) {
        Location desLocation = new Location("des");
        desLocation.setLatitude(dao.getGeometryDao().getLocation().getLat());
        desLocation.setLongitude(dao.getGeometryDao().getLocation().getLng());
        tvName.setText(dao.getName());
        tvDistance.setText(getDistance(myLocation, desLocation));
    }

    @SuppressLint("DefaultLocale")
    private String getDistance(Location origins, Location destination) {
        return String.format("%s : %.2f %s", itemView.getContext().getString(R.string.distance),
                (origins.distanceTo(destination) / 1000),
                itemView.getContext().getString(R.string.km));
    }

    @Override
    public void onClick(View v) {
        int position = getAdapterPosition();
        if (position == RecyclerView.NO_POSITION && listener == null) return;
        listener.itemClick(position);
    }
}
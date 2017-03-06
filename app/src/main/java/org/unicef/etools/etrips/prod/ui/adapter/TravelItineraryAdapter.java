package org.unicef.etools.etrips.prod.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.trip.Itinerary;
import org.unicef.etools.etrips.prod.util.DateUtil;
import org.unicef.etools.etrips.prod.util.StringUtils;

import java.util.ArrayList;

import static org.unicef.etools.etrips.prod.util.DateUtil.DD_MMM_YYYY;

public class TravelItineraryAdapter extends RecyclerView.Adapter<TravelItineraryAdapter.ViewHolder> {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = TravelItineraryAdapter.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private ArrayList<Itinerary> mItinararies;
    private OnItemClickListener mOnItemClickListener;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TravelItineraryAdapter(ArrayList<Itinerary> itinararies, OnItemClickListener onItemClickListener) {
        mItinararies = itinararies;
        mOnItemClickListener = onItemClickListener;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_item_travel_itinerary, viewGroup, false);
        return new ViewHolder(view, mItinararies, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData();
    }

    @Override
    public int getItemCount() {
        return mItinararies.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // ViewHolder
    // ===========================================================

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTvTiNumber;
        private TextView mTvTiFrom;
        private TextView mTvTiTo;
        private TextView mTvTiDeparting;
        private TextView mTvTiArriving;
        private TextView mTvTiMode;
        LinearLayout llTiContainer;
        OnItemClickListener onItemClickListener;
        ArrayList<Itinerary> mItinararies;

        ViewHolder(View itemView, ArrayList<Itinerary> mTravelRoutes1, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.mItinararies = mTravelRoutes1;
            this.onItemClickListener = onItemClickListener;
            findViews(itemView);
        }

        void findViews(View view) {
            mTvTiNumber = (TextView) view.findViewById(R.id.tv_ti_number);
            mTvTiFrom = (TextView) view.findViewById(R.id.tv_ti_from);
            mTvTiTo = (TextView) view.findViewById(R.id.tv_ti_to);
            mTvTiDeparting = (TextView) view.findViewById(R.id.tv_ti_departing);
            mTvTiArriving = (TextView) view.findViewById(R.id.tv_ti_arriving);
            mTvTiMode = (TextView) view.findViewById(R.id.tv_ti_mode);
            llTiContainer = (LinearLayout) view.findViewById(R.id.ll_ti_container);
        }

        void bindData() {

            mTvTiNumber.setText(String.valueOf(getAdapterPosition() + 1));

            mTvTiFrom.setText(mItinararies.get(getAdapterPosition()).getOrigin());

            mTvTiTo.setText(mItinararies.get(getAdapterPosition()).getDestination());

            mTvTiDeparting.setText(DateUtil.convertISOtoCalendarDate(
                    mItinararies.get(getAdapterPosition()).getDepartureDate(), DD_MMM_YYYY));

            mTvTiArriving.setText(DateUtil.convertISOtoCalendarDate(
                    mItinararies.get(getAdapterPosition()).getArrivalDate(), DD_MMM_YYYY));

            String modeOfTravel = mItinararies.get(getAdapterPosition()).getModeOfTravel();

            // capitalize first letter in travel mode str
            if (modeOfTravel != null) {
                modeOfTravel = StringUtils.textCapSentences(modeOfTravel);
            }

            mTvTiMode.setText(modeOfTravel);

            llTiContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                        Log.e(LOG_TAG, "onClick with position == -1.");
                        return;
                    }
                    onItemClickListener.onItemClick(mItinararies.get(getAdapterPosition()));
                }
            });

            llTiContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                        Log.e(LOG_TAG, "onLongClick with position == -1.");
                        return false;
                    }
                    onItemClickListener.onItemLongClick(mItinararies.get(getAdapterPosition()));
                    return true;
                }
            });
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public interface OnItemClickListener {

        void onItemClick(Itinerary itinerary);

        void onItemLongClick(Itinerary itinerary);
    }
}

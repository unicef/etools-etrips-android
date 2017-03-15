package org.unicef.etools.etrips.prod.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.trip.Trip;
import org.unicef.etools.etrips.prod.util.Constant;
import org.unicef.etools.etrips.prod.util.DateUtil;
import org.unicef.etools.etrips.prod.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.unicef.etools.etrips.prod.util.DateUtil.DD_MMM_YYYY;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = TripAdapter.class.getSimpleName();
    private static final int CONTENT_VIEW_TYPE = 0;
    private static final int LOAD_MORE_VIEW_TYPE = 1;

    // ===========================================================
    // Fields
    // ===========================================================

    private boolean showLoadMore;
    private ArrayList<Trip> mTripList;
    private OnItemClickListener mOnItemClickListener;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TripAdapter(ArrayList<Trip> tripList, OnItemClickListener onItemClickListener) {
        mTripList = tripList;
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
        int layout = 0;

        switch (viewType) {

            case CONTENT_VIEW_TYPE:
                layout = R.layout.adapter_item_trip;
                break;

            case LOAD_MORE_VIEW_TYPE:
                layout = R.layout.layout_pagination_preloader;
                break;
        }

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
        return new ViewHolder(viewType, view, mTripList, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position >= mTripList.size()) {
            holder.bindLoadMoreData();
        } else {
            holder.bindContentData();
        }
    }

    @Override
    public int getItemCount() {
        return showLoadMore && mTripList.size() > 0 ? mTripList.size() + 1 : mTripList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position >= mTripList.size() ? LOAD_MORE_VIEW_TYPE : CONTENT_VIEW_TYPE;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Click Listeners
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    public void add(List<Trip> trips, boolean forceUpdate) {
        if (trips == null || trips.isEmpty()) {
            return;
        }

        if (forceUpdate) {
            mTripList.clear();
            mTripList.addAll(trips);
            notifyDataSetChanged();
        } else {
            final int start = mTripList.size();
            final int count = trips.size();

            mTripList.addAll(trips);
            notifyItemRangeInserted(start, count);
        }
    }

    public void add(Trip trip) {
        if (trip == null || !trip.isValid()) {
            return;
        }

        final int start = mTripList.size();
        mTripList.add(trip);
        notifyItemInserted(start);
    }

    public void clear() {
        final int start = 0;
        final int count = mTripList.size();

        if (count == 0) {
            return;
        }

        mTripList.clear();
        notifyItemRangeRemoved(start, count);
    }

    public void showLoadMore() {
        if (getItemCount() == mTripList.size()) {
            showLoadMore = true;
            notifyItemInserted(mTripList.size() + 1);
        }
    }

    public void removeLoadMore() {
        if (getItemCount() > mTripList.size()) {
            showLoadMore = false;
            notifyItemRemoved(mTripList.size() + 1);
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    static class ViewHolder extends RecyclerView.ViewHolder {

        Context context;
        TextView tvTripTitle;
        TextView tvTripPeriod;
        TextView tvTripUserName;
        TextView tvTripStatue;
        TextView tvTripId;
        ImageView ivTripStatueLabel;
        CardView cvItemContainer;
        ProgressBar pbLoadMore;
        OnItemClickListener onItemClickListener;
        ArrayList<Trip> tripList;
        TextView tvTripDraft;

        ViewHolder(int type, View itemView, ArrayList<Trip> tripList, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.tripList = tripList;
            this.onItemClickListener = onItemClickListener;
            this.context = itemView.getContext();
            switch (type) {
                case CONTENT_VIEW_TYPE:
                    findContentViews(itemView);
                    break;

                case LOAD_MORE_VIEW_TYPE:
                    findLoadMoreViews(itemView);
                    break;
            }
        }

        void findContentViews(View view) {
            tvTripTitle = (TextView) view.findViewById(R.id.tv_item_trip_title);
            tvTripPeriod = (TextView) view.findViewById(R.id.tv_item_trip_period);
            tvTripUserName = (TextView) view.findViewById(R.id.tv_item_trip_user_name);
            tvTripStatue = (TextView) view.findViewById(R.id.tv_item_trip_status);
            tvTripId = (TextView) view.findViewById(R.id.tv_item_trip_id);
            tvTripDraft = (TextView) view.findViewById(R.id.tv_item_trip_traft);
            ivTripStatueLabel = (ImageView) view.findViewById(R.id.iv_item_trip_status);
            cvItemContainer = (CardView) view.findViewById(R.id.cv_item_trip_container);
        }

        void findLoadMoreViews(View view) {
            pbLoadMore = (ProgressBar) view.findViewById(R.id.pb_paginate);
        }

        void bindContentData() {

            if (tripList.get(getAdapterPosition()) == null || !tripList.get(getAdapterPosition()).isValid()) {
                return;
            }

            if (tripList.get(getAdapterPosition()).isNotSynced()) {
                tvTripDraft.setVisibility(View.VISIBLE);
            } else {
                tvTripDraft.setVisibility(View.GONE);
            }
            // set trip id
            tvTripId.setText(tripList.get(getAdapterPosition()).getReferenceNumber());

            // set trip name
            tvTripTitle.setText(tripList.get(getAdapterPosition()).getPurpose());

            // set trip period
            tvTripPeriod.setText(DateUtil.convertISOtoCalendarDate(tripList.get(getAdapterPosition()).getStartDate(), DD_MMM_YYYY)
                    + Constant.Symbol.SPACE_DASH
                    + DateUtil.convertISOtoCalendarDate(tripList.get(getAdapterPosition()).getEndDate(), DD_MMM_YYYY));

            // set trip status (text and right colored label)
            if (tripList.get(getAdapterPosition()).getStatus() != null) {
                tvTripStatue.setText(StringUtils.textCapSentences(
                        tripList.get(getAdapterPosition()).getStatus())
                        .replaceAll(Constant.Symbol.UNDERLINE, Constant.Symbol.SPACE));
            } else {
                tvTripStatue.setText(null);
            }

            // set trip user name (if is supervised - show)
            tvTripUserName.setText(tripList.get(getAdapterPosition()).isMyTrip() ?
                    null : tripList.get(getAdapterPosition()).getTravelerName());
            tvTripUserName.setVisibility(tripList.get(getAdapterPosition()).isMyTrip() ?
                    View.GONE : View.VISIBLE);

            switch (tripList.get(getAdapterPosition()).getStatus()) {
                case Trip.Status.APPROVED:
                    ivTripStatueLabel.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.color_c0e484));
                    break;

                case Trip.Status.PLANNED:
                    ivTripStatueLabel.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.color_fcf39a));
                    break;

                case Trip.Status.SUBMITTED:
                    ivTripStatueLabel.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.color_dcd051));
                    break;

                case Trip.Status.CANCELLED:
                    ivTripStatueLabel.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.color_848484));
                    break;

                case Trip.Status.COMPLETED:
                    ivTripStatueLabel.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.color_68ba00));
                    break;

                case Trip.Status.REJECTED:
                    ivTripStatueLabel.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.color_de2618));
                    break;

                case Trip.Status.CERTIFICATION_SUBMITTED:
                    ivTripStatueLabel.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.color_dcd051));
                    break;

                case Trip.Status.CERTIFIED:
                    ivTripStatueLabel.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.color_8cbedd));
                    break;

                case Trip.Status.SENT_FOR_PAYMENT:
                    ivTripStatueLabel.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.color_8cbedd));
                    break;

                case Trip.Status.CERTIFICATION_REJECTED:
                    ivTripStatueLabel.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.color_de2618));
                    break;

                case Trip.Status.CERTIFICATION_APPROVED:
                    ivTripStatueLabel.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.color_c0e484));
                    break;
            }

            // set Click Listener on item
            cvItemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                        Log.e(LOG_TAG, "onClick with position == -1.");
                        return;
                    }
                    onItemClickListener.onItemClick(tripList.get(getAdapterPosition()));
                }
            });

            // set Long Click Listener on item
            cvItemContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                        Log.e(LOG_TAG, "onLongClick with position == -1.");
                        return false;
                    }
                    onItemClickListener.onItemLongClick(tripList.get(getAdapterPosition()));
                    return true;
                }
            });
        }

        void bindLoadMoreData() {
        }
    }

    public interface OnItemClickListener {

        void onItemClick(Trip trip);

        void onItemLongClick(Trip trip);
    }
}

package org.unicef.etools.etrips.prod.ui.adapter;


import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.static_data.data.Partner;
import org.unicef.etools.etrips.prod.db.entity.static_data.data.PartnerShip;
import org.unicef.etools.etrips.prod.db.entity.trip.Activity;
import org.unicef.etools.etrips.prod.util.StringUtils;

import java.util.ArrayList;

import io.realm.Realm;

public class TravelActivityAdapter extends RecyclerView.Adapter<TravelActivityAdapter.ViewHolder> {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = TravelActivityAdapter.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private ArrayList<Activity> mActivities;
    private TravelActivityAdapter.OnItemClickListener mOnItemClickListener;

    // ===========================================================
    // Constructors
    // ===========================================================

    public TravelActivityAdapter(ArrayList<Activity> activities, TravelActivityAdapter.OnItemClickListener onItemClickListener) {
        mActivities = activities;
        mOnItemClickListener = onItemClickListener;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    public TravelActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_item_travel_activity, viewGroup, false);
        return new TravelActivityAdapter.ViewHolder(view, mActivities, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(TravelActivityAdapter.ViewHolder holder, int position) {
        holder.bindData();
    }

    @Override
    public int getItemCount() {
        return mActivities.size();
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

        private TextView mTvActNumber;
        private TextView mTvActTravelType;
        private TextView mTvActPartner;
        private TextView mTvActPartnerShip;
        private AppCompatCheckBox mChbActPrinaryTraveler;
        LinearLayout llTiContainer;
        TravelActivityAdapter.OnItemClickListener onItemClickListener;
        ArrayList<Activity> mActivities;

        ViewHolder(View itemView, ArrayList<Activity> activities, TravelActivityAdapter.OnItemClickListener onItemClickListener) {
            super(itemView);
            this.mActivities = activities;
            this.onItemClickListener = onItemClickListener;
            findViews(itemView);
        }

        void findViews(View view) {
            mTvActNumber = (TextView) view.findViewById(R.id.tv_act_number);
            mTvActTravelType = (TextView) view.findViewById(R.id.tv_act_travel_type);
            mTvActPartner = (TextView) view.findViewById(R.id.tv_act_partner);
            mTvActPartnerShip = (TextView) view.findViewById(R.id.tv_act_partnership);
            mChbActPrinaryTraveler = (AppCompatCheckBox) view.findViewById(R.id.chb_act_primary_traveler);
            llTiContainer = (LinearLayout) view.findViewById(R.id.ll_act_container);
        }

        void bindData() {

            mTvActNumber.setText(String.valueOf(getAdapterPosition() + 1));

            // retrieve data from date base by id
            Partner partner = Realm.getDefaultInstance().where(Partner.class)
                    .equalTo("id", mActivities.get(getAdapterPosition()).getPartner()).findFirst();

            PartnerShip partnerShip = Realm.getDefaultInstance().where(PartnerShip.class)
                    .equalTo("id", mActivities.get(getAdapterPosition()).getPartnerShip()).findFirst();

            String travelType = mActivities.get(getAdapterPosition()).getTravelType() != null
                    ? StringUtils.textCapSentences(mActivities.get(getAdapterPosition()).getTravelType()) : null;

            String partnerName = partner != null && partner.isValid() ? partner.getName() : null;
            String partnerShipName = partnerShip != null && partnerShip.isValid() ? partnerShip.getName() : null;

            mTvActTravelType.setText(travelType);

            mTvActPartner.setText(partnerName);

            mTvActPartnerShip.setText(partnerShipName);

            mChbActPrinaryTraveler.setChecked(mActivities.get(getAdapterPosition()).isPrimaryTraveler());

//            llTiContainer.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onItemClickListener.onItemClick(mActivities.get(getAdapterPosition()));
//                }
//            });
//
//            llTiContainer.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    onItemClickListener.onItemLongClick(mActivities.get(getAdapterPosition()));
//                    return true;
//                }
//            });
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public interface OnItemClickListener {

        void onItemClick(Activity activity);

        void onItemLongClick(Activity activity);
    }
}

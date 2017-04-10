package org.unicef.etools.etrips.prod.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.static_data.data_2.Fund;
import org.unicef.etools.etrips.prod.db.entity.static_data.data_2.Grant;
import org.unicef.etools.etrips.prod.db.entity.static_data.data_2.Wbs;
import org.unicef.etools.etrips.prod.db.entity.trip.CostAssignment;
import org.unicef.etools.etrips.prod.util.AppUtil;
import org.unicef.etools.etrips.prod.util.Constant;

import java.util.ArrayList;

import io.realm.Realm;

public class CostAssignmentAdapter extends RecyclerView.Adapter<CostAssignmentAdapter.ViewHolder> {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = CostAssignmentAdapter.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private ArrayList<CostAssignment> mCostAssignments;
    private OnItemClickListener mOnItemClickListener;

    // ===========================================================
    // Constructors
    // ===========================================================

    public CostAssignmentAdapter(ArrayList<CostAssignment> costAssignments, OnItemClickListener onItemClickListener) {
        mCostAssignments = costAssignments;
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
                .inflate(R.layout.adapter_item_cost_assignment, viewGroup, false);
        return new ViewHolder(view, mCostAssignments, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData();
    }

    @Override
    public int getItemCount() {
        return mCostAssignments.size();
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

        private TextView tvCaWbs;
        private TextView tvCaGrant;
        private TextView tvCaFund;
        private TextView tvCaShare;
        LinearLayout llCaContainer;
        OnItemClickListener onItemClickListener;
        ArrayList<CostAssignment> mCostAssignments;

        ViewHolder(View itemView, ArrayList<CostAssignment> costAssignments, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.mCostAssignments = costAssignments;
            this.onItemClickListener = onItemClickListener;
            findViews(itemView);
        }

        void findViews(View view) {
            tvCaWbs = (TextView) view.findViewById(R.id.tv_ca_wbs);
            tvCaGrant = (TextView) view.findViewById(R.id.tv_ca_grant);
            tvCaFund = (TextView) view.findViewById(R.id.tv_ca_fund);
            tvCaShare = (TextView) view.findViewById(R.id.tv_ca_share);
            llCaContainer = (LinearLayout) view.findViewById(R.id.ll_ca_container);
        }

        void bindData() {

            // retrieve data from date base by id
            Wbs wbs = Realm.getDefaultInstance().where(Wbs.class)
                    .equalTo("id", mCostAssignments.get(getAdapterPosition()).getWbs()).findFirst();

            Fund fund = Realm.getDefaultInstance().where(Fund.class)
                    .equalTo("id", mCostAssignments.get(getAdapterPosition()).getFund()).findFirst();

            Grant grant = Realm.getDefaultInstance().where(Grant.class)
                    .equalTo("id", mCostAssignments.get(getAdapterPosition()).getGrant()).findFirst();

            String wbsName = wbs != null && wbs.isValid() ? wbs.getName() : null;
            String fundName = fund != null && fund.isValid() ? fund.getName() : null;
            String grantName = grant != null && grant.isValid() ? grant.getName() : null;

            tvCaWbs.setText(wbsName);

            tvCaGrant.setText(grantName);

            tvCaFund.setText(fundName);

            tvCaShare.setText(String.valueOf(
                    AppUtil.formatDouble(mCostAssignments.get(getAdapterPosition()).getShare()))
                    + Constant.Symbol.PERCENT);

            llCaContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                        Log.e(LOG_TAG, "onClick with position == -1.");
                        return;
                    }
                    onItemClickListener.onItemClick(mCostAssignments.get(getAdapterPosition()));
                }
            });

            llCaContainer.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                        Log.e(LOG_TAG, "onLongClick with position == -1.");
                        return false;
                    }
                    onItemClickListener.onItemLongClick(mCostAssignments.get(getAdapterPosition()));
                    return true;
                }
            });
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public interface OnItemClickListener {

        void onItemClick(CostAssignment costAssignment);

        void onItemLongClick(CostAssignment costAssignment);
    }
}

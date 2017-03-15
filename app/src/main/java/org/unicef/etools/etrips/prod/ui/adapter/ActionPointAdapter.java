package org.unicef.etools.etrips.prod.ui.adapter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.trip.ActionPoint;
import org.unicef.etools.etrips.prod.util.DateUtil;
import org.unicef.etools.etrips.prod.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ActionPointAdapter extends RecyclerView.Adapter<ActionPointAdapter.ViewHolder> {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ActionPointAdapter.class.getSimpleName();
    private static final int CONTENT_VIEW_TYPE = 0;
    private static final int LOAD_MORE_VIEW_TYPE = 1;

    // ===========================================================
    // Fields
    // ===========================================================

    private boolean showLoadMore;
    private ArrayList<ActionPoint> mActionPointList;
    private OnItemClickListener mOnItemClickListener;

    // ===========================================================
    // Constructors
    // ===========================================================

    public ActionPointAdapter(ArrayList<ActionPoint> actionPointList, OnItemClickListener onItemClickListener) {
        mActionPointList = actionPointList;
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
                layout = R.layout.adapter_item_action_point;
                break;

            case LOAD_MORE_VIEW_TYPE:
                layout = R.layout.layout_pagination_preloader;
                break;
        }

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
        return new ViewHolder(viewType, view, mActionPointList, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (position >= mActionPointList.size()) {
            holder.bindLoadMoreData();
        } else {
            holder.bindContentData();
        }
    }

    @Override
    public int getItemCount() {
        return showLoadMore && mActionPointList.size() > 0 ? mActionPointList.size() + 1 : mActionPointList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position >= mActionPointList.size() ? LOAD_MORE_VIEW_TYPE : CONTENT_VIEW_TYPE;
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

    public void add(List<ActionPoint> actionPoints, boolean forceUpdate) {
        if (actionPoints == null || actionPoints.isEmpty()) {
            return;
        }

        if (forceUpdate) {
            mActionPointList.clear();
            mActionPointList.addAll(actionPoints);
            notifyDataSetChanged();
        } else {
            final int start = mActionPointList.size();
            final int count = actionPoints.size();

            mActionPointList.addAll(actionPoints);
            notifyItemRangeInserted(start, count);
        }
    }

    public void add(ActionPoint actionPoint) {
        if (actionPoint == null || !actionPoint.isValid()) {
            return;
        }

        final int start = mActionPointList.size();
        mActionPointList.add(actionPoint);
        notifyItemInserted(start);
    }

    public void clear() {
        final int start = 0;
        final int count = mActionPointList.size();

        if (count == 0) {
            return;
        }

        mActionPointList.clear();
        notifyItemRangeRemoved(start, count);
    }

    public void showLoadMore() {
        if (getItemCount() == mActionPointList.size()) {
            showLoadMore = true;
            notifyItemInserted(mActionPointList.size() + 1);
        }
    }

    public void removeLoadMore() {
        if (getItemCount() > mActionPointList.size()) {
            showLoadMore = false;
            notifyItemRemoved(mActionPointList.size() + 1);
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    static class ViewHolder extends RecyclerView.ViewHolder {

        Context context;
        TextView tvActionPointDescription;
        TextView tvActionPointAssignedBy;
        TextView tvActionPointDueDate;
        TextView tvActionPointStatus;
        ImageView ivActionPointStatusLabel;
        CardView cvItemContainer;
        ProgressBar pbLoadMore;
        OnItemClickListener onItemClickListener;
        ArrayList<ActionPoint> actionPointList;

        ViewHolder(int type, View itemView, ArrayList<ActionPoint> actionPointList, OnItemClickListener onItemClickListener) {
            super(itemView);
            this.actionPointList = actionPointList;
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
            tvActionPointDescription = (TextView) view.findViewById(R.id.tv_item_action_point_description);
            tvActionPointDueDate = (TextView) view.findViewById(R.id.tv_item_action_point_due_date);
            tvActionPointAssignedBy = (TextView) view.findViewById(R.id.tv_item_action_point_assigned_by);
            ivActionPointStatusLabel = (ImageView) view.findViewById(R.id.iv_item_action_point_status);
            tvActionPointStatus = (TextView) view.findViewById(R.id.tv_item_action_point_status);
            cvItemContainer = (CardView) view.findViewById(R.id.cv_item_action_point_container);
        }

        void findLoadMoreViews(View view) {
            pbLoadMore = (ProgressBar) view.findViewById(R.id.pb_paginate);
        }

        void bindContentData() {
            final ActionPoint actionPoint = actionPointList.get(getAdapterPosition());
            if (!actionPoint.isValid()) {
                return;
            }

            // set action point description
            tvActionPointDescription.setText(actionPoint.getDescription());

            // set action point assigned by
            final String name = !TextUtils.isEmpty(actionPoint.getAssignedByName())
                    ? actionPoint.getAssignedByName()
                    : context.getString(R.string.text_tv_assigned_by_unknown);
            tvActionPointAssignedBy.setText(
                    String.format(context.getString(R.string.text_assigned_by), name));

            // set action point due date
            final String dueDate = String.format(
                    context.getString(R.string.text_due_date),
                    DateUtil.convertISOtoCalendarDate(actionPoint.getDueDate(), DateUtil.DD_MMM_YYYY));
            tvActionPointDueDate.setText(dueDate);

            // set action point status (text and right colored label)
            if (actionPoint.getStatus() != null) {
                tvActionPointStatus.setText(StringUtils.textCapSentences(actionPoint.getStatus()));
            } else {
                tvActionPointStatus.setText(null);
            }
            switch (actionPoint.getStatus()) {

                case ActionPoint.Status.OPEN:
                    ivActionPointStatusLabel.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.color_fcf39a));
                    break;

                case ActionPoint.Status.ONGOING:
                    ivActionPointStatusLabel.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.color_dcd051));
                    break;

                case ActionPoint.Status.CANCELLED:
                    ivActionPointStatusLabel.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.color_848484));
                    break;

                case ActionPoint.Status.COMPLETED:
                    ivActionPointStatusLabel.setBackgroundColor(ContextCompat.getColor(context,
                            R.color.color_68ba00));
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
                    onItemClickListener.onItemClick(
                            actionPointList.get(getAdapterPosition()), getAdapterPosition());
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
                    onItemClickListener.onItemLongClick(actionPointList.get(getAdapterPosition()));
                    return true;
                }
            });
        }

        void bindLoadMoreData() {
        }
    }

    public interface OnItemClickListener {

        void onItemClick(ActionPoint actionPoint, int position);

        void onItemLongClick(ActionPoint actionPoint);
    }
}
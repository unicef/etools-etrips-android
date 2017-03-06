package org.unicef.etools.etrips.prod.ui.adapter;


import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.user.UserStatic;

import java.util.ArrayList;
import java.util.List;

public class UserStaticAdapter extends RecyclerView.Adapter<UserStaticAdapter.ViewHolder> {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = UserStaticAdapter.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private ArrayList<UserStatic> mUserStatics;
    private OnItemClickListener mOnItemClickListener;

    // ===========================================================
    // Constructors
    // ===========================================================

    public UserStaticAdapter(ArrayList<UserStatic> userStatics, OnItemClickListener onItemClickListener) {
        mUserStatics = userStatics;
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
                .inflate(R.layout.adapter_item_user_static, viewGroup, false);
        return new ViewHolder(view, mUserStatics, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData();
    }

    @Override
    public int getItemCount() {
        return mUserStatics.size();
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

    public void add(List<UserStatic> userStatics) {
        if (userStatics == null || userStatics.isEmpty()) {
            return;
        }

        mUserStatics.addAll(userStatics);
        notifyDataSetChanged();
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    static class ViewHolder extends RecyclerView.ViewHolder {

        OnItemClickListener onItemClickListener;
        ArrayList<UserStatic> userStatics;

        TextView tvUserFullName;

        ViewHolder(View itemView, final ArrayList<UserStatic> userStatics, final OnItemClickListener onItemClickListener) {
            super(itemView);

            this.userStatics = userStatics;
            this.onItemClickListener = onItemClickListener;
            findViews(itemView);
            setListeners(itemView);
        }

        void findViews(View view) {
            tvUserFullName = (TextView) view.findViewById(R.id.tv_item_user_full_name);
        }

        void setListeners(View view) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                        Log.e(LOG_TAG, "onClick with position == -1.");
                        return;
                    }
                    onItemClickListener.onItemClick(userStatics.get(getAdapterPosition()));
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                        Log.e(LOG_TAG, "onLongClick with position == -1.");
                        return false;
                    }
                    onItemClickListener.onItemLongClick(userStatics.get(getAdapterPosition()));
                    return true;
                }
            });
        }

        void bindData() {
            final UserStatic user = userStatics.get(getAdapterPosition());
            if (!user.isValid()) {
                return;
            }

            final String name = !TextUtils.isEmpty(user.getFullName())
                    ? user.getFullName() : user.getUsername();
            tvUserFullName.setText(name);
        }
    }

    public interface OnItemClickListener {

        void onItemClick(UserStatic userStatic);

        void onItemLongClick(UserStatic userStatic);
    }
}
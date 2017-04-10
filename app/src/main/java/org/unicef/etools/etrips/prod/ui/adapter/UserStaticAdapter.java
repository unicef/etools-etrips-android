package org.unicef.etools.etrips.prod.ui.adapter;


import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.user.UserStatic;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class UserStaticAdapter extends RealmRecyclerViewAdapter<UserStatic, UserStaticAdapter.ViewHolder> {

    private static final String LOG_TAG = UserStaticAdapter.class.getSimpleName();

    public UserStaticAdapter(@Nullable OrderedRealmCollection<UserStatic> data) {
        super(data, false);
        setHasStableIds(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_item_user_static, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindData(getItem(position));
    }

    @Override
    public long getItemId(int index) {
        //noinspection ConstantConditions
        return getItem(index).getId();
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserFullName;

        ViewHolder(View itemView) {
            super(itemView);
            findViews(itemView);
        }

        void findViews(View view) {
            tvUserFullName = (TextView) view.findViewById(R.id.tv_item_user_full_name);
        }

        void bindData(UserStatic userStatic) {
            final String name = !TextUtils.isEmpty(userStatic.getFullName())
                    ? userStatic.getFullName() : userStatic.getUsername();
            tvUserFullName.setText(name);
        }
    }
}

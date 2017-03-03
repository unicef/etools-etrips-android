package org.unicef.etools.etrips.ui.fragment.trip;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.unicef.etools.etrips.R;
import org.unicef.etools.etrips.db.entity.trip.Trip;
import org.unicef.etools.etrips.ui.activity.ActionPointActivity;
import org.unicef.etools.etrips.ui.adapter.ActionPointAdapter;
import org.unicef.etools.etrips.ui.fragment.BaseActionPointFragment;
import org.unicef.etools.etrips.util.AppUtil;
import org.unicef.etools.etrips.util.Constant;
import org.unicef.etools.etrips.util.widget.EmptyState;

import io.realm.Realm;

public class TripActionPointsFragment extends BaseActionPointFragment implements View.OnClickListener,
        ActionPointAdapter.OnItemClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = TripActionPointsFragment.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private long mTripId;
    private Trip mTrip;

    private Button mBtnAddActionPoint;

    // ===========================================================
    // Constructors
    // ===========================================================

    public static TripActionPointsFragment newInstance() {
        return new TripActionPointsFragment();
    }

    public static TripActionPointsFragment newInstance(long tripId) {
        Bundle args = new Bundle();
        args.putLong(Constant.Argument.ARGUMENT_TRIP_ID, tripId);
        TripActionPointsFragment fragment = new TripActionPointsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static TripActionPointsFragment newInstance(Bundle args) {
        TripActionPointsFragment fragment = new TripActionPointsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_action_points, container, false);
        findViews(view);
        setListeners();
        initListComponents();
        getData();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        retrieveTripFromDb();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Realm.getDefaultInstance().close();
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_action_point:
                final Intent intent = new Intent(getActivity(), ActionPointActivity.class);
                intent.putExtra(
                        Constant.Argument.ARGUMENT_ACTION_POINT_OPERATION, ActionPointActivity.ADD);
                intent.putExtra(Constant.Argument.ARGUMENT_TRIP_ID, mTripId);
                startActivityForResult(intent, REQUEST_CODE_ADD_ACTION_POINT);
                break;
        }
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    private void setListeners() {
        mBtnAddActionPoint.setOnClickListener(this);
    }

    private void findViews(View view) {
        mEmptyState = (EmptyState) view.findViewById(R.id.es_trip_actions_points);
        mRvActionPoints = (RecyclerView) view.findViewById(R.id.rv_trip_action_points_list);
        mBtnAddActionPoint = (Button) view.findViewById(R.id.btn_add_action_point);
    }

    public void getData() {
        if (getArguments() != null) {
            mTripId = getArguments().getLong(Constant.Argument.ARGUMENT_TRIP_ID);
        }
    }

    private void retrieveTripFromDb() {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mTrip = realm.where(Trip.class).equalTo("id", mTripId).findFirst();

                if (mTrip.isMyTrip()){
                    mBtnAddActionPoint.setVisibility(View.VISIBLE);
                } else {
                    mBtnAddActionPoint.setVisibility(View.GONE);
                }

                if (mTrip.getActionPoints().size() > 0) {
                    // TODO: it is temporary decision,
                    // before full name will be added on Server side.
                    // https://docs.google.com/document/d/1LrT3keXxudbgv725yVIdu6ZDQ6Hm5k71EeJisGKXWNw/edit - question 3.
                    AppUtil.addAssignedFullName(realm, mTrip.getActionPoints());
                    mActionPointAdapter.add(mTrip.getActionPoints(), true);
                } else {
                    mEmptyState.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}

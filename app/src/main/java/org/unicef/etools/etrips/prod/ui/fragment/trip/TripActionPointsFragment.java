package org.unicef.etools.etrips.prod.ui.fragment.trip;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.trip.Trip;
import org.unicef.etools.etrips.prod.ui.activity.ActionPointActivity;
import org.unicef.etools.etrips.prod.ui.adapter.ActionPointAdapter;
import org.unicef.etools.etrips.prod.ui.fragment.BaseActionPointFragment;
import org.unicef.etools.etrips.prod.util.Constant;
import org.unicef.etools.etrips.prod.util.NetworkUtil;
import org.unicef.etools.etrips.prod.util.Preference;
import org.unicef.etools.etrips.prod.util.manager.SnackBarManager;
import org.unicef.etools.etrips.prod.util.widget.EmptyState;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class TripActionPointsFragment extends BaseActionPointFragment implements View.OnClickListener,
        RealmChangeListener<Trip>, ActionPointAdapter.OnItemClickListener {

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

        releaseResources();
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_action_point:
                if (NetworkUtil.getInstance().isConnected(getActivity())) {
                    final Intent intent = new Intent(getActivity(), ActionPointActivity.class);
                    intent.putExtra(
                            Constant.Argument.ARGUMENT_ACTION_POINT_OPERATION, ActionPointActivity.ADD);
                    intent.putExtra(Constant.Argument.ARGUMENT_TRIP_ID, mTripId);
                    startActivity(intent);
                } else {
                    SnackBarManager.show(getActivity(), getString(R.string.msg_network_connection_error),
                            SnackBarManager.Duration.LONG);
                }
                break;
        }
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Override
    public void onChange(Trip element) {
        if (element.getActionPoints() == null || !element.getActionPoints().isValid()) {
            return;
        }
        if (element.getSupervisor() != Preference.getInstance(getActivity()).getUserId() || element.isMyTrip()) {
            mBtnAddActionPoint.setVisibility(View.VISIBLE);
        } else {
            mBtnAddActionPoint.setVisibility(View.GONE);
        }

        if (element.getActionPoints().size() == 0) {
            // There are no ActionPoints in our database, so clear our UI list and show empty state.
            mActionPointAdapter.clear();
            mEmptyState.setVisibility(View.VISIBLE);
        } else {
            mActionPointAdapter.add(element.getActionPoints().sort("dueDate"), true);
            if (mEmptyState.getVisibility() == View.VISIBLE) {
                mEmptyState.setVisibility(View.GONE);
            }
        }
    }

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
        mTrip = Realm.getDefaultInstance().where(Trip.class).equalTo("id", mTripId).findFirstAsync();
        mTrip.addChangeListener(this);
    }

    private void releaseResources() {
        if (mTrip != null) {
            mTrip.removeChangeListener(this);
        }
        Realm.getDefaultInstance().close();
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}

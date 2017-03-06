package org.unicef.etools.etrips.prod.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.trip.Activity;
import org.unicef.etools.etrips.prod.db.entity.trip.Trip;
import org.unicef.etools.etrips.prod.ui.adapter.TravelActivityAdapter;
import org.unicef.etools.etrips.prod.util.Constant;

import java.util.ArrayList;

import io.realm.Realm;

public class TravelActivity extends BaseActivity implements View.OnClickListener,
        TravelActivityAdapter.OnItemClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = TravelActivity.class.getSimpleName();
    public static final int DEFAULT_ID = 0;

    // ===========================================================
    // Fields
    // ===========================================================

    private long mTripId;
    private RecyclerView mRvTrevalActivity;
    private ArrayList<Activity> mActivities;
    private TravelActivityAdapter mTravelActivityAdapter;
    private Trip mTrip;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        findViews();
        customizeActionBar();
        findViews();
        init();
        getData();
        retrieveTripFromDb();
        populateList();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_travel;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Realm.getDefaultInstance().close();
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Override
    public void onItemClick(Activity activity) {

    }

    @Override
    public void onItemLongClick(Activity activity) {

    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void customizeActionBar() {
        setActionBarTitle(getString(R.string.screen_name_travel_activity));
        setActionBarIcon(R.drawable.ic_close_white);
    }

    private void init() {
        // init ll manager, list and adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRvTrevalActivity.setLayoutManager(linearLayoutManager);
        mActivities = new ArrayList<>();
        mTravelActivityAdapter = new TravelActivityAdapter(mActivities, this);
        mRvTrevalActivity.setAdapter(mTravelActivityAdapter);
    }

    private void findViews() {
        mRvTrevalActivity = (RecyclerView) findViewById(R.id.rv_activity_list);
    }

    private void getData() {
        mTripId = getIntent().getLongExtra(Constant.Extra.EXTRA_TRIP_ID, DEFAULT_ID);
    }

    private void retrieveTripFromDb() {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mTrip = realm.where(Trip.class).equalTo("id", mTripId).findFirst();
            }
        });
    }

    private void populateList() {
        if (mTrip != null && mTrip.isValid()) {
            mActivities.clear();
            mActivities.addAll(mTrip.getActivities());
            mTravelActivityAdapter.notifyDataSetChanged();
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
package org.unicef.etools.etrips.prod.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.trip.CostAssignment;
import org.unicef.etools.etrips.prod.db.entity.trip.Trip;
import org.unicef.etools.etrips.prod.ui.adapter.CostAssignmentAdapter;
import org.unicef.etools.etrips.prod.util.Constant;

import java.util.ArrayList;

import io.realm.Realm;

public class CostAssignmentActivity extends BaseActivity implements View.OnClickListener,
        CostAssignmentAdapter.OnItemClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = CostAssignmentActivity.class.getSimpleName();
    public static final int DEFAULT_ID = 0;

    // ===========================================================
    // Fields
    // ===========================================================

    private long mTripId;
    private RecyclerView mRvTrips;
    private ArrayList<CostAssignment> mCostAssignments;
    private CostAssignmentAdapter mCostAssignmentAdapter;
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
        return R.layout.activity_cost_assignment;
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
    public void onItemClick(CostAssignment costAssignment) {

    }

    @Override
    public void onItemLongClick(CostAssignment costAssignment) {

    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void customizeActionBar() {
        setActionBarTitle(getString(R.string.screen_name_cost_assignment));
        setActionBarIcon(R.drawable.ic_close_white);
    }

    private void init() {
        // init ll manager, list and adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRvTrips.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRvTrips.getContext(),
                linearLayoutManager.getOrientation());
        mRvTrips.addItemDecoration(dividerItemDecoration);
        mCostAssignments = new ArrayList<>();
        mCostAssignmentAdapter = new CostAssignmentAdapter(mCostAssignments, this);
        mRvTrips.setAdapter(mCostAssignmentAdapter);
    }

    private void findViews() {
        mRvTrips = (RecyclerView) findViewById(R.id.rv_ca_list);
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
            mCostAssignments.clear();
            mCostAssignments.addAll(mTrip.getCostAssignments());
            mCostAssignmentAdapter.notifyDataSetChanged();
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
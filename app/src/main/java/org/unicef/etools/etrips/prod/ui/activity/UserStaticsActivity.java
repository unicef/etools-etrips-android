package org.unicef.etools.etrips.prod.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import com.google.common.eventbus.Subscribe;

import org.unicef.etools.etrips.prod.BuildConfig;
import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.PersonResponsibleWrapper;
import org.unicef.etools.etrips.prod.db.entity.user.UserStatic;
import org.unicef.etools.etrips.prod.io.bus.BusProvider;
import org.unicef.etools.etrips.prod.io.bus.event.ApiEvent;
import org.unicef.etools.etrips.prod.io.bus.event.Event;
import org.unicef.etools.etrips.prod.io.bus.event.NetworkEvent;
import org.unicef.etools.etrips.prod.io.rest.retrofit.RetrofitUtil;
import org.unicef.etools.etrips.prod.ui.adapter.UserStaticAdapter;
import org.unicef.etools.etrips.prod.util.AppUtil;
import org.unicef.etools.etrips.prod.util.Constant;
import org.unicef.etools.etrips.prod.util.manager.SnackBarManager;
import org.unicef.etools.etrips.prod.util.receiver.NetworkStateReceiver;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;

public class UserStaticsActivity extends BaseActivity implements UserStaticAdapter.OnItemClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = UserStaticsActivity.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private UserStaticAdapter mAdapter;

    private RecyclerView mRvUsers;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Call mUserStaticRequest;

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
        setListeners();
        customizeActionBar();
        initListComponents();
        retrieveUserStaticsFromDb();

        BusProvider.register(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_user_statics;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        releaseResources();
    }

    // ===========================================================
    // Observer callback
    // ===========================================================

    @Subscribe
    public void onEventReceived(Event event) {
        if (event instanceof NetworkEvent) {
            if (event.getSubscriber().equals(getClass().getSimpleName())) {
                handleNetworkEvent((NetworkEvent) event);
            }
        } else if (event instanceof ApiEvent) {
            if (event.getSubscriber().equals(getClass().getSimpleName())) {
                handleApiEvents((ApiEvent) event);
            }
        }
    }

    // ===========================================================
    // Observer methods
    // ===========================================================

    private void handleNetworkEvent(NetworkEvent event) {
        switch (event.getEventType()) {
            case Event.EventType.Network.CONNECTED:
                loadUsersFromServer(true);
                break;
        }
    }

    private void handleApiEvents(ApiEvent event) {
        mSwipeRefreshLayout.setRefreshing(false);

        switch (event.getEventType()) {
            case Event.EventType.Api.USERS_LOADED:
                retrieveUserStaticsFromDb();
                break;

            case Event.EventType.Api.Error.NO_NETWORK:
                NetworkStateReceiver.registerBroadcast(this);
                SnackBarManager.show(this, getString(R.string.msg_network_connection_error),
                        SnackBarManager.Duration.LONG);
                break;

            case Event.EventType.Api.Error.PAGE_NOT_FOUND:
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_page_not_found));
                break;

            case Event.EventType.Api.Error.BAD_REQUEST:
                String body = (String) event.getEventData();
                if (body != null) {
                    SnackBarManager.show(this, body, SnackBarManager.Duration.LONG);
                    if (BuildConfig.isDEBUG)
                        Log.i(LOG_TAG, getString(R.string.msg_bad_request) + body);
                }
                break;

            case Event.EventType.Api.Error.UNAUTHORIZED:
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_not_authorized));
                AppUtil.logout(this);
                break;

            case Event.EventType.Api.Error.UNKNOWN:
                SnackBarManager.show(this, getString(R.string.msg_unknown_error),
                        SnackBarManager.Duration.LONG);
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_unknown_error));
                break;
        }
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

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
    public void onItemClick(UserStatic userStatic) {
        // Don't forget to handle case when action point is not valid as Realm object.
        final String name = !TextUtils.isEmpty(userStatic.getFullName())
                ? userStatic.getFullName() : userStatic.getUsername();
        final long id = userStatic.getId();

        final Intent data = new Intent();
        final PersonResponsibleWrapper wrapper = new PersonResponsibleWrapper(id, name);
        data.putExtra(Constant.Argument.ARGUMENT_PERSON_RESPONSIBLE_WRAPPER, wrapper);

        setResult(Activity.RESULT_OK, data);
        finish();
    }

    @Override
    public void onItemLongClick(UserStatic userStatic) {
        // Don't forget to handle case when action point is not valid as Realm object.
    }

    @Override
    public void onRefresh() {
        loadUsersFromServer(false);
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void findViews() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_users);
        mRvUsers = (RecyclerView) findViewById(R.id.rv_user_statics);
    }

    private void setListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void customizeActionBar() {
        setActionBarTitle(getString(R.string.screen_name_user_static_activity));
        setActionBarIcon(R.drawable.ic_close_white);
    }

    private void initListComponents() {
        // initListComponents ll manager, list and adapter
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRvUsers.setLayoutManager(linearLayoutManager);
        mRvUsers.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new UserStaticAdapter(new ArrayList<UserStatic>(), this);
        mRvUsers.setAdapter(mAdapter);
    }

    private void retrieveUserStaticsFromDb() {
        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<UserStatic> userStatics = realm.where(UserStatic.class)
                        .findAll();
                mAdapter.add(userStatics);
            }
        });
    }

    private void loadUsersFromServer(boolean showSwipeToRefresh) {
        if (mUserStaticRequest != null) {
            mUserStaticRequest.cancel();
        }
        if (showSwipeToRefresh) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        mUserStaticRequest = RetrofitUtil.getUsers(this, getClass().getSimpleName());
    }

    private void releaseResources() {
        if (mUserStaticRequest != null) {
            mUserStaticRequest.cancel();
        }
        BusProvider.unregister(this);
        Realm.getDefaultInstance().close();
        NetworkStateReceiver.unregisterBroadcast(this);

        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
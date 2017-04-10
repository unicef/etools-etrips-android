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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.common.eventbus.Subscribe;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.unicef.etools.etrips.prod.BuildConfig;
import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.PersonResponsibleWrapper;
import org.unicef.etools.etrips.prod.db.entity.user.UserStatic;
import org.unicef.etools.etrips.prod.io.bus.BusProvider;
import org.unicef.etools.etrips.prod.io.bus.event.ApiEvent;
import org.unicef.etools.etrips.prod.io.bus.event.ChangeDataEvent;
import org.unicef.etools.etrips.prod.io.bus.event.Event;
import org.unicef.etools.etrips.prod.io.bus.event.NetworkEvent;
import org.unicef.etools.etrips.prod.io.rest.retrofit.RetrofitUtil;
import org.unicef.etools.etrips.prod.ui.adapter.UserStaticAdapter;
import org.unicef.etools.etrips.prod.util.AppUtil;
import org.unicef.etools.etrips.prod.util.Constant;
import org.unicef.etools.etrips.prod.util.ItemClickSupport;
import org.unicef.etools.etrips.prod.util.manager.SnackBarManager;
import org.unicef.etools.etrips.prod.util.receiver.NetworkStateReceiver;
import org.unicef.etools.etrips.prod.util.widget.EmptyState;

import io.realm.Case;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;

public class UserStaticsActivity extends BaseActivity implements
        SwipeRefreshLayout.OnRefreshListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = UserStaticsActivity.class.getSimpleName();
    public static final String SEARCH_FIELD = "fullName";

    // ===========================================================
    // Fields
    // ===========================================================

    private UserStaticAdapter mAdapter;

    private RecyclerView mRvUsers;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private EmptyState mEmptyState;
    private MaterialSearchView mSvSearch;

    private String mQueryText;

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
        setupSearchView();
        retrieveUserStaticsFromDb(true);
        BusProvider.register(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_user_statics;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_static, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        mSvSearch.setMenuItem(item);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        releaseResources();
    }

    @Override
    public void onBackPressed() {
        if (mSvSearch.isSearchOpen()) {
            mSvSearch.closeSearch();
        } else {
            super.onBackPressed();
        }
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
                retrieveUserStaticsFromDb(false);
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
    public void onRefresh() {
        loadUsersFromServer(false);
    }

    // ===========================================================
    // Methods
    // ===========================================================

    private void findViews() {
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_users);
        mRvUsers = (RecyclerView) findViewById(R.id.rv_user_statics);
        mEmptyState = (EmptyState) findViewById(R.id.es_users);
        mSvSearch = (MaterialSearchView) findViewById(R.id.search_view);
    }

    private void setListeners() {
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSvSearch.setOnQueryTextListener(new SearchQueryListener());
        mSvSearch.setOnSearchViewListener(new SearchViewListener());
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
        mAdapter = new UserStaticAdapter(null);
        mRvUsers.setAdapter(mAdapter);
        ItemClickSupport.addTo(mRvUsers).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                UserStatic userStatic = ((UserStaticAdapter) recyclerView.getAdapter()).getItem(position);
                if (userStatic != null) {
                    final String name = !TextUtils.isEmpty(userStatic.getFullName())
                            ? userStatic.getFullName() : userStatic.getUsername();
                    final long id = userStatic.getId();

                    final Intent data = new Intent();
                    final PersonResponsibleWrapper wrapper = new PersonResponsibleWrapper(id, name);
                    data.putExtra(Constant.Argument.ARGUMENT_PERSON_RESPONSIBLE_WRAPPER, wrapper);

                    setResult(Activity.RESULT_OK, data);
                    finish();
                }
            }
        });
    }

    private void setupSearchView() {
        mSvSearch.setVoiceSearch(true);
    }

    private void retrieveUserStaticsFromDb(boolean loadFromServerIfNeeded) {
        OrderedRealmCollection<UserStatic> userStatics = Realm.getDefaultInstance().where(UserStatic.class)
                .contains(SEARCH_FIELD, mQueryText, Case.INSENSITIVE)
                .findAll();

        if (userStatics == null || !userStatics.isValid() || userStatics.size() == 0) {
            mEmptyState.setVisibility(View.VISIBLE);
            if (loadFromServerIfNeeded) {
                loadUsersFromServer(true);
            }
            return;
        }

        mEmptyState.setVisibility(View.GONE);
        mAdapter.updateData(userStatics);
    }

    private void loadUsersFromServer(boolean showSwipeToRefresh) {
        if (mUserStaticRequest != null) {
            mUserStaticRequest.cancel();
        }
        if (showSwipeToRefresh) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        BusProvider.getInstance().post(new ChangeDataEvent<>(Event.EventType.ChangeData.STOP_LOADING_USERS));
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
        mRvUsers.setAdapter(null);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    private class SearchQueryListener implements MaterialSearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            AppUtil.closeKeyboard(UserStaticsActivity.this);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            RealmResults<UserStatic> searchResults = Realm.getDefaultInstance().where(UserStatic.class)
                    .contains(SEARCH_FIELD, newText.trim(), Case.INSENSITIVE).findAll();

            if (searchResults == null || !searchResults.isValid()) {
                return false;
            }

            if (!newText.trim().isEmpty()) {
                if (searchResults.size() > 0) {
                    ((UserStaticAdapter) mRvUsers.getAdapter()).updateData(searchResults);
                } else {
                    ((UserStaticAdapter) mRvUsers.getAdapter()).updateData(null);
                }
            } else {
                ((UserStaticAdapter) mRvUsers.getAdapter()).updateData(searchResults);
            }
            mEmptyState.setVisibility(searchResults.size() > 0 ? View.GONE : View.VISIBLE);
            mQueryText = newText;
            return true;
        }
    }

    private class SearchViewListener implements MaterialSearchView.SearchViewListener {

        @Override
        public void onSearchViewShown() {
            if (!mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setEnabled(false);
            }
        }

        @Override
        public void onSearchViewClosed() {
            mSwipeRefreshLayout.setEnabled(true);
        }
    }
}
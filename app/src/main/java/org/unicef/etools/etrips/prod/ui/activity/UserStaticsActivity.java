package org.unicef.etools.etrips.prod.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;

import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.PersonResponsibleWrapper;
import org.unicef.etools.etrips.prod.db.entity.user.UserStatic;
import org.unicef.etools.etrips.prod.ui.adapter.UserStaticAdapter;
import org.unicef.etools.etrips.prod.util.Constant;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

public class UserStaticsActivity extends BaseActivity implements UserStaticAdapter.OnItemClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = UserStaticsActivity.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private RecyclerView mRvUsers;
    private UserStaticAdapter mAdapter;

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
        initListComponents();
        retrieveUserStaticsFromDb();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_user_statics;
    }

    private void findViews() {
        mRvUsers = (RecyclerView) findViewById(R.id.rv_user_statics);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Realm.getDefaultInstance().close();
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

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}
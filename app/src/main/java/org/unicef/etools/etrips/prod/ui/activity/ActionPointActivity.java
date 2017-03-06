package org.unicef.etools.etrips.prod.ui.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;

import org.unicef.etools.etrips.prod.BuildConfig;
import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.DateWrapper;
import org.unicef.etools.etrips.prod.db.entity.PersonResponsibleWrapper;
import org.unicef.etools.etrips.prod.db.entity.static_data.data.ActionPointStatus;
import org.unicef.etools.etrips.prod.db.entity.trip.ActionPoint;
import org.unicef.etools.etrips.prod.db.entity.trip.Trip;
import org.unicef.etools.etrips.prod.io.bus.BusProvider;
import org.unicef.etools.etrips.prod.io.bus.event.ApiEvent;
import org.unicef.etools.etrips.prod.io.bus.event.Event;
import org.unicef.etools.etrips.prod.io.rest.retrofit.RetrofitUtil;
import org.unicef.etools.etrips.prod.util.AppUtil;
import org.unicef.etools.etrips.prod.util.Constant;
import org.unicef.etools.etrips.prod.util.DateUtil;
import org.unicef.etools.etrips.prod.util.Preference;
import org.unicef.etools.etrips.prod.util.manager.DialogManager;
import org.unicef.etools.etrips.prod.util.manager.EmptyDialogClickListener;
import org.unicef.etools.etrips.prod.util.manager.SnackBarManager;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;

public class ActionPointActivity extends BaseActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = ActionPointActivity.class.getSimpleName();

    @IntDef({UNDEFINED, EDIT, ADD})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Operation {
    }

    public static final int UNDEFINED = 0;
    public static final int EDIT = 1;
    public static final int ADD = 2;

    private static final int REQUEST_CODE_GET_PERSON_RESPONSIBLE = 1000;

    // ===========================================================
    // Fields
    // ===========================================================

    private boolean shouldReturnToRealm = true;

    @IdRes
    private int mClickedViewId;
    @ColorInt
    private int mNormalTextColor;

    @Operation
    private int mOperation;
    private String mPersonResponsibleFullName;
    private ActionPoint mActionPoint;
    private Trip mTrip;
    private RealmResults<ActionPointStatus> mActionPointStatuses;

    private DatePickerDialog mDpdRef;

    private TextView mTvPersonResponsible;
    private TextView mTvDueDate;
    private TextView mTvStatus;
    private TextView mTvCompletedDate;
    private SwitchCompat mSwcFollowUp;
    private EditText mEdtDescription;
    private EditText mEdtActionsTaken;
    private Button mBtnExecuteOperation;

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
        getData();
        applyOperationRules(mOperation);

        BusProvider.register(this);

        // This text color is the same for all views.
        mNormalTextColor = mTvPersonResponsible.getCurrentTextColor();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_action_point;
    }

    private void findViews() {
        mTvPersonResponsible = (TextView) findViewById(R.id.tv_action_point_person_responsible);
        mTvDueDate = (TextView) findViewById(R.id.tv_action_point_due_date);
        mTvStatus = (TextView) findViewById(R.id.tv_action_point_status);
        mTvCompletedDate = (TextView) findViewById(R.id.tv_action_point_completed_date);
        mSwcFollowUp = (SwitchCompat) findViewById(R.id.swc_action_point_follow_up);
        mEdtDescription = (EditText) findViewById(R.id.edt_action_point_description);
        mEdtActionsTaken = (EditText) findViewById(R.id.edt_action_point_actions_taken);
        mBtnExecuteOperation = (Button) findViewById(R.id.btn_action_point_execute_operation);
    }

    private void setListeners() {
        mBtnExecuteOperation.setOnClickListener(this);
        mTvDueDate.setOnClickListener(this);
        mTvCompletedDate.setOnClickListener(this);
        mTvStatus.setOnClickListener(this);
        mTvPersonResponsible.setOnClickListener(this);
    }

    private void customizeActionBar() {
        setActionBarIcon(R.drawable.ic_close_white);
    }

    private void getData() {
        final Intent intent = getIntent();

        final @Operation int op = intent.getIntExtra(
                Constant.Argument.ARGUMENT_ACTION_POINT_OPERATION, UNDEFINED);
        if (op == UNDEFINED) {
            throw new IllegalArgumentException("Provide ADD or Edit operation.");
        }
        mOperation = op;

        final long id;
        if (op == EDIT) {
            id = intent.getLongExtra(Constant.Argument.ARGUMENT_ACTION_POINT_ID, -1);
        } else {
            id = intent.getLongExtra(Constant.Argument.ARGUMENT_TRIP_ID, -1);
        }

        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if (op == EDIT) {
                    final ActionPoint actionPoint = realm.where(ActionPoint.class)
                            .equalTo("id", id)
                            .findFirst();
                    mActionPoint = realm.copyFromRealm(actionPoint);

                    mPersonResponsibleFullName = mActionPoint.getPersonResponsibleName();
                } else {
                    final Trip trip = realm.where(Trip.class)
                            .equalTo("id", id)
                            .findFirst();
                    mTrip = realm.copyFromRealm(trip);

                    mActionPoint = new ActionPoint();
                    mActionPoint.setTripId(mTrip.getId());
                    mActionPoint.setPersonResponsible(ActionPoint.INVALID_ID);

                    mActionPointStatuses = realm.where(ActionPointStatus.class)
                            .findAll();
                }
            }
        });
    }

    private void applyOperationRules(@Operation int op) {
        final String screenName;
        final String btnText;

        if (op == EDIT) {
            screenName = getString(R.string.screen_name_my_action_point);
            btnText = getString(R.string.text_btn_action_point_save);

            if (mActionPoint.getAssignedBy() == Preference.getInstance(this).getUserId()) {
                mTvPersonResponsible.setEnabled(false);
                mEdtDescription.setEnabled(false);
                mTvDueDate.setEnabled(false);
                mTvStatus.setEnabled(false);

                removeEditRightDrawable(mTvPersonResponsible);
                removeEditRightDrawable(mTvDueDate);
            }
            applyActionPointData(mActionPoint);
        } else {
            mSwcFollowUp.setChecked(true);

            screenName = getString(R.string.screen_name_add_action_point);
            btnText = getString(R.string.text_btn_action_point_add);
        }
        setActionBarTitle(screenName);
        mBtnExecuteOperation.setText(btnText);
    }

    private void removeEditRightDrawable(TextView view) {
        final Drawable[] drawables = view.getCompoundDrawables();
        view.setCompoundDrawables(drawables[0], drawables[1], null, drawables[3]);
    }

    private void applyActionPointData(@NonNull ActionPoint actionPoint) {
        mTvPersonResponsible.setText(mPersonResponsibleFullName);
        mEdtDescription.setText(actionPoint.getDescription());
        mTvStatus.setText(actionPoint.getStatus());
        mEdtActionsTaken.setText(actionPoint.getActionsTaken());
        mSwcFollowUp.setChecked(actionPoint.isfollowUp());
        mTvDueDate.setText(
                DateUtil.convertISOtoCalendarDate(actionPoint.getDueDate(), DateUtil.DD_MMM_YYYY));

        final String completedAt = DateUtil.convertISOtoCalendarDate(
                actionPoint.getCompletedAt(), DateUtil.DD_MMM_YYYY);
        if (!TextUtils.isEmpty(completedAt)) {
            mTvCompletedDate.setText(completedAt);
        } else {
            mTvCompletedDate.setText(getString(R.string.hint_tv_action_point_date));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (shouldReturnToRealm) {
            Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    if (mOperation == EDIT) {
                        realm.copyToRealmOrUpdate(mActionPoint);
                    } else {
                        realm.copyToRealmOrUpdate(mTrip);
                    }
                }
            });
            shouldReturnToRealm = false;
        }

        BusProvider.unregister(this);
        Realm.getDefaultInstance().close();
        DialogManager.getInstance().dismissAlertDialog(getClass());
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(Activity.RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GET_PERSON_RESPONSIBLE) {
            if (resultCode == Activity.RESULT_OK) {
                final PersonResponsibleWrapper wrapper = data.getParcelableExtra(
                        Constant.Argument.ARGUMENT_PERSON_RESPONSIBLE_WRAPPER);

                mActionPoint.setPersonResponsible(wrapper.getId());
                mTvPersonResponsible.setText(wrapper.getName());
                removeErrorColorIfNeeded(mTvPersonResponsible);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_action_point_execute_operation:
                executeOperation(mOperation);
                break;
            case R.id.tv_action_point_due_date:
            case R.id.tv_action_point_completed_date:
                final String date = obtainInitDate(v.getId());
                final Calendar calendar = DateUtil.composeCalendarFromDate(date);

                showDatePickerDialog(v.getId(), DateWrapper.from(calendar));
                break;
            case R.id.tv_action_point_status:
                DialogManager.getInstance().showSingleChoiceAlertDialog(
                        this,
                        getString(R.string.dialog_text_title_status),
                        ActionPointStatus.obtainNames(mActionPointStatuses),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ListView lv = ((AlertDialog) dialog).getListView();
                                final String status = (String)
                                        lv.getAdapter().getItem(lv.getCheckedItemPosition());

                                mActionPoint.setStatus(status);
                                mTvStatus.setText(status);
                                removeErrorColorIfNeeded(mTvStatus);
                            }
                        }, new EmptyDialogClickListener(),
                        0,
                        DialogManager.DialogIdentifier.STATUS_DIALOG,
                        false
                );
                break;
            case R.id.tv_action_point_person_responsible:
                Intent intent = new Intent(this, UserStaticsActivity.class);
                startActivityForResult(intent, REQUEST_CODE_GET_PERSON_RESPONSIBLE);
                break;
        }
    }

    private void executeOperation(@Operation int op) {
        boolean isError = false;
        if (op == ADD) {
            @ColorInt final int red = ContextCompat.getColor(this, R.color.color_ff4f4f);

            if (mActionPoint.getPersonResponsible() == ActionPoint.INVALID_ID) {
                mTvPersonResponsible.setTextColor(red);
                isError = true;
            }
            if (TextUtils.isEmpty(mEdtDescription.getText())) {
                mEdtDescription.setError("Please, enter description.");
                isError = true;
            } else {
                mActionPoint.setDescription(mEdtDescription.getText().toString());
            }
            if (mActionPoint.getStatus() == null) {
                mTvStatus.setTextColor(red);
                isError = true;
            }

            final String emptyDate = getString(R.string.hint_tv_action_point_date);
            final String dueDate = mTvDueDate.getText().toString();

            if (dueDate.equals(emptyDate)) {
                mTvDueDate.setTextColor(red);
                isError = true;
            } else {
                if (DateUtil.isDateOutdated(DateUtil.composeCalendarFromDate(dueDate))) {
                    mTvDueDate.setTextColor(red);
                    isError = true;
                }
            }
        }

        if (isError) {
            return;
        }

        mActionPoint.setIsfollowUp(mSwcFollowUp.isChecked());
        mActionPoint.setActionsTaken(mEdtActionsTaken.getText().toString());

        AppUtil.closeKeyboard(this);
        DialogManager.getInstance().showPreloader(this, getClass().getSimpleName());

        if (op == ADD) {
            mTrip.getActionPoints().add(mActionPoint);
            RetrofitUtil.addActionPointToTrip(this, mTrip, getClass().getSimpleName());
        } else {
            RetrofitUtil.updateActionPoint(this, mActionPoint, getClass().getSimpleName());
        }
    }

    @Nullable
    private String obtainInitDate(@IdRes int labelId) {
        final String emptyDate = getString(R.string.hint_tv_action_point_date);
        final String date;

        if (labelId == R.id.tv_action_point_due_date) {
            date = mTvDueDate.getText().toString();
        } else {
            date = mTvCompletedDate.getText().toString();
        }

        return !date.equals(emptyDate) ? date : null;
    }

    private void showDatePickerDialog(@IdRes int viewId, @NonNull DateWrapper date) {
        mClickedViewId = viewId;

        mDpdRef = new DatePickerDialog(this, R.style.DatePicker, this,
                date.getYear(), date.getMonth(), date.getDayOfMonth());
        mDpdRef.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        final String date = DateUtil.convertDatePickerResultToString(
                year, month, dayOfMonth, DateUtil.DD_MMM_YYYY);
        final String serverDate = DateUtil.convertDatePickerResultToString(
                year, month, dayOfMonth, DateUtil.SERVER_FORMAT);

        if (mClickedViewId == R.id.tv_action_point_due_date) {
            mActionPoint.setDueDate(serverDate);
            mTvDueDate.setText(date);
            removeErrorColorIfNeeded(mTvDueDate);
        } else {
            mActionPoint.setCompletedAt(serverDate);
            mTvCompletedDate.setText(date);
        }
    }

    private void removeErrorColorIfNeeded(TextView view) {
        @ColorInt int current = view.getCurrentTextColor();
        if (current != mNormalTextColor) {
            view.setTextColor(mNormalTextColor);
        }
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    @Subscribe
    public void onEventReceived(Event event) {
        if (event instanceof ApiEvent) {
            if (event.getSubscriber().equals(getClass().getSimpleName())) {
                handleApiEvents((ApiEvent) event);
            }
        }
    }

    private void handleApiEvents(ApiEvent event) {
        DialogManager.getInstance().dismissPreloader(getClass());

        if (event.getEventType() == Event.EventType.Api.ACTION_POINT_UPDATED
                || event.getEventType() == Event.EventType.Api.ACTION_POINT_ADDED) {
            shouldReturnToRealm = false;

            final Intent data = new Intent();
            if (mOperation == EDIT) {
                data.putExtra(Constant.Argument.ARGUMENT_ACTION_POINT_ID, mActionPoint.getId());
            } else {
                // Reload trip from db to get the last action point.
                Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        mTrip = realm.where(Trip.class)
                                .equalTo("id", mTrip.getId())
                                .findFirst();

                        // Try to find recently added action point id in trip.
                        long id;
                        final int size = mTrip.getActionPoints().size();
                        if (size > 0) {
                            id = mTrip.getActionPoints().get(size - 1).getId();
                        } else {
                            id = -1;
                        }
                        data.putExtra(Constant.Argument.ARGUMENT_ACTION_POINT_ID, id);
                    }
                });
            }
            setResult(Activity.RESULT_OK, data);
            finish();
        } else {
            // When op == ADD and error happens, in trip's action point list will be broken object,
            // so remove it from the list.
            if (mOperation == ADD) {
                final int size = mTrip.getActionPoints().size();
                mTrip.getActionPoints().remove(size - 1);
            }

            switch (event.getEventType()) {
                case Event.EventType.Api.Error.NO_NETWORK:
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
    }

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}

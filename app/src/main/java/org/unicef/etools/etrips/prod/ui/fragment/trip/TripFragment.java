package org.unicef.etools.etrips.prod.ui.fragment.trip;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;

import org.unicef.etools.etrips.prod.BuildConfig;
import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.static_data.data_2.Currency;
import org.unicef.etools.etrips.prod.db.entity.trip.Trip;
import org.unicef.etools.etrips.prod.db.entity.user.UserStatic;
import org.unicef.etools.etrips.prod.io.bus.BusProvider;
import org.unicef.etools.etrips.prod.io.bus.event.ApiEvent;
import org.unicef.etools.etrips.prod.io.bus.event.Event;
import org.unicef.etools.etrips.prod.io.rest.retrofit.RetrofitUtil;
import org.unicef.etools.etrips.prod.ui.activity.CostAssignmentActivity;
import org.unicef.etools.etrips.prod.ui.activity.TravelActivity;
import org.unicef.etools.etrips.prod.ui.activity.TravelItineraryActivity;
import org.unicef.etools.etrips.prod.ui.fragment.BaseFragment;
import org.unicef.etools.etrips.prod.ui.fragment.SupervisedFragment;
import org.unicef.etools.etrips.prod.ui.fragment.TripsFragment;
import org.unicef.etools.etrips.prod.util.AppUtil;
import org.unicef.etools.etrips.prod.util.Constant;
import org.unicef.etools.etrips.prod.util.DateUtil;
import org.unicef.etools.etrips.prod.util.Preference;
import org.unicef.etools.etrips.prod.util.StringUtils;
import org.unicef.etools.etrips.prod.util.manager.DialogManager;
import org.unicef.etools.etrips.prod.util.manager.SnackBarManager;

import io.realm.Realm;

import static org.unicef.etools.etrips.prod.util.DateUtil.DD_MMM_YYYY;

public class TripFragment extends BaseFragment implements View.OnClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = TripFragment.class.getSimpleName();
    public static final String DEFAULT_CURRENCY = "USD";

    // ===========================================================
    // Fields
    // ===========================================================

    private long mTripId;
    private Bundle mArgumentData;
    private TextView mTvTripTraveler;
    private TextView mTvTripGroup;
    private TextView mTvTripSuperviser;
    private TextView mTvTripDate;
    private TextView mTvTripPurpose;
    private TextView mTvTripStatus;
    private Button mBtnTripSubmit;
    private Button mBtnTripReject;
    private LinearLayout mLlTripTravelActivity;
    private TextView mTvTripTravelActivityAmount;
    private LinearLayout mLlTripItinerary;
    private TextView mTvTripItineraryAmount;
    private LinearLayout mLlTripCostAssignment;
    private TextView mTvTripCostAssignmentAmount;
    private TextView mTvTripDsaTotal;
    private TextView mTvTripExpenseTotal;
    private TextView mTvTripDeducationsTotal;
    private TextView mTvTripTotalCost;
    private Trip mTrip;
    private Currency mCurrency;
    private UserStatic mTraveler;
    private UserStatic mSupervisor;
    private EditText mEdtRn;
    private LinearLayout mLlRn;


    // ===========================================================
    // Constructors
    // ===========================================================

    public static TripFragment newInstance() {
        return new TripFragment();
    }

    public static TripFragment newInstance(long tripId) {
        Bundle args = new Bundle();
        args.putLong(Constant.Argument.ARGUMENT_TRIP_ID, tripId);
        TripFragment fragment = new TripFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static TripFragment newInstance(Bundle args) {
        TripFragment fragment = new TripFragment();
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
        View view = inflater.inflate(R.layout.fragment_trip, container, false);
        BusProvider.register(this);
        findViews(view);
        setListeners();
        getData();
        retrieveTripFromDb();
        setData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.unregister(this);
        DialogManager.getInstance().dismissPreloader(getClass());
        Realm.getDefaultInstance().close();
    }

    // ===========================================================
    // Click Listeners
    // ===========================================================

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_trip_activity:
                if (mTrip != null && mTrip.isValid() && mTrip.getActivities() != null
                        && mTrip.getActivities().size() > 0) {
                    Intent intent = new Intent(getActivity(), TravelActivity.class);
                    intent.putExtra(Constant.Extra.EXTRA_TRIP_ID, mTrip.getId());
                    startActivity(intent);
                }
                break;

            case R.id.ll_trip_itinerary:
                if (mTrip != null && mTrip.isValid() && mTrip.getItineraries() != null
                        && mTrip.getItineraries().size() > 0) {
                    Intent intent = new Intent(getActivity(), TravelItineraryActivity.class);
                    intent.putExtra(Constant.Extra.EXTRA_TRIP_ID, mTrip.getId());
                    startActivity(intent);
                }
                break;

            case R.id.ll_trip_cost_assignment:
                if (mTrip != null && mTrip.isValid() && mTrip.getCostAssignments() != null
                        && mTrip.getCostAssignments().size() > 0) {
                    Intent intent = new Intent(getActivity(), CostAssignmentActivity.class);
                    intent.putExtra(Constant.Extra.EXTRA_TRIP_ID, mTrip.getId());
                    startActivity(intent);
                }
                break;

            case R.id.btn_trip_submit:
                if (mTrip != null && mTrip.isValid() && mTrip.getStatus() != null) {
                    switch (mTrip.getStatus()) {
                        case Trip.Status.PLANNED:
                            changeTripStatusRequest(Trip.StatusSend.SUBMIT_FOR_APPROVAL);
                            break;

                        case Trip.Status.SUBMITTED:
                            changeTripStatusRequest(Trip.StatusSend.APPROVE);
                            break;

                        case Trip.Status.CERTIFICATION_SUBMITTED:
                            changeTripStatusRequest(Trip.StatusSend.APPROVE_CERTIFICATE);
                            break;
                    }
                }
                break;

            case R.id.btn_trip_reject:
                if (mTrip != null && mTrip.isValid() && mTrip.getStatus() != null) {
                    switch (mTrip.getStatus()) {
                        case Trip.Status.SUBMITTED:
                            mTrip.setRejectionNote(mEdtRn.getText().toString().trim());
                            changeTripStatusRequest(Trip.StatusSend.REJECT);
                            break;

                        case Trip.Status.CERTIFICATION_SUBMITTED:
                            mTrip.setRejectionNote(mEdtRn.getText().toString().trim());
                            changeTripStatusRequest(Trip.StatusSend.REJECT_CERTIFICATE);
                            break;
                    }
                }
                break;
        }
    }

    // ===========================================================
    // Observer callback
    // ===========================================================

    @Subscribe
    public void onEventReceived(Event event) {
        if (event instanceof ApiEvent) {
            if (event.getSubscriber().equals(getClass().getSimpleName())) {
                handleApiEvents((ApiEvent) event);
            }
        }
    }

    // ===========================================================
    // Observer methods
    // ===========================================================

    private void handleApiEvents(ApiEvent event) {
        DialogManager.getInstance().dismissPreloader(getClass());

        switch (event.getEventType()) {
            case Event.EventType.Api.TRIP_STATUS_CHANGED:
                DialogManager.getInstance().dismissPreloader(getClass());
                AppUtil.closeKeyboard(getActivity());
                retrieveTripFromDb();
                setData();
                // notify trip list screens about changes
                BusProvider.getInstance().post(new ApiEvent(Event.EventType.Api.TRIPS_LOADED,
                        TripsFragment.class.getSimpleName()));
                BusProvider.getInstance().post(new ApiEvent(Event.EventType.Api.TRIPS_LOADED,
                        SupervisedFragment.class.getSimpleName()));
                break;

            case Event.EventType.Api.Error.NO_NETWORK:
                SnackBarManager.show(getActivity(), getString(R.string.msg_network_connection_error),
                        SnackBarManager.Duration.LONG);
                break;

            case Event.EventType.Api.Error.PAGE_NOT_FOUND:
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_page_not_found));
                break;

            case Event.EventType.Api.Error.BAD_REQUEST:
                String body = (String) event.getEventData();
                if (body != null) {
                    SnackBarManager.show(getActivity(), body, SnackBarManager.Duration.LONG);
                    if (BuildConfig.isDEBUG)
                        Log.i(LOG_TAG, getString(R.string.msg_bad_request) + body);
                }
                break;

            case Event.EventType.Api.Error.UNAUTHORIZED:
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_not_authorized));
                break;

            case Event.EventType.Api.Error.UNKNOWN:
                SnackBarManager.show(getActivity(), getString(R.string.msg_unknown_error),
                        SnackBarManager.Duration.LONG);
                if (BuildConfig.isDEBUG) Log.i(LOG_TAG, getString(R.string.msg_unknown_error));
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
        mLlTripTravelActivity.setOnClickListener(this);
        mLlTripItinerary.setOnClickListener(this);
        mLlTripCostAssignment.setOnClickListener(this);
        mBtnTripSubmit.setOnClickListener(this);
        mBtnTripReject.setOnClickListener(this);
    }

    private void findViews(View view) {
        mTvTripTraveler = (TextView) view.findViewById(R.id.tv_trip_traveler);
        mTvTripGroup = (TextView) view.findViewById(R.id.tv_trip_group);
        mTvTripSuperviser = (TextView) view.findViewById(R.id.tv_trip_superviser);
        mTvTripDate = (TextView) view.findViewById(R.id.tv_trip_date);
        mTvTripPurpose = (TextView) view.findViewById(R.id.tv_trip_purpose);
        mTvTripStatus = (TextView) view.findViewById(R.id.tv_trip_status);
        mEdtRn = (EditText) view.findViewById(R.id.edt_rn);
        mLlRn = (LinearLayout) view.findViewById(R.id.ll_rn);
        mBtnTripSubmit = (Button) view.findViewById(R.id.btn_trip_submit);
        mBtnTripReject = (Button) view.findViewById(R.id.btn_trip_reject);
        mLlTripTravelActivity = (LinearLayout) view.findViewById(R.id.ll_trip_activity);
        mTvTripTravelActivityAmount = (TextView) view.findViewById(R.id.tv_trip_travel_activity_amount);
        mLlTripItinerary = (LinearLayout) view.findViewById(R.id.ll_trip_itinerary);
        mTvTripItineraryAmount = (TextView) view.findViewById(R.id.tv_trip_itinerary_amount);
        mLlTripCostAssignment = (LinearLayout) view.findViewById(R.id.ll_trip_cost_assignment);
        mTvTripCostAssignmentAmount = (TextView) view.findViewById(R.id.tv_trip_cost_assignment_amount);
        mTvTripDsaTotal = (TextView) view.findViewById(R.id.tv_trip_dsa_total);
        mTvTripExpenseTotal = (TextView) view.findViewById(R.id.tv_trip_expense_total);
        mTvTripDeducationsTotal = (TextView) view.findViewById(R.id.tv_trip_deducations_total);
        mTvTripTotalCost = (TextView) view.findViewById(R.id.tv_trip_total_cost);
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
                if (mTrip != null && mTrip.isValid()) {
                    mTraveler = realm.where(UserStatic.class).equalTo("id", mTrip.getTraveler()).findFirst();
                    mSupervisor = realm.where(UserStatic.class).equalTo("id", mTrip.getSupervisor()).findFirst();
                    mCurrency = realm.where(Currency.class).equalTo("id", mTrip.getCurrency()).findFirst();
                    mTrip = realm.copyFromRealm(mTrip);
                }
            }
        });
    }

    private void setData() {
        if (mTrip != null && !mTrip.isValid()) {
            return;
        }

        // traveler name
        if (mTraveler != null && mTraveler.isValid()) {
            mTvTripTraveler.setText(mTraveler.getFullName());
        }

        // traveler group
        mTvTripGroup.setText("");

        checkStatuses();

        // traveler supervisor
        if (mSupervisor != null && mSupervisor.isValid()) {
            mTvTripSuperviser.setText(mSupervisor.getFullName());
        }

        // trip period
        mTvTripDate.setText(DateUtil.convertISOtoCalendarDate(mTrip.getStartDate(), DD_MMM_YYYY)
                + Constant.Symbol.SPACE_DASH
                + DateUtil.convertISOtoCalendarDate(mTrip.getEndDate(), DD_MMM_YYYY));

        // trip purpose
        mTvTripPurpose.setText(mTrip.getPurpose());

        String status = mTrip.getStatus() != null ?
                StringUtils.textCapSentences(mTrip.getStatus()).replaceAll(Constant.Symbol.UNDERLINE, Constant.Symbol.SPACE) : null;

        // trip status
        mTvTripStatus.setText(status);

        // ta amount
        if (mTrip.getActivities() != null && mTrip.getActivities().size() > 0) {
            mTvTripTravelActivityAmount.setText(String.format(
                    getString(R.string.text_activity_amount),
                    String.valueOf(mTrip.getActivities().size()))
            );
        } else {
            mTvTripItineraryAmount.setText(null);
        }

        // itinerary amount
        if (mTrip.getItineraries() != null && mTrip.getItineraries().size() > 0) {
            mTvTripItineraryAmount.setText(String.format(
                    getString(R.string.text_itinerary_amount),
                    String.valueOf(mTrip.getItineraries().size()))
            );
        } else {
            mTvTripItineraryAmount.setText(null);
        }

        // ca amount
        if (mTrip.getCostAssignments() != null && mTrip.getCostAssignments().size() > 0) {
            mTvTripCostAssignmentAmount.setText(String.format(
                    getString(R.string.text_cost_assignmetn_amount),
                    String.valueOf(mTrip.getCostAssignments().size()))
            );
        } else {
            mTvTripCostAssignmentAmount.setText(null);
        }

        setCostSummaryData();
    }

    private void checkStatuses() {
        // check statuses: set needed buttons and colors
        if (mTrip.getStatus() != null) {
            switch (mTrip.getStatus()) {
                case Trip.Status.PLANNED:
                    if (Preference.getInstance(getActivity()).getUserId() == mTrip.getTraveler()) {
                        mBtnTripSubmit.setText(R.string.text_btn_trip_submit);
                        mBtnTripSubmit.setVisibility(View.VISIBLE);
                        mBtnTripReject.setVisibility(View.GONE);
                    } else {
                        mBtnTripReject.setVisibility(View.GONE);
                        mBtnTripSubmit.setVisibility(View.GONE);
                    }
                    break;

                case Trip.Status.SUBMITTED:
                case Trip.Status.CERTIFICATION_SUBMITTED:
                    if (Preference.getInstance(getActivity()).getUserId() == mTrip.getSupervisor()) {
                        mBtnTripSubmit.setText(R.string.text_btn_trip_approve);
                        mBtnTripReject.setText(R.string.text_btn_trip_reject);
                        mBtnTripReject.setVisibility(View.VISIBLE);
                        mBtnTripSubmit.setVisibility(View.VISIBLE);
                        mLlRn.setVisibility(View.VISIBLE);
                    } else {
                        mBtnTripReject.setVisibility(View.GONE);
                        mBtnTripSubmit.setVisibility(View.GONE);
                    }
                    break;

                case Trip.Status.REJECTED:
                case Trip.Status.CERTIFICATION_REJECTED:
                    mTvTripStatus.setTextColor(ContextCompat.getColor(getActivity(), R.color.color_de2618));
                    mBtnTripReject.setVisibility(View.GONE);
                    mBtnTripSubmit.setVisibility(View.GONE);
                    if (mTrip.getRejectionNote() != null && !mTrip.getRejectionNote().isEmpty()){
                        mLlRn.setVisibility(View.VISIBLE);
                        mEdtRn.setFocusable(false);
                        mEdtRn.setCursorVisible(false);
                        mEdtRn.setKeyListener(null);
                        mEdtRn.setBackgroundColor(Color.TRANSPARENT);
                        mEdtRn.setText(mTrip.getRejectionNote());
                    } else {
                        mLlRn.setVisibility(View.GONE);
                    }
                    break;

                default:
                    mBtnTripReject.setVisibility(View.GONE);
                    mBtnTripSubmit.setVisibility(View.GONE);
                    mLlRn.setVisibility(View.GONE);
                    break;
            }
        }
    }

    private void setCostSummaryData() {
        String currency = DEFAULT_CURRENCY;
        double dsaTotal = 0;
        double expensesTotal = 0;
        double deductionsTotal = 0;
        double totalCost;

        if (mCurrency != null && mCurrency.isValid()) {
            currency = mCurrency.getCode();
        }

        setActionBarTitle(mTrip.getReferenceNumber());

        if (mTrip.getCostSummary() != null) {
            try {
                dsaTotal = Double.parseDouble(mTrip.getCostSummary().getDsaTotal());
            } catch (NumberFormatException exc) {
                exc.printStackTrace();
            }
            try {
                expensesTotal = Double.parseDouble(mTrip.getCostSummary().getExpensesTotal());
            } catch (NumberFormatException exc) {
                exc.printStackTrace();
            }
            try {
                deductionsTotal = Double.parseDouble(mTrip.getCostSummary().getDeductionsTotal());
            } catch (NumberFormatException exc) {
                exc.printStackTrace();
            }
        }

        // calculate total cost
        totalCost = dsaTotal + expensesTotal - deductionsTotal;

        // dsa total amount
        mTvTripDsaTotal.setText(String.format(getString(R.string.text_cost_currency),
                String.valueOf(dsaTotal), currency));

        // expense total amount
        mTvTripExpenseTotal.setText(String.format(getString(R.string.text_cost_currency),
                String.valueOf(expensesTotal), currency));

        // deductions total amount
        mTvTripDeducationsTotal.setText(String.format(getString(R.string.text_cost_currency),
                String.valueOf(deductionsTotal), currency));

        // total amount cost
        mTvTripTotalCost.setText(String.format(getString(R.string.text_cost_currency),
                String.valueOf(totalCost), currency));
    }

    private void changeTripStatusRequest(String status) {
        DialogManager.getInstance().showPreloader(getActivity(), getClass().getSimpleName());
        RetrofitUtil.changeTripStatusRequest(
                getActivity(),
                TripFragment.class.getSimpleName(),
                status,
                mTrip
        );
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}
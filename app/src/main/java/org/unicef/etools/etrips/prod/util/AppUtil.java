package org.unicef.etools.etrips.prod.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;

import org.unicef.etools.etrips.prod.R;
import org.unicef.etools.etrips.prod.db.entity.static_data.data.ActionPointStatus;
import org.unicef.etools.etrips.prod.db.entity.static_data.data.Location;
import org.unicef.etools.etrips.prod.db.entity.static_data.data.Partner;
import org.unicef.etools.etrips.prod.db.entity.static_data.data.PartnerShip;
import org.unicef.etools.etrips.prod.db.entity.static_data.data.Result;
import org.unicef.etools.etrips.prod.db.entity.static_data.data_2.Airline;
import org.unicef.etools.etrips.prod.db.entity.static_data.data_2.BusinessArea;
import org.unicef.etools.etrips.prod.db.entity.static_data.data_2.Country;
import org.unicef.etools.etrips.prod.db.entity.static_data.data_2.Currency;
import org.unicef.etools.etrips.prod.db.entity.static_data.data_2.DsaRegion;
import org.unicef.etools.etrips.prod.db.entity.static_data.data_2.ExpenseType;
import org.unicef.etools.etrips.prod.db.entity.static_data.data_2.Fund;
import org.unicef.etools.etrips.prod.db.entity.static_data.data_2.Grant;
import org.unicef.etools.etrips.prod.db.entity.static_data.data_2.Region;
import org.unicef.etools.etrips.prod.db.entity.static_data.data_2.Wbs;
import org.unicef.etools.etrips.prod.db.entity.trip.ActionPoint;
import org.unicef.etools.etrips.prod.db.entity.trip.CostSummary;
import org.unicef.etools.etrips.prod.db.entity.trip.Dsa;
import org.unicef.etools.etrips.prod.db.entity.trip.LocalTrip;
import org.unicef.etools.etrips.prod.db.entity.trip.Trip;
import org.unicef.etools.etrips.prod.db.entity.user.UserStatic;
import org.unicef.etools.etrips.prod.ui.activity.AuthActivity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;

public class AppUtil {

    private static final String LOG_TAG = AppUtil.class.getSimpleName();

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int booleanToInt(boolean b) {
        return b ? 1 : 0;
    }

    public static boolean intToBoolean(int b) {
        return b == 1;
    }

    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void closeKeyboard(Activity activity) {
        if (activity != null) {
            if (activity.getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity
                        .getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
                        .getWindowToken(), 0);
            }
        }
    }

    public static void showKeyboard(Activity activity) {
        if (activity != null) {
            ((InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE))
                    .toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void logout(Context context) {
        // drop user token and static data on logout
        Preference.getInstance(context).setUserToken(null);

        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // drop data without primary key
                realm.delete(CostSummary.class);
                realm.delete(Dsa.class);

                // drop static data
                realm.delete(Location.class);
                realm.delete(ActionPointStatus.class);
                realm.delete(Partner.class);
                realm.delete(PartnerShip.class);
                realm.delete(Result.class);
                realm.delete(UserStatic.class);
                realm.delete(Airline.class);
                realm.delete(BusinessArea.class);
                realm.delete(Country.class);
                realm.delete(Currency.class);
                realm.delete(DsaRegion.class);
                realm.delete(ExpenseType.class);
                realm.delete(Fund.class);
                realm.delete(Grant.class);
                realm.delete(Region.class);
                realm.delete(Wbs.class);

                realm.delete(Trip.class);
                realm.delete(ActionPoint.class);
                realm.delete(LocalTrip.class);
            }
        });

        Intent intent = new Intent(context, AuthActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        ((Activity) context).finish();
    }

    public static void addAssignedFullName(Realm realm, ActionPoint actionPoint) {
        addAssignedFullName(realm, Collections.singletonList(actionPoint));
    }

    public static void addAssignedFullName(Realm realm, List<ActionPoint> actionPoints) {
        for (ActionPoint object : actionPoints) {
            final UserStatic user = realm.where(UserStatic.class)
                    .equalTo("id", object.assignedBy)
                    .findFirst();

            if (user != null) {
                String name = !TextUtils.isEmpty(user.getFullName())
                        ? user.getFullName() : user.getUsername();
                object.setAssignedByFullName(name);
            }
        }
    }

    public static String formatDouble(double d) {
        BigDecimal bd = new BigDecimal(String.valueOf(d));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator(' ');
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##", decimalFormatSymbols);
        return decimalFormat.format(bd);
    }

    // simple fix for set correct traveler field and separate my and supervised trips
    public static void extendTripModel(boolean isMyTrip, long travelerId, Trip trip) {
        if (trip != null && trip.isValid()) {
            trip.setMyTrip(isMyTrip);
            trip.setTraveler(travelerId);
        }
    }

    // simple fix for set correct traveler field and separate my and supervised trips
    public static void extendTripModel(boolean isMyTrip, long travelerId, ArrayList<Trip> trips) {
        if (trips != null) {
            for (Trip trip : trips) {
                trip.setMyTrip(isMyTrip);
                trip.setTraveler(travelerId);
            }
        }
    }

    public static void restoreDrafts(@NonNull Realm realm, @NonNull ArrayList<Trip> trips) {
        if (realm.isClosed()) {
            return;
        }
        for (Trip trip : trips) {
            LocalTrip localTrip = realm.where(LocalTrip.class).equalTo("id", trip.getId()).findFirst();
            if (localTrip != null && localTrip.isValid()) {

                trip.setNotSynced(true);

                if (localTrip.getReport() != null) {
                    trip.setReport(localTrip.getReport());
                }

                if (localTrip.getAttachments() != null) {
                    trip.setAttachments(localTrip.getAttachments());
                }
            }
        }
    }

    public static void checkDraftForTrip(@NonNull Realm realm, @NonNull Trip trip) {
        if (realm.isClosed()) {
            return;
        }
        LocalTrip localTrip = realm.where(LocalTrip.class).equalTo("id", trip.getId()).findFirst();

        // check case when report from server is not empty
        if (trip.getReport() != null && !trip.getReport().isEmpty()) {
            trip.setNotSynced(false);
            if (localTrip != null && localTrip.isValid()) {
                localTrip.deleteFromRealm();
            }
        } else {
            if (trip.getAttachments() != null && !trip.getAttachments().isEmpty()) {
                trip.setAttachments(null);
            }
            if (localTrip != null && localTrip.isValid()) {
                trip.setNotSynced(true);
                if (localTrip.getReport() != null) {
                    trip.setReport(localTrip.getReport());
                }

                if (localTrip.getAttachments() != null) {
                    trip.setAttachments(realm.copyFromRealm(localTrip).getAttachments());
                }
            }
        }
    }

    public static String findElementFromXml(String xml, String elem) {
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            parser.setInput(new StringReader(xml));
            while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {

                final int event = parser.getEventType();
                if (event == XmlPullParser.START_TAG && parser.getName().equals(elem)) {
                    parser.next();
                    if (parser.getEventType() == XmlPullParser.TEXT) {
                        return parser.getText();
                    }
                } else {
                    parser.next();
                }
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int calculateStatusListItemPosition(String status, List<ActionPointStatus> statusList) {
        if (status == null || statusList == null) {
            return 0;
        }
        for (ActionPointStatus actionPointStatus : statusList) {
            if (status.equalsIgnoreCase(actionPointStatus.getStatus())) {
                return statusList.indexOf(actionPointStatus);
            }
        }
        return 0;
    }
}

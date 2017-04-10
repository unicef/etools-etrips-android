package org.unicef.etools.etrips.prod.util.manager;


import android.util.SparseBooleanArray;

import static org.unicef.etools.etrips.prod.util.manager.LogInManager.Request.GET_CURRENCIES;
import static org.unicef.etools.etrips.prod.util.manager.LogInManager.Request.GET_PROFILE;
import static org.unicef.etools.etrips.prod.util.manager.LogInManager.Request.GET_STATIC_DATA;
import static org.unicef.etools.etrips.prod.util.manager.LogInManager.Request.GET_TOKEN;
import static org.unicef.etools.etrips.prod.util.manager.LogInManager.Request.GET_WBS_GRANTS_FUNDS;

public class LogInManager {


    private static final int INITIAL_CAPACITY = 5;

    public static class Request {
        public static final int GET_TOKEN = 0;
        public static final int GET_PROFILE = 1;
        public static final int GET_WBS_GRANTS_FUNDS = 2;
        public static final int GET_CURRENCIES = 3;
        public static final int GET_STATIC_DATA = 4;
    }

    private SparseBooleanArray mRequestStatus;

    public LogInManager() {
        init();
    }

    private void init() {
        mRequestStatus = new SparseBooleanArray(INITIAL_CAPACITY);
        mRequestStatus.append(GET_TOKEN, false);
        mRequestStatus.append(GET_PROFILE, false);
        mRequestStatus.append(GET_WBS_GRANTS_FUNDS, false);
        mRequestStatus.append(GET_CURRENCIES, false);
        mRequestStatus.append(GET_STATIC_DATA, false);
    }

    public void setRequestStatus(int requestType, boolean isSuccessfully) {
        mRequestStatus.append(requestType, isSuccessfully);
    }

    public void resetStatuses() {
        for (int i = 0; i < mRequestStatus.size(); i++) {
            mRequestStatus.append(i, false);
        }
    }

    public boolean isLogInCompleted() {
        for (int i = 0; i < mRequestStatus.size(); i++) {
            if (!mRequestStatus.get(i)) {
                return false;
            }
        }
        return true;
    }

}

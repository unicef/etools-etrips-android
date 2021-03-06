package org.unicef.etools.etrips.prod.ui.fragment;


import android.support.v4.app.Fragment;

import org.unicef.etools.etrips.prod.ui.activity.BaseActivity;

public class BaseFragment extends Fragment {

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    // ===========================================================
    // Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    protected void hideActionBarIcon() {
        ((BaseActivity) getActivity()).hideActionBarIcon();
    }

    protected void showActionBarIcon() {
        ((BaseActivity) getActivity()).showActionBarIcon();
    }

    protected void setActionBarIcon() {
        ((BaseActivity) getActivity()).hideActionBarIcon();
    }

    protected void setActionBarTitle(String actionBarTitle) {
        ((BaseActivity) getActivity()).setActionBarTitle(actionBarTitle);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}

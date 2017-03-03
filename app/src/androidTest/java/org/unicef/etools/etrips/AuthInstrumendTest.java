package org.unicef.etools.etrips;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unicef.etools.etrips.ui.activity.AuthActivity;

@RunWith(AndroidJUnit4.class)
public class AuthInstrumendTest {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = AuthInstrumendTest.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    @Rule
    public ActivityTestRule<AuthActivity> mAuthActivityTestRule
            = new ActivityTestRule<>(AuthActivity.class);
    private Context instrumentationCtx;

    // ===========================================================
    // Constructors
    // ===========================================================

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    @Before
    public void setup() {
        instrumentationCtx = InstrumentationRegistry.getContext();
    }

    @Test()
    public void authTest() throws Exception {

    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Click Listeners
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

}

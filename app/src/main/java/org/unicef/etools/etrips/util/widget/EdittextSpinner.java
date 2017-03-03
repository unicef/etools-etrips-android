package org.unicef.etools.etrips.util.widget;


import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import org.unicef.etools.etrips.R;
import org.unicef.etools.etrips.util.Constant;

import java.util.Calendar;

public class EdittextSpinner extends AppCompatAutoCompleteTextView
        implements AdapterView.OnItemClickListener {

    // ===========================================================
    // Constants
    // ===========================================================

    private static final String LOG_TAG = EdittextSpinner.class.getSimpleName();

    // ===========================================================
    // Fields
    // ===========================================================

    private long mStartClickTime;
    private boolean isDropDownShown;

    // ===========================================================
    // Constructors
    // ===========================================================

    public EdittextSpinner(Context context) {
        super(context);
        setOnItemClickListener(this);
    }

    public EdittextSpinner(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setOnItemClickListener(this);
    }

    public EdittextSpinner(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setOnItemClickListener(this);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    // ===========================================================
    // Methods for/from SuperClass
    // ===========================================================

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            performFiltering(Constant.Symbol.NULL, 0);
            closeKeyboard();
            setKeyListener(null);
            dismissDropDown();
        } else {
            isDropDownShown = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mStartClickTime = Calendar.getInstance().getTimeInMillis();
                break;
            }

            case MotionEvent.ACTION_UP: {
                long clickDuration = Calendar.getInstance().getTimeInMillis() - mStartClickTime;
                if (clickDuration < 200) {
                    if (isDropDownShown) {
                        dismissDropDown();
                        isDropDownShown = false;
                    } else {
                        requestFocus();
                        showDropDown();
                        isDropDownShown = true;
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        isDropDownShown = false;
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top,
                                                        Drawable right, Drawable bottom) {
        Drawable dropdownIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_drop_down_white);
        if (dropdownIcon != null) {
            right = dropdownIcon;
            right.mutate();
        }
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
    }

    // ===========================================================
    // Other Listeners, methods for/from Interfaces
    // ===========================================================

    // ===========================================================
    // Click Listeners
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    private void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getWindowToken(), 0);
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================
}

package com.davis.tyler.magpiehunt.Spinners;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.util.Log;

public class SpinnerSearchFilter extends AppCompatSpinner {
    private static final String TAG = "SearchSpinner";
    private SpinnerSearchFilter.OnSpinnerSearchEventsListener mListener;
    private boolean mOpenInitiated = false;

    public SpinnerSearchFilter(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public SpinnerSearchFilter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SpinnerSearchFilter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpinnerSearchFilter(Context context, int mode) {
        super(context, mode);
    }

    public SpinnerSearchFilter(Context context) {
        super(context);
    }

    public interface OnSpinnerSearchEventsListener {

        void onSpinnerSearchOpened();

        void onSpinnerSearchClosed();

    }

    @Override
    public boolean performClick() {
        // register that the Spinner was opened so we have a status
        // indicator for the activity(which may lose focus for some other
        // reasons)
        mOpenInitiated = true;
        if (mListener != null) {
            mListener.onSpinnerSearchOpened();
        }
        return super.performClick();
    }

    public void setSpinnerSearchEventsListener(SpinnerSearchFilter.OnSpinnerSearchEventsListener onSpinnerEventsListener) {
        mListener = onSpinnerEventsListener;
    }

    /**
     * Propagate the closed Spinner event to the listener from outside.
     */
    public void performClosedEvent() {
        Log.e(TAG, "spinner closed");
        mOpenInitiated = false;
        if (mListener != null) {
            mListener.onSpinnerSearchClosed();

        }
    }

    /**
     * A boolean flag indicating that the Spinner triggered an open event.
     *
     * @return true for opened Spinner
     */
    public boolean hasBeenOpened() {
        return mOpenInitiated;
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        android.util.Log.d(TAG, "onWindowFocusChanged");
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasBeenOpened() && hasWindowFocus) {
            android.util.Log.i(TAG, "closing popup");
            performClosedEvent();
        }
    }
}

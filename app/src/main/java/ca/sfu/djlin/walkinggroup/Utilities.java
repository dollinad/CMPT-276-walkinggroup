package ca.sfu.djlin.walkinggroup;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Utilities {
    private static final String TAG = "Utilities";

    public static Handler mMailCheckHandler;
    public static Runnable mMailStatusChecker;

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboardFocus(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void startMessageChecking() {
        // Start background task test
        // Note: To be used for checking for new messages
        mMailCheckHandler = new Handler();
        mMailStatusChecker = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Checking for new mail");
                mMailCheckHandler.postDelayed(mMailStatusChecker, 60000);
            }
        };
        mMailCheckHandler.post(mMailStatusChecker);
        // End background task
    }

    public static void removeMessageChecking() {
        mMailCheckHandler.removeCallbacks(mMailStatusChecker);
    }
}

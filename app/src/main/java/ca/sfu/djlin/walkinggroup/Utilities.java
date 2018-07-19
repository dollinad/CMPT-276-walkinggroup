package ca.sfu.djlin.walkinggroup;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.List;

import ca.sfu.djlin.walkinggroup.app.ViewMessagesActivity;
import ca.sfu.djlin.walkinggroup.model.Session;
import ca.sfu.djlin.walkinggroup.model.User;
import ca.sfu.djlin.walkinggroup.proxy.ProxyBuilder;
import ca.sfu.djlin.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

import static java.security.AccessController.getContext;

public class Utilities {
    private static final String TAG = "Utilities";

    public static Handler mMailCheckHandler;
    public static Runnable mMailStatusChecker;

    public Session userSession;
    public static User currentUser;
    public WGServerProxy proxy;

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

    public static void startMessageChecking(Context context, WGServerProxy proxy, User user) {
        // Set up JSON depth to use
        Long depth = new Long(1);
        currentUser = user;

        // Start background task test
        // Note: To be used for checking for new messages
        mMailCheckHandler = new Handler();
        mMailStatusChecker = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Checking for new mail for user id: " + currentUser.getId());
                Call<List<ca.cmpt276.walkinggroup.dataobjects.Message>> call = proxy.getMessages(currentUser.getId(), depth);
                ProxyBuilder.callProxy(context, call, messageList -> getMessageListResponse(messageList, currentUser));
                mMailCheckHandler.postDelayed(mMailStatusChecker, 60000);
            }
        };
        mMailCheckHandler.post(mMailStatusChecker);
        // End background task
    }

    public static void getMessageListResponse(List<ca.cmpt276.walkinggroup.dataobjects.Message> messages, User user) {
        // Log.d("TAG", "getMessageListResponse: " + messages.toString());
        if (user.getMessages() != null) {
            // Log.d("TAG", "the current user.getMessages is: " + user.getMessages().toString());
            if (messages.equals(user.getMessages())) {
                Log.d("TAG", "No new messages");
            } else {
                Log.d("TAG", "You have received a new message!");
                // Update the message information
                user.setMessages(messages);
            }
        }
    }

    public static void removeMessageChecking() {
        mMailCheckHandler.removeCallbacks(mMailStatusChecker);
    }
}

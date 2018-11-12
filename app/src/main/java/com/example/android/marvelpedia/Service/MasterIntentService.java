package com.example.android.marvelpedia.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.example.android.marvelpedia.Utils.Network.CharacterHelper;
import com.example.android.marvelpedia.model.Character;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MasterIntentService extends IntentService {

    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_CHARS = "com.example.android.marvelpedia.action.CHARS";
    private static final String ACTION_EXTRAS = "com.example.android.marvelpedia.action.EXTRAS";
    private static final String ACTION_COMICS = "com.example.android.marvelpedia.action.COMICS";
    private static final String ACTION_EVENTS = "com.example.android.marvelpedia.action.EVENTS";
    private static final String ACTION_SERIES = "com.example.android.marvelpedia.action.SERIES";
    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.android.marvelpedia.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.android.marvelpedia.extra.PARAM2";
    private static List<Character> mCharacters = new ArrayList<>();
    private final String LOG_TAG = MasterIntentService.class.getSimpleName();
    private CharacterHelper characterHelper;

    public MasterIntentService() {
        super("MyIntentService");
    }

    /**
     * Starts this service to perform action Chars with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionPopulateCharacters(Context context, String searchTerm) {
        Intent intent = new Intent(context, MasterIntentService.class);
        Log.v("IntentService", "Searching for Characters");
        intent.setAction(ACTION_CHARS);
        intent.putExtra(EXTRA_PARAM1, searchTerm);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CHARS.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                handleActionGetChars(param1);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionGetChars(String searchTerm) {
        // TODO: Handle action GetChars
        Log.v(LOG_TAG, searchTerm);
        characterHelper = new CharacterHelper();
        characterHelper.retrieveCharacters(searchTerm);
        //While Loop so that other lines won't run unless
        //List is updated
        while (true) {
            if (characterHelper.isFinished.equals(true)) {
                Log.v(LOG_TAG, "In While Loop");
                break;
            }
        }
        //Retrieve the character list from the character helper
        mCharacters = characterHelper.mCharacters;
        //Create a new bundle
        Bundle receiverArgs = new Bundle();
        //Put the CharacterList into the bundle
        receiverArgs.putParcelableArrayList(EXTRA_PARAM1, (ArrayList<? extends Parcelable>) mCharacters);
        receiverArgs.putString(EXTRA_PARAM2, searchTerm);
        //Create a new intent for the broadcast
        Intent broadcastIntent = new Intent();
        //Set the action to the one we set for this action
        broadcastIntent.setAction(ACTION_CHARS);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        //Put the Bundle into the Intent extras
        broadcastIntent.putExtra(EXTRA_PARAM1, receiverArgs);
        //Send the broadcast
        sendBroadcast(broadcastIntent);


        //throw new UnsupportedOperationException("Not yet implemented");
    }
}

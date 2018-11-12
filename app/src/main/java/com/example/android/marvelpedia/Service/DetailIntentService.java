package com.example.android.marvelpedia.Service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.example.android.marvelpedia.Utils.Network.CharacterHelper;
import com.example.android.marvelpedia.Utils.Network.ComicHelper;
import com.example.android.marvelpedia.Utils.Network.EventHelper;
import com.example.android.marvelpedia.Utils.Network.SeriesHelper;
import com.example.android.marvelpedia.model.Character;
import com.example.android.marvelpedia.model.Comic;
import com.example.android.marvelpedia.model.Event;
import com.example.android.marvelpedia.model.Series;

import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class DetailIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_EXTRAS = "com.example.android.marvelpedia.action.EXTRAS";
    private static final String ACTION_EXTRASC = "com.example.android.marvelpedia.action.EXTRASC";
    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.android.marvelpedia.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.android.marvelpedia.extra.PARAM2";
    private static final String EXTRA_PARAM3 = "com.example.android.marvelpedia.extra.PARAM3";
    private static final String EXTRA_PARAM4 = "com.example.android.marvelpedia.extra.PARAM4";
    private static int queueCount;
    private static List<Character> mCharacters = new ArrayList<>();
    private static List<Comic> mComics = new ArrayList<>();
    private static List<Event> mEvents = new ArrayList<>();
    private static List<Series> mSeries = new ArrayList<>();
    private final String LOG_TAG = DetailIntentService.class.getSimpleName();
    private CharacterHelper characterHelper;
    private ComicHelper comicHelper;
    private EventHelper eventHelper;
    private SeriesHelper seriesHelper;

    public DetailIntentService() {
        super("MyIntentService");
    }

    /**
     * Starts this service to perform action Extras with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionGetCharacterData(Context context, Character character) {
        queueCount += 1;
        Intent intent = new Intent(context, DetailIntentService.class);
        intent.setAction(ACTION_EXTRAS);
        intent.putExtra(EXTRA_PARAM1, character);
        context.startService(intent);
    }

    public static void startActionGetComicData(Context context, Comic comic) {
        queueCount += 1;
        Intent intent = new Intent(context, DetailIntentService.class);
        intent.setAction(ACTION_EXTRAS);
        intent.putExtra(EXTRA_PARAM2, comic);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_EXTRAS.equals(action)) {
                if (intent.getParcelableExtra(EXTRA_PARAM1) != null) {
                    final Character character = intent.getParcelableExtra(EXTRA_PARAM1);
                    handleActionExtrasCharacter(character);
                } else if (intent.getParcelableExtra(EXTRA_PARAM2) != null) {
                    final Comic comic = intent.getParcelableExtra(EXTRA_PARAM2);
                    handleActionExtrasComic(comic);
                }
            }
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionExtrasCharacter(Character character) {
        characterHelper = new CharacterHelper();
        characterHelper.retrieveCharacterComics(character);
        characterHelper.retrieveCharacterEvents(character);
        characterHelper.retrieveCharacterSeries(character);
        //While Loop so that other lines won't run unless
        //List is updated
        while (true) {
            if (characterHelper.checkIfAllFinished().equals(true)) {
                Log.v(LOG_TAG, "In While Loop");
                break;
            }
        }
        //Retrieve the character list from the character helper
        mComics = characterHelper.mComics;
        mEvents = characterHelper.mEvents;
        mSeries = characterHelper.mSeries;
        //Create a new bundle
        Bundle receiverArgs = new Bundle();
        //Put the CharacterList into the bundle
        receiverArgs.putParcelableArrayList(EXTRA_PARAM2, (ArrayList<? extends Parcelable>) mComics);
        receiverArgs.putParcelableArrayList(EXTRA_PARAM3, (ArrayList<? extends Parcelable>) mEvents);
        receiverArgs.putParcelableArrayList(EXTRA_PARAM4, (ArrayList<? extends Parcelable>) mSeries);
        //Create a new intent for the broadcast
        Intent broadcastIntent = new Intent();
        //Set the action to the one we set for this action
        broadcastIntent.setAction(ACTION_EXTRAS);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        //Put the Bundle into the Intent extras
        broadcastIntent.putExtra(EXTRA_PARAM1, receiverArgs);
        //Send the broadcast
        sendBroadcast(broadcastIntent);
    }

    private void handleActionExtrasComic(Comic comic) {
        comicHelper = new ComicHelper();
        comicHelper.retrieveComicEvents(comic);
        comicHelper.retrieveComicCharacters(comic);
        //While Loop so that other lines won't run unless
        //Lists are updated
        while (true) {
            if (comicHelper.checkIfAllFinished().equals(true)) {
                Log.v(LOG_TAG, "In While Loop");
                break;
            }
        }
        //Retrieve the character list from the character helper
        mCharacters = comicHelper.mCharacters;
        mEvents = comicHelper.mEvents;
        mSeries = comicHelper.mSeries;
        //Create a new bundle
        Bundle receiverArgs = new Bundle();
        //Put the CharacterList into the bundle
        receiverArgs.putParcelableArrayList(EXTRA_PARAM1, (ArrayList<? extends Parcelable>) mCharacters);
        receiverArgs.putParcelableArrayList(EXTRA_PARAM2, (ArrayList<? extends Parcelable>) mComics);
        receiverArgs.putParcelableArrayList(EXTRA_PARAM3, (ArrayList<? extends Parcelable>) mEvents);
        receiverArgs.putParcelableArrayList(EXTRA_PARAM4, (ArrayList<? extends Parcelable>) mSeries);
        //Create a new intent for the broadcast
        Intent broadcastIntent = new Intent();
        //Set the action to the one we set for this action
        broadcastIntent.setAction(ACTION_EXTRAS);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        //Put the Bundle into the Intent extras
        broadcastIntent.putExtra(EXTRA_PARAM1, receiverArgs);
        //Send the broadcast
        sendBroadcast(broadcastIntent);
    }
}

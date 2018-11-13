package com.example.android.marvelpedia.Widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.android.marvelpedia.R;
import com.example.android.marvelpedia.model.Character;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of App Widget functionality.
 */
public class MarvelAppWidget extends AppWidgetProvider {

    private static final String TEAM_KEY = "team_name_key";
    private static List<Character> mTeamMembers;
    private static String teamName;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.marvel_app_widget);

        remoteViews.setTextViewText(R.id.widget_team_name, teamName);

        Bundle factoryIntentBundle = new Bundle();
        factoryIntentBundle.putParcelableArrayList("team_list", (ArrayList<? extends Parcelable>) mTeamMembers);

        Intent factoryIntent = new Intent(context, WidgetService.class);
        factoryIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        factoryIntent.putExtra("team_members", factoryIntentBundle);
        factoryIntent.setData(Uri.parse(factoryIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(appWidgetId, R.id.team_widget_stack_view, factoryIntent);

        // Instruct the widget manager to update the widget
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(appWidgetId, R.id.team_widget_stack_view);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        //Check if the action in the Intent is updating the Widget
        if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            //Get the Extras from the Intent and put it into a Bundle
            Bundle appWidgetBundle = intent.getBundleExtra("bundle_extra");
            Log.v("Tag", "In Widget OnReceive");
            if (appWidgetBundle != null) {
                teamName = appWidgetBundle.getString("team_name");
                mTeamMembers = appWidgetBundle.getParcelableArrayList("widget_extras");
                Log.v("Tag", mTeamMembers.get(0).getName());

                Log.v("WidgetTag", "Something: " + teamName);
            }
            //Get the widget Ids for the Application
            int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, MarvelAppWidget.class));
            this.onUpdate(context, AppWidgetManager.getInstance(context), ids);
        }
    }
}


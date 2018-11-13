package com.example.android.marvelpedia.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;
import android.widget.RemoteViews;

import com.example.android.marvelpedia.R;

/**
 * Implementation of App Widget functionality.
 */
public class MarvelAppWidget extends AppWidgetProvider {

    public static final String WIDGET_IDS_KEY = "myWidgetIdKey";
    private static final String TEAM_NAME_KEY = "name_of_team";

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.marvel_app_widget);

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(context);

        String retrievedTeamName = preferences.getString(TEAM_NAME_KEY, "");

        remoteViews.setTextViewText(R.id.widget_team_name, retrievedTeamName);

        //Intent to launch the WidgetViewsFactory
        Intent factoryIntent = new Intent(context, WidgetService.class);
        factoryIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        factoryIntent.setData(Uri.parse(factoryIntent.toUri(Intent.URI_INTENT_SCHEME)));
        remoteViews.setRemoteAdapter(appWidgetId, R.id.team_widget_stack_view, factoryIntent);

        //Intent used to update the Widget
        Intent updateIntent = new Intent();
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(WIDGET_IDS_KEY, appWidgetId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.update_widget_button, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.team_widget_stack_view);
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
            //Get the widget Ids for the Application
            int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, MarvelAppWidget.class));
            this.onUpdate(context, AppWidgetManager.getInstance(context), ids);
        }
    }
}


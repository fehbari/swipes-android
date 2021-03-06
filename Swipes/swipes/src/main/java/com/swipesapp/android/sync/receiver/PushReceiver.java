package com.swipesapp.android.sync.receiver;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;
import com.swipesapp.android.BuildConfig;
import com.swipesapp.android.sync.service.SyncService;
import com.swipesapp.android.util.PreferenceUtils;
import com.swipesapp.android.values.Constants;

import org.json.JSONObject;

/**
 * Custom receiver to handle push messages.
 *
 * @author Fernanda Bari
 */
public class PushReceiver extends ParsePushBroadcastReceiver {

    private static final String LOG_TAG = PushReceiver.class.getSimpleName();

    public static final String KEY_CHANNELS = "channels";
    public static final String CHANNEL_DEV = "Development";

    private static final String EXTRA_PAYLOAD = "com.parse.Data";

    private static final String JSON_DATA = "aps";
    private static final String JSON_CONTENT = "content-available";
    private static final String JSON_SYNC_ID = "syncId";

    private static SyncService sSyncService = SyncService.getInstance();

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        super.onPushReceive(context, intent);

        // Initialize service if needed.
        if (sSyncService == null) sSyncService = SyncService.newInstance(context);

        try {
            if (PreferenceUtils.isBackgroundSyncEnabled(context)) {
                // Read payload from intent.
                String payload = intent.getStringExtra(EXTRA_PAYLOAD);
                JSONObject json = new JSONObject(payload);

                // Read payload data in the JSON.
                JSONObject data = new JSONObject(json.getString(JSON_DATA));
                String contentAvailable = data.getString(JSON_CONTENT);
                String syncId = json.getString(JSON_SYNC_ID);

                // Determine sync conditions.
                boolean hasContent = contentAvailable != null && contentAvailable.equals("1");
                String lastSyncId = PreferenceUtils.readString(PreferenceUtils.LAST_SYNC_ID, context);
                boolean isDifferentDevice = (syncId != null && !syncId.isEmpty()) && lastSyncId != null && !syncId.equals(lastSyncId);

                if (hasContent && isDifferentDevice) {
                    // Perform background sync.
                    sSyncService.performSync(true, Constants.SYNC_DELAY);
                }
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error parsing push payload.", e);
        }

        if (BuildConfig.DEBUG) Log.d(LOG_TAG, "Push received");
    }

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        // We don't want the push to trigger a notification.
        return null;
    }

}

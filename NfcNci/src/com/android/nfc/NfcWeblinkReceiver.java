/*
 * Copyright (C) 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.nfc;

import android.app.ActivityOptions;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NfcWeblinkReceiver extends BroadcastReceiver {
    private static final String TAG = "NfcWeblinkReceiver";
    public static final String ACTION_WEBLINK = "com.android.nfc.ACTION_WEBLINK";
    public static final String ACTION_WEBLINK_CANCEL = "com.android.nfc.ACTION_WEBLINK_CANCEL";
    public static final String EXTRA_PENDING_INTENT = "com.android.nfc.EXTRA_PENDING_INTENT";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_WEBLINK.equals(action) || ACTION_WEBLINK_CANCEL.equals(action)) {
            NotificationManager nm = context.getSystemService(NotificationManager.class);
            if (nm != null) {
                nm.cancel(NfcWeblinkNotification.NOTIFICATION_ID_NFC);
            }

            if (ACTION_WEBLINK.equals(action)) {
                PendingIntent pendingIntent = intent.getParcelableExtra(
                        EXTRA_PENDING_INTENT, PendingIntent.class);
                if (pendingIntent != null) {
                    try {
                        ActivityOptions options = ActivityOptions.makeBasic();
                        options.setPendingIntentBackgroundActivityStartMode(
                                ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED);
                        pendingIntent.send(context, 0, null, null, null, null, options.toBundle());
                    } catch (PendingIntent.CanceledException e) {
                        Log.e(TAG, "Failed to send PendingIntent", e);
                    }
                }
            }
        }
    }
}

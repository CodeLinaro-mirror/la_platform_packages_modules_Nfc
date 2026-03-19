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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * This class handles the Notification Manager for the NDEF weblink info
 */
public class NfcWeblinkNotification {
    private static final String NFC_WEBLINK_NOTIFICATION_CHANNEL =
            "nfc_weblink_channel";
    private NotificationChannel mNotificationChannel;
    public static final int NOTIFICATION_ID_NFC = -1000004;
    Context mContext;
    String mUri;
    PendingIntent mLaunchIntent;

    /**
     * Constructor
     *
     * @param ctx The context to use to obtain access to the resources
     * @param uri The URI to open
     * @param launchIntent The intent to launch when notification is clicked
     */
    public NfcWeblinkNotification(Context ctx, String uri, PendingIntent launchIntent) {
        mContext = ctx;
        mUri = uri;
        mLaunchIntent = launchIntent;
    }

    /**
     * Start the notification.
     */
    public void startNotification() {
        Notification.Builder builder =
                new Notification.Builder(mContext, NFC_WEBLINK_NOTIFICATION_CHANNEL);

        Intent actionIntent = new Intent(mContext, NfcWeblinkReceiver.class);
        actionIntent.setAction(NfcWeblinkReceiver.ACTION_WEBLINK);
        actionIntent.putExtra(NfcWeblinkReceiver.EXTRA_PENDING_INTENT, mLaunchIntent);

        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(mContext, 0,
                actionIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
                        | PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action action = new Notification.Action.Builder(null,
                mContext.getString(R.string.action_confirm_url_open), actionPendingIntent).build();

        Intent cancelIntent = new Intent(mContext, NfcWeblinkReceiver.class);
        cancelIntent.setAction(NfcWeblinkReceiver.ACTION_WEBLINK_CANCEL);

        PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(mContext, 1,
                cancelIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
                        | PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Action cancelAction = new Notification.Action.Builder(null,
                mContext.getString(R.string.cancel), cancelPendingIntent).build();

        builder.setContentTitle(mContext.getString(R.string.title_confirm_url_open))
                .setContentText(mUri)
                .setSmallIcon(R.drawable.nfc_icon)
                .setPriority(Notification.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .addAction(action)
                .addAction(cancelAction)
                .setContentIntent(mLaunchIntent);

        mNotificationChannel = new NotificationChannel(NFC_WEBLINK_NOTIFICATION_CHANNEL,
                mContext.getString(R.string.nfcWeblinkUserLabel),
                NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager =
                mContext.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(mNotificationChannel);
        notificationManager.notify(NOTIFICATION_ID_NFC, builder.build());
    }
}

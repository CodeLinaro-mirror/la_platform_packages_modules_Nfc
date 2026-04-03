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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

@RunWith(AndroidJUnit4.class)
public class NfcWeblinkReceiverTest {

    @Mock
    private Context mContext;
    @Mock
    private NotificationManager mNotificationManager;
    @Mock
    private PendingIntent mPendingIntent;

    private NfcWeblinkReceiver mReceiver;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(mContext.getSystemService(NotificationManager.class)).thenReturn(mNotificationManager);
        mReceiver = new NfcWeblinkReceiver();
    }

    @Test
    public void testOnReceive() throws PendingIntent.CanceledException {
        Intent mockIntent = Mockito.mock(Intent.class);
        when(mockIntent.getAction()).thenReturn(NfcWeblinkReceiver.ACTION_WEBLINK);
        when(mockIntent.getParcelableExtra(
                eq(NfcWeblinkReceiver.EXTRA_PENDING_INTENT), eq(PendingIntent.class)))
                .thenReturn(mPendingIntent);

        mReceiver.onReceive(mContext, mockIntent);

        verify(mNotificationManager).cancel(eq(NfcWeblinkNotification.NOTIFICATION_ID_NFC));
        verify(mPendingIntent).send(eq(mContext), anyInt(), isNull(), isNull(), isNull(),
                isNull(), any());
    }

    @Test
    public void testOnReceiveCancel() throws PendingIntent.CanceledException {
        Intent mockIntent = Mockito.mock(Intent.class);
        when(mockIntent.getAction()).thenReturn(NfcWeblinkReceiver.ACTION_WEBLINK_CANCEL);

        mReceiver.onReceive(mContext, mockIntent);

        verify(mNotificationManager).cancel(eq(NfcWeblinkNotification.NOTIFICATION_ID_NFC));
        verify(mPendingIntent, org.mockito.Mockito.never()).send();
    }
}

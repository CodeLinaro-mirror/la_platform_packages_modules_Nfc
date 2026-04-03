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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Process;
import android.util.DisplayMetrics;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android.dx.mockito.inline.extended.ExtendedMockito;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.MockitoSession;
import org.mockito.quality.Strictness;


@RunWith(AndroidJUnit4.class)
public class NfcWeblinkNotificationTest {

    private static final String TAG = NfcWeblinkNotificationTest.class.getSimpleName();
    private MockitoSession mStaticMockSession;
    private Context mMockContext;
    private NfcWeblinkNotification mWeblinkNotification;
    private NotificationManager mMockNotificationManager;

    @Before
    public void setUp() throws Exception {
        mStaticMockSession = ExtendedMockito.mockitoSession()
                .mockStatic(PendingIntent.class)
                .strictness(Strictness.LENIENT)
                .startMocking();
        mMockNotificationManager = Mockito.mock(NotificationManager.class);
        Resources mockResources = Mockito.mock(Resources.class);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        displayMetrics.setToDefaults();
        when(mockResources.getDisplayMetrics()).thenReturn(displayMetrics);
        ApplicationInfo mockApplicationInfo = new ApplicationInfo();
        PackageManager mockPackageManager = Mockito.mock(PackageManager.class);
        when(mockPackageManager.getApplicationLabel(any())).thenReturn("test_label");

        mMockContext = Mockito.mock(Context.class);
        when(mMockContext.getSystemService(Context.NOTIFICATION_SERVICE))
                .thenReturn(mMockNotificationManager);
        when(mMockContext.getSystemService(NotificationManager.class))
                .thenReturn(mMockNotificationManager);
        when(mMockContext.getResources()).thenReturn(mockResources);
        when(mMockContext.getPackageManager()).thenReturn(mockPackageManager);
        when(mMockContext.getString(anyInt())).thenReturn("test_string");
        when(mMockContext.getApplicationInfo()).thenReturn(mockApplicationInfo);
        when(mMockContext.getPackageName()).thenReturn("com.android.nfc");
        when(mMockContext.getUser()).thenReturn(Process.myUserHandle());

        PendingIntent mockBroadcastPendingIntent = Mockito.mock(PendingIntent.class);
        when(PendingIntent.getBroadcast(any(), anyInt(), any(), anyInt()))
                .thenReturn(mockBroadcastPendingIntent);

        PendingIntent mockPendingIntent = Mockito.mock(PendingIntent.class);
        mWeblinkNotification = new NfcWeblinkNotification(
                mMockContext, "https://example.com", mockPendingIntent);
        Assert.assertNotNull(mWeblinkNotification);
    }

    @After
    public void tearDown() throws Exception {
        mStaticMockSession.finishMocking();
    }

    @Test
    public void testStartNotification() {
        mWeblinkNotification.startNotification();
        ArgumentCaptor<NotificationChannel> channelCaptor =
                ArgumentCaptor.forClass(NotificationChannel.class);
        verify(mMockNotificationManager).createNotificationChannel(channelCaptor.capture());
        NotificationChannel channel = channelCaptor.getValue();
        Assert.assertEquals("nfc_weblink_channel", channel.getId());
        Assert.assertEquals("test_string", channel.getName().toString());
        Assert.assertEquals(NotificationManager.IMPORTANCE_HIGH, channel.getImportance());
        verify(mMockNotificationManager).notify(
                eq(NfcWeblinkNotification.NOTIFICATION_ID_NFC), any());
    }
}

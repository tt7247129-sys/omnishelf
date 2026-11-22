package com.foobnix.model;

import android.content.Context;
import android.os.RemoteException;

import com.android.installreferrer.api.InstallReferrerClient;
import com.android.installreferrer.api.InstallReferrerStateListener;
import com.android.installreferrer.api.ReferrerDetails;
import com.foobnix.android.utils.LOG;

/**
 * Records where an install came from, once, without collecting anything else.
 *
 * <p>The site links to the Play listing with a <code>referrer</code> parameter carrying
 * <code>utm_source / utm_medium / utm_campaign / utm_content</code>. Play's Install Referrer API
 * hands that string back on the first launch after install. This class asks for it once,
 * stores it beside the other app state, and never opens a network connection of its own -
 * there is no analytics SDK here, and the referrer never leaves the device.
 *
 * <p>Outside a Play installation (a sideloaded build, an emulator without Play) the API simply
 * fails, which is expected: attribution is a bonus for store installs, not a requirement.
 */
public final class InstallAttribution {

    private static final String TAG = "InstallAttribution";

    private InstallAttribution() {
    }

    /** Called once from the first activity; does nothing after the first successful attempt. */
    public static void captureOnce(final Context context) {
        final AppState state = AppState.get();
        if (state.installReferrerChecked) {
            return;
        }
        state.installReferrerChecked = true;
        if (!isPlayInstall(context)) {
            LOG.d(TAG, "not a Play install, no referrer to read");
            return;
        }
        try {
            final InstallReferrerClient client = InstallReferrerClient.newBuilder(context).build();
            client.startConnection(new InstallReferrerStateListener() {
                @Override
                public void onInstallReferrerSetupFinished(int responseCode) {
                    try {
                        if (responseCode == InstallReferrerClient.InstallReferrerResponse.OK) {
                            ReferrerDetails details = client.getInstallReferrer();
                            String referrer = details == null ? null : details.getInstallReferrer();
                            if (referrer != null && referrer.length() > 0) {
                                state.installReferrer = truncate(referrer);
                                LOG.d(TAG, "install referrer", state.installReferrer);
                            }
                        } else {
                            LOG.d(TAG, "referrer unavailable, code " + responseCode);
                        }
                        client.endConnection();
                    } catch (RemoteException | RuntimeException e) {
                        LOG.e(e);
                    }
                }

                @Override
                public void onInstallReferrerServiceDisconnected() {
                    LOG.d(TAG, "referrer service disconnected");
                }
            });
        } catch (Throwable e) {
            // Missing Play services, an emulator, a sideload: nothing to attribute, no crash.
            LOG.e(e);
        }
    }

    /** Play is the only installer that answers the referrer API; anything else is a sideload. */
    private static boolean isPlayInstall(Context context) {
        try {
            String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());
            return "com.android.vending".equals(installer);
        } catch (Throwable e) {
            return false;
        }
    }

    private static String truncate(String referrer) {
        return referrer.length() <= 512 ? referrer : referrer.substring(0, 512);
    }
}

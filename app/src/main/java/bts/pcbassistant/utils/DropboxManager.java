package bts.pcbassistant.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.android.Auth;
import com.dropbox.core.http.OkHttp3Requestor;
import com.dropbox.core.v2.DbxClientV2;

import bts.pcbassistant.R;

/**
 * Created by a on 2017-05-21.
 */

public class DropboxManager {

    final public static int STATE_NONE = 0;
    final public static int STATE_WAITING_OAUTH = 1;
    final public static int STATE_OK = 2;
    final public static int STATE_CANCELLED = 3;

    public static DbxClientV2 getClient() {
        return sDbxClient;
    }

    public static boolean clientReady() {
        return (sDbxClient != null);
    }

    private static DbxClientV2 sDbxClient;

    static {
        sDbxClient = null;
    }

    public static int getState() {
        return state;
    }

    private static int state = STATE_NONE;

    /* kasuj identyfikator sesji, wymuś logowanie */
    public static void resetSession(Context ctx) {
        SharedPreferences prefs = ctx.getSharedPreferences("dropbox", Context.MODE_PRIVATE);
        prefs.edit().remove("access-token1").commit();

        sDbxClient = null;
        state = STATE_NONE;
        //https://stackoverflow.com/questions/41053675/how-to-disconnect-one-dropbox-account-from-the-app
        com.dropbox.core.android.AuthActivity.result = null;
    }

    public static boolean hasToken(Context ctx) {
        if (sDbxClient == null) {
            SharedPreferences prefs = ctx.getSharedPreferences("dropbox", Context.MODE_PRIVATE);
            String accessToken = prefs.getString("access-token1", null);
            if (accessToken == null) {
                return (Auth.getOAuth2Token() != null);
            }
            return true;
        }
        return true;
    }

    public static int checkInitialization(Context ctx) {
        if (sDbxClient == null) {

            SharedPreferences prefs = ctx.getSharedPreferences("dropbox", Context.MODE_PRIVATE);
            String accessToken = prefs.getString("access-token1", null);

            if (accessToken == null) {
                accessToken = Auth.getOAuth2Token();
                if (accessToken != null) {
                    Editor editor = prefs.edit();
                    editor.putString("access-token1", accessToken);
                    editor.commit();
                    state = STATE_OK;
                } else {
                    if (state == STATE_WAITING_OAUTH) {
                        //anulowano logowanie?
                        state = STATE_NONE;
                    } else
                    if (state == STATE_NONE) {
                        //logowanie
                        state = STATE_WAITING_OAUTH;
                        Auth.startOAuth2Authentication(ctx, ctx.getString(R.string.dropbox_app_key));
                    }
                    return state;
                }

            } else {
                state = STATE_OK;
            }

            DbxRequestConfig requestConfig = DbxRequestConfig.newBuilder(ctx.getString(R.string.app_name))
                    .withHttpRequestor(new OkHttp3Requestor(OkHttp3Requestor.defaultOkHttpClient()))
                    .build();

            sDbxClient = new DbxClientV2(requestConfig, accessToken);
        }
        return state;
    }
}

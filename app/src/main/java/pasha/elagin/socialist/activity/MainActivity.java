package pasha.elagin.socialist.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkListener;
import com.vk.sdk.VKUIHelper;
import com.vk.sdk.api.VKError;
import com.vk.sdk.dialogs.VKCaptchaDialog;

import org.json.JSONException;
import org.json.JSONObject;

import pasha.elagin.socialist.Const;
import pasha.elagin.socialist.MyApp;
import pasha.elagin.socialist.MyIntentService;
import pasha.elagin.socialist.R;

public class MainActivity extends AppCompatActivity {

    private static final String sTokenKey = "VK_ACCESS_TOKEN_FULL";
    private static final String[] sMyScope = new String[]{VKScope.WALL,VKScope.FRIENDS};
    private final String appID = "5143648";
    private static final String CLASS_TAG = "MainActivity";

    private MyApp myApp = null;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VKUIHelper.onCreate(this);
        myApp = (MyApp) getApplicationContext();
        context = getApplicationContext();
        setContentView(R.layout.activity_main);

        // The filter's action is BROADCAST_ACTION
        IntentFilter statusIntentFilter = new IntentFilter(Const.BROADCAST_ACTION);

        // Sets the filter's category to DEFAULT
        statusIntentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        // Instantiates a new ResponseStateReceiver
        ResponseStateReceiver mDownloadStateReceiver = new ResponseStateReceiver();

        // Registers the ResponseStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(mDownloadStateReceiver, statusIntentFilter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        VKSdk.initialize(sdkListener, appID, VKAccessToken.tokenFromSharedPreferences(this, sTokenKey));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        VKSdk.processActivityResult(VKSdk.VK_SDK_REQUEST_CODE, resultCode, data);
    }

    private void getVKUserInfo() {
        MyIntentService.startActionGetUserInfoVKRequest(this, myApp.getPreferences().getVkToken());
    }

    private void NewsfeedGetVKRequest() {
        MyIntentService.startActionNewsfeedGetVKRequest(this, myApp.getPreferences().getVkToken());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_newsfeed_get_vk_requestequest) {
            MyIntentService.startActionNewsfeedGetVKRequest(context, myApp.getPreferences().getVkToken());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final VKSdkListener sdkListener = new VKSdkListener() {
        @Override
        public void onCaptchaError(VKError captchaError) {
            new VKCaptchaDialog(captchaError).show();
        }

        @Override
        public void onTokenExpired(VKAccessToken expiredToken) {
            VKSdk.authorize(sMyScope);
        }

        @Override
        public void onAccessDenied(VKError authorizationError) {
            new AlertDialog.Builder(MainActivity.this)
                    .setMessage(authorizationError.errorMessage)
                    .show();
        }

        @Override
        public void onReceiveNewToken(VKAccessToken newToken) {
            newToken.saveTokenToSharedPreferences(context, sTokenKey);
            myApp.getPreferences().setVkToken(newToken.accessToken);
        }

        // Вызывается после VKSdk.authorize, но до отображения окна VK.
        // Так что на этом этапе не понятно, авторизовался ли юзер успешно.
        @Override
        public void onAcceptUserToken(VKAccessToken token) {
            //TODO верятно сохранять по новой не нужно, токен-то старый
            myApp.getPreferences().setVkToken(token.accessToken);
            //myApp.getSession().collectData();
        }

        public void onRenewAccessToken(VKAccessToken token) {
            onReceiveNewToken(token);
        }
    };

    private class ResponseStateReceiver extends BroadcastReceiver {
        private ResponseStateReceiver() {
            // prevents instantiation by other packages.
        }

        /**
         * This method is called by the system when a broadcast Intent is matched by this class'
         * intent filters
         *
         * @param context An Android context
         * @param intent  The incoming broadcast Intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(CLASS_TAG, "onReceive");
            int resultCode = intent.getIntExtra(MyIntentService.RESULT_CODE, 0);
            if (resultCode == MyIntentService.RESULT_SUCCSESS) {
                switch (intent.getStringExtra(Const.EXTENDED_OPERATION_TYPE)) {
                    case MyIntentService.ACTION_GET_USER_INFO_VK:
                        String userInfo = intent.getStringExtra(MyIntentService.RESULT);
                        if (userInfo != null) {
                            try {
                                JSONObject res = new JSONObject(userInfo);
                                String userName = res.getString("userName");
                                String versionName = res.getString("versionName");
                                MyIntentService.startActionGetUserInfoVKRequest(context, myApp.getPreferences().getVkToken());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case MyIntentService.ACTION_NEWSFEED_GET_VK:

                        break;
                    default:
                        break;
                }
            } else if (resultCode == MyIntentService.RESULT_ERROR) {
                String error = intent.getStringExtra(MyIntentService.RESULT);
                Toast.makeText(context, error, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "В onReceiveResult пришло не понятно что.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        VKUIHelper.onResume(this);

//        VKSdk.authorize(sMyScope, true, true);

        if (VKSdk.wakeUpSession()) {
//            //myApp.getSession().collectData();
            MyIntentService.startActionNewsfeedGetVKRequest(context, myApp.getPreferences().getVkToken());
//            //MyIntentService.startActionGetUserInfoVKRequest(context, myApp.getPreferences().getVkToken());
//            //MyIntentService.startActionIsOpenMemberVKRequest(this, myApp.getPreferences().getVkToken());
        } else {
            VKSdk.authorize(sMyScope, true, true);
        }
    }
}

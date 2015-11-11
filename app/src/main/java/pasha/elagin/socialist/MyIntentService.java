package pasha.elagin.socialist;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import pasha.elagin.socialist.DataSource.Vk.VKNewsfeedItem;
import pasha.elagin.socialist.network.Vk.GetUserInfo;
import pasha.elagin.socialist.network.Vk.NewsfeedGet;
import pasha.elagin.socialist.network.Vk.RequestErrors;

/**
 * Created by elagin on 11.11.15.
 */
public class MyIntentService extends IntentService {
    private MyApp myApp;

    public static final String ACTION_GET_USER_INFO_VK = "pasha.elagin.socialist.action.GetUserInfoVK";
    public static final String ACTION_NEWSFEED_GET_VK = "pasha.elagin.socialist.action.NewsfeedGetVK";

    private static final String ACCESS_TOKEN_VK = "access_token";

    public static final String RESULT_CODE = "result_code";

    public final static int RESULT_SUCCSESS = 0;
    public final static int RESULT_ERROR = 1;

    private static final String USER_ID = "userID";
    private static final String USER_NAME = "userName";
    private static final String VERSION_NAME = "versionName";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String GROUP_ID = "group_id";

    private static final String POINT = "point";
    private static final String MEMBER_GROUP = "memberGroup";

    public static final String RESULT = "RESULT";


    private final BroadcastNotifier mBroadcaster = new BroadcastNotifier(this);

    public MyIntentService() {
        super("MyIntentService");
    }

    public void onCreate() {
        super.onCreate();
        myApp = (MyApp) getApplicationContext();
    }

    private static Intent newIntent(Context context, String action) {
        Intent res = new Intent(context, MyIntentService.class);
        res.setAction(action);
        return res;
    }

    public static void startActionGetUserInfoVKRequest(Context context, String token) {
        Intent intent = newIntent(context, ACTION_GET_USER_INFO_VK);
        intent.putExtra(ACCESS_TOKEN_VK, token);
        context.startService(intent);
    }

    public static void startActionNewsfeedGetVKRequest(Context context, String token) {
        Intent intent = newIntent(context, ACTION_NEWSFEED_GET_VK);
        intent.putExtra(ACCESS_TOKEN_VK, token);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String action = intent.getAction();
        switch (action) {
            case ACTION_GET_USER_INFO_VK:
                JSONObject userInfo = handleGetUserInfoVKRequest(intent);
                if (RequestErrors.isVkError(userInfo)) {
                    returnError(userInfo, action);
                } else {
                    try {
                        JSONArray resArr = (JSONArray) userInfo.get("response");
                        JSONObject resp = (JSONObject) resArr.get(0);
                        final String userName = resp.getString("first_name") + " " + resp.getString("last_name");
                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        JSONObject userInfoJ = new JSONObject().put("userName", userName).put("versionName", pInfo.versionName);
                        mBroadcaster.broadcastIntentWithState(action, RESULT_SUCCSESS, userInfoJ.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mBroadcaster.broadcastIntentWithState(action, RESULT_ERROR, e.getLocalizedMessage());
                    } catch (PackageManager.NameNotFoundException pme) {
                        pme.printStackTrace();
                        mBroadcaster.broadcastIntentWithState(action, RESULT_ERROR, pme.getLocalizedMessage());
                    }
                }
                break;
            case ACTION_NEWSFEED_GET_VK:
                JSONObject newsfeedGetVK = handleNewsfeedGetVKRequest(intent);
                if (RequestErrors.isVkError(newsfeedGetVK)) {
                    returnError(newsfeedGetVK, action);
                } else {
                    try {
                        //JSONArray resArr = (JSONArray) newsfeedGetVK.get("response");
                        JSONObject res = newsfeedGetVK.getJSONObject("response");
                        JSONArray items = res.getJSONArray("items");

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = (JSONObject) items.get(i);
                            String dateStr = item.getString("date");
                            Date date = new Date(Integer.decode(dateStr) * 1000);
                            VKNewsfeedItem feedItem = new VKNewsfeedItem(date, item.getString("text"));
                            myApp.addNewsfeedItemList(feedItem);
                        }
                        mBroadcaster.broadcastIntentWithState(action, RESULT_SUCCSESS, "OK");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mBroadcaster.broadcastIntentWithState(action, RESULT_ERROR, e.getLocalizedMessage());
//                    } catch (PackageManager.NameNotFoundException pme) {
//                        pme.printStackTrace();
//                        mBroadcaster.broadcastIntentWithState(action, RESULT_ERROR, pme.getLocalizedMessage());
                    }
                }
                break;
            default:
                mBroadcaster.broadcastIntentWithState(action, RESULT_SUCCSESS, "");
                break;
        }
    }

    private JSONObject handleGetUserInfoVKRequest(Intent intent) {
        final String access_token = intent.getStringExtra(ACCESS_TOKEN_VK);
        return new GetUserInfo(this, access_token).request();
    }

    private JSONObject handleNewsfeedGetVKRequest(Intent intent) {
        final String access_token = intent.getStringExtra(ACCESS_TOKEN_VK);
        return new NewsfeedGet(this, access_token).request();
    }

    private void returnError(JSONObject response, String action) {
        //{"error":{"error_code":5,"error_msg":"User authorization failed: no access_token passed.","request_params":[{"value":"1","key":"oauth"},{"value":"newsfeed.get","key":"method"},{"value":"post","key":"filters"}]}}
        mBroadcaster.broadcastIntentWithState(action, RESULT_ERROR, RequestErrors.getError(response));
    }
}

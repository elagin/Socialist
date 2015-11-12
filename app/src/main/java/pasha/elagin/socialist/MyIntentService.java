package pasha.elagin.socialist;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import pasha.elagin.socialist.DataSource.Vk.VKGroup;
import pasha.elagin.socialist.DataSource.Vk.VKNewsfeedItem;
import pasha.elagin.socialist.network.Vk.GetUserInfo;
import pasha.elagin.socialist.network.Vk.NewsfeedGet;
import pasha.elagin.socialist.network.Vk.RequestErrors;
import pasha.elagin.socialist.network.Vk.VKGroupsGetById;

/**
 * Created by elagin on 11.11.15.
 */
public class MyIntentService extends IntentService {
    private MyApp myApp;
    public static final String ACTION_PREFIX = "pasha.elagin.socialist.action.";

    public static final String ACTION_GET_USER_INFO_VK = ACTION_PREFIX + "GetUserInfoVK";
    public static final String ACTION_NEWSFEED_GET_VK = ACTION_PREFIX + "NewsfeedGetVK";
    public static final String ACTION_VK_GROUPS_GET_BY_ID = ACTION_PREFIX + "VKGroupsGetByIdRequest";

    private static final String ACCESS_TOKEN_VK = "access_token";
    private static final String GROUP_IDS = "group_ids";

    public static final String RESULT_CODE = "result_code";

    public final static int RESULT_SUCCSESS = 0;
    public final static int RESULT_ERROR = 1;
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

    public static void startActionVKGroupsGetByIdRequest(Context context, String token, String groupIds) {
        Intent intent = newIntent(context, ACTION_VK_GROUPS_GET_BY_ID);
        intent.putExtra(ACCESS_TOKEN_VK, token);
        intent.putExtra(GROUP_IDS, groupIds);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final String action = intent.getAction();
        Log.d(getClass().toString(), action);
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
                        JSONObject res = newsfeedGetVK.getJSONObject("response");
                        JSONArray items = res.getJSONArray("items");

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = (JSONObject) items.get(i);
                            String dateStr = item.getString("date");
                            String source_id = item.getString("source_id");
                            long dateLong = Long.decode(dateStr) * 1000;
                            Date date = new Date(dateLong);

//                            JSONArray attachments = item.getJSONArray("attachments");
//                            if(attachments != null) {
//                                JSONObject att = (JSONObject) attachments.get(0);
//                            }
                            VKNewsfeedItem feedItem = new VKNewsfeedItem(date, item.getString("text"), source_id);
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

            case ACTION_VK_GROUPS_GET_BY_ID:
                JSONObject groups = handleVKGroupsGetByIdRequest(intent);
                if (RequestErrors.isVkError(groups)) {
                    returnError(groups, action);
                } else {
                    try {
                        JSONArray res = groups.getJSONArray("response");
                        for (int i = 0; i < res.length(); i++) {
                            JSONObject item = (JSONObject) res.get(i);

                            String id = item.getString("gid");
                            String name = item.getString("name");
                            String photo50 = item.getString("photo");
                            VKGroup group = new VKGroup(id, name, photo50);

                            for (int j = 0; j < myApp.getNewsfeedItemList().size(); j++) {
                                VKNewsfeedItem feedItem = myApp.getNewsfeedItemList().get(j);
                                if (feedItem.getSourceID().equals("-" + id))
                                    feedItem.setSourceName(name);
                            }
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

    private JSONObject handleVKGroupsGetByIdRequest(Intent intent) {
        final String access_token = intent.getStringExtra(ACCESS_TOKEN_VK);
        final String groupIds = intent.getStringExtra(GROUP_IDS);
        return new VKGroupsGetById(this, access_token, groupIds).request();
    }

    private void returnError(JSONObject response, String action) {
        //{"error":{"error_code":5,"error_msg":"User authorization failed: no access_token passed.","request_params":[{"value":"1","key":"oauth"},{"value":"newsfeed.get","key":"method"},{"value":"post","key":"filters"}]}}
        Log.e(getClass().toString(), response.toString());
        mBroadcaster.broadcastIntentWithState(action, RESULT_ERROR, RequestErrors.getError(response));
    }
}

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

import pasha.elagin.socialist.DataSource.Vk.Store;
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
    private static final String GROUP_IDS_VK = "group_ids";
    private static final String START_FROM_VK = "start_from";

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

    public static void startActionNewsfeedGetVKRequest(Context context, String token, String startFrom) {
        Intent intent = newIntent(context, ACTION_NEWSFEED_GET_VK);
        intent.putExtra(ACCESS_TOKEN_VK, token);
        intent.putExtra(START_FROM_VK, startFrom);
        context.startService(intent);
    }

    public static void startActionVKGroupsGetByIdRequest(Context context, String token, String groupIds) {
        Intent intent = newIntent(context, ACTION_VK_GROUPS_GET_BY_ID);
        intent.putExtra(ACCESS_TOKEN_VK, token);
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
                        myApp.getPreferences().setVKFeedStartFrom(res.getString("new_from"));
                        myApp.getPreferences().setVKFeedNewOffset(res.getString("new_offset"));

                        parseVKGroups(res.getJSONArray("groups"));

                        for (int i = 0; i < items.length(); i++) {
                            JSONObject item = (JSONObject) items.get(i);
                            String dateStr = item.getString("date");
                            String sourceID = item.getString("source_id");
                            String sourseName = "";
                            String sourseAvatar = "";
                            long dateLong = Long.decode(dateStr) * 1000;
                            Date date = new Date(dateLong);

                            if (sourceID.startsWith("-")) {
                                String srcID = sourceID.substring(1, sourceID.length());
                                for (int j = 0; j < Store.getGroups().size(); j++) {
                                    VKGroup group = Store.getGroups().get(j);
                                    if (group.getId().equals(srcID)) {
                                        sourseName = group.getName();
                                        sourseAvatar = group.getPhoto50();
                                        break;
                                    }
                                }
                            }
                            VKNewsfeedItem feedItem = new VKNewsfeedItem(date, item.getString("text"), sourceID, sourseName, sourseAvatar);
                            myApp.addNewsfeedItemList(feedItem);
                        }
                        mBroadcaster.broadcastIntentWithState(action, RESULT_SUCCSESS, "OK");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mBroadcaster.broadcastIntentWithState(action, RESULT_ERROR, e.getLocalizedMessage());
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

                            VKGroup group = new VKGroup(item.getString("gid"), item.getString("name"), item.getString("photo"));
                            Store.getGroups().add(group);


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
        final String accessToken = intent.getStringExtra(ACCESS_TOKEN_VK);
        final String startFrom = intent.getStringExtra(START_FROM_VK);
        return new NewsfeedGet(this, accessToken, startFrom).request();
    }

    private JSONObject handleVKGroupsGetByIdRequest(Intent intent) {
        final String access_token = intent.getStringExtra(ACCESS_TOKEN_VK);
        final String groupIds = intent.getStringExtra(GROUP_IDS_VK);
        return new VKGroupsGetById(this, access_token, groupIds).request();
    }

    private void returnError(JSONObject response, String action) {
        Log.e(getClass().toString(), response.toString());
        System.out.print(response.toString());
        mBroadcaster.broadcastIntentWithState(action, RESULT_ERROR, RequestErrors.getError(response));
    }

    private void parseVKGroups(JSONArray groups) {
        for (int i = 0; i < groups.length(); i++) {
            JSONObject group = null;
            try {
                group = (JSONObject) groups.get(i);
                VKGroup VKgroup = new VKGroup(group.getString("gid"), group.getString("name"), group.getString("photo"));
                Store.getGroups().add(VKgroup);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

package pasha.elagin.socialist;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by elagin on 11.11.15.
 */
public class MyPreferences {

    private final static String VK_TOKEN_KEY = "vk_access_token";
    private static SharedPreferences preferences;
    private static Context context;

    public MyPreferences(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        MyPreferences.context = context;
    }

    public String getVkToken() {
        return preferences.getString(VK_TOKEN_KEY, "");
    }

    public void setVkToken(String value) {
        preferences.edit().putString(VK_TOKEN_KEY, value).commit();
    }
}

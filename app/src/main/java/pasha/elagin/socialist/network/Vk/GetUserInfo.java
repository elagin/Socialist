package pasha.elagin.socialist.network.Vk;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by elagin on 11.11.15.
 */
public class GetUserInfo extends VKHTTPClient{
    public GetUserInfo(Context context, String access_token) {
        this.context = context;
        post = new HashMap<>();
        post.put("method", "users.get ");
        post.put("access_token", access_token);
    }
}


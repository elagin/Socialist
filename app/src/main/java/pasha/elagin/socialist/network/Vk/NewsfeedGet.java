package pasha.elagin.socialist.network.Vk;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by elagin on 11.11.15.
 */
public class NewsfeedGet extends VKHTTPClient {

    public NewsfeedGet(Context context, String access_token) {
        this.context = context;
        post = new HashMap<>();
        post.put("method", "newsfeed.get ");
        post.put("filters", "post");
    }
}

package pasha.elagin.socialist.network.Vk;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by elagin on 11.11.15.
 */
public class NewsfeedGet extends VKHTTPClient {

    public NewsfeedGet(Context context, String access_token, String startFrom) {
        this.context = context;
        post = new HashMap<>();
        post.put("method", "newsfeed.get");
        post.put("access_token", access_token);
        post.put("filters", "post");
        post.put("count", "8");
        if (!startFrom.isEmpty())
            post.put("from", startFrom);
    }
}

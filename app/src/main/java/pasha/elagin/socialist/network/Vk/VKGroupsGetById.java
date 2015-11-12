package pasha.elagin.socialist.network.Vk;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by pavel on 12.11.15.
 */
public class VKGroupsGetById extends VKHTTPClient {

    public VKGroupsGetById(Context context, String access_token, String groupIds) {
        this.context = context;
        post = new HashMap<>();
        post.put("method", "groups.getById");
        post.put("access_token", access_token);
        post.put("group_ids", groupIds);
    }
}

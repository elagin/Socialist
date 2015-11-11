package pasha.elagin.socialist;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import pasha.elagin.socialist.DataSource.Vk.VKNewsfeedItem;

/**
 * Created by elagin on 11.11.15.
 */
public class MyApp extends Application {

    private MyPreferences prefs = null;
    private List<VKNewsfeedItem> newsfeedItemList;

    public MyApp() {
    }

    public MyPreferences getPreferences() {
        if (prefs == null)
            prefs = new MyPreferences(getApplicationContext());
        return prefs;
    }

    public List<VKNewsfeedItem> getNewsfeedItemList() {
        if (newsfeedItemList == null)
            newsfeedItemList = new ArrayList<>();
        return newsfeedItemList;
    }

    public void addNewsfeedItemList(VKNewsfeedItem newsfeedItem) {
        if (newsfeedItemList == null)
            newsfeedItemList = new ArrayList<>();
        newsfeedItemList.add(newsfeedItem);
    }
}

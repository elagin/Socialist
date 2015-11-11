package pasha.elagin.socialist;

import android.app.Application;

/**
 * Created by elagin on 11.11.15.
 */
public class MyApp extends Application {

    private MyPreferences prefs = null;

    public MyApp() {
    }

    public MyPreferences getPreferences() {
        if (prefs == null)
            prefs = new MyPreferences(getApplicationContext());
        return prefs;
    }
}

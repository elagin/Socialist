package pasha.elagin.socialist.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import pasha.elagin.socialist.DataSource.Vk.VKNewsfeedItem;
import pasha.elagin.socialist.MyApp;
import pasha.elagin.socialist.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private View messagesTable;
    private MyApp myApp;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewMain = inflater.inflate(R.layout.fragment_main, container, false);
        messagesTable = viewMain.findViewById(R.id.messages_table);

        myApp = (MyApp) getActivity().getApplicationContext();
        update();
        return viewMain;

    }

    public void update() {
        ViewGroup messageView = (ViewGroup) messagesTable;
        final TableLayout messagesTableLayout = (TableLayout) messagesTable;

        messageView.removeAllViews();
        for (int i = 0; i < myApp.getNewsfeedItemList().size(); i++) {
            VKNewsfeedItem item = myApp.getNewsfeedItemList().get(i);
            item.inflateRow(getActivity(), messageView);
        }
    }
}

package pasha.elagin.socialist.DataSource.Vk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Date;

import pasha.elagin.socialist.MyUtils;
import pasha.elagin.socialist.R;

/**
 * Created by elagin on 11.11.15.
 */
public class VKNewsfeedItem {

    private Date date;
    private String text;
    private String source_id;
    private String sourceName;

    public VKNewsfeedItem(Date date, String text, String source_id) {
        this.date = date;
        this.text = text;
        this.source_id = source_id;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Date getDate() {
        return date;
    }

    public String getText() {
        return text;
    }

    public String getSourceID() {
        return source_id;
    }

    public String getSourceName() {
        return sourceName;
    }
}

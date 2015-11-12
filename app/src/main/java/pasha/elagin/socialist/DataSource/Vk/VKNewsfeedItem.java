package pasha.elagin.socialist.DataSource.Vk;

import java.util.Date;

/**
 * Created by elagin on 11.11.15.
 */
public class VKNewsfeedItem {

    private Date date;
    private String text;
    private String source_id;
    private String sourceName;

    private String sourceAvatar;

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

    public String getSourceAvatar() {
        return sourceAvatar;
    }

    public void setSourceAvatar(String sourceAvatar) {
        this.sourceAvatar = sourceAvatar;
    }

}

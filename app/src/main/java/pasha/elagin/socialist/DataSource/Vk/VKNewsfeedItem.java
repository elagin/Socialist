package pasha.elagin.socialist.DataSource.Vk;

import java.util.Date;

/**
 * Created by elagin on 11.11.15.
 */
public class VKNewsfeedItem {

    private Date date;
    private String text;
    private String sourceID;
    private String sourceName;
    private String sourceAvatar;

    public VKNewsfeedItem(Date date, String text, String sourceID, String sourceName, String sourceAvatar) {
        this.date = date;
        this.text = text;
        this.sourceID = sourceID;
        this.sourceName = sourceName;
        this.sourceAvatar = sourceAvatar;
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
        return sourceID;
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

package pasha.elagin.socialist.DataSource.Vk;

/**
 * Created by pavel on 12.11.15.
 */
public class VKGroup {
    private String id;
    private String name;
    private String photo50;

    public VKGroup(String id, String name, String photo50) {
        this.id = id;
        this.name = name;
        this.photo50 = photo50;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoto50() {
        return photo50;
    }
}

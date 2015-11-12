package pasha.elagin.socialist.DataSource.Vk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by elagin on 12.11.15.
 */
public class Store {

    private static List<VKGroup> groups;

    public static List<VKGroup> getGroups() {
        if (groups == null)
            groups = new ArrayList<>();
        return groups;
    }
}

package tfar.nations3.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TownPermissions {

    private static final Map<String,TownPermission> PERMISSIONS = new HashMap<>();

    public static final TownPermission MANAGE_CLAIMS = register("manage_claims");
    public static final TownPermission MANAGE_PERSONAL_TAX = register("manage_personal_tax");

    public static TownPermission register(String name) {
        TownPermission townPermission = new TownPermission(name);
        PERMISSIONS.put(name,townPermission);
        return townPermission;
    }

    public static Collection<TownPermission> getAllPermissions() {
        return PERMISSIONS.values();
    }

    public static TownPermission getPermission(String name) {
        return PERMISSIONS.get(name);
    }

}

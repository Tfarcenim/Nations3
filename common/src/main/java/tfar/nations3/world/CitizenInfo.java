package tfar.nations3.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.HashSet;
import java.util.Set;

public class CitizenInfo {
    public long money;
    Set<TownPermission> permissions;
    public CitizenInfo() {
        this(0,new HashSet<>());
    }

    public CitizenInfo(long money,Set<TownPermission> permissions) {
        this.money = money;
        this.permissions = permissions;
    }

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        ListTag permissionTag = new ListTag();
        for (TownPermission townPermission : permissions) {
            permissionTag.add(StringTag.valueOf(townPermission.key()));
        }
        tag.put("permissions",permissionTag);
        tag.putLong("money",money);
        return tag;
    }

    public static CitizenInfo fromTag(CompoundTag tag) {
        ListTag listTag = tag.getList("permissions", StringTag.TAG_STRING);
        Set<TownPermission> permissions = new HashSet<>();
        for (Tag tag1 : listTag) {
            String string = tag1.getAsString();
            TownPermission permission = TownPermissions.getPermission(string);
            permissions.add(permission);
        }
        long money = tag.getLong("money");
        return new CitizenInfo(money,permissions);
    }

}

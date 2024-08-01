package tfar.nations3.world;

import net.minecraft.network.FriendlyByteBuf;

import java.util.Objects;

public record TownInfo(String name, Relations relations, boolean isNation) {

    public void toPacket(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        buf.writeInt(relations.ordinal());
        buf.writeBoolean(isNation);
    }

    public static TownInfo fromPacket(FriendlyByteBuf buf) {
        return new TownInfo(buf.readUtf(),Relations.values()[buf.readInt()],buf.readBoolean());
    }

    public int getColor() {
        if (this.equals(ClaimDisplay.WILDERNESS)) {
            return 0x00ffffff;
        }
        return 0xff00ff00;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TownInfo townInfo = (TownInfo) o;
        return isNation == townInfo.isNation && Objects.equals(name, townInfo.name) && relations == townInfo.relations;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, relations, isNation);
    }
}

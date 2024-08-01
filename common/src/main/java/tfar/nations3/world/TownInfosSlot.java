package tfar.nations3.world;

import java.util.Objects;

public abstract class TownInfosSlot {
    private TownInfo prevValue;

    public static TownInfosSlot forContainer(final TownInfos infos, final int pIdx) {
        return new TownInfosSlot() {
            public TownInfo get() {
                return infos.get(pIdx);
            }

            public void set(TownInfo p_39416_) {
                infos.set(pIdx, p_39416_);
            }
        };
    }

    public static TownInfosSlot shared(final TownInfo[] pData, final int pIdx) {
        return new TownInfosSlot() {
            public TownInfo get() {
                return pData[pIdx];
            }

            public void set(TownInfo p_39424_) {
                pData[pIdx] = p_39424_;
            }
        };
    }

    public static TownInfosSlot standalone() {
        return new TownInfosSlot() {
            private TownInfo value;

            public TownInfo get() {
                return this.value;
            }

            public void set(TownInfo p_39429_) {
                this.value = p_39429_;
            }
        };
    }

    public abstract TownInfo get();

    public abstract void set(TownInfo pValue);

    public boolean checkAndClearUpdateFlag() {
        TownInfo i = this.get();
        boolean flag = !Objects.equals(i ,this.prevValue);
        this.prevValue = i;
        return flag;
    }
}

package tfar.nations3.world;

public interface TownInfos {
    TownInfo get(int pIndex);

    void set(int pIndex, TownInfo pValue);

    int getCount();
}

package tfar.nations3.world;

public class ClientTownInfos implements TownInfos{
    private final TownInfo[] infos;

    public ClientTownInfos(int size) {
        this.infos = new TownInfo[size];
    }

    @Override
    public TownInfo get(int pIndex) {
        return infos[pIndex];
    }

    @Override
    public void set(int pIndex, TownInfo pValue) {
        this.infos[pIndex] = pValue;
    }

    @Override
    public int getCount() {
        return infos.length;
    }
}

package tfar.nations3.world;

public enum Relations {
    WILDERNESS(0x00ffffff),OWN(0xff00ff00),NEUTRAL(0xff00ffff),FRIENDLY(0xffffff00),HOSTILE(0xff0000ff);

    /**
     * agbr
     */
    public final int color;

    Relations(int color) {

        this.color = color;
    }

}

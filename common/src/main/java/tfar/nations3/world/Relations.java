package tfar.nations3.world;

public enum Relations {
    WILDERNESS(0x00ffffff),//clear
    OWN(0xff00ff00),//green
    NEUTRAL(0xffffff00),//yellow
    FRIENDLY(0xff00ffff),//cyan
    HOSTILE(0xffff0000);//red

    /**
     * argb
     */
    public final int color;

    Relations(int color) {

        this.color = color;
    }

}

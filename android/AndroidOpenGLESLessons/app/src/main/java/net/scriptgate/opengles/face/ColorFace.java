package net.scriptgate.opengles.face;

import net.scriptgate.common.Color;

public class ColorFace extends Face<Color> {
    public ColorFace(Color p1, Color p2, Color p3, Color p4) {
        super(p1, p2, p3, p4);
    }

    public ColorFace(Color face) {
        this(face, face, face, face);
    }

    @Override
    public int getNumberOfElements() {
        return 4;
    }

    @Override
    public void addToArray(Color element, float[] data, int offset) {
        data[offset] = element.red;
        data[offset + 1] = element.green;
        data[offset + 2] = element.blue;
        data[offset + 3] = element.alpha;
    }


}

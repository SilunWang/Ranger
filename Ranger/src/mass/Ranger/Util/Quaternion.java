package mass.Ranger.Util;

import mass.Ranger.Data.IO.RawName;

public class Quaternion {
    @RawName(value = "W", priority = 4)
    public float W;
    @RawName(value = "X", priority = 1)
    public float X;
    @RawName(value = "Y", priority = 2)
    public float Y;
    @RawName(value = "Z", priority = 3)
    public float Z;

    public Quaternion(float... quaternion) {
        this.W = quaternion[0];
        this.X = quaternion[1];
        this.Y = quaternion[2];
        this.Z = quaternion[3];
    }
}

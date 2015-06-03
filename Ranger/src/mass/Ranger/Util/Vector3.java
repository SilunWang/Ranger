package mass.Ranger.Util;

import mass.Ranger.Data.IO.RawName;

public class Vector3 {
    private final static String TAG = Vector3.class.getName();
    @RawName(value = "X", priority = 1)
    public float X;
    @RawName(value = "Y", priority = 2)
    public float Y;
    @RawName(value = "Z", priority = 3)
    public float Z;

    public Vector3(float[] array) {
        //if (array.length != 3) {
            //throw new IndexOutOfBoundsException("Array should contains exactly 3 elements");
        //}
        this.X = array[0];
        this.Y = array[1];
        this.Z = array[2];
    }

    public Vector3(float x, float y, float z) {
        this.X = x;
        this.Y = y;
        this.Z = z;
    }

    public float[] toArray() {
        return new float[]{X, Y, Z};
    }

    public static float dist(Vector3 v1, Vector3 v2) {
        return (float) Math.sqrt((v1.X - v2.X)*(v1.X - v2.X) + (v1.Y - v2.Y)*(v1.Y - v2.Y)  + (v1.Z - v2.Z)*(v1.Z - v2.Z));
    };

    public float magnitude(){
        return dist(new Vector3(new float[]{0,0,0}),this);
    }
}

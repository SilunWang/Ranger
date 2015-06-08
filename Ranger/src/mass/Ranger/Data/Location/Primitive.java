package mass.Ranger.Data.Location;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("UnusedDeclaration")
public class Primitive {
    private final static String TAG = Primitive.class.getName();
    @SerializedName("Name")
    private String name;
    @SerializedName("X")
    private double[] x;
    @SerializedName("Y")
    private double[] y;
}

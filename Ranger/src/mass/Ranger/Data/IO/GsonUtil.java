package mass.Ranger.Data.IO;

import android.util.Base64;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import mass.Ranger.Algorithm.Localization.Location;
import mass.Ranger.Data.Location.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class GsonUtil {
    public static final ThreadLocal<DateTimeFormatter> DATE_TIME_FORMATTER = new ThreadLocal<DateTimeFormatter>() {
        @Override
        protected DateTimeFormatter initialValue() {
            return ISODateTimeFormat.dateTime();
        }
    };
    private final static String TAG = GsonUtil.class.getName();
    private final static Gson gson = getGson();

    public static Gson get() {
        return gson;
    }

    /**
     * Create a gson to serialize and deserialize customer data type
     */
    public static Gson getGson() {
        return getGsonBuilder().serializeSpecialFloatingPointValues().create();
    }

    public static GsonBuilder getGsonBuilder() {
        GsonBuilder builder = new GsonBuilder();
        registerCSharpStyleDateSerializer(builder);
        registerCSharpStyleMapSerializer(builder);
        registerIBoundarySerializer(builder);
        registerIZoneInfoSerializer(builder);
        registerByteArraySerializer(builder);
        registerFloorsSerializer(builder);
        registerLocationSerializer(builder);
        return builder;
    }


    private static GsonBuilder registerFloorsSerializer(GsonBuilder builder) {
        JsonDeserializer<Floors> zoneInfoJsonDeserializer = new JsonDeserializer<Floors>() {
            @Override
            public Floors deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
                    JsonParseException {
                if (json instanceof JsonObject) {
                    JsonObject object = (JsonObject) json;
                    Floors floors = new Floors();
                    for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                        final String key = entry.getKey();
                        final Floor value = context.deserialize(entry.getValue(), Floor.class);
                        Floors.Entry floorEntry = new Floors.Entry(key, value);
                        floors.add(floorEntry);
                    }
                    return floors;
                }
                return null;
            }
        };
        builder.registerTypeAdapter(Floors.class, zoneInfoJsonDeserializer);
        return builder;
    }

    private static GsonBuilder registerIZoneInfoSerializer(GsonBuilder builder) {
        JsonDeserializer<IZoneInfo> zoneInfoJsonDeserializer = new JsonDeserializer<IZoneInfo>() {
            @Override
            public IZoneInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws
                    JsonParseException {
                if (json instanceof JsonObject) {
                    JsonObject object = (JsonObject) json;
                    String typeString = object.get("__type").getAsString();
                    if (typeString.startsWith("FloorInfo")) {
                        return context.deserialize(json, FloorInfo.class);
                    } else if (typeString.startsWith("TileInfo")) {
                        return context.deserialize(json, TileInfo.class);
                    }
                }
                return null;
            }
        };
        builder.registerTypeAdapter(IZoneInfo.class, zoneInfoJsonDeserializer);
        return builder;
    }

    private static GsonBuilder registerIBoundarySerializer(GsonBuilder builder) {
        JsonDeserializer<IBoundary> boundaryJsonDeserializer = new
                JsonDeserializer<IBoundary>() {
                    @Override
                    public IBoundary deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                            throws JsonParseException {
                        // only for Rectangle now
                        if (json instanceof JsonObject && ((JsonObject) json).get("__type").getAsString().startsWith
                                ("Rectangle")) {
                            JsonObject object = (JsonObject) json;
                            double maxX = object.get("MaxX").getAsDouble();
                            double maxY = object.get("MaxY").getAsDouble();
                            double minX = object.get("MinX").getAsDouble();
                            double minY = object.get("MinY").getAsDouble();
                            try {
                                return new Rectangle(minX, minY, maxX, maxY);
                            } catch (Exception e) {

                            }
                        }
                        return null;
                    }
                };

        builder.registerTypeAdapter(IBoundary.class, boundaryJsonDeserializer);
        return builder;
    }

    private static GsonBuilder registerCSharpStyleDateSerializer(GsonBuilder builder) {
        JsonSerializer<Date> dateJsonSerializer = new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                if (src == null) {
                    return null;
                }
                return new JsonPrimitive(DATE_TIME_FORMATTER.get().print(new DateTime(src)));
            }
        };
        JsonDeserializer<Date> dateJsonDeserializer = new

                JsonDeserializer<Date>() {
                    @Override
                    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                            throws JsonParseException {
                        try {
                            return new DateTime(json.getAsString()).toDate();
                        } catch (IllegalArgumentException e) {
                            return null;
                        }
                    }
                };
        builder.registerTypeAdapter(Date.class, dateJsonSerializer);
        builder.registerTypeAdapter(Date.class, dateJsonDeserializer);
        return builder;
    }

    private static GsonBuilder registerCSharpStyleMapSerializer(GsonBuilder builder) {
        JsonDeserializer<HashMap> mapJsonDeserializer = new JsonDeserializer<HashMap>() {
            @Override
            public HashMap deserialize(
                    JsonElement json,
                    Type typeOfT,
                    JsonDeserializationContext context) throws JsonParseException {
                JsonArray array = json.getAsJsonArray();
                HashMap map = new HashMap();
                for (int i = 0; i < array.size(); i++) {
                    JsonObject child = array.get(i).getAsJsonObject();
                    JsonElement keyElement = child.get("Key");
                    JsonElement valueElement = child.get("Value");
                    Object key = context.deserialize(keyElement, ((ParameterizedType) typeOfT).getActualTypeArguments
                            ()[0]);
                    Object value = context.deserialize(valueElement, ((ParameterizedType) typeOfT)
                            .getActualTypeArguments()[1]);
                    //noinspection unchecked
                    map.put(key, value);
                }
                return map;
            }
        };
        JsonSerializer<HashMap> mapJsonSerializer = new

                JsonSerializer<HashMap>() {
                    @Override
                    public JsonElement serialize(HashMap src, Type typeOfSrc, JsonSerializationContext context) {
                        JsonArray array = new JsonArray();
                        for (Object entry : src.entrySet()) {
                            Map.Entry e = (Map.Entry) entry;
                            JsonObject object = new JsonObject();
                            array.add(object);
                            object.add("Key", context.serialize(e.getKey()));
                            object.add("Value", context.serialize(e.getValue()));
                        }
                        return array;
                    }
                };
        Type type = new TypeToken<HashMap>() {
        }.getType();
        builder.registerTypeAdapter(type, mapJsonDeserializer);
        builder.registerTypeAdapter(type, mapJsonSerializer);
        return builder;
    }

    private static GsonBuilder registerByteArraySerializer(GsonBuilder builder) {
        JsonDeserializer<byte[]> bytesJsonDeserializer = new JsonDeserializer<byte[]>() {
            @Override
            public byte[] deserialize(
                    JsonElement json,
                    Type typeOfT,
                    JsonDeserializationContext context) throws JsonParseException {
                return Base64.decode(json.getAsString(), Base64.DEFAULT);
            }
        };
        JsonSerializer<byte[]> bytesJsonSerializer = new

                JsonSerializer<byte[]>() {
                    @Override
                    public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
                        return new Gson().toJsonTree(Base64.encodeToString(src, android.util.Base64.DEFAULT));
                    }
                };
        Type type = new TypeToken<byte[]>() {
        }.getType();
        builder.registerTypeAdapter(type, bytesJsonDeserializer);
        builder.registerTypeAdapter(type, bytesJsonSerializer);
        return builder;
    }

    private static GsonBuilder registerLocationSerializer(GsonBuilder builder) {
        JsonDeserializer<Location> locationJsonDeserializer = new JsonDeserializer<Location>() {
            @Override
            public Location deserialize(
                    JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                try {
                    String floor = null;
                    double x = Double.NaN;
                    double y = Double.NaN;
                    JsonObject object = json.getAsJsonObject();
                    JsonElement floorElement = object.get("Floor");
                    if (floorElement != null) {
                        floor = floorElement.getAsString();
                    }
                    JsonElement xElement = object.get("X");
                    if (xElement != null) {
                        try {
                            x = Double.parseDouble(xElement.getAsString());
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    JsonElement yElement = object.get("Y");
                    if (yElement != null) {
                        try {
                            y = Double.parseDouble(yElement.getAsString());
                        } catch (NumberFormatException ignored) {
                        }
                    }
                    return new Location(x, y, floor);
                } catch (IllegalStateException ex) {
                    return null;
                }
            }
        };
        return builder.registerTypeAdapter(Location.class, locationJsonDeserializer);
    }
}
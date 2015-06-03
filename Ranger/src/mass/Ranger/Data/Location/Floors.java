package mass.Ranger.Data.Location;

import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings("UnusedDeclaration")
public class Floors extends ArrayList<Floors.Entry> {
    private final static String TAG = Floors.class.getName();

    public static class Entry implements Map.Entry<String, Floor> {
        private String key;
        private Floor value;

        public Entry(String key, Floor value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        @Override
        public Floor getValue() {
            return value;
        }

        @Override
        public Floor setValue(Floor value) {
            Floor oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }
}

package mass.Ranger.Data.IO;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RawDataSerializer<T> {
    private final static Class<RawName> TYPE = RawName.class;
    private RawNameGetter<T> getter;

    public RawDataSerializer(Class<T> klass) {
        getter = new RawNameGetter<T>(klass, null);
    }

    public String getHeader() {
        try {
            return getter.getName();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getContent(T object) {
        try {
            if (object == null) {
                return getter.getEmptyValue();
            }
            return getter.getValue(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<Field> getAnnotatedField(Class<?> klass) {
        ArrayList<Field> fields = new ArrayList<Field>();
        for (Field field : klass.getDeclaredFields()) {
            if (field.isAnnotationPresent(TYPE)) {
                fields.add(field);
            }
        }
        Collections.sort(fields, new Comparator<Field>() {
            @Override
            public int compare(Field field1, Field field2) {
                RawName type1 = field1.getAnnotation(TYPE);
                RawName type2 = field2.getAnnotation(TYPE);
                return type1.priority() - type2.priority();
            }
        });
        return fields;
    }

    class RawNameGetter<T> {
        private boolean isPrimitive;
        private RawNameGetter[] children;
        private Field rawNameField;
        private RawName rawName;

        public RawNameGetter(Class<T> klass, Field field) {
            if (field == null) {
                this.isPrimitive = false;
                this.rawNameField = null;
                this.rawName = null;
            } else {
                this.rawNameField = field;
                this.rawNameField.setAccessible(true);
                this.rawName = field.getAnnotation(TYPE);
                this.isPrimitive = this.rawName.type().equals(RawName.RawType.Primitive);
            }
            ArrayList<Field> annotatedFields = getAnnotatedField(klass);
            int size = annotatedFields.size();
            this.children = new RawNameGetter[size];
            for (int i = 0; i < size; i++) {
                Field f = annotatedFields.get(i);
                //noinspection unchecked
                children[i] = new RawNameGetter(f.getType(), f);
            }
        }

        public String getName() throws IllegalAccessException {
            if (isPrimitive) {
                return rawName.value();
            }
            StringBuilder buffer = new StringBuilder();
            String prefix = rawName == null ? "" : rawName.value() + ".";
            for (RawNameGetter child : children) {
                buffer.append(prefix).append(child.getName()).append(',');
            }
            buffer.deleteCharAt(buffer.length() - 1);
            return buffer.toString();
        }

        public String getEmptyValue() throws IllegalAccessException {
            if (isPrimitive) {
                return "0";
            }
            StringBuilder buffer = new StringBuilder();

            for (RawNameGetter child : children) {
                buffer.append(child.getEmptyValue()).append(',');
            }
            buffer.deleteCharAt(buffer.length() - 1);
            return buffer.toString();
        }

        public String getValue(T object) throws IllegalAccessException {
            if (object == null) {
                return getEmptyValue();
            }
            if (isPrimitive) {
                return object.toString();
            } else {
                StringBuilder buffer = new StringBuilder();
                for (RawNameGetter child : children) {
                    //noinspection unchecked
                    buffer.append(child.getValue(child.rawNameField.get(object))).append(',');
                }
                buffer.deleteCharAt(buffer.length() - 1);
                return buffer.toString();
            }
        }
    }
}

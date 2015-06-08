package mass.Ranger.Data.IO;

import android.content.Context;
import android.text.format.Time;
import com.example.travinavi.R;
import mass.Ranger.Device.ComboSensorReading;
import mass.Ranger.Device.LocationData;
import mass.Ranger.User.UserInfo;
import mass.Ranger.Web.WebHelper;

import java.io.*;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class DataSerializer {
    // TODO distinguish patrolling and floor setup
    UserInfo userInfo;
    public final static String SENSOR_FILE = "sensor.txt";
    public final static String WIFI_FILE = "wifi.txt";
    public final static String INFO_FILE = "info.txt";
    private final static String TAG = DataSerializer.class.getName();
    private final static ReentrantLock lock = new ReentrantLock();
    public TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            lock.lock();
            try {
                // Not store stepTrace, just clear the cache
                /*
                if (!"1".equals(AppContext.get().getConfigs().get(AppContext.STORE_TRACE))) {
                    linkedList.clear();
                    return;
                }*/
                int count = linkedList.size();
                HashSet<BufferedWriter> writers = new HashSet<BufferedWriter>();
                for (int i = 0; i < count; i++) {
                    Tuple tuple = linkedList.poll();
                    BufferedWriter writer = tuple.writer;
                    Object object = tuple.object;
                    try {
                        writer.write(object.toString());
                        writer.newLine();
                        writers.add(writer);
                    } catch (IOException e) {
                        //Logger.v(TAG, String.format("Failed to write to file: %s, %s", writer, object), e);
                    }
                }
                for (BufferedWriter writer : writers) {
                    try {
                        writer.flush();
                    } catch (IOException e) {
                        //Logger.v(TAG, String.format("Failed to flush to file: %s", writer), e);
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    };
    private BufferedWriter infoWriter;
    private BufferedWriter wifiWriter;
    private BufferedWriter sensorWriter;
    private RawDataSerializer<ComboSensorReading> comboSensorSerializer = new RawDataSerializer<ComboSensorReading>
            (ComboSensorReading.class);
    private ConcurrentLinkedQueue<Tuple> linkedList = new ConcurrentLinkedQueue<Tuple>();
    private File root;
    public static String rootname;

    public DataSerializer(Context context, String uuid) {
        String rootName = context.getString(R.string.app_name);
        root = new File(FileUtility.getOrCreateDefaultDirectory(rootName), uuid);
        //noinspection ResultOfMethodCallIgnored
        FileUtility.mkdirs(root);
        rootname = root.getPath();

        try {
            File sensorFile = new File(root, SENSOR_FILE);
            sensorWriter = createWriter(sensorFile);
            sensorWriter.write(comboSensorSerializer.getHeader());
            sensorWriter.newLine();

            File wifiFile = new File(root, WIFI_FILE);
            wifiWriter = createWriter(wifiFile);

            File infoFile = new File(root, INFO_FILE);
            infoWriter = createWriter(infoFile);
            infoWriter.write("BJW2\r\n" + getSystemTimeStr() + "\r\n" + "Admin"); //这里还需要加上UserId
            infoWriter.newLine();

            linkedList = new ConcurrentLinkedQueue<Tuple>();
            Timer writeTimer = new Timer("DataSerializeTimer " + uuid);
            writeTimer.schedule(timerTask, 0, 1000);
        } catch (IOException ex) {
            //Logger.e(TAG, "Fail to create writers", ex);
        }
    }

    private BufferedWriter createWriter(final File sensorFile) throws FileNotFoundException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sensorFile), WebHelper.UTF8));
    }

    public File getRoot() {
        return root;
    }

    public void serialize(ComboSensorReading data) {
        serialize(sensorWriter, comboSensorSerializer.getContent(data));
    }

    private void serialize(final BufferedWriter writer, final Object obj) {
        if (writer == null) {
            return;
        }
        linkedList.add(new Tuple(writer, obj));
    }

    public void serialize(LocationData data) {
        serialize(wifiWriter, GsonUtil.get().toJson(data));
    }


    public void close() {
        try {
            // flush
            timerTask.run();
            if (sensorWriter != null) {
                sensorWriter.flush();
                sensorWriter.close();
            }
            if (wifiWriter != null) {
                wifiWriter.flush();
                wifiWriter.close();
            }
            if (infoWriter != null) {
                infoWriter.flush();
                infoWriter.close();
            }

        } catch (IOException ex) {
            //Logger.w(TAG, "Fail to close writers", ex);
        } catch (Exception ignored) {
        }
    }

    private static class Tuple {
        BufferedWriter writer;
        Object object;

        private Tuple(BufferedWriter writer, Object object) {
            this.writer = writer;
            this.object = object;
        }
    }

    private String getSystemTimeStr() {
        String timeStr;
        Time t = new Time();
        t.setToNow();
        int year = t.year;
        int month = t.month + 1;
        int day = t.monthDay;
        int hour = t.hour;
        int minute = t.minute;
        int second = t.second;
        if (hour > 12) {
            hour = hour - 12;
            timeStr = month + "/" + day + "/" + year + " " + hour + ":" + minute + ":" + second + " PM";
        } else {
            timeStr = month + "/" + day + "/" + year + " " + hour + ":" + minute + ":" + second + " AM";
        }
        System.out.println(timeStr);
        return timeStr;
    }
}

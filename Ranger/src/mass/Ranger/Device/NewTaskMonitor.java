package mass.Ranger.Device;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import java.util.HashSet;

public class NewTaskMonitor {
    private static final String TAG = NewTaskMonitor.class.getName();
    private static final HashSet<ChangeListener> listeners = new HashSet<ChangeListener>();
    private static Cursor taskCursor;
    private static Cursor localEventCursor;
    private static ContentObserver localContentObserver = new ContentObserver(new Handler()) {
        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            queryUnFinishCount();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            queryUnFinishCount();
        }
    };
    private static ContentObserver taskContentObserver = new ContentObserver(new Handler()) {
        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            queryUnFinishCount();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            queryUnFinishCount();
        }
    };

    public static void registerListener(ChangeListener changeListener) {
        listeners.add(changeListener);
        if (listeners.size() == 1) {
            startMonitor();
        }
    }

    public static void unRegisterListener(ChangeListener changeListener) {
        listeners.remove(changeListener);
        if (listeners.size() == 0) {
            StopMonitor();
        }
    }

    private static void StopMonitor() {
        localEventCursor.unregisterContentObserver(localContentObserver);
        taskCursor.unregisterContentObserver(taskContentObserver);
        localEventCursor = null;
        taskCursor = null;
    }

    private static void startMonitor() {
        queryUnFinishCount();
    }

    private static void queryUnFinishCount() {

    }

    public static interface ChangeListener {
        public void onChanged(int localCount, int taskCount);
    }
}

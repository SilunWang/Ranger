package mass.Ranger.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;
import com.example.travinavi.R;
import mass.Ranger.Data.IO.FileUtility;
import mass.Ranger.Security.AccessToken;
import mass.Ranger.Security.UserManager;
import mass.Ranger.Web.WebHelper;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class AppContext extends Application {
    public final static  String                  BASIC_URL            = "BASIC_URL";
    public static final  String                  LOG_LEVEL            = "LOG_LEVEL";
    public static final  String                  LOG_TO_FILE          = "LOG_TO_FILE";
    public static final  String                  STORE_TRACE          = "STORE_TRACE";
    private final static String                  TAG                  = AppContext.class.getName();
    private static final int                     CORE_POOL_SIZE       = 5;
    private static final int                     MAXIMUM_POOL_SIZE    = 128;
    private static final int                     KEEP_ALIVE           = 1;
    private static final ThreadFactory           sThreadFactory       = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTask #" + mCount.getAndIncrement());
        }
    };
    private static final BlockingQueue<Runnable> sPoolWorkQueue       =
            new PriorityBlockingQueue<Runnable>(128, new Comparator<Runnable>() {
                @SuppressWarnings("ComparatorMethodParameterNotUsed")
                @Override
                public int compare(Runnable lhs, Runnable rhs) {
                    // all tasks are equally important
                    return 0;
                }
            });
    /**
     * An {@link java.util.concurrent.Executor} that can be used to execute tasks in parallel.
     */
    private static final ThreadPoolExecutor      THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE,
            TimeUnit.HOURS,
            sPoolWorkQueue,
            sThreadFactory);
    private static AppContext CONTEXT;
    private final TimerTask                       backgroundSyncTask        = new TimerTask() {
        @Override
        public void run() {

        }
    };
    private final DialogInterface.OnClickListener longPollingDialogListener =
            new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == AlertDialog.BUTTON_POSITIVE) {
                        //Intent intent = new Intent(getActiveActivity(),
                        //EventActivity.class).putExtra(EventActivity.TAB_STRING, 0);
                        //getActiveActivity().startActivity(intent);
                    }
                    dialog.dismiss();
                }
            };

    private SharedPreferences globalSharedPreferences;
    private File              root;
    private boolean  initialized = false;

    private Activity activeActivity;
    private ReentrantLock       initializeLock        = new ReentrantLock();
    private ReentrantLock       backgroundTaskLock    = new ReentrantLock();
    private ReentrantLock       configLock            = new ReentrantLock();
    private boolean             isBackgroundTaskStart = false;
    private Map<String, String> configMap             = new HashMap<String, String>();
    private Thread longPollingThread;

    public static AppContext get() {
        return CONTEXT;
    }

    public static void showToast(final int stringId, final int toastLength) {
        if (CONTEXT != null && CONTEXT.getActiveActivity() != null) {
            CONTEXT.getActiveActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CONTEXT, CONTEXT.getString(stringId), toastLength).show();
                }
            });
        }
    }

    public static Future<?> execute(final Runnable command) {
        return THREAD_POOL_EXECUTOR.submit(command);
    }

    public static <V> Future<V> execute(final Callable<V> task) {
        return THREAD_POOL_EXECUTOR.submit(task);
    }

    @SuppressLint("NewApi") public static <Params, Progress, Result> void execute(AsyncTask<Params, Progress, Result> task, Params... params) {
        task.executeOnExecutor(THREAD_POOL_EXECUTOR, params);
    }

    public static void showToast(final String text, final int toastLength) {
        if (CONTEXT != null && CONTEXT.getActiveActivity() != null) {
            CONTEXT.getActiveActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CONTEXT, text, toastLength).show();
                }
            });
        }
    }

    public boolean showTaskOnMap() {
        AccessToken token = UserManager.getInstance().getToken();
        return token != null && globalSharedPreferences.getBoolean(getShowTaskKey(token.getUserId()), true);
    }

    public void setShowTaskOnMap(boolean show) {
        AccessToken token = UserManager.getInstance().getToken();
        if (token == null) {
            return;
        }
        globalSharedPreferences.edit().putBoolean(getShowTaskKey(token.getUserId()), show).apply();
    }

    private String getShowTaskKey(String userId) {
        return userId + "show_task";
    }

    public Activity getActiveActivity() {
        return activeActivity;
    }

    public void setActiveActivity(Activity activity) {
        this.activeActivity = activity;
    }

    private void showNewTaskDialog() {
        getActiveActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(getActiveActivity())
                        .setMessage("æ¨ææ°ä»»å¡")
                        .setPositiveButton("æå¼", longPollingDialogListener)
                        .setNegativeButton("ç¨?å?ç", longPollingDialogListener)
                        .create();
                // Avoid dismiss from touch outsidehaihenbukeguanha
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });
    }

    public File getRoot() {
        if (root == null) {
            root = FileUtility.getOrCreateDefaultDirectory(getString(R.string.app_name));
        }
        return root;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (AppContext.class) {
            CONTEXT = this;
        }

        loadConfig();
        globalSharedPreferences = CONTEXT.getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
        startBackgroundTask();
    }

    private void startBackgroundTask() {
        if (isBackgroundTaskStart) {
            return;
        }
        backgroundTaskLock.lock();
        try {
            // double check to make sure it's indeed not started
            if (!isBackgroundTaskStart) {
                isBackgroundTaskStart = true;
                new Timer("Thread-background-sync").schedule(backgroundSyncTask, 600000, 600000);
            }
        } finally {
            backgroundTaskLock.unlock();
        }
    }

    private void loadConfig() {
        configLock.lock();
        try {
            File root = FileUtility.getOrCreateDefaultDirectory("iNav");
            File config = new File(root, "config.ini");
            HashMap<String, String> configMap = new HashMap<String, String>();
            if (!config.exists()) {
                configMap.put(LOG_TO_FILE, "0");
                configMap.put(LOG_LEVEL, "4");
                configMap.put(BASIC_URL, "https://inavtest.cloudapp.net/v1");
                PrintWriter writer = null;
                try {
                    writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(config), WebHelper.UTF8));
                    for (Map.Entry<String, String> entry : configMap.entrySet()) {
                        writer.println(entry.getKey() + "=" + entry.getValue());
                    }
                    writer.flush();
                    writer.close();
                } catch (IOException ignored) {
                } finally {
                    if (writer != null) {
                        writer.close();
                    }
                }
            } else {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(config), WebHelper.UTF8));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] pair = line.split("=");
                        if (pair.length == 2) {
                            configMap.put(pair[0].trim(), pair[1].trim());
                        }
                    }
                } catch (IOException ignored) {
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
            // Ensure basic url exists, hard code the test site url here. this is not a good idea
            if (!configMap.containsKey(BASIC_URL)) {
                configMap.put(BASIC_URL, "https://inavtest.cloudapp.net/v1");
            }
            this.configMap = Collections.unmodifiableMap(configMap);
            //Logger.setupLog(getConfigs());
        } finally {
            configLock.unlock();
        }
    }

    public Map<String, String> getConfigs() {
        return configMap;
    }


    public void initialized() {
        if (isInitialized()) {
            return;
        }
        initializeLock.lock();
        try {
            if (!isInitialized()) {
                //Logger.i(TAG, "Start to initialize");
                setInitialized(true);
            }
        } finally {
            initializeLock.unlock();
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
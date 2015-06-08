package mass.Ranger.Activity;

import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.example.travinavi.R;
import mass.Ranger.Device.NewTaskMonitor;


public class MainActivity extends FragmentActivity implements OnClickListener, NewTaskMonitor.ChangeListener {

    /**
     * Author: Silun Wang
     * Alias: v-silwa
     * Email: badjoker@163.com
     */


    private ActionBar mActionBar;

    private ConnectivityManager cm;
    private static Handler handler;


    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initActionBar();
        //initView();

        NewTaskMonitor.registerListener(this);

        cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        cm.setNetworkPreference(ConnectivityManager.TYPE_MOBILE);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.i("MSG", (String) msg.obj);
                String message = (String) msg.obj;
                if (message.equals("uploadDone")) {
                    Toast.makeText(getApplicationContext(), "Upload Done", Toast.LENGTH_SHORT).show();
                } else if (message.equals("downloadDone")) {
                    Toast.makeText(getApplicationContext(), "Download Done", Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }

        };
    }

    private void initActionBar() {
        //mActionBar = getSupportActionBar();
        //mActionBar.hide();
    }

    public static Handler getHandler() {
        return handler;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View v) {
    }

    @Override
    public void onChanged(int localCount, int taskCount) {
    }


    private void changeFragment(Fragment targetFragment) {
        /*
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();*/
    }


}

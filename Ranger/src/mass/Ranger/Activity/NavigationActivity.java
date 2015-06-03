package mass.Ranger.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.example.travinavi.R;
import mass.Ranger.Data.IO.DataFiles;
import mass.Ranger.Device.BackgroundPositioning;
import mass.Ranger.View.PathView;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;

/**
 * Author: Silun Wang
 * Alias: v-silwa
 * Email: badjoker@163.com
 */
public class NavigationActivity extends Activity {

    private static final int FILE_SELECT_CODE = 0;
    private ToggleButton startBtn;
    private Button browseBtn;
    public static PathView pathView;
    protected BackgroundPositioning backPositioning;
    public static boolean serviceOn = false;
    public static float terminalXCoordinate = 0;
    public static float terminalYCoordinate = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigator);
        initView();
        if (backPositioning == null) {
            backPositioning = new BackgroundPositioning(getApplicationContext());
        }
        backPositioning.setParentActivity(this);
        browseBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(NavigationActivity.this, ChooseTraceActivity.class);
                startActivityForResult(intent, FILE_SELECT_CODE);
            }
        });
        startBtn.setChecked(true);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (serviceOn) {
                    stopNaviService();
                    serviceOn = false;
                } else {
                    startNaviService();
                    serviceOn = true;
                }
            }
        });
    }

    private void initView(){
        browseBtn = (Button) findViewById(R.id.browsebutton);
        startBtn = (ToggleButton) findViewById(R.id.startbutton);
        pathView = (PathView) findViewById(R.id.navigator_view);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View parentView = inflater.inflate(R.layout.navigator, container, false);
        return parentView;
    }

    public void startNaviService() {
        //Debug.startMethodTracing("calc");
        try {
            backPositioning.initPF();
            backPositioning.start();
            Toast.makeText(getApplicationContext(), "导航开始", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void stopNaviService() {
        //Debug.stopMethodTracing();
        try {
            backPositioning.stop();
            Toast.makeText(getApplicationContext(), "终止导航", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Recall function
     * @param requestCode
     * @param resultCode
     * @param data file data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            File root = Environment.getExternalStorageDirectory();
            root = new File(root, "/Travi-Navi");
            File path = new File(root, uri.toString());
            path = new File(path, "step.txt");
            String url = path.getAbsolutePath();
            try {
                terminalXCoordinate = 0;
                terminalYCoordinate = 0;
                pathView.init();
                DataFiles.readFile(url);
                pathView.renderTrackingPath(BackgroundPositioning.stepTrace);
                startBtn.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
    }

    public void setIndicator(boolean state){
        ToggleButton activityIndicator = (ToggleButton) findViewById(R.id.activity_indicator);
        activityIndicator.setChecked(state);
    }

}

package mass.Ranger.Activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.example.travinavi.R;
import mass.Ranger.Data.IO.DataFiles;
import mass.Ranger.Data.IO.DataSerializer;
import mass.Ranger.Data.IO.ZipToFile;
import mass.Ranger.Device.BackgroundPositioning;
import mass.Ranger.Fragment.DialogFragment.CompleteAlertDialogFragment;
import mass.Ranger.View.CameraView;
import mass.Ranger.View.PathView;
import mass.Ranger.Web.WebHelper;

import java.io.IOException;

/**
 * Author: Silun Wang
 * Alias: v-silwa
 * Email: badjoker@163.com
 */

public class TrackingActivity extends Activity {

    private ToggleButton togglebtn;
    private Button saveBtn;
    public static PathView pathView;
    public CameraView mCameraView;

    public static boolean serviceOn = false;
    protected BackgroundPositioning backPositioning;
    private WebHelper webHelper;
    public static String zipFilename;

    public Context context;
    DataFiles dataFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking_layout);

        if (dataFiles == null)
            dataFiles = new DataFiles(this);
        if (webHelper == null)
            webHelper = new WebHelper(this);

        pathView = (PathView) findViewById(R.id.pathview);
        mCameraView = new CameraView(this);

        togglebtn = (ToggleButton) findViewById(R.id.toggleButton1);
        togglebtn.setChecked(true);
        togglebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (serviceOn) {
                    try {
                        stopTrackingService();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    serviceOn = false;
                } else {
                    startTrackingService();
                    serviceOn = true;
                }
            }
        });

        saveBtn = (Button) findViewById(R.id.id_save_confirm_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompleteAlertDialogFragment confirmAlertWindow = CompleteAlertDialogFragment.newInstance("是否结束记录?");
                confirmAlertWindow.show(getFragmentManager(),"confirmDialog");
            }
        });

        if (backPositioning == null) {
            backPositioning = new BackgroundPositioning(getApplicationContext());
        }

    }

    public void onDestroy(){
        if (serviceOn)
            try {
                stopTrackingService();
            } catch (IOException e) {
                e.printStackTrace();
            }
        super.onDestroy();
    }


    public void startTrackingService() {
        backPositioning.start();
        dataFiles.init();
        Toast.makeText(getApplicationContext(), "开始记录", Toast.LENGTH_SHORT).show();
    }

        public void stopTrackingService() throws IOException {
        dataFiles.closeFiles();
        backPositioning.stop();
        Toast.makeText(getApplicationContext(), "停止记录", Toast.LENGTH_SHORT).show();
    }

    public void uploadData(){
        try {

                zipFilename = DataSerializer.rootname + "/" + BackgroundPositioning.patrollingID.toString() + ".zip";
                ZipToFile.zipFile(DataSerializer.rootname, zipFilename);
                webHelper.PostData(zipFilename);
        } catch (Exception e) {
                    e.printStackTrace();
        }
    }

    public void downloadData(){
        webHelper.GetData();
    }

}

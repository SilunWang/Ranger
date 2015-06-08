package mass.Ranger.View;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.MediaController.MediaPlayerControl;

import java.io.IOException;

/**
 * Author: Silun Wang
 * Alias: v-silwa
 * Email: badjoker@163.com
 */
public class CameraView extends SurfaceView implements OnPreparedListener, SurfaceHolder.Callback, MediaPlayerControl {

    SurfaceHolder holder;
    Camera mCamera;

    private ShutterCallback shutter = new ShutterCallback() {

        @Override
        public void onShutter() {
            // TODO Auto-generated method stub

        }
    };

    private PictureCallback raw = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub

        }
    };

    private PictureCallback jpeg = new PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub

        }
    };

    public void voerTack() {
        mCamera.startPreview();
    }

    public CameraView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public CameraView(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        holder = getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void takePicture() {
        if (mCamera != null)
            mCamera.takePicture(null, null, null);
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            // TODO: handle exception
        }
        return c;
    }

    @Override
    public void start() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public int getDuration() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void seekTo(int pos) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isPlaying() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getBufferPercentage() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean canPause() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canSeekForward() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getAudioSessionId() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                mCamera.setPreviewDisplay(holder);
                //Parameters parameters = mCamera.getParameters();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                mCamera.setPreviewDisplay(holder);
                //Parameters parameters = mCamera.getParameters();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // TODO Auto-generated method stub

    }

}

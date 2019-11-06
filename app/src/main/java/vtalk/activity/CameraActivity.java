package vtalk.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


import signalprocess.facecapture.CameraPreview;
import signalprocess.facecapture.Live2dGLSurfaceView;
import vtalk.activity.R;
import network.Live2DReference;
import network.Packet;

public class CameraActivity extends AppCompatActivity {

    public Packet packet;
    private String TAG = "CameraActivity";
    private int CAMERA_REQUEST_CODE = 20;

    private CameraPreview mCameraPreview;
    public Live2dGLSurfaceView mGLSurfaceView;

    public final double[] emotion = new double[10];

    private int mModel = 0;

    protected void changeVisiable() {
        mGLSurfaceView.setVisibility(View.GONE);
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        if(Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                        CAMERA_REQUEST_CODE);
            }
        }

        packet = new Packet("", "");

        mCameraPreview = (CameraPreview) findViewById(R.id.cam_preview);
        mCameraPreview.init(this, packet);

        mGLSurfaceView = (Live2dGLSurfaceView) findViewById(R.id.live2dView);
        mGLSurfaceView.init(CameraActivity.this, packet, Live2DReference.MODEL_SET.get(0), Live2DReference.TEXTURE_SET.get(0), 1, 1);


        MainActivity.sholdShowLoadingView = false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}

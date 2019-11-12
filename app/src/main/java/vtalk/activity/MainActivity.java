package vtalk.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;


import fr.pchab.androidrtc.RtcActivity;
import scut.carson_ho.kawaii_loadingview.Kawaii_LoadingView;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {

    public String callerId = null;

    public static Boolean sholdShowLoadingView = false;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1)
                loadingView.setVisibility(View.INVISIBLE);
        }
    };

    private static final String TAG = "vtalk.activity.MainActivity";
    private static String[] PERMISSIONS_REQ = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
    };
    private static final int REQUEST_CODE_PERMISSION = 2;

    @ViewById(R.id.cam_button)
    protected Button mCamButton;

    @ViewById(R.id.Kawaii_LoadingView)
    public Kawaii_LoadingView kawaii_loadingView;

    @ViewById(R.id.loadingView)
    public View loadingView;

    @Click({R.id.cam_button})
    protected void launchCamera() {

        /*sholdShowLoadingView = true;

        new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (MainActivity.sholdShowLoadingView){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }).start();*/

        //Toast.makeText(this, "初始化将会占用几秒，请耐心等待", Toast.LENGTH_LONG);

        Intent intent = new Intent(this, CameraActivity.class);
        intent.putExtra("callerId", callerId);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        // For API 23+ you need to request the read/write permissions even if they are already in your manifest.
        int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentApiVersion >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_REQ,
                    REQUEST_CODE_PERMISSION
            );
        }

        final Intent intent = getIntent();
        final String action = intent.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            final String urlId =intent.getData().getQueryParameter("id");
            callerId = urlId;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        kawaii_loadingView.startMoving();
    }

}

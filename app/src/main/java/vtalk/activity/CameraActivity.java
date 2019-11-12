package vtalk.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.MediaStream;
import org.webrtc.VideoRendererGui;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.StringTokenizer;

import fr.pchab.webrtcclient.PeerConnectionParameters;
import fr.pchab.webrtcclient.WebRtcClient;
import signalprocess.facecapture.CameraPreview;
import signalprocess.facecapture.Live2dGLSurfaceView;
import network.Live2DReference;
import network.NetworkActivity;

public class CameraActivity extends AppCompatActivity implements WebRtcClient.RtcListener, NetworkActivity {

    /** 属性 **/
    private static final String VIDEO_CODEC_VP9 = "VP9";
    private static final String AUDIO_CODEC_OPUS = "opus";
    private static final int VIDEO_CALL_SENT = 666;

    private final int FILL_FRAME_FACTOR = 10;

    private int CAMERA_REQUEST_CODE = 20;

    private String TAG = "CameraActivity";

    /**  通讯部分 **/
    private String callerId;
    public String mSocketAddress;
    public WebRtcClient client;
    private WebRtcClient.MessageListener messageListener;

    public Queue<double[]> queue;

    /** 显示部分 **/
    private CameraPreview mCameraPreview; //相机view
    public Live2dGLSurfaceView mGLSurfaceView; //虚拟人物view

    public final double[] emotion = new double[10]; // 人物表情控制参数
    private int emotionSize = 4; // 总共的表情数 用于通讯控制
    private int mModel = 0; // 使用的模型号

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

        queue = new LinkedList<>();

        callerId = null;
        Intent intent = getIntent();
        callerId = intent.getStringExtra("callerId");

        mCameraPreview = (CameraPreview) findViewById(R.id.cam_preview);
        mCameraPreview.init(this);

        mGLSurfaceView = (Live2dGLSurfaceView) findViewById(R.id.live2dView);
        mGLSurfaceView.init(CameraActivity.this, Live2DReference.MODEL_SET.get(0), Live2DReference.TEXTURE_SET.get(0), 1, 1);

        networkSetup();

        MainActivity.sholdShowLoadingView = false;
    }

    /**********************************  通讯部分 ************************************/
    /**
     * 这耦合度过高 无法拆分 只能写在一个activity里
     * 视频通讯通过传输点来完成
     * 音频通讯未完成
     */

    private void networkSetup()
    {
        mSocketAddress = "http://" + getResources().getString(R.string.host);
        mSocketAddress += (":" + getResources().getString(R.string.port) + "/");

        PeerConnectionParameters params = new PeerConnectionParameters(
                false, false, 0, 0, 30, 1, VIDEO_CODEC_VP9, false, 1, AUDIO_CODEC_OPUS, true);

        messageListener = new WebRtcClient.MessageListener() {
            @Override
            public void onMessage(String message) {

                Log.d(TAG, message);

                StringTokenizer tokenizer = new StringTokenizer(message, " ");

                if (tokenizer.countTokens() != emotionSize)
                    Log.d(TAG, "received message error");
                else
                {
                    double[] tmp = new double[10];
                    for (int i = 0; i < emotionSize; i++)
                        tmp[i] = Double.valueOf(tokenizer.nextToken());

                    synchronized(queue) {

                        for (int i = 1; i <= FILL_FRAME_FACTOR; i++)
                        {
                            double[] tt = new double[10];
                            for (int j = 0; j < emotionSize; j++)
                                tt[j] = (emotion[j]*(FILL_FRAME_FACTOR-i)+tmp[j]*i)/FILL_FRAME_FACTOR;
                            queue.add(tt);
                        }
                        for (int i = 0; i < emotionSize; i++)
                            emotion[i] = tmp[i];
                    }
                }
            }
        };

        client = new WebRtcClient(this, mSocketAddress, params, VideoRendererGui.getEGLContext(), messageListener);

        //client.start("virtual_talk");

        Log.d(TAG, "network init");
    }

    public double[] getEmotion(){
        synchronized (queue){
            if  (queue.isEmpty())
                return this.emotion;
            else
                return queue.poll();
        }
    }

    public void postEmotion(double emotion[]) {

        String message = "";
        for (int i = 0; i < emotionSize; i++) {
            message = message + String.valueOf(emotion[i]);
            if (i != emotionSize-1)
                message += " ";
        }

        client.sendMessageViaDataChannelToAllPeers(message);
    }

    public void call(String callId) {

        Intent msg = new Intent(Intent.ACTION_SEND);
        String url = mSocketAddress + callId;

        System.out.println("URL: " + url);

        msg.putExtra(Intent.EXTRA_TEXT, url);
        msg.setType("text/plain");
        startActivityForResult(Intent.createChooser(msg, "Call someone :"), VIDEO_CALL_SENT);
    }

    public void answer(String callerId) throws JSONException {
        client.sendMessage(callerId, "init", null);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCallReady(String callId) {

        if (callerId != null) {
            try {
                answer(callerId);
            } catch (JSONException e) {
                Log.d(TAG, "json error");
            }
        } else {
            call(callId);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        callerId = null;
    }

    @Override
    public void onStatusChanged(String newStatus) {

    }

    @Override
    public void onLocalStream(MediaStream localStream) {

    }

    @Override
    public void onAddRemoteStream(MediaStream remoteStream, int endPoint) {

    }

    @Override
    public void onRemoveRemoteStream(int endPoint) {

    }
}

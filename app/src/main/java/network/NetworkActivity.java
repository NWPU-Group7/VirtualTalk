package network;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONException;
import org.webrtc.MediaStream;
import org.webrtc.VideoRendererGui;

import java.util.StringTokenizer;

import fr.pchab.webrtcclient.PeerConnectionParameters;
import fr.pchab.webrtcclient.WebRtcClient;
import vtalk.activity.R;

/**
 * Created by bullypaulo on 2019/11/4.
 */

public interface NetworkActivity
{
    public double[] getEmotion();
    public void postEmotion(double emotion[]);

}

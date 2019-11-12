package signalprocess.face;

/**
 * Created by Administrator on 2017/5/11.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import vtalk.activity.TalkActivity;


public class CameraPreview extends SurfaceView
                           implements SurfaceHolder.Callback {

    public int frameRate = 2;
    private Context mContext;
    private TalkActivity mActivity;

    private int CAMERA_REQUEST_CODE = 20;
    private Camera mCamera = null;
    private SurfaceHolder mSurfaceHolder = null;
    private SurfaceTexture mSurfaceTexture = null;

    private FaceDetector mFaceDetector;

    //private int pWidth = 480, pHeight = 320;
    private int pWidth = 352, pHeight = 288;
    public int bufferSize;
    public byte[] buffer;

    public CameraPreview(Context context) {
        super(context);
        this.mContext = context;
    }
    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
    }
    public CameraPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public void init(TalkActivity activity) {

        this.mActivity = activity;

        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.addCallback(this);

        mSurfaceTexture = new SurfaceTexture(10);
        mFaceDetector = new FaceDetector(this.mContext, this.mActivity);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (mCamera == null) {
                mCamera = Camera.open(1);
            }

            mCamera.setPreviewTexture(mSurfaceTexture);
        } catch (Exception e) {
            Toast.makeText(mActivity, "error to open camera", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        initCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.setPreviewCallbackWithBuffer(null); //这个必须在前，不然退出出错
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    private void initCamera() {
        if (mCamera != null) {
            try {
                Camera.Parameters params = mCamera.getParameters();

                params.setPreviewSize(pWidth, pHeight); // 指定preview的大小

                params.setPreviewFormat(ImageFormat.NV21);
                mCamera.setParameters(params);

                bufferSize = pWidth * pHeight * ImageFormat.getBitsPerPixel(params.getPreviewFormat()) / 8;
                buffer = new byte[bufferSize];

                mCamera.addCallbackBuffer(buffer);
                mCamera.setPreviewCallbackWithBuffer(new FrameCallback());
                mCamera.startPreview();

            } catch (Exception e) {
                Toast.makeText(mActivity, "init camera error", Toast.LENGTH_SHORT);
            }
        }
    }

    private class FrameCallback implements Camera.PreviewCallback {

        int factor = 0;
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            camera.addCallbackBuffer(buffer);
            final YuvImage image = new YuvImage(data, ImageFormat.NV21, pWidth, pHeight, null);
            ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);

            if (image.compressToJpeg(new Rect(0, 0, pWidth, pHeight), 100, os)) {
                byte[] tmp = os.toByteArray();
                Bitmap src = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);

                Matrix matrix = new Matrix();
                matrix.postScale(-1, 1);
                matrix.postTranslate(src.getWidth(), 0);

                Paint paint = new Paint();
                PaintFlagsDrawFilter pfd= new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
                paint.setFilterBitmap(true); //对Bitmap进行滤波处理
                paint.setAntiAlias(true);//设置抗锯齿
                Rect rect = new Rect(0, 0, src.getWidth(), src.getHeight());
                final Bitmap dst = Bitmap.createBitmap(rect.width(), rect.height(), src.getConfig());
                Canvas canvas = new Canvas(dst);
                canvas.setDrawFilter(pfd);
                canvas.drawBitmap(src, matrix, paint);

                synchronized (mSurfaceHolder) {

                    factor++;
                    if (factor == frameRate) {
                        mFaceDetector.solveFace(dst);
                        factor = 0;
                    }

                    Canvas canvas1 = mSurfaceHolder.lockCanvas();
                    canvas1.drawBitmap(dst, 0, 0, null);
                    mSurfaceHolder.unlockCanvasAndPost(canvas1);
                }
            }
        }
    }
}


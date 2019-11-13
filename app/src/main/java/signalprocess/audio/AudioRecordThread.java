package signalprocess.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import com.smp.soundtouchandroid.SoundTouch;

import java.io.DataOutputStream;
import java.util.LinkedList;

import vtalk.activity.TalkActivity;

/**
 * Created by bullypaulo on 2019/11/12.
 */

public class AudioRecordThread extends Thread
{
    public TalkActivity talkActivity;

    protected AudioRecord m_in_rec ;
    protected int         m_in_buf_size ;
    protected byte []     m_in_bytes ;
    protected boolean     m_keep_running ;
    protected LinkedList<byte[]> m_in_q ;


    public void run()
    {
        try
        {
            byte [] bytes_pkg ;
            m_in_rec.startRecording() ;
            while(m_keep_running)
            {
                m_in_rec.read(m_in_bytes, 0, m_in_buf_size) ;
                bytes_pkg = m_in_bytes.clone() ;
                if(m_in_q.size() >= 2)
                {
                    byte[] processed = m_in_q.removeFirst();

                    talkActivity.postAudio(processed);
                }
                m_in_q.add(bytes_pkg) ;
            }

            m_in_rec.stop() ;
            m_in_rec = null ;
            m_in_bytes = null ;

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void init(TalkActivity talkActivity)
    {
        m_in_buf_size =  AudioRecord.getMinBufferSize(8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        m_in_rec = new AudioRecord(MediaRecorder.AudioSource.MIC,
                8000,
                AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                m_in_buf_size) ;

        m_in_bytes = new byte [m_in_buf_size] ;

        m_keep_running = true ;
        m_in_q=new LinkedList<byte[]>();

        this.talkActivity = talkActivity;

    }

    public void free()
    {
        m_keep_running = false ;
        try {
            Thread.sleep(1000) ;
        } catch(Exception e) {
            Log.d("sleep exceptions...\n","") ;
        }
    }
}

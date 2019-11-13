package signalprocess.audio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.smp.soundtouchandroid.SoundTouch;

import java.io.IOException;

import vtalk.activity.TalkActivity;

/**
 * Created by bullypaulo on 2019/11/12.
 */

public class AudioPlayThread extends Thread
{
    public TalkActivity talkActivity;

    protected AudioTrack m_out_trk ;
    protected int        m_out_buf_size ;
    protected byte []    m_out_bytes ;
    protected boolean    m_keep_running ;

    public void init(TalkActivity talkActivity)
    {
        try
        {
            m_keep_running = true ;

            m_out_buf_size = AudioTrack.getMinBufferSize(8000,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);

            m_out_trk = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                    AudioFormat.CHANNEL_CONFIGURATION_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    m_out_buf_size,
                    AudioTrack.MODE_STREAM);

            m_out_bytes=new byte[m_out_buf_size];

            this.talkActivity = talkActivity;
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
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

    public void run()
    {
        Log.d("playThread", "run");
        byte [] bytes_pkg = null ;
        m_out_trk.play() ;
        while(m_keep_running) {
            try {
                m_out_bytes = talkActivity.getAudio();

                if (m_out_bytes != null) {
                    bytes_pkg = m_out_bytes.clone() ;
                    m_out_trk.write(bytes_pkg, 0, bytes_pkg.length);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

        }
        m_out_trk.stop() ;
        m_out_trk = null ;

    }
}

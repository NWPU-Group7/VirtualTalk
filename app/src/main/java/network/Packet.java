package network;

/**
 * Created by bullypaulo on 2019/11/4.
 */

public class Packet
{
    private double[] emotion = new double[10];
    private String url_emotion;
    private String url_speck;

    public Packet(String url_emotion, String url_speck){
        this.url_emotion = url_emotion;
        this.url_speck = url_speck;
    }

    public double[] getEmotion(){

        return emotion;
    }

    public void postEmotion(double emotion[]) {

        this.emotion = emotion;
    }
}

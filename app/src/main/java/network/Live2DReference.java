package network;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by bullypaulo on 2019/11/4.
 */

public class Live2DReference
{
    public static ArrayList<String> MODEL_SET = new ArrayList<String>(Arrays.asList("live2d/haru/haru.moc", "live2d/shizuku/shizuku.moc"));

    public static final String[] HARU = {
            "live2d/haru/haru.1024/texture_00.png",
            "live2d/haru/haru.1024/texture_01.png",
            "live2d/haru/haru.1024/texture_02.png"};

    public static final String[] SHIZUKY = {
            "live2d/shizuku/shizuku.1024/texture_00.png",
            "live2d/shizuku/shizuku.1024/texture_01.png",
            "live2d/shizuku/shizuku.1024/texture_02.png",
            "live2d/shizuku/shizuku.1024/texture_03.png",
            "live2d/shizuku/shizuku.1024/texture_04.png"
    };
    public static ArrayList<String[]> TEXTURE_SET = new ArrayList<String[]>(Arrays.asList(HARU, SHIZUKY));
}

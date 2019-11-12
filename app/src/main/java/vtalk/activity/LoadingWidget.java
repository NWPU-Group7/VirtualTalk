package vtalk.activity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * 一个简单的等待条
 */
public class LoadingWidget extends ProgressBar {

    public LoadingWidget(Context context) {
        super(context);
    }

    public LoadingWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}

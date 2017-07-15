package xy.temp.outside;

import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

/**
 * Created by Xiang-Yi on 2016-04-24.
 */
public class GameTimer extends CountDownTimer {

    TextView text;
    private long timePassed = 0;
    private long timeRemmaining = 0;
    boolean isRunning = true;
    long pausedAt;
    long interval;
    OnTimerFinishedEventListener mListener;

    public GameTimer(long startTime, long interval, TextView textView) {
        super(startTime, interval);
        this.interval = interval;
        text = textView;
    }

    @Override
    public void onFinish() {
        text.setText("0:00");
        isRunning = false;
        if(mListener!=null) {
            mListener.onFinishedEvent();
        }
    }

    @Override
    public void onTick(long millisUntilFinished) {
        timePassed++;
        text.setText("Time remaining:" + millisUntilFinished);
        timeRemmaining = millisUntilFinished;
        long millis = millisUntilFinished;
        String hms = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        text.setText(hms);
    }

    public long getTimePassed() {
        return timePassed;
    }

    public long getTimeRemaining() {
        return timeRemmaining / 1000;
    }

    public long pauseGameTimer(){
        this.cancel();
        return timeRemmaining;
    }

    public TextView getTextView(){
        return text;
    }

    public void setCustomEventListener(OnTimerFinishedEventListener eventListener) {
        mListener = eventListener;
    }


}
package xy.temp.outside;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class GeneralGameActivity extends AppCompatActivity {

    public GameTimer GameControlTimer;
    TextView timerTextView;

    final String HIGHSCORE = "High_Score";
    int highscore = 0;
    public String scoreType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_game);

        SharedPreferences settings = getSharedPreferences(HIGHSCORE, 0);
        highscore = settings.getInt(scoreType, 0); //get highscore preference
    }

    public void setGameControlTimer(int startTime, int timeIntervals, TextView timerText) {
        timerTextView = timerText;
        GameControlTimer = new GameTimer(startTime, timeIntervals, timerText);
        GameControlTimer.start();
    }

    public GameTimer updateGameControlTimer(int newStartTime, int timeIntervals) {
        GameControlTimer.cancel();
        GameControlTimer = new GameTimer(newStartTime, timeIntervals, timerTextView);
        GameControlTimer.start();
        return GameControlTimer;
    }

    public void updateHighScore(int highscore){
        this.highscore = highscore;
    }

    public int getHighscore(){
        return highscore;
    }

    @Override
    protected void onStop(){
        super.onStop();
        SharedPreferences settings = getSharedPreferences(HIGHSCORE, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(scoreType, highscore);
        editor.commit();

        GameControlTimer.cancel();
    }


}


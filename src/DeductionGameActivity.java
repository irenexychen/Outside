package xy.temp.outside;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DeductionGameActivity extends GeneralGameActivity {
    TextView timerText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.scoreType = "deductionHighScore";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deduction_game);

        //set timer
        timerText = (TextView) findViewById(R.id.txtTime);
        setGameControlTimer(120000, 1000, timerText);
    }



}



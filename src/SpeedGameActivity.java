package xy.temp.outside;

import android.os.Bundle;
import android.widget.TextView;

public class SpeedGameActivity extends GeneralGameActivity {
    TextView timerText;
    BoardFragment speedBoardFragment;
    SideBarMenu sideBarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.scoreType = "speedHighScore";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_game);
        speedBoardFragment = (BoardFragment)getSupportFragmentManager().findFragmentById(R.id.board_fragment);
        speedBoardFragment.GameType = "SpeedGameActivity";

        sideBarFragment = (SideBarMenu)getSupportFragmentManager().findFragmentById(R.id.sidebar_fragment);
        sideBarFragment.GameType = "SpeedGameActivity";

        //set timer
        timerText = (TextView) findViewById(R.id.txtTime);
        setGameControlTimer(30000, 1000, timerText);
    }

    @Override
    protected void onStop(){
        super.onStop();
        speedBoardFragment.MemoryTimer.cancel();
        speedBoardFragment.close();
    }


}

package xy.temp.outside;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;

public class MemoryGameActivity extends GeneralGameActivity{
    TextView timerText;
    public BoardFragment MemoryBoardFragment;
    SideBarMenu sideBarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.scoreType = "memoryHighScore";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);
        MemoryBoardFragment = (BoardFragment)getSupportFragmentManager().findFragmentById(R.id.board_fragment);
        MemoryBoardFragment.GameType = "MemoryGameActivity";

        sideBarFragment = (SideBarMenu)getSupportFragmentManager().findFragmentById(R.id.sidebar_fragment);
        sideBarFragment.GameType = "MemoryGameActivity";


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.memory_text);
        builder.setCancelable(false);
        builder.setPositiveButton("I'm ready!",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set timer
                        timerText = (TextView) findViewById(R.id.txtTime);
                        setGameControlTimer(10000, 1000, timerText);
                        MemoryBoardFragment.scheduleInvisibleTask();
                    }
                });
        builder.show();
    }

    @Override
    protected void onStop(){
        super.onStop();
        MemoryBoardFragment.MemoryTimer.cancel();
        MemoryBoardFragment.close();
    }


}

package xy.temp.outside;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */

public class BoardFragment extends Fragment {
    Unit gridArray[][];

    float unitHeight = 0;
    float unitWidth = 0;
    int xTotal = 0;
    int yTotal = 0;
    float xLocation = 0;
    float yLocation = 0;
    int xCurrentIndex = 0;
    int yCurrentIndex = 0;
    int score = 0;
    int highscore = 0;
    int fails = 0;
    int rounds = 1;
    int newSpeedStartTime = 30000;
    int newMemoryStartTime = 10000;

    ImageView imageview;
    View mainView;
    GameTimer gameTimer;
    Animation animation;
    List<Unit> answers = new LinkedList<Unit>();

    public String GameType;
    final String SPEEDGAMETYPE = "SpeedGameActivity";
    final String MEMORYGAMETYPE = "MemoryGameActivity";

    public Timer MemoryTimer = new Timer();
    long memoryPreviousSystemTime;
    long memoryTimerRemaining;
    TimerTask taskSetInvisible;
    boolean isTaskCompleted;
    boolean isResumeNeeded;

    OnTimerFinishedEventListener mListener;

    public BoardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView =
                inflater.inflate(R.layout.fragment_board, container, false);

        mainView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mainView.post(new Runnable() {
                    public void run() {
                        if (gridArray == null) {
                            unitHeight = (mainView.getHeight() /  Math.round(mainView.getHeight() / 36)); //divide by 50px for exact unit height/width
                            unitWidth = (mainView.getWidth() /  Math.round(mainView.getWidth() / 36));

                            Log.d("GlobalLayout:", "width" + mainView.getWidth() + ", height " + mainView.getHeight());
                            Log.d("GlobalLayout:", " x" + unitWidth + ", y " + unitHeight);
                            Log.d("Total Units:", " x" + xTotal + ", y " + yTotal);

                            xTotal = mainView.getWidth() / 36;
                            yTotal = mainView.getHeight() / 36;
                            gridArray = new Unit[xTotal][yTotal];

                            gameTimer = ((GeneralGameActivity) getActivity()).GameControlTimer;

                            if (GameType.equals(SPEEDGAMETYPE)) {
                                gameTimer.setCustomEventListener(mListener);
                            }

                            initializeBoard();

                            generateDots(mainView, xTotal, yTotal);

                            for (int i = 1; i <= 6; i++) {
                                addRandom();
                            }
                        }
                    }
                });
            }
        });


        mainView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if ((GameType.equals(MEMORYGAMETYPE)) && (!isTaskCompleted)) {
                    return false;
                }

                displayHighscore();

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    xLocation = event.getX();
                    yLocation = event.getY();

                    if ((xLocation > 36 * xTotal) || (yLocation > 36 * yTotal)){
                        return false;
                    }

                    Log.d("&&&TouchListener:", " x" + xLocation + ", y " + yLocation);
                    Log.d("&&&divided by:", " width" + unitWidth + ", height " + unitHeight);

                    xCurrentIndex = (int) (xLocation / unitWidth);
                    yCurrentIndex = (int) (yLocation / unitHeight);

                    Log.d("&&&Index x&y:", (xCurrentIndex + ", " + yCurrentIndex));
                    Log.d("&&&ImageID", "" + gridArray[xCurrentIndex][yCurrentIndex].ImageId);
                    Log.d("&&&Current Surrounding", String.valueOf((gridArray[xCurrentIndex][yCurrentIndex]).mSurrounding));

                    if (GameType.equals(MEMORYGAMETYPE)) {
                        setVisibleMemoryGame();
                    }
                    makeWinnerList(); //find largest
                    regulateRounds(checkForCorrect());  //true == update score, false == show answer

                    if (GameType.equals(SPEEDGAMETYPE)) {
                        if (regulateSpeedTimer()) {
                            winGame();
                            return true;
                        }
                    } else if (GameType.equals(MEMORYGAMETYPE)) {
                        if (regulateMemoryTimer()) {
                            winGame();
                        } else {
                            //continue playing
                            scheduleInvisibleTask();
                        }
                    }

                    addRandom();
                    if (checkDotDensity()) {
                        removeRandom();
                    }
                }
                return true;
            }
        });

        mListener = new OnTimerFinishedEventListener() {
            @Override
            public void onFinishedEvent() {
                //time out!! lose a life
                if (GameType.equals(SPEEDGAMETYPE)) {
                    fails++;
                    Log.d("fails", "" + fails);
                    if (fails == 1) {
                        ((GeneralGameActivity) getActivity()).findViewById(R.id.imgLive1).setVisibility(View.INVISIBLE);
                    } else if (fails == 2) {
                        ((GeneralGameActivity) getActivity()).findViewById(R.id.imgLive2).setVisibility(View.INVISIBLE);
                    } else if(fails >= 3) {
                        ((GeneralGameActivity) getActivity()).GameControlTimer.pauseGameTimer();
                        ((GeneralGameActivity) getActivity()).findViewById(R.id.imgLive3).setVisibility(View.INVISIBLE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Lol GAME OVER. Better luck next time?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Okay.",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ((GeneralGameActivity) getActivity()).finish();
                                    }
                                });
                        builder.show();
                    }
                    //next round
                    if (regulateSpeedTimer()) {
                        winGame();
                    }
                    addRandom();
                    if (checkDotDensity()) {
                        removeRandom();
                    }
                }
            }
        };
        return mainView;
    }


    private void generateDots(View mainView, int xTotal, int yTotal) {
        int img_id = 1;
        TableLayout r = (TableLayout) mainView;
        r.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.TOP);
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();

        TableRow tableRow;
        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
        tableRowParams.width = (int) unitWidth;
        tableRowParams.height = (int) unitHeight;
        tableRowParams.gravity=Gravity.CENTER;

        animation = new AlphaAnimation(1, 0);
        animation.setInterpolator(new AccelerateInterpolator());
        animation.setStartOffset(200);
        animation.setDuration(200);
        animation.setRepeatCount(5);
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will be invisible

        //for loop, width and height
        for (int i = 1; i <= yTotal; i++) {
            tableRow = new TableRow((GeneralGameActivity) getActivity());
            tableRow.setGravity(Gravity.CENTER);
            for (int j = 1; j <= xTotal; j++) {
                imageview = new ImageView((GeneralGameActivity) getActivity());
                imageview.setImageResource(R.drawable.white_dot_small);
                imageview.setTag(new TableData(j, i, img_id));
                imageview.setId(img_id);
                gridArray[j - 1][i - 1].ImageId = img_id;
                imageview.setVisibility(View.INVISIBLE);
                tableRow.addView(imageview, tableRowParams);
                img_id++;
            }
            r.addView(tableRow, tableLayoutParams);
        }
    }


    private void initializeBoard() {
        for (int i = 0; i < xTotal; i++) {
            for (int k = 0; k < yTotal; k++) {
                gridArray[i][k] = new Unit(gridArray, i, k);
            }
        }
    }


    private void setMemoryModeCallback() {
        isTaskCompleted = false;
        taskSetInvisible = new TimerTask() {
            @Override
            public void run() {
                ((GeneralGameActivity) getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < xTotal; i++) {
                            for (int j = 0; j < yTotal; j++) {
                                if (gridArray[i][j].mUsed) {
                                    imageview = (ImageView) mainView.findViewById(gridArray[i][j].ImageId);
                                    imageview.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                        isTaskCompleted = true;
                    }
                });
            }
        };
    }


    private void setVisibleMemoryGame() {
        for (int i = 0; i < xTotal; i++) {
            for (int j = 0; j < yTotal; j++) {
                if (gridArray[i][j].mUsed) {
                    imageview = (ImageView) mainView.findViewById(gridArray[i][j].ImageId);
                    imageview.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    private void displayHighscore() {
        View gameView = (View) mainView.getParent();
        highscore = ((GeneralGameActivity) getActivity()).getHighscore();
        TextView txtHighScoreView = (TextView) gameView.findViewById(R.id.txtHighScore);
        txtHighScoreView.setText(String.valueOf(highscore));
    }


    private void makeWinnerList() {
        answers.clear();
        int largestSurrounding = 0;

        for (int i = 0; i < xTotal; i++) {
            for (int k = 0; k < yTotal; k++) {
                if (gridArray[i][k].mSurrounding > largestSurrounding) {
                    largestSurrounding = gridArray[i][k].mSurrounding;
                }
            }
        }
        Log.d("LARGERST", String.valueOf(largestSurrounding));
        for (int i = 0; i < xTotal; i++) {
            for (int k = 0; k < yTotal; k++) {
                if ((gridArray[i][k].mSurrounding == largestSurrounding)) {
                    answers.add(gridArray[i][k]);
                    Log.d("ANSWER", "" + i + ", " + k + " surrounding: " + gridArray[i][k].mSurrounding);
                    Log.d("ANSWER", "" + i + ", " + k + " imageid: " + gridArray[i][k].ImageId);
                }
            }
        }
        Log.d("NUM OF ANSWERS", "" + answers.size());
    }


    private void winGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Congratulations, you win!");
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok_label,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((GeneralGameActivity) getActivity()).finish();
                    }
                });
        builder.show();
    }


    private void addRandom() {
        int visibleId;
        ImageView imageview;

        int addNum = (int) ((Math.random() * 3) + 2);
        int xIndexNum = (int) (Math.random() * xTotal);
        int yIndexNum = (int) (Math.random() * yTotal);

        for (int i = 1; i <= addNum; i++) {
            while (gridArray[xIndexNum][yIndexNum].mUsed) {
                xIndexNum = (int) (Math.random() * xTotal);
                yIndexNum = (int) (Math.random() * yTotal);
            }
            gridArray[xIndexNum][yIndexNum].mUsed = true;
            gridArray[xIndexNum][yIndexNum].AddSurrounding(xTotal, yTotal);
            visibleId = gridArray[xIndexNum][yIndexNum].ImageId;
            imageview = (ImageView) mainView.findViewById(visibleId);
            imageview.setVisibility(View.VISIBLE);
        }
    }


    private void removeRandom() {
        int visibleId = 0;
        ImageView imageview;

        int removeNum = (int) ((Math.random() * 10) + 6);
        int xIndexNum = (int) (Math.random() * xTotal);
        int yIndexNum = (int) (Math.random() * yTotal);

        for (int i = 1; i <= removeNum; i++) {
            while (gridArray[xIndexNum][yIndexNum].mUsed == false) {
                xIndexNum = (int) (Math.random() * xTotal);
                yIndexNum = (int) (Math.random() * yTotal);
            }
            gridArray[xIndexNum][yIndexNum].mUsed = false;
            gridArray[xIndexNum][yIndexNum].RemoveSurrounding(xTotal, yTotal);
            visibleId = gridArray[xIndexNum][yIndexNum].ImageId;
            imageview = (ImageView) mainView.findViewById(visibleId);
            imageview.setVisibility(View.INVISIBLE);
        }
    }


    private boolean checkDotDensity() {
        float ratioCount = 0.0f;
        for (int i = 0; i < xTotal; i++) {
            for (int j = 0; j < yTotal; j++) {
                if (gridArray[i][j].mUsed) {
                    ratioCount++;
                }
            }
        }
        if (ratioCount / (xTotal * yTotal) >= 0.6) {
            return true;
        }
        return false;
    }


    private boolean checkForCorrect() {
        for (int i = 0; i < answers.size(); i++) {
            if ((xCurrentIndex == answers.get(i).IndexX) && (yCurrentIndex == answers.get(i).IndexY)) {
                updateScore();
                return true;
            }
        }
        showAnswer();
        return false;
    }


    private void updateScore() {
        int tempTimeRemaining;
        View gameView = (View) mainView.getParent();
        TextView txtScoreView = (TextView) gameView.findViewById(R.id.txtScore);
        tempTimeRemaining = (int) gameTimer.getTimeRemaining();
        if (GameType.equals(SPEEDGAMETYPE)) {
            score = score + tempTimeRemaining;
        } else { //assume MEMORYGAMETYPE
            score = score + (60 - newMemoryStartTime / 1000);
        }
        txtScoreView.setText(String.valueOf(score)); //*remaindertime
        if (score > highscore) {
            ((GeneralGameActivity) getActivity()).updateHighScore(score);
            TextView txtHighScoreView = (TextView) gameView.findViewById(R.id.txtHighScore);
            txtHighScoreView.setText(String.valueOf(score));
        }
    }


    public void close() {
        gridArray = null;
        answers.clear();
    }


    public class TableData {
        public int RowIndex;
        public int ColumnIndex;
        public int ImageId;

        public TableData(int rowIndex, int columnIndex, int imageId) {
            RowIndex = rowIndex;
            ColumnIndex = columnIndex;
            ImageId = imageId;
        }
    }


    private boolean regulateRounds(boolean isCorrectAnswer) {
        if (!isCorrectAnswer) {
            fails++;
            Log.d("fails", "" + fails);
            if (fails == 1){
                ((GeneralGameActivity) getActivity()).findViewById(R.id.imgLive1).setVisibility(View.INVISIBLE);
            }else if (fails ==2){
                ((GeneralGameActivity) getActivity()).findViewById(R.id.imgLive2).setVisibility(View.INVISIBLE);
            }
            if (fails >= 3) {
                ((GeneralGameActivity) getActivity()).GameControlTimer.pauseGameTimer();
                ((GeneralGameActivity) getActivity()).findViewById(R.id.imgLive3).setVisibility(View.INVISIBLE);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Lol GAME OVER. Better luck next time?");
                builder.setCancelable(false);
                builder.setPositiveButton("Okay.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((GeneralGameActivity) getActivity()).finish();
                            }
                        });
                builder.show();
            }
        }
        rounds++;
        return true;
    }


    private boolean regulateSpeedTimer() {
        if (rounds >= 1 && rounds < 10) {
            newSpeedStartTime = newSpeedStartTime - 2000; //-1 second every round from 1-20 (-20 seconds, 10 seconds remaining)
        } else if (rounds >= 10 && rounds < 20) {
             //-0.5 second every round from 10-30 (-5 seconds, 5 seconds remaining)
            newSpeedStartTime = newSpeedStartTime - 500;
        } else if (rounds >= 20) {
            if (rounds % 2 == 0) { //-0.25 second every 2 rounds from 20 onwards
                newSpeedStartTime = newSpeedStartTime - 250;
            }
        }
        if (newSpeedStartTime >= 1000) {
            gameTimer = ((GeneralGameActivity) getActivity()).updateGameControlTimer(newSpeedStartTime, 1000);
            gameTimer.setCustomEventListener(mListener);
            Log.d("regulateSpeedTimer", "" + newSpeedStartTime);
            return false; //continue playing
        } else {
            return true; //if no more time to remove = win game
        }
    }


    private boolean regulateMemoryTimer() {
        if (rounds >= 1 && rounds < 10) {
            newMemoryStartTime = newMemoryStartTime - 500; // -0.5 second every rounds from 1-10
        } else if (rounds >= 10 && rounds < 20) {
            if (rounds % 2 == 0) { //-0.5 second every 2 rounds from 10-20
                newMemoryStartTime = newMemoryStartTime - 500;
            }
        } else if (rounds >= 20) {
            newMemoryStartTime = newMemoryStartTime - 250;  //-0.25 second every rounds from 20 onwards
        }

        if (newMemoryStartTime >= 1000) {
            gameTimer = ((GeneralGameActivity) getActivity()).updateGameControlTimer(newMemoryStartTime, 1000);
            return false; //continue playing
        } else {
            return true; //if no more time to remove = win game
        }
    }


    public void scheduleInvisibleTask() {
        setMemoryModeCallback();
        MemoryTimer.schedule(taskSetInvisible, newMemoryStartTime); //from regulatememorytimer
        memoryPreviousSystemTime = System.currentTimeMillis();
    }

    private void showAnswer() {
        ImageView flashingImageView;
        for (int i = 0; i < answers.size(); i++) {
            flashingImageView = (ImageView) mainView.findViewById((answers.get(i)).ImageId);
            //flashingImageView.setVisibility(View.VISIBLE);
            flashingImageView.startAnimation(animation);
            if (!answers.get(i).mUsed){
                flashingImageView.setVisibility(View.INVISIBLE);
            }
        }
    }


    public void pauseTimerTask(){
        long currentSystemTime = System.currentTimeMillis();
        if (currentSystemTime > memoryPreviousSystemTime + newMemoryStartTime){
            memoryTimerRemaining = memoryPreviousSystemTime + newMemoryStartTime - currentSystemTime;
            //Log.d("pauseTimerTask", "" + memoryPreviousSystemTime + ", " + newMemoryStartTime + ", " + System.currentTimeMillis() + " remaining: " + memoryTimerRemaining);
            MemoryTimer.cancel();
            isResumeNeeded = true;
        }
    }


    public void resumeTimerTask(){
        if (isResumeNeeded) {
            newMemoryStartTime = (int) memoryTimerRemaining;
            //Log.d("newMemoryStartTime", "" + newMemoryStartTime);
            MemoryTimer = new Timer();
            scheduleInvisibleTask();
        }
    }
}














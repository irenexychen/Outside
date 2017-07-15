package xy.temp.outside;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class SideBarMenu extends Fragment {

    public String GameType;
    final String SPEEDGAMETYPE = "SpeedGameActivity";
    final String MEMORYGAMETYPE = "MemoryGameActivity";
    long pausedAt;


    public SideBarMenu() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_side_bar_menu, container, false);

        View btnHelp = rootView.findViewById(R.id.btnHelp);


        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder helpBuilder = new AlertDialog.Builder(getActivity());
                helpBuilder.setMessage(R.string.help_text);
                helpBuilder.setCancelable(false);
                helpBuilder.setPositiveButton("Got it",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((GeneralGameActivity) getActivity()).GameControlTimer = new GameTimer(pausedAt, 1000, ((GeneralGameActivity) getActivity()).GameControlTimer.getTextView());
                                ((GeneralGameActivity) getActivity()).GameControlTimer.start();
                                if (GameType == MEMORYGAMETYPE) {
                                    (((MemoryGameActivity) getActivity()).MemoryBoardFragment).resumeTimerTask();
                                }
                            }
                        });
                pausedAt = ((GeneralGameActivity) getActivity()).GameControlTimer.pauseGameTimer();
                if (GameType == MEMORYGAMETYPE) {
                    (((MemoryGameActivity) getActivity()).MemoryBoardFragment).pauseTimerTask();
                }
                helpBuilder.show();
            }
        });

        View btnPause = rootView.findViewById(R.id.btnPause);
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder pauseBuilder = new AlertDialog.Builder(getActivity());
                pauseBuilder.setMessage("Paused. Good luck~");
                pauseBuilder.setCancelable(false);
                pauseBuilder.setPositiveButton("Resume",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ((GeneralGameActivity) getActivity()).updateGameControlTimer((int)pausedAt, 1000);
                                if (GameType == MEMORYGAMETYPE) {
                                    (((MemoryGameActivity) getActivity()).MemoryBoardFragment).resumeTimerTask();
                                }
                            }
                        });
                pausedAt = ((GeneralGameActivity) getActivity()).GameControlTimer.pauseGameTimer();
                if (GameType == MEMORYGAMETYPE) {
                    (((MemoryGameActivity) getActivity()).MemoryBoardFragment).pauseTimerTask();
                }
                pauseBuilder.show();
            }
        });
        return rootView;
    }
}




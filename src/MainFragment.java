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
public class MainFragment extends Fragment {

    private AlertDialog mDialog;


    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        View newbtnHelp = rootView.findViewById(R.id.btnHelp);

        newbtnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.help_text);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // nothing
                            }
                        });
                mDialog = builder.show();
            }
        });

        View newbtnSpeedPlay = rootView.findViewById(R.id.btnSpeedPlay);
        newbtnSpeedPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SpeedGameActivity.class);
                getActivity().startActivity(intent);
            }
        });

        View newbtnMemoryPlay = rootView.findViewById(R.id.btnMemoryPlay);
        newbtnMemoryPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MemoryGameActivity.class);
                getActivity().startActivity(intent);
            }
        });

        /*View newbtnDeductionPlay = rootView.findViewById(R.id.btnDeductionPlay);
        newbtnDeductionPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DeductionGameActivity.class);
                getActivity().startActivity(intent);
            }
        });*/


        return rootView;

    }

}

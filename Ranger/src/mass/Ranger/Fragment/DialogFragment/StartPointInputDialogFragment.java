package mass.Ranger.Fragment.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import com.example.travinavi.R;


/**
 * Created by v-mengyl on 2015/1/25.
 */
public class StartPointInputDialogFragment extends DialogFragment {

    public static StartPointInputDialogFragment newInstance(String title) {
        StartPointInputDialogFragment fragment = new StartPointInputDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        String title = getArguments().getString("title");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.start_point_input_dialog, null))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EndPointInputDialogFragment endPointInputDialogFragment = EndPointInputDialogFragment.newInstance("请输入终点:");
                        endPointInputDialogFragment.show(getFragmentManager(), "endPointInputDialog");
                    }
                })
                .setNegativeButton("跳过", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EndPointInputDialogFragment endPointInputDialogFragment = EndPointInputDialogFragment.newInstance("请输入终点:");
                        endPointInputDialogFragment.show(getFragmentManager(), "endPointInputDialog");
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

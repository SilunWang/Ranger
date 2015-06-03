package mass.Ranger.Fragment.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import com.example.travinavi.R;

/**
 * Created by v-mengyl on 2015/1/25.
 */
public class EndPointInputDialogFragment extends DialogFragment {

    public static EndPointInputDialogFragment newInstance(String title){

        EndPointInputDialogFragment fragment = new EndPointInputDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        String title =getArguments().getString("title");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.end_point_input_dialog,null);
        builder.setView(view)
            .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    ShareCheckDialogFragment shareCheckDialogFragment = ShareCheckDialogFragment.newInstance("查看");
                    shareCheckDialogFragment.show(getFragmentManager(), "shareCheckDialog");
                }
            })
            .setNegativeButton("上一步", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    StartPointInputDialogFragment startPointInputDialogFragment = StartPointInputDialogFragment.newInstance("请输入起点:");
                    startPointInputDialogFragment.show(getFragmentManager(), "startPointInputDialog");
                }
            });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}

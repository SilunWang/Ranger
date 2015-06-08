package mass.Ranger.Fragment.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by v-mengyl on 2015/1/22.
 */
public class CompleteAlertDialogFragment extends DialogFragment {

    public static CompleteAlertDialogFragment newInstance(String title) {
        CompleteAlertDialogFragment fragment = new CompleteAlertDialogFragment();
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
        builder.setTitle(title)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        StartPointInputDialogFragment startPointInputDialogFragment = StartPointInputDialogFragment.newInstance("请输入起点:");
                        startPointInputDialogFragment.show(getFragmentManager(), "startPointInputDialog");
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}

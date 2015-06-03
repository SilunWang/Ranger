package mass.Ranger.Fragment.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.example.travinavi.R;

/**
 * Created by v-mengyl on 2015/1/25.
 */
public class ShareCheckDialogFragment extends DialogFragment {
    private ImageButton weixinShareBtn;
    private ImageButton weiboShareBtn;
    private Button otherShareBtn;

    public static ShareCheckDialogFragment newInstance(String title){
        ShareCheckDialogFragment fragment = new ShareCheckDialogFragment();
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
        View view = inflater.inflate(R.layout.share_check_dialog,null);
        initView(view);
        builder.setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
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

    public void initView(View view){
        weixinShareBtn = (ImageButton) view.findViewById(R.id.id_weixin_share);
        weiboShareBtn = (ImageButton) view.findViewById(R.id.id_weibo_share);
        otherShareBtn = (Button) view.findViewById(R.id.id_other_share);

        weixinShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setPackage("com.tencent.mm");
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.putExtra(Intent.EXTRA_TEXT, "http://navimap.azurewebsites.net/Map/Index?id=78578377-9e1b-4629-a5d0-4440552ca45b");
                intent.putExtra(Intent.EXTRA_TITLE, "我是标题");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, "请选择"));
            }
        });
        weiboShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setPackage("com.sina.weibo"); //setPackage方法直接跳到新浪微博
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.putExtra(Intent.EXTRA_TEXT, "http://navimap.azurewebsites.net/Map/Index?id=78578377-9e1b-4629-a5d0-4440552ca45b");
                intent.putExtra(Intent.EXTRA_TITLE, "我是标题");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, "请选择"));
            }
        });
        otherShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                intent.putExtra(Intent.EXTRA_TEXT, "http://navimap.azurewebsites.net/Map/Index?id=78578377-9e1b-4629-a5d0-4440552ca45b");
                intent.putExtra(Intent.EXTRA_TITLE, "我是标题");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, "请选择"));
            }
        });
    }
}

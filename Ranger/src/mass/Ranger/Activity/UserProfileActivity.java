package mass.Ranger.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.travinavi.R;
import mass.Ranger.View.CircleImageView;

/**
 * Created by v-mengyl on 1/10/2015.
 */
public class UserProfileActivity extends FragmentActivity implements OnClickListener {
    private ImageButton toolRouteButton, toolUserButton,toolGuiderButton;
    private TextView toolRouteTv,toolUserTv;
    private CircleImageView userImageButton;

    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.user_profile);

        initView();
    }

    private void initView(){
        toolRouteButton = (ImageButton) findViewById(R.id.id_toolbar_route_button);
        toolGuiderButton = (ImageButton) findViewById(R.id.id_toolbar_guider_button);
        toolUserButton = (ImageButton) findViewById(R.id.id_toolbar_user_button);
        toolRouteTv = (TextView) findViewById(R.id.id_toolbar_route_tv);
        toolUserTv = (TextView) findViewById(R.id.id_toolbar_user_tv);
        userImageButton = (CircleImageView) findViewById(R.id.id_user_image);

        toolRouteButton.setOnClickListener(this);
        toolGuiderButton.setOnClickListener(this);
        toolUserButton.setOnClickListener(this);
        userImageButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        clickToolBarButton(v);

    }

    /**
     * 点击toolBar中的按钮
     * @param v
     */
    private void clickToolBarButton(View v) {
        switch (v.getId())
        {
            case R.id.id_toolbar_route_button:
                Intent i1 = new Intent(getApplication(), MainActivity.class);
                startActivity(i1);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
            case R.id.id_toolbar_guider_button:
                Intent i2 = new Intent(getApplication(), TrackingActivity.class);
                startActivity(i2);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
            case R.id.id_toolbar_user_button:
                Intent i3 = new Intent(getApplication(), UserProfileActivity.class);
                startActivity(i3);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                break;
            case R.id.id_user_image:
                Intent i4 = new Intent(getApplication(), LoginActivity.class);
                startActivity(i4);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        }

    }

}

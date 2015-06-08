package mass.Ranger.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.travinavi.R;
import mass.Ranger.User.UserInfo;
import mass.Ranger.Web.WebHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Handler;


public class RegisterActivity extends Activity {

    private static Handler handler;

    private EditText newUserNameEdt;
    private EditText newPasswordEdt1;
    private EditText newPasswordEdt2;
    private WebHelper webHelper;
    public UserInfo userInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        webHelper = new WebHelper(this);

        newUserNameEdt = (EditText) findViewById(R.id.etNewUserName);
        newPasswordEdt1 = (EditText) findViewById(R.id.etNewPassword);
        newPasswordEdt2 = (EditText) findViewById(R.id.etNewPasswordConfirm);

        Button confirmBtn = (Button) findViewById(R.id.btnSingUpConfirm);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterMe();
            }
        });

    }

    private void RegisterMe() {
        //Get user details.
        String username = newUserNameEdt.getText().toString();
        String password = newPasswordEdt1.getText().toString();
        String confirmPassword = newPasswordEdt2.getText().toString();

        //Check if all fields have been completed.
        if (username.equals("") || password.equals("")) {
            Toast.makeText(getApplicationContext(),
                    "Please ensure all fields have been completed.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Check password match.
        if (!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(),
                    "The password does not match.",
                    Toast.LENGTH_SHORT).show();
            newPasswordEdt2.setText("");
            return;
        }

        //Encrypt password with MD5.
        password = md5(password);
        //Send username and password to web, implement registering.
        userInfo = webHelper.registerInWeb(username, password);

        if (userInfo.getUserId() == null) {
            Toast.makeText(getApplicationContext(),
                    "Register error, please try again",
                    Toast.LENGTH_SHORT);
        } else {
            saveLoggedInUId(userInfo.getUserName(),
                    userInfo.getUserId(),
                    userInfo.getSessionID()
            );
            Intent i = new Intent(RegisterActivity.this, UserProfileActivity.class);
            startActivity(i);
        }


    }

    /**
     * Hashes the password with MD5.
     *
     * @param s
     * @return
     */
    private String md5(String s) {
        try {

            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            return s;
        }
    }

    private void saveLoggedInUId(String username, String id, String sessionID) {
        SharedPreferences userSettings = getSharedPreferences(LoginActivity.USER_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString("username", username);
        editor.putString("userID", id);
        editor.putString("sessionID", sessionID);
        editor.putBoolean("loggedIn", true);
        editor.commit();
    }

}

package mass.Ranger.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.example.travinavi.R;
import mass.Ranger.Web.WebHelper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginActivity extends Activity {

    public static final String USER_PREFS = "UserSharedPreferences";
    private EditText theUsername;
    private EditText thePassword;
    private CheckBox rememberDetails;
    private TextView forgetSecretLink;
    private Button btnSignIn;
    private Button btnSignUp;
    private Button btnSinaSignIn;
    private Button btnTencentSignIn;
    private Button btnBaiduSignIn;

    public Context context;
    private WebHelper webHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        webHelper = new WebHelper(this);

        theUsername = (EditText) findViewById(R.id.etUserName);
        thePassword = (EditText) findViewById(R.id.etPass);
        rememberDetails = (CheckBox) findViewById(R.id.checkBoxRemember);
        forgetSecretLink = (TextView) findViewById(R.id.forget_secret_link);
        btnSignIn = (Button) findViewById(R.id.loginBtnSingIn);
        btnSignUp = (Button) findViewById(R.id.loginBtnSingUp);

        btnSinaSignIn = (Button) findViewById(R.id.sinaSignInButton);
        btnTencentSignIn = (Button) findViewById(R.id.tencentSignInButton);
        btnBaiduSignIn = (Button) findViewById(R.id.baiduSignInButton);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                logMeIn();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(i);
            }
        });

        rememberDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rememberMe();
            }
        });
        forgetSecretLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnSinaSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnTencentSignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });
        btnBaiduSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * Handles the remember password option.
     */
    private void rememberMe() {
        boolean thisRemember = rememberDetails.isChecked();

        SharedPreferences prefs = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("rememberMe", thisRemember);
        editor.commit();
    }

    /**
     * This method handles the user login process.
     */
    private void logMeIn() {
        //Get the username and password
        String thisUsername = theUsername.getText().toString();
        String thisPassword = thePassword.getText().toString();
        //Assign the hash to the password
        thisPassword = md5(thisPassword);

        String sessionID = webHelper.getSessionIdFromWeb();
        String validateResult = webHelper.validateInWeb(thisUsername, thisPassword, sessionID);
        if(validateResult.equals("SUCCESS")){
            Toast.makeText(getApplicationContext(),
                    "Login Success !",
                    Toast.LENGTH_SHORT);

            SharedPreferences pref = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
            String userID = pref.getString("userID",""); //如果userID为空，需要web返回userID
            saveLoggedInUserInfo(thisUsername, userID, sessionID);
            //Jump to UserInfo layout
            Intent i = new Intent(LoginActivity.this, UserProfileActivity.class);
            startActivity(i);
        }
        else{
            Toast.makeText(getApplicationContext(),
                    "Login error, please try again",
                    Toast.LENGTH_SHORT);
        }

    }

    private void saveLoggedInUserInfo( String username, String id, String sessionID) {
        SharedPreferences userSettings = getSharedPreferences(USER_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString("username", username);
        editor.putString("userID", id);
        editor.putString("sessionID", sessionID);
        editor.putBoolean("loggedIn",true);
        boolean rememberThis = rememberDetails.isChecked();
        editor.putBoolean("rememberThis", rememberThis);
        editor.commit();
    }

    /**
     * Deals with the password encryption.
     * @param s The password.
     * @return
     */
    private String md5(String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        }

        catch (NoSuchAlgorithmException e) {
            return s;
        }
    }

}

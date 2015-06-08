package mass.Ranger.Web;

import android.content.Context;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import mass.Ranger.Activity.MainActivity;
import mass.Ranger.Activity.TrackingActivity;
import mass.Ranger.Device.BackgroundPositioning;
import mass.Ranger.User.UserInfo;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.*;
import java.nio.charset.Charset;

/**
 * Author: Silun Wang
 * Alias: v-silwa
 * Email: badjoker@163.com
 */

public class WebHelper {

    public final static Charset UTF8 = Charset.forName("UTF8");
    public final static String baseURL = "http://23.102.225.129:80/DataProvider.svc/";
    //public final static String baseURL ="http://d16297828e524316bbb9bd26c99fc4df.cloudapp.net/DataProvider.svc/";

    String responseString = "";
    Context mContext;
    UserInfo newUser;
    String sessionIdResult;
    String validateResult;

    public WebHelper(Context context) {
        mContext = context;
    }

    public WebHelper() {
    }

    public UserInfo registerInWeb(final String username, final String password) {
        final String registerUrl = baseURL + "User/Register/" + username + "/" + password;
        Thread registerThread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpGet httpGet = new HttpGet(registerUrl);
                        HttpResponse response = httpClient.execute(httpGet);
                        HttpEntity entity = response.getEntity();
                        String userID = getASCIIContentFromEntity(entity);
                        Log.i("GO", "registerInWeb---->" + userID);
                        newUser = new UserInfo(username, userID);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        registerThread.start();
        return newUser;
    }

    public String getSessionIdFromWeb() {
        final String sessionIdUrl = baseURL + "User/InitialSession";
        Thread getSessionIdThread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpGet httpGet = new HttpGet(sessionIdUrl);
                        HttpResponse response = httpClient.execute(httpGet);
                        HttpEntity entity = response.getEntity();
                        sessionIdResult = getASCIIContentFromEntity(entity);
                        Log.i("GO", "getSessionID---->" + sessionIdResult);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        getSessionIdThread.start();
        return sessionIdResult;
    }

    public String validateInWeb(final String username, final String password, final String sessionID) {
        final String validateUrl = baseURL + "User/Validate/" + sessionID + "/" + username + "/" + password;
        Thread validateThread = new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (this) {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpGet httpGet = new HttpGet(validateUrl);
                        HttpResponse response = httpClient.execute(httpGet);
                        HttpEntity entity = response.getEntity();
                        validateResult = getASCIIContentFromEntity(entity);
                        Log.i("GO", "validateInWeb---->" + validateResult);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        validateThread.start();
        return validateResult;
    }

    public void GetData() {
        Thread con = new Thread() {

            @Override
            public void run() {
                try {
                    synchronized (this) {
                        HttpClient Client = new DefaultHttpClient();
                        String URL0 = baseURL + "Data/Download/" + BackgroundPositioning.patrollingID;
                        // Create Request to server and get response
                        HttpGet httpget = new HttpGet(URL0);
                        Log.i("GO", "Entering the server session" + URL0);
                        HttpResponse response = Client.execute(httpget);
                        HttpEntity entity = response.getEntity();
                        // you must use byte[] to transfer zip to avoid mistakes
                        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
                        entity.writeTo(byteOutputStream);
                        byte[] bytes = byteOutputStream.toByteArray();

                        File root = Environment.getExternalStorageDirectory();
                        root = new File(root, "/Travi-Download");

                        if (!root.exists())
                            root.mkdirs();

                        File downFile = new File(root, BackgroundPositioning.patrollingID + ".zip");
                        downFile.createNewFile();
                        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(downFile));
                        out.write(bytes);
                        out.flush();
                        out.close();

                        String str = "downloadDone";
                        Message message = Message.obtain();
                        message.obj = str;
                        MainActivity.getHandler().sendMessage(message);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        con.start();
    }

    public void PostData(final String str) {
        Thread con = new Thread() {

            @Override
            public void run() {
                try {
                    synchronized (this) {
                        HttpClient Client = new DefaultHttpClient();
                        String URL1 = baseURL + "Data/Upload/";
                        Log.i("GO", TrackingActivity.zipFilename);

                        /* example for setting a HttpMultipartMode */
                        File file = new File(str);
                        URL1 += BackgroundPositioning.patrollingID;
                        byte[] byteFile = getByte(file);
                        ByteArrayEntity entity2 = new ByteArrayEntity(byteFile);
                        HttpPost httpPost = new HttpPost(URL1);
                        httpPost.setEntity(entity2);
                        Log.i("GO", "Entering the server session");
                        HttpResponse response1 = Client.execute(httpPost);
                        Log.i("GO", "Response Ok");
                        HttpEntity entity = response1.getEntity();
                        Log.i("GO", "Entity Ok");
                        responseString = getASCIIContentFromEntity(entity);
//                        if (responseString.equals("SUCCESS")){
//                            String str = "uploadDone";
//
//                            Log.i("SEND", str);
//                            Message message = Message.obtain();
//                            message.obj = str;
//                            MainActivity.getHandler().sendMessage(message);
//                        }
                        Log.i("GO", responseString);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        con.start();
    }


    protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {

        InputStream in = entity.getContent();
        StringBuffer out = new StringBuffer();
        int n = 1;
        while (n > 0) {
            byte[] b = new byte[4096];
            n = in.read(b);
            if (n > 0) out.append(new String(b, 0, n));
        }
        return out.toString();
    }

    public static byte[] getByte(File file) throws Exception {
        byte[] bytes = null;
        if (file != null) {
            InputStream is = new FileInputStream(file);
            int length = (int) file.length();
            if (length > Integer.MAX_VALUE)   //当文件的长度超过了int的最大值
            {
                System.out.println("this file is max ");
                return null;
            }
            bytes = new byte[length];
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            //如果得到的字节长度和file实际的长度不一致就可能出错了
            if (offset < bytes.length) {
                System.out.println("file length is error");
                return null;
            }
            is.close();
        }
        return bytes;
    }

//    public String getSessionID(){
//        getSessionIdFromWeb();
//        this.sessionID = intent.getStringExtra("sessionID");
//        Log.i("getSessionID",sessionID);
//        return this.sessionID;
//    }
}

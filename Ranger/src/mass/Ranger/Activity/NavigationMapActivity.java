package mass.Ranger.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.*;
import com.example.travinavi.R;

/**
 * Created by v-yucliu on 2015/1/12.
 * Changed by v-mengyl on 2015/1/26.
 */
public class NavigationMapActivity extends Activity {
    String ID = "78578377-9e1b-4629-a5d0-4440552ca45b";
    WebView myWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_trace_map);

        Intent i_getvalue = getIntent();
        String action = i_getvalue.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {

            Uri uri = i_getvalue.getData();
            if (uri != null) {
                ID = uri.getQueryParameter("id");
                Log.i("show id", "ID=" + ID);
            }
        }
        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.addJavascriptInterface(new JsInteration(), "control");
        myWebView.setWebChromeClient(new WebChromeClient() {
        });
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 4.1.1; HTC One X Build/JRO03C) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.58 Mobile Safari/537.31 TraviNaviApp");
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                clientAction();
            }

        });
        myWebView.loadUrl("http://navimap.azurewebsites.net/Map/Index?id=" + ID);

    }

    public void drawNavPoint(int x, int y, int radius) {
        String call = "javascript:drawNav(" + x + "," + y + "," + radius + ")";
        myWebView.loadUrl(call);
    }

    public class JsInteration {

        @JavascriptInterface
        public void startNavigation() {
            Log.i("webview", "start Navigation");
        }

        @JavascriptInterface

        public void endNavigation() {
            Log.i("webview", "end Navigation");
        }
    }

    public void clientAction() {
        //drawNavPoint(50,50,10);
    }
}

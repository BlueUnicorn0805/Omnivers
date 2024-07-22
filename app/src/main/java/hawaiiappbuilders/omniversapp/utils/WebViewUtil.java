package hawaiiappbuilders.omniversapp.utils;

import android.content.Context;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import hawaiiappbuilders.omniversapp.global.BaseActivity;
import hawaiiappbuilders.omniversapp.global.MyChromeClient;

public class WebViewUtil {
    public static final String TAG = "WebView";
    public static final String YOUTUBE_URL = "https://www.youtube.com/embed/";
    public static final String YOUTUBE_IMG_URL = "http://img.youtube.com/vi/%s/0.jpg";
    // public static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";
    public static final String TEXT_HTML = "text/html";
    public static final String UTF_8 = "utf-8";


//    <iframe width="560" height="315" src="https://www.youtube.com/embed/oBuWJHVX2x8" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" allowfullscreen></iframe>

    public static WebView initialize(Context context, WebView webView) {
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        // The default behavior when a user clicks on a link inside the webpage
        // is to open the systems default browser app.
        // This can break the user experience of the app users.
        // To keep page navigation within the WebView and hence within the app,
        // we need to create a subclass of WebViewClient,
        // and override its shouldOverrideUrlLoading(WebView webView, String url) method.
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyChromeClient((BaseActivity) context) {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d(TAG, cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId() );
                return true;
            }
        });
        // JavaScript is by default turned off in WebView widgets.
        // Hence web pages containing javascript references won’t work properly.
        // To enable java script the following snippet needs to be called on the webview instance:
        webView.getSettings().setJavaScriptEnabled(true);
        // webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.setInitialScale(1);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);
        return webView;
    }

    public static String getFrameVideo(String videoId) {
        String frameVideo = "<html><body>Video From YouTube<br><iframe width=\"420\" height=\"315\" src=\"https://www.youtube.com/embed/%s\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
        return String.format(frameVideo, videoId);
    }

    public static class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            // When the shouldOverrideUrlLoading() method returns false,
            // the URLs passed as parameter to the method is loaded inside the WebView
            // instead of the browser.
            webView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(url);
                }
            }, 500);
            return false;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Log.i(TAG, "Error code : " + error.getErrorCode() + " Desc : " + error.getDescription());
            if (error.getErrorCode() == 404) {
                // view.loadUrl("file:///android_asset/Page_Not_found.html");
                // TODO:  Show page not found
            }
        }
    }
}

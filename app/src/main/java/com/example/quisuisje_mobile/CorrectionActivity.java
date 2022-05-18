package com.example.quisuisje_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.example.quisuisje_mobile.util.RestHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class CorrectionActivity extends AppCompatActivity {
    ProgressBar bar;
    JSONArray quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correction);
        RequestParams params = new RequestParams();
        params.put("identifier", getIntent().getStringExtra("identifier"));
        params.put("topic", getIntent().getStringExtra("topic"));
        bar = findViewById(R.id.progressBar6);
        RestHttpClient.post("corrige", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                bar.setVisibility(View.GONE);
                try {
                    JSONObject res=new JSONObject(new String(responseBody));
                    WebView web = findViewById(R.id.web);
                    String summary = "<h2>" + res.getJSONObject("res").getString("topic") + "</h2>";

                    quiz = res.getJSONObject("res").getJSONArray("quiz");

                    summary = addHtml(summary);
                    web.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            return false;
                        }
                    });
                    WebSettings webSettings = web.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                    webSettings.setUseWideViewPort(true);
                    web.loadData(summary, "text/html", "utf-8");
//                    web.setWebViewClient(new WebViewClient());
//                    web.setWebChromeClient(new WebChromeClient());


                }catch (JSONException e){
                    Log.i("LessonActivity", "unexpected JSON exception", e);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                bar.setVisibility(View.GONE);
                Log.i("LessonActivity", new String(responseBody));
            }
        });
    }

    public String addHtml(String summary) throws JSONException {
        Log.i("lesson.length", Integer.toString(quiz.length()));
        for(int i = 0; i < quiz.length(); i++) {
            summary += "<h3>" + quiz.getJSONObject(i).getString("question") + "</h3>" +
                    "<ul> <li style=\" color: green\"> vraie: " + quiz.getJSONObject(i).getString("trueAnswer") + "</li>";
            if(!quiz.getJSONObject(i).getString("trueAnswer").equalsIgnoreCase(quiz.getJSONObject(i).getString("givenAnswer")))
                    summary += "<li style=\"color:red\"> ta réponse: " + quiz.getJSONObject(i).getString("givenAnswer") + "</li>";
            summary += "</ul>";
        }
        Log.i("Summary", summary);
        return summary;
    }
}
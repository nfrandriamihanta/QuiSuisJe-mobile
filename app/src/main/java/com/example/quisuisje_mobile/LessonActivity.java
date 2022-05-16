package com.example.quisuisje_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.quisuisje_mobile.util.RestHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LessonActivity extends AppCompatActivity {
    ProgressBar bar;
    JSONArray lesson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        bar = findViewById(R.id.progressBar2);
        bar.setVisibility(View.VISIBLE);

        RequestParams params = new RequestParams();
        params.put("topic", getIntent().getStringExtra("topic"));
        RestHttpClient.post("lecon", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                bar.setVisibility(View.GONE);
                try {
                    JSONObject res=new JSONObject(new String(responseBody));
                    WebView web = findViewById(R.id.web);
                    String summary = "<h2>" + res.getJSONObject("res").getString("topic") + "</h2>";

                    lesson = res.getJSONObject("res").getJSONArray("lesson");

                    Log.i("LessonActivity", res.getJSONObject("res").getJSONArray("lesson").toString());

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
        Log.i("lesson.length", Integer.toString(lesson.length()));
        for(int i = 0; i < lesson.length(); i++) {
            summary += "<h3>" + lesson.getJSONObject(i).getString("title") + "</h3>" +
                    "<iframe width=\"300\" height=\"315\" src=\" " + lesson.getJSONObject(i).getString("video") + " \" allowfullscreen></iframe>";
        }
        Log.i("Summary", summary);
        return summary;
    }
}
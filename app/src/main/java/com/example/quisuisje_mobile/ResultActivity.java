package com.example.quisuisje_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.example.quisuisje_mobile.util.RestHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ResultActivity extends AppCompatActivity {
    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        bar = findViewById(R.id.progressBar5);
        bar.setVisibility(View.VISIBLE);

        RequestParams param = new RequestParams();
        param.put("identifier", getIntent().getStringExtra("identifier"));

        RestHttpClient.post("resultats", param, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                bar.setVisibility(View.GONE);
                try {
                    JSONArray res=new JSONObject(new String(responseBody)).getJSONArray("res");
                    LinearLayout linearLayout = findViewById(R.id.linear);
                    for(int i = 0; i < res.length(); i ++) {
                        Button btn = new Button(getApplicationContext());
                        btn.setText(res.getJSONObject(i).get("topic").toString());
                        linearLayout.addView(btn);

                        int finalI = i;
                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    redirect(res.getJSONObject(finalI).getString("topic"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                bar.setVisibility(View.GONE);
            }
        });
    }
    private void redirect(String topic) {
        Intent i = new Intent(ResultActivity.this, CorrectionActivity.class);
        i.putExtra("identifier", getIntent().getStringExtra("identifier"));
        i.putExtra("topic", topic);
        startActivity(i);
        finish();
    }
}
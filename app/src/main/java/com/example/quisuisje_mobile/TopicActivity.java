package com.example.quisuisje_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quisuisje_mobile.util.RestHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class TopicActivity extends AppCompatActivity {
    public String[] topics;
    public String test = "";
    public ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);

        bar = findViewById(R.id.progressBar3);
        bar.setVisibility(View.VISIBLE);

        RestHttpClient.get("themes", null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                bar.setVisibility(View.GONE);
                try {
                    JSONObject res=new JSONObject(new String(responseBody));
                    toStringArray(res.getJSONArray("res"));
                    addFragment();
                    Log.i("TopicActivity", res.getJSONArray("res").getJSONObject(1).getString("topic"));
                }catch (JSONException e){
                    Log.i("TopicActivity", "unexpected JSON exception", e);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                bar.setVisibility(View.GONE);
                Log.i("TopicActivity", new String(responseBody));
            }
        });

    }

    private void toStringArray(JSONArray array) {
        topics = new String[array.length()];
        for (int i = 0; i < array.length(); i++) {
            try {
                topics[i] = array.getJSONObject(i).getString("topic");
            } catch (JSONException e) {
                Log.i("TopicActivity", "unexpected JSON exception", e);
            }

        }
    }

    private void addFragment() {
        Bundle bundle = new Bundle();
        bundle.putStringArray("titles", topics);
        bundle.putString("identifier", getIntent().getStringExtra("identifier"));
        bundle.putString("mode", getIntent().getStringExtra("mode"));
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragmentContainerView2, ListFragment.class, bundle)
                .commit();
    }

}

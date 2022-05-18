package com.example.quisuisje_mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.quisuisje_mobile.util.RestHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;

public class QuizActivity extends AppCompatActivity {


    TextView[] tabQuestion;
    EditText[] tabGivenAnswer;
    String[] tabTrueAnswer;
    JSONArray data;
    JSONObject body;
    ProgressBar bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        bar = findViewById(R.id.progressBar4);
        bar.setVisibility(View.VISIBLE);

        RequestParams params = new RequestParams();
        params.put("topic", getIntent().getStringExtra("topic"));
        RestHttpClient.post("lecon", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    bar.setVisibility(View.GONE);
                    JSONObject res=new JSONObject(new String(responseBody));
                    tabQuestion = new TextView[res.getJSONObject("res").getJSONArray("lesson").length()];
                    tabGivenAnswer = new EditText[res.getJSONObject("res").getJSONArray("lesson").length()];
                    tabTrueAnswer = new String[res.getJSONObject("res").getJSONArray("lesson").length()];

                    data = res.getJSONObject("res").getJSONArray("lesson");

                    LinearLayout linearLayout = findViewById(R.id.linear);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(10,10,10,10);
                    linearLayout.setLayoutParams(params);
                    for(int i = 0; i < tabQuestion.length; i++) {
                        TextView text = new TextView(getApplicationContext());
                        text.setText(data.getJSONObject(i).getString("question"));
                        text.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
                        text.setTextColor(getResources().getColor(R.color.black));

                        EditText editText = new EditText(getApplicationContext());
                        editText.setHint("Ta réponse");
                        tabQuestion[i] = text;
                        tabGivenAnswer[i] = editText;
                        tabTrueAnswer[i] = data.getJSONObject(i).getString("trueAnswer");
                        linearLayout.addView(text);
                        linearLayout.addView(editText);
                    }

                    Button btnSubmit = new Button(getApplicationContext());
                    btnSubmit.setText("Soumettre");
                    linearLayout.addView(btnSubmit);

                    btnSubmit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bar.setVisibility(View.VISIBLE);
                            String json = "{" +
                                    "\"identifier\": \""+  getIntent().getStringExtra("identifier")   +"\"," +
                                    "\"topic\": \"" + getIntent().getStringExtra("topic") + "\"," +
                                    "\"quiz\": [";
                            for(int i = 0; i < tabQuestion.length; i++) {
                                json += "{" +
                                        "\"question\": \"" + tabQuestion[i].getText().toString() + "\"," +
                                        "\"trueAnswer\": \"" + tabTrueAnswer[i] + "\"," +
                                        "\"givenAnswer\": \"" + tabGivenAnswer[i].getText().toString() + "\"" +
                                        "}" ;
                                if(i != tabQuestion.length - 1)
                                    json += ",";
                            }
                            json += "]}";
                            try {
                                body = new JSONObject(json);
                                ByteArrayEntity entity = new ByteArrayEntity(body.toString().getBytes(StandardCharsets.UTF_8));
                                RestHttpClient.post("soumission", entity, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        try {
                                            JSONObject res=new JSONObject(new String(responseBody));
                                            bar.setVisibility(View.GONE);
                                            redirect();

//                                            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
//                                                    .setSmallIcon(R.drawable.logo)
//                                                    .setContentTitle("QUI SUIS-JE?")
//                                                    .setContentText("Tes réponses ont bien été enregistrées")
//                                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
////                                                    .setContentIntent(setPendingIntent())
////                                                    .setAutoCancel(true);
//                                            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//// notificationID allows you to update the notification later on.
//                                            mNotificationManager.notify(0,builder.build());

                                            Log.i("QuizActivity", res.toString());
                                        }catch (JSONException e) {
                                            Log.i("QuizActivity", "unexpected JSON exception", e);
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        Log.i("QuizActivity", new String(responseBody));
                                    }
                                });

                            }catch (JSONException e) {
                                Log.i("QuizActivity", "unexpected JSON exception", e);
                            }



                        }
                    });
                    Log.i("QuizActivity", res.getJSONObject("res").getJSONArray("lesson").getJSONObject(0).getString("question"));


                }catch (JSONException e){
                    bar.setVisibility(View.GONE);
                    Log.i("QuizActivity", "unexpected JSON exception", e);
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                bar.setVisibility(View.GONE);
                Log.i("QuizActivity", new String(responseBody));
            }
        });
    }
    private void redirect() {
        Intent i = new Intent(QuizActivity.this, ResultActivity.class);
        i.putExtra("identifier", getIntent().getStringExtra("identifier"));
        startActivity(i);
        finish();
    }
//    public PendingIntent setPendingIntent() {
//        Intent intent = new Intent(QuizActivity.this, MenuActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
//        return pendingIntent;
//    }
//    private void showNotif() {
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//
//// notificationId is a unique int for each notification that you must define
//        notificationManager.notify(notificationId, builder.build());
//    }

}
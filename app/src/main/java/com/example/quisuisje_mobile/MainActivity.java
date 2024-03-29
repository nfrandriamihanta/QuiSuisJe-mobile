package com.example.quisuisje_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quisuisje_mobile.util.RestHttpClient;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    Button logBtn = null;
    TextInputEditText identifier = null;
    TextInputEditText password = null;
    ProgressBar bar = null;
    boolean isAuthentified = false;
    String message = "authentification échouée";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        bar = findViewById(R.id.progressBar);
        bar.setVisibility(View.GONE);
        identifier = findViewById(R.id.identifier);
        password = findViewById(R.id.password);


        logBtn = findViewById(R.id.loginButton);
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setVisibility(View.VISIBLE);
                RequestParams params = new RequestParams();
                params.put("identifier", identifier.getText());
                params.put("password", password.getText());


                RestHttpClient.post("connexion", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        bar.setVisibility(View.GONE);
                        try {
                            JSONObject testV=new JSONObject(new String(responseBody));
                            if(testV.getString("status").compareTo("200") == 0)
                                redirect();
                            else
                                Toast.makeText(getApplicationContext(), "Authentification échouée", Toast.LENGTH_LONG).show();

                        }catch (JSONException e){
                            Log.i("MainActivity", "unexpected JSON exception", e);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        bar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Veuillez vérifier votre connexion internet", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    private void redirect() {
        Intent i = new Intent(MainActivity.this, MenuActivity.class);
        i.putExtra("identifier", identifier.getText().toString());
        Log.i("Debogage", identifier.getText().toString());
        startActivity(i);
        finish();
    }
}
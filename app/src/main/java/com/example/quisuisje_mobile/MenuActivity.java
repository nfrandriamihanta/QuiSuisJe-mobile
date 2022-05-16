package com.example.quisuisje_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class MenuActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "Bienvenue " + getIntent().getStringExtra("identifier"), Toast.LENGTH_LONG).show();
        if (savedInstanceState == null) {
            String[] menu = new String[3];
            menu[0] = "Apprendre";
            menu[1]= "Quiz";
            menu[2] = "Resultat";

            Bundle bundle = new Bundle();
            bundle.putStringArray("titles", menu);
            bundle.putString("identifier", getIntent().getStringExtra("identifier"));
            bundle.putString("redirect", "TopicActivity");
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragmentContainerView, ListFragment.class, bundle)
                    .commit();
        }
        setContentView(R.layout.activity_menu);
    }
}
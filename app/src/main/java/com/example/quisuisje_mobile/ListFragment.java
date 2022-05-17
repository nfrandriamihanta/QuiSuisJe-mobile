package com.example.quisuisje_mobile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


public class ListFragment extends Fragment {

    public String[] titles;
    public String identifier = "";
    public String redirect = "";
    public String mode = "";
    public String topic = "";

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (requireArguments() != null) {
            titles = requireArguments().getStringArray("titles");
            identifier = requireArguments().getString("identifier");
            redirect = requireArguments().getString("redirect");
            mode = requireArguments().getString("mode");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout list = getActivity().findViewById(R.id.linearLayout);

        for(int i = 0; i < titles.length; i++) {
            Button btn = new Button(getContext());
            btn.setText(titles[i]);
            list.addView(btn);

            int finalI = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(redirect != null)
                        mode = titles[finalI];
                    else
                        topic = titles[finalI];
                    redirect();
                }
            });
        }
    }

    private void redirect() {
        Intent i = new Intent();
        if (redirect != null) {
            i = new Intent(getActivity(), TopicActivity.class);
            i.putExtra("identifier", identifier);
            i.putExtra("mode", mode);

        } else {
            if(mode.compareTo("Apprendre") == 0) {
                i = new Intent(getActivity(), LessonActivity.class);
                i.putExtra("topic", topic);
            }
            else if(mode.compareTo("Quiz") == 0) {
                i = new Intent(getActivity(), QuizActivity.class);
                i.putExtra("identifier", identifier);
                i.putExtra("topic", topic);
            }
        }
        startActivity(i);
    }
}
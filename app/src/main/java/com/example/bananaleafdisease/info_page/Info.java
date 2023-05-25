package com.example.bananaleafdisease.info_page;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.bananaleafdisease.R;


public class Info extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        CardView cardViewSigatoka = rootView.findViewById(R.id.sigatoka_card_view);
        Button sigatokaExplore = rootView.findViewById(R.id.sigatoka_cardview_read_more_button);
        sigatokaExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Sigatoka.class);
                startActivity(intent);
            }
        });
        CardView cardViewCordana = rootView.findViewById(R.id.cordana_card_view);
        Button cordanaExplore = rootView.findViewById(R.id.cordana_cardview_read_more_button);
        cordanaExplore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Cordana.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
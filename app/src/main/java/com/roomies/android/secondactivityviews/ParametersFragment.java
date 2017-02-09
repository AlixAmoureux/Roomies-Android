package com.roomies.android.secondactivityviews;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.roomies.android.HomeFragment;
import com.roomies.android.R;

public class ParametersFragment extends Fragment {

    private LinearLayout mProfile;
    private LinearLayout mRoomies;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_parameters, container, false);
        mProfile = (LinearLayout) v.findViewById(R.id.update_profile);
        mRoomies = (LinearLayout) v.findViewById(R.id.update_roomies);
        return (v);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new HomeFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        mRoomies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new HomeFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Parameters");
    }
}

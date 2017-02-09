package com.roomies.android.mainactivityviews.managecoloc;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.roomies.android.R;
import com.roomies.android.mainactivityviews.managecoloc.createcoloc.CreateColocFragment;
import com.roomies.android.mainactivityviews.managecoloc.joincoloc.ColocsListFragment;

public class JointOrCreateColocFragment extends Fragment {
    private Button mJoinColocButton;
    private Button mCreateColocButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_joint_or_create_coloc, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mJoinColocButton = (Button) view.findViewById(R.id.join_colocation);
        mCreateColocButton = (Button) view.findViewById(R.id.create_colocation);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mJoinColocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new ColocsListFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        mCreateColocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new CreateColocFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Join / Create a Roomies Group");
    }
}

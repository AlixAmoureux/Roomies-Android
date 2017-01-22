package roomies.com.roomies.mainactivityviews;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import roomies.com.roomies.R;

public class JointOrCreateColocFragment extends Fragment
{
    private Button join_coloc;
    private Button create_coloc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_joint_or_create_coloc, container, false);
        join_coloc = (Button)v.findViewById(R.id.join_colocation);
        create_coloc = (Button)v.findViewById(R.id.create_colocation);
        return (v);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);



        join_coloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Fragment newFragment = new ListColocFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        create_coloc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new CreateColocFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Join / Create a Roomies Group");
    }
}

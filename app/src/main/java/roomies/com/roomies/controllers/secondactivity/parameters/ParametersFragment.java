package roomies.com.roomies.controllers.secondactivity.parameters;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import roomies.com.roomies.R;
import roomies.com.roomies.controllers.ManageObjects;
import roomies.com.roomies.models.users.ConnectedUserInfo;

public class ParametersFragment extends Fragment
{
    private LinearLayout m_profile;
    private LinearLayout m_roomies;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_parameters, container, false);
        m_profile = (LinearLayout)v.findViewById(R.id.update_profile);
        m_roomies = (LinearLayout)v.findViewById(R.id.update_roomies);
        return (v);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);



        m_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Fragment newFragment = new ProfileFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        m_roomies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new RoomiesFragment();
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
        ConnectedUserInfo user = ManageObjects.readUserInfosInPrefs("userInfos", getActivity());
        getActivity().setTitle("Parameters");
    }
}

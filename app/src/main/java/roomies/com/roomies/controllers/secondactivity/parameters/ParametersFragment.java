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
import roomies.com.roomies.controllers.mainactivity.managecoloc.createcoloc.AddMembersFragment;
import roomies.com.roomies.models.users.ConnectedUserInfo;

public class ParametersFragment extends Fragment
{
    private LinearLayout mProfile;
    private LinearLayout mRoomies;
    private LinearLayout mRequests;
    private LinearLayout mRoomiesMembers;
    //private LinearLayout mAddMembers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_parameters, container, false);
        mProfile = (LinearLayout)v.findViewById(R.id.update_profile);
        mRoomies = (LinearLayout)v.findViewById(R.id.update_roomies);
        mRequests = (LinearLayout)v.findViewById(R.id.requests);
        mRoomiesMembers = (LinearLayout)v.findViewById(R.id.roomies_members);
        //mAddMembers = (LinearLayout)v.findViewById(R.id.roomies_add_members);
        return (v);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);



        mProfile.setOnClickListener(new View.OnClickListener() {
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

        mRoomies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new RoomiesFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        mRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new InvitationsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        mRoomiesMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new RoomiesMembersFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
       /*mAddMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new AddMembersFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });*/
    }

    @Override
    public void onResume() {
        super.onResume();
        ConnectedUserInfo user = ManageObjects.readUserInfosInPrefs("userInfos", getActivity());
        getActivity().setTitle("Parameters");
    }
}
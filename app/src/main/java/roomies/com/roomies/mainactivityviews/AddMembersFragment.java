package roomies.com.roomies.mainactivityviews;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import roomies.com.roomies.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddMembersFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_members, container, false);
        return (v);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        getActivity().setTitle("Add Members");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}

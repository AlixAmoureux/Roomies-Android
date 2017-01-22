package roomies.com.roomies.mainactivityviews;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import roomies.com.roomies.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayAColocInfosFragment extends Fragment {

    private SharedPreferences prefs;
    private String coloc_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_display_acoloc_infos, container, false);
        return (v);
    }

    @Override
    public void onResume() {
        super.onResume();

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        coloc_id = prefs.getString("coloc_id", "");

        getActivity().setTitle("Roomies Group Name");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
}

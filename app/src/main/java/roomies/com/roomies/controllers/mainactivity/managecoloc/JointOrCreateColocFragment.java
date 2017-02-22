package roomies.com.roomies.controllers.mainactivity.managecoloc;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import roomies.com.roomies.R;
import roomies.com.roomies.controllers.ManageObjects;
import roomies.com.roomies.controllers.mainactivity.managecoloc.createcoloc.CreateColocFragment;
import roomies.com.roomies.controllers.mainactivity.managecoloc.joincoloc.listcolocs.ListColocFragment;

public class JointOrCreateColocFragment extends Fragment
{
    private Button join_coloc;
    private Button create_coloc;
    private Button mAcceptInvitation;
    private String mToken;
    private TextView mInvitationInfos;
    private String mColocInvitationId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_joint_or_create_coloc, container, false);
        join_coloc = (Button)v.findViewById(R.id.join_colocation);
        create_coloc = (Button)v.findViewById(R.id.create_colocation);
        mAcceptInvitation = (Button)v.findViewById(R.id.accept_invitation);
        mToken = ManageObjects.readUserInfosInPrefs("userInfos", getActivity()).token;
        mInvitationInfos = (TextView) v.findViewById(R.id.invitation_infos);
        return (v);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mAcceptInvitation.setEnabled(false);
        mAcceptInvitation.setVisibility(View.INVISIBLE);
        mInvitationInfos.setVisibility(View.INVISIBLE);

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
        // On accepte l'invitation reçue
        mAcceptInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("JoinOrCreate", mColocInvitationId);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle("Join / Create a Roomies Group");

        //getInvitations();
    }

    private void getInvitations() {
        // HTTP POST
        String url = getString(R.string.url_base) +  "/api/roomies-group/me";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        try
        {
            StringRequest jsonobject = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    try {
                        JSONArray colocs = new JSONArray(response);
                        for (int i = 0; i < colocs.length(); i++)
                        {
                            JSONObject coloc = colocs.getJSONObject(i);
                            String status = coloc.getString("status");
                            String id = coloc.getString("id");
                            String title = coloc.getString("title");

                            // on a reçu une invitation
                            if (status.compareTo("pending") == 0)
                            {
                                mColocInvitationId = id;
                                mAcceptInvitation.setEnabled(true);
                                mAcceptInvitation.setVisibility(View.VISIBLE);
                                mInvitationInfos.setText("You received an invitation to join the group " + title);
                                mInvitationInfos.setVisibility(View.VISIBLE);
                                break;
                            }
                        }
                    }
                    catch (JSONException e)
                    {
                        Log.e("ERROR VOLLEY", e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("ERROR VOLLEY", error.getMessage());
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "Application/json");
                    headers.put("Authorization", "Bearer " + mToken);
                    return headers;
                }
            };
            requestQueue.add(jsonobject);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

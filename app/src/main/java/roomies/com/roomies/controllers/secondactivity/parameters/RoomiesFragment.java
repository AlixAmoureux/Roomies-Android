package roomies.com.roomies.controllers.secondactivity.parameters;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import roomies.com.roomies.R;
import roomies.com.roomies.controllers.ManageObjects;
import roomies.com.roomies.controllers.mainactivity.managecoloc.JointOrCreateColocFragment;
import roomies.com.roomies.models.ColocsInfos;

import static android.app.Activity.RESULT_OK;

public class RoomiesFragment extends Fragment {

    private EditText mName;
    private EditText mDescription;
    private ImageButton mPhoto;
    private Button mUpdate;
    private Button mLeave;
    private TextView mMessage;

    private static int RESULT_LOAD_IMAGE = 1;
    private String mPhotoPath;
    private String mNameVal;
    private String mDescriptionVal;
    private String mToken;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_roomies, container, false);
        mName = (EditText) v.findViewById(R.id.coloc_name);
        mDescription = (EditText) v.findViewById(R.id.coloc_description);
        mPhoto = (ImageButton) v.findViewById(R.id.coloc_image);
        mUpdate = (Button) v.findViewById(R.id.update_coloc_button);
        mLeave = (Button) v.findViewById(R.id.leave_coloc_button);
        mMessage = (TextView) v.findViewById(R.id.update_roomie_message);
        return (v);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.e("Roomie Update", "onActivityCreated");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("Roomie Update", "onResume");

        ColocsInfos colocInfos = ManageObjects.readColocInfosInPrefs("colocInfos", getActivity());
        mToken = ManageObjects.readUserInfosInPrefs("userInfos", getActivity()).token;

        setDatasAndGetEvents(colocInfos);
        getActivity().setTitle("Roomie Group");
    }

    private void setDatasAndGetEvents(final ColocsInfos colocInfos) {
        mName.setText(colocInfos.title);
        mDescription.setText(colocInfos.description);

        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkFields() != -1) {
                    updateObject(colocInfos);
                }
            }
        });

        mLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //leaveRoomies(colocInfos.id);
            }
        });

        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    private int checkFields() {
        mNameVal = mName.getText().toString();
        mDescriptionVal = mDescription.getText().toString();

        if (TextUtils.isEmpty(mNameVal)) {
            mName.setError("The roomies's name is not set");
        } else if (TextUtils.isEmpty(mDescriptionVal)) {
            mDescription.setError("The roomies's description is not set");
        } else
            return (1);
        return (-1);
    }

    private void leaveRoomies(final String roomieId) {
        // HTTP DELETE
        String url = getString(R.string.url_base) + "/api/roomies-group/" + roomieId + "/leave";
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mMessage.setText("You have left the roomie");
                        mMessage.setTextColor(getResources().getColor(R.color.messageSuccess));
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            Log.e("RoomiesFragment", e.getMessage());
                        }
                        Fragment newFragment = new JointOrCreateColocFragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Update Profile", error.getMessage());
                mMessage.setText(getString(R.string.update_roomie_error));
                mMessage.setTextColor(getResources().getColor(R.color.messageError));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + mToken);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void updateObject(ColocsInfos oldColocInfos) {
        ColocsInfos newColocInfos = new ColocsInfos();
        newColocInfos.createdAt = oldColocInfos.createdAt;
        newColocInfos.description = mDescriptionVal;
        newColocInfos.id = oldColocInfos.id;
        newColocInfos.picturePath = mPhotoPath;
        newColocInfos.status = oldColocInfos.status;
        newColocInfos.title = mNameVal;
        ManageObjects.writeColocInfosInPrefs(newColocInfos, "colocInfos", getActivity());
        updateRoomie(newColocInfos.id);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContext().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mPhotoPath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mPhoto.setImageBitmap(bmp);
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContext().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void updateRoomie(String roomieId) {
        // HTTP POST
        String url = getString(R.string.url_base) + "/api/roomies-group/" + roomieId;
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JSONObject object = new JSONObject();
        try {
            object.put("title", mNameVal);
            object.put("description", mDescriptionVal);
            object.put("picturePath", mPhotoPath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, object,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //mMessage.setText(getString(R.string.update_roomie_success));
                        //mMessage.setTextColor(getResources().getColor(R.color.messageSuccess));
                        Toast.makeText(getContext(), "The group has been updated", Toast.LENGTH_LONG).show();
                        Fragment newFragment = new ParametersFragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Update Profile", error.getMessage());
                mMessage.setText(getString(R.string.update_roomie_error));
                mMessage.setTextColor(getResources().getColor(R.color.messageError));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                final Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + mToken);
                return headers;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }
}

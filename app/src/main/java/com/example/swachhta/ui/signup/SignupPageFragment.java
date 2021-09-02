package com.example.swachhta.ui.signup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.swachhta.databinding.FragmentLoginpageBinding;
import com.example.swachhta.databinding.FragmentSignuppageBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SignupPageFragment extends Fragment {
    private FragmentSignuppageBinding binding;
    private Button sigupbutton ;
    private EditText name;
    private EditText password;
    private EditText emailid;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        binding = FragmentSignuppageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        sigupbutton= binding.signupButton;
        emailid= binding.signupEmaiid;
        name= binding.signupName;
        password=binding.signupPassword;
      sigupbutton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                String userId  =emailid.getText().toString();
                String userPassword=password.getText().toString();
                String userName=name.getText().toString();
                signUpServer(userId,userPassword,userName);

            }
        });
        return root;
    }
    public void signUpServer(String userId,String password,String userName)
    {
        // rating server talk
        String apikey = "https://rating001.herokuapp.com/api/swachhta//signup";
        HashMap<String,String> params = new HashMap<>();
        params.put("email",userId);
        params.put("password",password);
        params.put("name",userName);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                apikey,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if(response.getBoolean("success")){
                                Toast.makeText(getContext(),"SignUp successfully",Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        if(error instanceof ServerError && response != null){
                            try{
                                String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                                JSONObject jsonObject = new JSONObject(res);
                                Toast.makeText(getContext(),"SignUp failed",Toast.LENGTH_SHORT).show();
                            }
                            catch(JSONException | UnsupportedEncodingException je){
                                je.printStackTrace();
                            }
                        }
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String,String>headers = new HashMap<>();
                headers.put("content-type", "application/json");
                return headers;
            }
        };

        int socketTime = 3000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTime,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(policy);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(jsonObjectRequest);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
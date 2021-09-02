package com.example.swachhta.ui.mainpage;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.swachhta.MainActivity;
import com.example.swachhta.databinding.FragmentMainpageBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



public class MainPageFragment extends Fragment {
    private FragmentMainpageBinding binding;
    private String currentPhotoPath;
    private ImageView IDProf;
    private Button  Upload_Btn;
    private Button Share_Btn;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1888;
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding =FragmentMainpageBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Upload_Btn= binding.clickpicbutton;
        Upload_Btn.setVisibility(View.INVISIBLE);
        Share_Btn =binding.uploadbutton;
        Share_Btn.setVisibility(View.INVISIBLE);

        IDProf = binding.selectedImage;
        IDProf.setImageDrawable(null);
        cameraCLick();
        Upload_Btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                cameraCLick();
            }
        });
        Share_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public  void onClick(View view)
            {
                uploadBitmap();
            }
        });


        return root;
    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
    private void uploadBitmap() {

        //getting the tag from the edittext
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, EndPoints.UPLOAD_URL, new Response.Listener<NetworkResponse>()
        {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                            obj.get("status");
                            Toast.makeText(getContext(), "your problem send respective municipal board succesfully", Toast.LENGTH_SHORT).show();
                            Alpha(getView());
                        } catch (JSONException e) {
                            Toast.makeText(getContext(), "your problem send respective municipal board with some problem", Toast.LENGTH_SHORT).show();
                            e.printStackTrace();

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tags", tags);
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map <String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("uploadedFile", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(getContext()).add(volleyMultipartRequest);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==  CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == getActivity().RESULT_OK) {
                Toast.makeText(getContext(), "image captured successFully", Toast.LENGTH_SHORT).show();
                Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
                IDProf.setImageBitmap(bitmap);
                Share_Btn.setVisibility(View.VISIBLE);
                Upload_Btn.setVisibility(View.VISIBLE);
            }


        }



    }

    public void cameraCLick()
    {
        String filename="photo";
        File storageDirectory= getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            File imageFile= File.createTempFile(filename,".jpg",storageDirectory);
            currentPhotoPath=imageFile.getAbsolutePath();
            Uri imageUri=FileProvider.getUriForFile(getContext(),"com.example.swachhta.provider",imageFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
        catch (IOException e)
        {
            Toast.makeText(getContext(), "error occured", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    public void Alpha(View v){
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
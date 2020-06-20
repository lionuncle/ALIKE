package com.example.alike;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Declaring views
    private Button buttonChoose;
    private Button buttonUpload;
    private ImageView imageView;
    private EditText nameText;

    //Image request code
    private int PICK_IMAGE_REQUEST = 1;

    //storage permission code
    private static final int STORAGE_PERMISSION_CODE = 123;

    //Bitmap to get image from gallery
    private Bitmap bitmap;

    //Uri to store the image uri
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Requesting storage permission
        requestStoragePermission();

        //Initializing views
        buttonChoose = findViewById(R.id.buttonChoose);
        buttonUpload =  findViewById(R.id.buttonUpload);
        imageView =  findViewById(R.id.imageView);
        nameText =  findViewById(R.id.editTextName);

        //Setting clicklistener
        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        requestStoragePermission();
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//******************************** UPLOAD TO SERVER*************************************************************************************************
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @GET("addpic")
    private void uploadToServer(String filePath) {
        final ProgressDialog dialog =
                ProgressDialog.show(MainActivity.this, "UPLOADING IMAGE", "Please Wait...");
        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        UploadAPIs uploadAPIs = retrofit.create(UploadAPIs.class);
        File file = new File(filePath);
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("upload", file.getName(), fileReqBody);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");
        //
        /*Call call = uploadAPIs.uploadImage(part, description);
        call.enqueue(new Callback() {

            @Override
            @GET("addpic")
            public void onResponse(@NonNull Call call,@NonNull Response response) {
                dialog.cancel();
                Toast.makeText(MainActivity.this, "Upload Successful, Response: "+response.message(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onFailure(@NonNull Call call,@NonNull Throwable t) {
                dialog.cancel();
                Toast.makeText(MainActivity.this, "error: "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/
        uploadAPIs.getFile().enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "RES "+response.body().toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {

            }
        });
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//******************************** PICK IMAGE From MOBILE*************************************************************************************************
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //method to show file chooser
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    //handling the image chooser activity result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    //Requesting permission
    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
            Toast.makeText(this, "YOU NEED TO GIVE ACCESS TO STORAGE", Toast.LENGTH_SHORT).show();
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }


    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showAlertDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("WHICH API YOU WANT TO USE?");
        alertDialog.setCancelable(false);
        String[] items = {"Uploading","AddPic","InsertEmbedings"};
        int checkedItem = 1;

        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file = new File(Objects.requireNonNull(FileUtil.getPath(filePath, getBaseContext())));
                switch (which) {
                    case 0:
                        NetworkClient.BASE_URL = "https://alikefaceapp.herokuapp.com/";
                        uploadToServer(file.getPath());


                        //Toast.makeText(getApplicationContext(), "Clicked on ADMIN", Toast.LENGTH_LONG).show();
                    case 1:
                        NetworkClient.BASE_URL = "https://alikefaceapp.herokuapp.com/";
                        uploadToServer(file.getPath());
                        //File file = new File(Objects.requireNonNull(FileUtil.getPath(filePath, getBaseContext())));
                        //uploadToServer(file.getPath());
                        //Toast.makeText(getApplicationContext(), "Clicked on STUDENT", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        NetworkClient.BASE_URL = "https://alikefaceapp.herokuapp.com/";
                        uploadToServer(file.getPath());
                        //File file = new File(Objects.requireNonNull(FileUtil.getPath(filePath, getBaseContext())));
                        //uploadToServer(file.getPath());
                        //Toast.makeText(getApplicationContext(), "Clicked on ADVISER", Toast.LENGTH_LONG).show();
                        break;

                }
            }
        });
        final AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();


    }
    @Override
    public void onClick(View v) {
        if (v == buttonChoose) {
            showFileChooser();
        }
        if (v == buttonUpload) {
            showAlertDialog();
            //File file = new File(Objects.requireNonNull(FileUtil.getPath(filePath, getBaseContext())));
            //uploadToServer(file.getPath());
        }
    }


}
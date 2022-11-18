package com.example.farmersblog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.checkerframework.checker.units.qual.C;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

public class AddPostActivity extends AppCompatActivity {
    EditText edtPostTitle, edtPostDescription;
    ImageView postImage;
    Button uploadPost;

    Uri imageUri = null;

    private static final int GALLERY_IMAGE_CODE = 100;
    private static final int CAMERA_IMAGE_CODE = 200;

    ProgressDialog progressDialog;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Add Post");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        // ask for permission
        requestPermission();

        edtPostDescription = (EditText) findViewById(R.id.post_description);
        edtPostTitle = (EditText) findViewById(R.id.post_title);
        postImage = (ImageView) findViewById(R.id.blog_image);
        uploadPost = (Button) findViewById(R.id.btnPost);

        progressDialog =  new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePickerDialogue();
            }
        });
        
        uploadPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = edtPostTitle.getText().toString().trim();
                String description = edtPostDescription.getText().toString().trim();
                
                if (title.isEmpty()){
                    edtPostTitle.setError("Post Title Required");
                    edtPostTitle.requestFocus();
                }
                else if (description.isEmpty()){
                    edtPostDescription.setError("Post Body Required");
                    edtPostDescription.requestFocus();
                }
                else {
                    uploadPostData(title, description);
                }
            }
        });
    }

    private void uploadPostData(String title, String description) {

        progressDialog.setMessage("Uploading Post...");
        progressDialog.show();

        final String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePath = "Posts/"+"post_"+timeStamp;

        if (postImage.getDrawable() != null){
            Bitmap bitmap = ((BitmapDrawable)postImage.getDrawable()).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] data = byteArrayOutputStream.toByteArray();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(filePath);
            storageReference.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            String downloadUri = uriTask.getResult().toString();
                            if (uriTask.isSuccessful()){
                                FirebaseUser user = auth.getCurrentUser();
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("uId", user.getUid());
                                hashMap.put("uEmail", user.getEmail());
                                hashMap.put("pId", timeStamp);
                                hashMap.put("pTitle", title);
                                hashMap.put("pImage", downloadUri);
                                hashMap.put("pDescription", description);
                                hashMap.put("pTime", timeStamp);

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Posts");
                                databaseReference.child(timeStamp).setValue(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddPostActivity.this, "Post Successfully Published", Toast.LENGTH_SHORT).show();
                                                edtPostTitle.setText("");
                                                edtPostDescription.setText("");
                                                postImage.setImageURI(null);
                                                imageUri = null;
                                                startActivity(new Intent(AddPostActivity.this, HomeActivity.class));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                                Toast.makeText(AddPostActivity.this, "Data save failed" +e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddPostActivity.this, "upload failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void imagePickerDialogue() {
        String []options = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose image from");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i==0){
                    cameraPick();
                }
                if (i == 1){
                    galleryPick();
                }
            }
        });
        builder.create().show();
    }

    private void galleryPick() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        //all images
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_IMAGE_CODE);
    }

    private void cameraPick() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp Desc");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_IMAGE_CODE);
    }

    private void requestPermission(){
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
                    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {

                    }
                    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == GALLERY_IMAGE_CODE){
                imageUri = data.getData();
                postImage.setImageURI(imageUri);
            }
            if (requestCode == CAMERA_IMAGE_CODE){
                postImage.setImageURI(imageUri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
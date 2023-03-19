package com.example.nhom4_duan_1.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.nhom4_duan_1.R;
import com.example.nhom4_duan_1.models.Users;
import com.example.nhom4_duan_1.models.Vouchers;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CircleImageView ciPic;
    private final int GALLERY_REQ_CODE = 1000;
    EditText edtName, edtPhoneNumber, edtAddress;
    Button btnSave;
    Users user;
    String IdUser;
    Uri dowloadUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        Intent intent = getIntent();
        IdUser = intent.getStringExtra("Id");

        ciPic = (CircleImageView) findViewById(R.id.ciPic);
        ciPic.setDrawingCacheEnabled(true);

        user = new Users();
        edtName = (EditText) findViewById(R.id.edtName);
        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        btnSave = (Button) findViewById(R.id.btnSave);

        ciPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_REQ_CODE);
            }
        });

        getUser();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upLoadImagetoFireBase(ciPic.getDrawingCache());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if (requestCode==GALLERY_REQ_CODE){
                ciPic.setImageURI(data.getData());
//                upLoadImagetoFireBase(ciPic.getDrawingCache());
                System.out.println(ciPic.getDrawingCache());
            }
        }
    }

    public void upLoadImagetoFireBase(Bitmap bitmap){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        StorageReference imageReference = storageReference.child(Calendar.getInstance().getTimeInMillis()+".jpg");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] bytes = outputStream.toByteArray();
        UploadTask uploadTask = imageReference.putBytes(bytes);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (task.isSuccessful()){
                    return imageReference.getDownloadUrl();
                }
                return null;
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    dowloadUri = task.getResult();
                    System.out.println("onComplete : "+ dowloadUri);
                    updateUser();
                }
            }
        });
    }

    public void getUser(){
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, Object> item = document.getData();
                                System.out.println(document.getId());
                                System.out.println(IdUser);
                                System.out.println(document.getId().equals(IdUser));
                                if (document.getId().equals(IdUser)){
                                    user.setId(document.getId());
                                    user.setName(item.get("Name").toString());
                                    user.setImage(item.get("Image").toString());
                                    user.setPass(item.get("Pass").toString());
                                    user.setPhone(item.get("Phone").toString());
                                    user.setAddress(item.get("Address").toString());
                                    System.out.println("Day la User " + user );

                                    Picasso.get()
                                            .load(user.getImage())
                                            .placeholder(R.drawable.android)
                                            .error(R.drawable.man)
                                            .fit()
                                            .into(ciPic);
                                    edtName.setText(user.getName());
                                    edtPhoneNumber.setText(user.getPhone());
                                    edtAddress.setText(user.getAddress());

                                    dowloadUri = Uri.parse(user.getImage());
                                    break;
                                }
                            }
                        } else {
                            Log.w(">>>TAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void updateUser(){
        String name = edtName.getText().toString().trim();
        String phone = edtPhoneNumber.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();

        if (name.length() >0 || phone.length() > 0 || address.length() > 0){
            Map<String, Object> userUp = new HashMap<>();
            userUp.put("Name", name);
            userUp.put("Phone", phone);
            userUp.put("Address", address);
            userUp.put("Image",String.valueOf(dowloadUri));

            db.collection("Users")
                    .document(IdUser)
                    .update(userUp)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            System.out.println("Sửa thành công");
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("Sửa không thành công");
                        }
                    });
        }


    }

}
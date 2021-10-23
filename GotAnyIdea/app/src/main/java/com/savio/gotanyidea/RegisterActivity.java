package com.savio.gotanyidea;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private EditText cxPassword,cxConfirmPassword,cxName,cxEmail;
    private ImageView photoSelect;
    private Button btnRegister;
    private Uri photoSelectedDiretory;
    private String meUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        cxName = findViewById(R.id.cx_register_name);
        cxEmail = findViewById(R.id.cx_register_email);
        cxPassword = findViewById(R.id.cx_register_password);
        cxConfirmPassword = findViewById(R.id.cx_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        photoSelect = findViewById(R.id.img_photo_select);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
        photoSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });
    }
   private void selectPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 0) && (data != null)) {
            photoSelectedDiretory = data.getData();
            Bitmap bitmap = null;
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),photoSelectedDiretory);
                photoSelect.setImageDrawable(new BitmapDrawable(bitmap));
            }
            catch (IOException e){

            }
        }
    }

    private void createUser(){
        String name = cxName.getText().toString();
        String email = cxEmail.getText().toString();
        String password = cxPassword.getText().toString();
        String confirmPassword = cxConfirmPassword.getText().toString();
        if( (name == null) || (name.isEmpty()) ||(email == null) || (email.isEmpty()) || (password == null) || (password.isEmpty()) || (confirmPassword == null) || (confirmPassword.isEmpty())  ){
            Toast.makeText(RegisterActivity.this,"Nome, e-mail e senha devem ser inseridos",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(confirmPassword)) {
            Toast.makeText(RegisterActivity.this,"Senhas não correspondem",Toast.LENGTH_SHORT).show();
            return;
        }
        if((photoSelectedDiretory == null) || (photoSelectedDiretory.toString().isEmpty()) ) {
            Toast.makeText(RegisterActivity.this,"Insira uma foto",Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        meUserID = task.getResult().getUser().getUid();
                        saveUserFirebase();
                    }
                });
    }
    private void saveUserFirebase(){
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images-profile/"+filename);

        ref.putFile(photoSelectedDiretory)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        FirebaseUser userMe = FirebaseAuth.getInstance().getCurrentUser();
                        userMe.getIdToken(true)
                                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                    @Override
                                    public void onComplete(@NonNull  Task<GetTokenResult> task) {
                                        if(task.isSuccessful()){
                                            String urlProfilePhoto = uri.toString();
                                            String name = cxName.getText().toString();
                                            String token = task.getResult().getToken();
                                            User user = new User();
                                            user.setUserID(meUserID);
                                            user.setToken(token);
                                            user.setName(name);
                                            user.setUrlProfilePhoto(urlProfilePhoto);

                                            FirebaseFirestore.getInstance().collection("/users")
                                                    .document(meUserID)
                                                    .set(user)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {

                                                            Intent intent =  new Intent(RegisterActivity.this,FeedActivity.class);

                                                             intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);

                                                        }
                                                    })
                                            ;
                                        }
                                    }
                                });
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        })
        ;
    }
}
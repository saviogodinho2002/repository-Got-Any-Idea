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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {
    private User me;
    private Button btnConfirmAlterations, btnCancel;
    private EditText cxUserName;
    private ImageView photoUser;
    Uri newProfilePhoto;
    String newPhoto;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Log.e("teste","oi casada");

        btnCancel = findViewById(R.id.btn_cancel);
        btnConfirmAlterations = findViewById(R.id.btn_confirm_alterations);
        cxUserName = findViewById(R.id.cx_username_editiprofile);

        photoUser = findViewById(R.id.photo_user_editiprofile);
        photoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickPhoto();
            }
        });

        verifyAutentication();
        btnConfirmAlterations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeProfile();
            }
        });
    }
    private void fetchPerfil(){
        Picasso.get()
                .load(me.getUrlProfilePhoto())
                .into(photoUser);
        cxUserName.setText(me.getName());
    }
    private void pickPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,3);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==3 && data != null){
            newProfilePhoto = data.getData();
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),newProfilePhoto);
                photoUser.setImageDrawable(new BitmapDrawable(bitmap));
                me.setUrlProfilePhoto(null);

            }
            catch (IOException e){

            }
        }

    }
    private void changeProfile(){
        String newName = cxUserName.getText().toString();

        if((newName == null) || (newName.isEmpty())){
            Toast.makeText(EditProfileActivity.this,"Coloque um nome",Toast.LENGTH_SHORT).show();
            return;
        }

        if((me.getName().equals(newName))&&(me.getUrlProfilePhoto()!= null && !me.getUrlProfilePhoto().isEmpty() ) ){
            Toast.makeText(EditProfileActivity.this,"Nada para alterar",Toast.LENGTH_SHORT).show();
        }
        else if((me.getUrlProfilePhoto() == null) || (me.getUrlProfilePhoto().isEmpty())){
            Log.e("teste","Mudando foto");
            FirebaseStorage.getInstance().getReference("/images-profile/"+me.getFileNameProfilePhoto())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.e("teste","foto anterior delatada");
                            String filename = UUID.randomUUID().toString();
                            final StorageReference ref = FirebaseStorage.getInstance().getReference("/images-profile/"+filename);
                            ref.putFile(newProfilePhoto)
                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Log.e("teste","upou nova foto");
                                       ref.getDownloadUrl()
                                               .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                   @Override
                                                   public void onSuccess(Uri uri) {
                                                    newPhoto = uri.toString();
                                                    User user = new User();

                                                    user.setName(me.getName() == newName ? me.getName():newName);
                                                    user.setUserID(me.getUserID());
                                                    user.setFileNameProfilePhoto(filename);
                                                    user.setUrlProfilePhoto(newPhoto);

                                                    FirebaseFirestore.getInstance().collection("/users")
                                                            .document(me.getUserID())
                                                            .set(user)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Log.e("teste","PERFIL ALTERADO FODAMENTE");
                                                            Toast.makeText(EditProfileActivity.this,"Perfil alterado com sucesso",Toast.LENGTH_SHORT).show();

                                                        }
                                                    });

                                                   }
                                               });
                                        }
                                    });

                        }
                    });

        }else {
            User user = new User();
            user.setName(newName);
            user.setUserID(me.getUserID());
            user.setFileNameProfilePhoto(me.getFileNameProfilePhoto());
            user.setUrlProfilePhoto(me.getUrlProfilePhoto());

            FirebaseFirestore.getInstance().collection("/users")
                    .document(me.getUserID())
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(EditProfileActivity.this,"Perfil alterado com sucesso",Toast.LENGTH_SHORT).show();
                        }
                    });

        }



    }

    private void verifyAutentication(){
        if(FirebaseAuth.getInstance().getUid() == null){
            Intent intent =  new Intent(EditProfileActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e("teste","wtf pq");
            startActivity(intent);
        }else {
            FirebaseUser meUser = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore.getInstance().collection("users")
                    .document(meUser.getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            me = documentSnapshot.toObject(User.class);
                            fetchPerfil();
                        }
                    });

        }
    }
}
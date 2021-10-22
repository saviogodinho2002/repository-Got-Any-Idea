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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CreatePostActivity extends AppCompatActivity {
    private ImageView photoSelect,photoUser,photoPost;
    private Uri selectedPhotoDirectory;
    private EditText cxTextoPost;
    private Button btnPost;
    private TextView txtUserName;
    private CheckBox chkAlimentos, chkTecnologia,chkArte,chkGambiarra;
    private List<CheckBox> checkBoxesList;
    private String tag;
    User me;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        checkBoxesList =  new ArrayList<>();
        selectedPhotoDirectory = null;

        chkAlimentos = findViewById(R.id.check_tag_alimento);
        chkTecnologia = findViewById(R.id.check_tag_tecnologia);
        chkArte =  findViewById(R.id.check_tag_arte);
        chkGambiarra =  findViewById(R.id.check_tag_gambiarra);

        checkBoxesList.add(chkArte);
        checkBoxesList.add(chkAlimentos);
        checkBoxesList.add(chkTecnologia);
        checkBoxesList.add(chkGambiarra);

        photoSelect = findViewById(R.id.select_photo_post);
        photoUser = findViewById(R.id.photo_createpost_user);
        photoPost = findViewById(R.id.photo_createpost);

        cxTextoPost = findViewById(R.id.cx_createpost_text);

        txtUserName = findViewById(R.id.txt_createpost_name);

        btnPost = findViewById(R.id.btnPost);
        verifyAutentication();

        photoSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });
        chkTecnologia.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    unMarkCheckBox(chkTecnologia);
                tag = chkTecnologia.getText().toString();
                }
            }
        });
        chkGambiarra.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    unMarkCheckBox(chkGambiarra);
                    tag = chkGambiarra.getText().toString();

                }
            }
        });
        chkArte.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    unMarkCheckBox(chkArte);
                    tag = chkArte.getText().toString();
                }
            }
        });
        chkAlimentos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    unMarkCheckBox(chkAlimentos);
                    tag = chkAlimentos.getText().toString();
                }
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPost();
            }
        });

    }
    private void unMarkCheckBox(CheckBox checkBox){
        for (CheckBox check:
             checkBoxesList) {
            if(check!=checkBox) check.setChecked(false);
        }

    }

    private void createPost(){
        String tag = null;
        for (CheckBox check:
             checkBoxesList) {
            if(check.isChecked()) tag = check.getText().toString();
        }
        if(tag == null) {
            Toast.makeText(CreatePostActivity.this, "Marque uma Tag", Toast.LENGTH_SHORT).show();
            return;
        }
        String textoPost = cxTextoPost.getText().toString();
        if(textoPost == null || textoPost.isEmpty()){
            Toast.makeText(CreatePostActivity.this, "Insira um texto", Toast.LENGTH_SHORT).show();
            return;
        }
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images-post/"+filename);

        String finalTag = tag;
        //String urlPhotoPost = uri.toString();
        String meUserName = me.getName();
        String meId = me.getUserID();
        String urlPhotoUser = me.getUrlProfilePhoto();
        long timestamp = System.currentTimeMillis();
        Posts post = new Posts();
        //post.setUrlPhotoPost(urlPhotoPost);
        post.setTag(finalTag);
        post.setFromName(meUserName);
        post.setTimestamp(timestamp);
        post.setFromID(meId);
        post.setPostText(textoPost);
        post.setUrlPhotoUser(urlPhotoUser);
        Intent intent = new Intent(CreatePostActivity.this, FeedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        FirebaseFirestore.getInstance().collection("/"+finalTag)
                .add(post)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        if (selectedPhotoDirectory != null && !selectedPhotoDirectory.toString().isEmpty()) {
                            Log.e("teste","UPANDO IMG");
                            ref.putFile(selectedPhotoDirectory).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Log.e("teste","baixar IMG");
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.e("teste","baixou");
                                            FirebaseFirestore.getInstance().collection("/"+finalTag)
                                                    .document(documentReference.getId())
                                                    .update("urlPhotoPost", uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.e("teste","atualizou no server");
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("teste","ERRO AO BAIXAR A REF");
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull  Exception e) {
                                    Log.e("teste","ERRO AO POR A FODA");
                                }
                            });
                        }else{

                        startActivity(intent);
                        }
                    }
                });

        }

    private void selectPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        selectedPhotoDirectory = null;
        photoPost.setImageDrawable(null);
        if ((requestCode == 1) && (data != null)) {
            selectedPhotoDirectory = data.getData();
            Bitmap bitmap = null;
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),selectedPhotoDirectory);
                photoPost.setImageDrawable(new BitmapDrawable(bitmap));
            }
            catch (IOException e){

            }
        }
    }
    private void verifyAutentication(){
        if(FirebaseAuth.getInstance().getUid() == null){
            Intent intent =  new Intent(CreatePostActivity.this,LoginActivity.class);
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
                            Picasso.get()
                                    .load(me.getUrlProfilePhoto())
                                    .into(photoUser);
                            txtUserName.setText(me.getName());
                        }
                    });

        }
    }
}
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    private CheckBox chkCulinaria, chkTecnologia,chkArte,chkGambiarra;
    private List<CheckBox> checkBoxesList;
    private List<String> tags;
    private FloatingActionButton floatBtnNullImage;
    private User me;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        tags = new ArrayList<>();
        checkBoxesList =  new ArrayList<>();
        selectedPhotoDirectory = null;

        chkCulinaria = findViewById(R.id.check_tag_culinaria);
        chkTecnologia = findViewById(R.id.check_tag_tecnologia);
        chkArte =  findViewById(R.id.check_tag_arte);
        chkGambiarra =  findViewById(R.id.check_tag_gambiarra);
        floatBtnNullImage = findViewById(R.id.floatbtn_null_image);

        checkBoxesList.add(chkArte);
        checkBoxesList.add(chkCulinaria);
        checkBoxesList.add(chkTecnologia);
        checkBoxesList.add(chkGambiarra);


        photoSelect = findViewById(R.id.select_photo_post);
        photoUser = findViewById(R.id.photo_createpost_user);
        photoPost = findViewById(R.id.photo_createpost);

        cxTextoPost = findViewById(R.id.cx_createpost_text);

        txtUserName = findViewById(R.id.txt_createpost_name);

        btnPost = findViewById(R.id.btnPost);

        floatBtnNullImage.setVisibility(View.INVISIBLE);
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
                   tags.add(chkTecnologia.getText().toString());

                }else  tags.remove(chkTecnologia.getText().toString());
            }
        });
        chkGambiarra.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){

                    tags.add(chkGambiarra.getText().toString());
                }else  tags.remove(chkGambiarra.getText().toString());
            }
        });
        chkArte.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    tags.add(chkArte.getText().toString());
                }else tags.remove(chkArte.getText().toString());
            }
        });
        chkCulinaria.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {

                    tags.add(chkCulinaria.getText().toString());
                }else tags.remove(chkCulinaria.getText().toString());
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPost();
            }
        });
        floatBtnNullImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoPost.setImageDrawable(null);
                selectedPhotoDirectory = null;
                floatBtnNullImage.setVisibility(View.INVISIBLE);
            }
        });

    }

    private void createPost(){
        if(tags.isEmpty() ) {
            Toast.makeText(CreatePostActivity.this, "Marque uma Tag", Toast.LENGTH_SHORT).show();
            return;
        }
        String textoPost = cxTextoPost.getText().toString();
        if(textoPost == null || textoPost.isEmpty()){
            Toast.makeText(CreatePostActivity.this, "Insira um texto", Toast.LENGTH_SHORT).show();
            return;
        }


        String meUserName = me.getName();
        String meId = me.getUserID();
        String urlPhotoUser = me.getUrlProfilePhoto();
        long timestamp = System.currentTimeMillis();
        Posts post = new Posts();

        post.setTag(tags);
        post.setFromName(meUserName);
        post.setTimestamp(timestamp);
        post.setFromID(meId);
        post.setPostText(textoPost);
        post.setUrlPhotoUser(urlPhotoUser);
        post.setNumLikes(0);


        Intent intent = new Intent(CreatePostActivity.this, FeedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        String postID = UUID.randomUUID().toString();
        post.setPostID(postID);

        Log.e("teste","chegou ate aqui?");
        if (selectedPhotoDirectory != null && !selectedPhotoDirectory.toString().isEmpty()) {
            String filename = UUID.randomUUID().toString();
            final StorageReference ref = FirebaseStorage.getInstance().getReference("/images-post/"+filename);

            Log.e("teste", "UPANDO IMG");
            ref.putFile(selectedPhotoDirectory).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e("teste", "baixar IMG");
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            post.setUrlPhotoPost(uri.toString());
                            post.setPhotoPostFileName(filename);

                            FirebaseFirestore.getInstance().collection("/posts")
                                    .document(postID)
                                    .set(post)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            startActivity(intent);
                                        }
                                    });

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("teste", "ERRO AO BAIXAR A REF");
                }
            });
        }else {
            FirebaseFirestore.getInstance().collection("/posts")
                    .document(postID)
                    .set(post)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            startActivity(intent);
                        }
                    });

        }
    }

    private void selectPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 1) && (data != null)) {
            selectedPhotoDirectory = data.getData();
            Bitmap bitmap = null;
            try{
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),selectedPhotoDirectory);
                photoPost.setImageDrawable(new BitmapDrawable(bitmap));
                floatBtnNullImage.setVisibility(View.VISIBLE);
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
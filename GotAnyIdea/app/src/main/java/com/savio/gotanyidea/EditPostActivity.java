package com.savio.gotanyidea;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class EditPostActivity extends AppCompatActivity {
    private ImageView photoSelect,photoUser,photoPost;
    private Uri selectedPhotoDirectory;
    private EditText cxTextoPost;
    private Button btnPost;
    private TextView txtUserName;
    private CheckBox chkCulinaria, chkTecnologia,chkArte,chkGambiarra;
    private List<CheckBox> checkBoxesList;
    private List<String> tags;
    private String postID;
    private  Posts postFromIntent;
    private User me;
    private List<String> userLikeID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        tags = new ArrayList<>();
        checkBoxesList =  new ArrayList<>();
        userLikeID = new ArrayList<>();
        selectedPhotoDirectory = null;

        chkCulinaria = findViewById(R.id.check_tag_culinaria);
        chkTecnologia = findViewById(R.id.check_tag_tecnologia);
        chkArte =  findViewById(R.id.check_tag_arte);
        chkGambiarra =  findViewById(R.id.check_tag_gambiarra);

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
              //  tag = chkTecnologia.getText().toString();
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


        Log.e("teste","ENTROU EM EDIT");
       postID = getIntent().getExtras().getString("postID") ;
        fetchPostData();
    }
    private void fetchPostData(){
        Log.e("teste","FETCHIN POSTS");
        FirebaseFirestore.getInstance().collection("/posts")
                .document(postID)
                .get().onSuccessTask(new SuccessContinuation<DocumentSnapshot, Object>() {
            @NonNull
            @Override
            public Task<Object> then(@Nullable  DocumentSnapshot documentSnapshot) throws Exception {
                Log.e("teste","conseguiu chegar no post");
                if(documentSnapshot != null){
                    Log.e("teste","nao nulo");
                postFromIntent = documentSnapshot.toObject(Posts.class);
                    Log.e("teste","PASSOU PRO POSTFORM");
                if ((postFromIntent.getUrlPhotoPost() != null) && (!postFromIntent.getUrlPhotoPost().isEmpty())) {
                    Log.e("teste","nao nulo e nem vazio foto");
                    Picasso.get()
                            .load(postFromIntent.getUrlPhotoPost())
                            .into(photoPost);
                }
                cxTextoPost.setText(postFromIntent.getPostText());
                List<String> tagsList = postFromIntent.getTag();
                userLikeID = postFromIntent.getUserLikedId();
                tags.clear();
                for (String tag : tagsList) {
                    for (CheckBox check : checkBoxesList) {
                        if (check.getText().equals(tag)) {
                            check.setChecked(true);
                            if(!tags.contains(tag))
                            tags.add(tag);
                        }

                    }

                }
            }
                return null;
            }

        });

    }

    private void createPost(){
        if(tags.isEmpty() ) {
            Toast.makeText(EditPostActivity.this, "Marque uma Tag", Toast.LENGTH_SHORT).show();
            return;
        }
        String textoPost = cxTextoPost.getText().toString();
        if(textoPost == null || textoPost.isEmpty()){
            Toast.makeText(EditPostActivity.this, "Insira um texto", Toast.LENGTH_SHORT).show();
            return;
        }
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images-post/"+filename);

        String meUserName = me.getName();
        String meId = me.getUserID();
        String urlPhotoUser = me.getUrlProfilePhoto();
        long timestamp = System.currentTimeMillis();
        Posts post = new Posts();
        if((userLikeID != null) && (!userLikeID.isEmpty()) )   post.setUserLikedId(userLikeID);
        post.setNumLikes(postFromIntent.getNumLikes());
        post.setTag(tags);
        post.setFromName(meUserName);
        post.setTimestamp(timestamp);
        post.setFromID(meId);
        post.setPostID(postID);
        post.setPostText(textoPost);
        post.setUrlPhotoUser(urlPhotoUser);
        Intent intent = new Intent(EditPostActivity.this, FeedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        Log.e("teste","chegou ate aqui?");
        FirebaseFirestore.getInstance().collection("/posts")
                .document(postID)
                .set(post)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       FirebaseStorage.getInstance().getReference("/images-posts/"+post.getPhotoPostFileName()).delete();
                        Log.e("teste","DELETOU IMG ANTEIROR");

                        if (selectedPhotoDirectory != null && !selectedPhotoDirectory.toString().isEmpty()) {
                            Log.e("teste","UPANDO  NOVA IMG");
                            ref.putFile(selectedPhotoDirectory).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Log.e("teste","baixar IMG");
                                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.e("teste","baixou");
                                            FirebaseFirestore.getInstance().collection("/posts")
                                                    .document(post.getPostID())
                                                    .update("urlPhotoPost", uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    FirebaseFirestore.getInstance().collection("/posts")
                                                            .document(post.getPostID())
                                                            .update("photoPostFileName",filename)
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
            Intent intent =  new Intent(EditPostActivity.this,LoginActivity.class);
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
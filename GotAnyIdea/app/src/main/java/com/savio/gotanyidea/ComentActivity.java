package com.savio.gotanyidea;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ComentActivity extends AppCompatActivity {

    private Posts post;
    private Button btnEnterComent;
    private TextView txtUserName,txtPostText;
    private ImageView photoUser,photoPost;
    private EditText cxTextComent;
    private User me;
    private GroupAdapter adapter;
    private RecyclerView rv;
    ArrayAdapter<String> adapterSpinComent;
    private String[] itensSpinnerComent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coment);


        cxTextComent = findViewById(R.id.cx_comentText_coment);
        btnEnterComent = findViewById(R.id.btn_entercoment_coment);
        txtUserName = findViewById(R.id.txt_postUserName_coment);
        photoUser = findViewById(R.id.img_photoUserPost);
        txtPostText = findViewById(R.id.txt_postText_coment);
        photoPost = findViewById(R.id.img_photoPost_coment);
        rv = findViewById(R.id.recycler_coments);
        post =(Posts) getIntent().getParcelableExtra("post");

        verifyAutentication();
        adapter = new GroupAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        rv.addItemDecoration(new DividerItemDecoration(ComentActivity.this,LinearLayoutManager.VERTICAL));

        btnEnterComent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createComent();
            }
        });
        itensSpinnerComent = new String[]{"     [OPÇÕES]","      EXCLUIR"};
        adapterSpinComent = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,itensSpinnerComent);

        photoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileActivity(post.getFromID());
            }
        });
        txtUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileActivity(post.getFromID());
            }
        });
    }
    private void verifyAutentication(){
        if(FirebaseAuth.getInstance().getUid() == null){
            Intent intent =  new Intent(ComentActivity.this,LoginActivity.class);
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
                            fetchPost();

                        }
                    });

        }
    }
    private void fetchPost(){
        Log.e("teste","fetcubcinebts");
        FirebaseFirestore.getInstance().collection("users")
                .document(post.getFromID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User userPost = documentSnapshot.toObject(User.class);
                        txtUserName.setText(userPost.getName());
                        txtPostText.setText(post.getPostText());
                        Picasso.get()
                                .load(userPost.getUrlProfilePhoto())
                                .into(photoUser);
                        if((post.getUrlPhotoPost() == null) || (post.getUrlPhotoPost().isEmpty())) {
                            photoPost.setVisibility(View.GONE);
                        }else{
                            Picasso.get()
                                    .load(post.getUrlPhotoPost())
                                    .into(photoPost);

                        }
                        fetchComentarios();
                    }
                });


    }
    private void fetchComentarios(){
        if(me != null){
            adapter.clear();
            FirebaseFirestore.getInstance().collection("/posts")
                    .document(post.getPostID())
                    .collection("coments")
                    .orderBy("timestamp", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable  QuerySnapshot value, FirebaseFirestoreException error) {

                            List<DocumentChange> documentChanges = value.getDocumentChanges();
                            Log.e("teste","PEGOU LISTA DE COMENTARIOS");
                            for (DocumentChange doc: documentChanges) {
                                if(doc.getType() == DocumentChange.Type.ADDED) {
                                    Coment coment = doc.getDocument().toObject(Coment.class);
                                    FirebaseFirestore.getInstance().collection("/users")
                                            .document(coment.getFromtID())
                                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                            User userComent = documentSnapshot.toObject(User.class);
                                            adapter.add(new ItemComent(coment,userComent));
                                            Log.e("teste","FETCHCOMENTARIO");
                                        }
                                    });

                                }
                            }
                        }
                    });


        }
    }

    private void createComent(){
        String text = cxTextComent.getText().toString();
        if( (text == null) || (text.isEmpty())  ){
            Toast.makeText(ComentActivity.this,"Insira um texto para comentar",Toast.LENGTH_SHORT).show();
            return;
        }
        cxTextComent.setText(null);
        long timestamp = System.currentTimeMillis();
        Coment coment =  new Coment();
        coment.setComentText(text);
        coment.setTimestamp(timestamp);
        coment.setFromtID(me.getUserID());
        String comentID = UUID.randomUUID().toString();
        coment.setComentID(comentID);

        FirebaseFirestore.getInstance().collection("/posts")
                .document(post.getPostID())
                .collection("coments")
                .document(comentID)
                .set(coment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {


            }
        });



    }
    private void profileActivity(String userID){
        Intent intent = new Intent(ComentActivity.this,ProfileActivity.class);
        intent.putExtra("userID",userID);
        startActivity(intent);
    }

    private class ItemComent extends Item<ViewHolder> {
        Coment coment;
        User userComent;

        public ItemComent(Coment coment,User userComent)
        {
            this.coment = coment;
            this.userComent = userComent;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView txtName = viewHolder.getRoot().findViewById(R.id.txt_username_coment);
            TextView txtComentText = viewHolder.getRoot().findViewById(R.id.txt_textocoment_coment);
            ImageView photoUserComent = viewHolder.getRoot().findViewById(R.id.img_photoUser_coment);
            Spinner spinner = viewHolder.getRoot().findViewById(R.id.spinner_coment);

            if( (post.getFromID().equals(me.getUserID()) ) || (coment.getFromtID().equals(me.getUserID()))) spinner.setVisibility(View.VISIBLE);
            else spinner.setVisibility(View.GONE);

            spinner.setAdapter(adapterSpinComent);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch(position){
                        case 1:
                            FirebaseFirestore.getInstance().collection("/posts")
                                    .document(post.getPostID())
                                    .collection("coments")
                                    .document(coment.getComentID())
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            adapter.removeGroup(adapter.getAdapterPosition(ComentActivity.ItemComent.this));
                                        }
                                    });
                            break;
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            txtComentText.setText(coment.getComentText());
            txtName.setText(userComent.getName());
            Picasso.get()
                    .load(userComent.getUrlProfilePhoto())
                    .into(photoUserComent);
            txtName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profileActivity(coment.getFromtID());
                }
            });
            photoUserComent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profileActivity(coment.getFromtID());
                }
            });

        }

        @Override
        public int getLayout() {
            return R.layout.item_coment;
        }
    }
}
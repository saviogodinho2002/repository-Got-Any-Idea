package com.savio.gotanyidea;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
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

import java.util.List;

public class FeedActivity extends AppCompatActivity {
    Button btnNewPost;
    ImageView photoUser;
    GroupAdapter adapter;
    User me;
    String tag;
    RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        verifyAutentication();
        adapter = new GroupAdapter();

        rv = findViewById(R.id.recycler_posts);
        rv.setLayoutManager(new LinearLayoutManager(FeedActivity.this));
        rv.setAdapter(adapter);

        btnNewPost = findViewById(R.id.btn_newpost);
        photoUser = findViewById(R.id.img_profilephoto);

        btnNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCreatePostActivity();
            }
        });

    }
    private void toCreatePostActivity(){
        Intent intent =  new Intent(FeedActivity.this,CreatePostActivity.class);
        startActivity(intent);
    }
    private void fethPosts(){
        tag = "ALIMENTO";
        if(me != null){

            FirebaseFirestore.getInstance().collection("/"+tag)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable  QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            List<DocumentChange> documentChanges = value.getDocumentChanges();
                            for (DocumentChange doc: documentChanges){
                                if(doc.getType() == DocumentChange.Type.ADDED){
                                    Posts post = doc.getDocument().toObject(Posts.class);
                                    adapter.add(new ItemPost(post));
                                }
                            }
                        }
                    });

        }

    }

    private void verifyAutentication(){
        if(FirebaseAuth.getInstance().getUid() == null){
            Intent intent =  new Intent(FeedActivity.this,LoginActivity.class);
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
                          fethPosts();
                        }
                    });

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                verifyAutentication();
                break;
        }
        return true;
    }
private class ItemPost extends Item<ViewHolder> {
        Posts post;

    public ItemPost(Posts post) {
        this.post = post;
    }

    @Override
    public void bind(@NonNull  ViewHolder viewHolder, int position) {
        ImageView photoUserPost = viewHolder.getRoot().findViewById(R.id.photo_user_post);
        TextView txtPost = viewHolder.getRoot().findViewById(R.id.txt_post);
        TextView txtNamePost = viewHolder.getRoot().findViewById(R.id.txt_fromname_post);
        TextView txtTagPost = viewHolder.getRoot().findViewById(R.id.txt_tag_post);
        ImageView photoPost = viewHolder.getRoot().findViewById(R.id.photo_post);

        Picasso.get()
                .load(post.getUrlPhotoUser())
                .into(photoUserPost);

        txtTagPost.setText("#"+post.getTag());

        txtPost.setText(post.getPostText());

        txtNamePost.setText(post.getFromName());

        if(post.getUrlPhotoPost() != null) {
            Picasso.get()
                    .load(post.getUrlPhotoPost())
                    .into(photoPost);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.item_post;
    }
}




}
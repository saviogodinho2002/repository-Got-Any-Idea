package com.savio.gotanyidea;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;

import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

public class FeedActivity extends AppCompatActivity {
    private Button btnNewPost;
    private ImageView photoUser;
    public GroupAdapter adapter;
    private User me;
    private List<Posts> postContainTag;
    private List<String> tags;
    private RecyclerView rv;
    private Spinner dropList;
    private String[] itemsDropList;
    private String[] itensDropOptions;
    ArrayAdapter<String> adapterSpinPost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        adapter = new GroupAdapter();

        rv = findViewById(R.id.recycler_posts);
        rv.setLayoutManager(new LinearLayoutManager(FeedActivity.this));
        rv.addItemDecoration(new DividerItemDecoration(FeedActivity.this,LinearLayoutManager.VERTICAL));
        rv.setAdapter(adapter);




        dropList = findViewById(R.id.spin_tags);
        btnNewPost = findViewById(R.id.btn_newpost);
        photoUser = findViewById(R.id.img_profilephoto);

        itemsDropList = new String[]{"       TAGS","      #ALLTAGS","      #TECNOLOGIA","      #CULINARIA","      #GAMBIARRA","      #ARTES"};
         itensDropOptions =  new String[]{"    [OPÇÕES]","      EXCLUIR","      EDITAR"};
          adapterSpinPost = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,itensDropOptions);
        ArrayAdapter<String> adapterDropList = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,itemsDropList);
        dropList.setAdapter(adapterDropList);
        tags = new ArrayList<>();
        postContainTag = new ArrayList<>();
        tags.clear();
        tags.add("TECNOLOGIA");
        tags.add("CULINARIA");
        tags.add("GAMBIARRA");
        tags.add("ARTES");


        dropList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 1:
                        tags.clear();
                        tags.add("TECNOLOGIA");
                        tags.add("CULINARIA");
                        tags.add("GAMBIARRA");
                        tags.add("ARTES");
                        fethPosts();
                        break;
                    case 2:
                        tags.clear();
                        tags.add("TECNOLOGIA");
                        fethPosts();
                        break;
                    case 3:
                        tags.clear();
                        tags.add("CULINARIA");
                        fethPosts();
                        break;
                    case 4:
                        tags.clear();
                        tags.add("GAMBIARRA");
                        fethPosts();
                        break;
                    case 5:
                        tags.clear();
                        tags.add("ARTES");
                        fethPosts();
                        break;
                    default:

                        break;
                }

                    Log.e("teste","executando qoque nao deve");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        btnNewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toCreatePostActivity();
            }
        });
        photoUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileActivity(me.getUserID());
            }
        });


        verifyAutentication();


    }

    private void toCreatePostActivity(){
        Intent intent =  new Intent(FeedActivity.this,CreatePostActivity.class);
        startActivity(intent);
    }

    private void fethPosts(){
        if(me != null){
            adapter.clear();

            Log.e("teste","clenando adapter");
            FirebaseFirestore.getInstance().collection("posts")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable  QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            List<DocumentChange> documentChanges = value.getDocumentChanges();
                              for (DocumentChange doc: documentChanges){
                                Posts post = doc.getDocument().toObject(Posts.class);
                              if((doc.getType() == DocumentChange.Type.ADDED) ) {

                                  try {
                                      new Thread().sleep(80);
                                  } catch (InterruptedException e) {
                                      e.printStackTrace();
                                  }

                                  FirebaseFirestore.getInstance().collection("users")
                                          .document(post.getFromID())
                                          .get()
                                          .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                              @Override
                                              public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                  task.addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                      @Override
                                                      public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                          if (!Collections.disjoint(post.getTag(), tags)) {
                                                              if ((documentChanges.size() <= 1)) {
                                                                  if (adapter.getItemCount() == 0) {

                                                                      User userpost = documentSnapshot.toObject(User.class);

                                                                      adapter.add(new ItemPost(post, userpost));



                                                                  } else return;

                                                              } else {


                                                                  User userpost = documentSnapshot.toObject(User.class);
                                                                  Log.e("teste", String.valueOf(post.getTimestamp()));
                                                                  adapter.add(adapter.getItemCount(), new ItemPost(post, userpost));

                                                              }


                                                          }
                                                      }
                                                  });
                                              }

                                          });

                                  Log.e("teste", "Itens na lista de documentos: " + String.valueOf(documentChanges.size()));


                              }
                              }
                        }

                    });

            Log.e("teste","Itens no adapter: " + String.valueOf(adapter.getItemCount()));
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
                            adapter.clear();
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
    private void profileActivity(String userID){
        Intent intent = new Intent(FeedActivity.this,ProfileActivity.class);

        intent.putExtra("userID",userID);
        startActivity(intent);
    }



private class ItemPost extends Item<ViewHolder> {
        Posts post;
        User userPost;

    public ItemPost(Posts post,User userPost) {
        this.post = post;
        this.userPost = userPost;
    }
    private Spinner spinner;
    @Override
    public void bind(@NonNull  ViewHolder viewHolder, int position) {

        ImageView likeStar = viewHolder.getRoot().findViewById(R.id.photo_like_star);
        ImageView photoUserPost = viewHolder.getRoot().findViewById(R.id.photo_user_post);
        TextView txtPost = viewHolder.getRoot().findViewById(R.id.txt_post);
        TextView txtNamePost = viewHolder.getRoot().findViewById(R.id.txt_fromname_post);
        TextView txtTagPost = viewHolder.getRoot().findViewById(R.id.txt_tag_post);
        ImageView photoPost = viewHolder.getRoot().findViewById(R.id.photo_post);
        TextView txtNumLikes = viewHolder.getRoot().findViewById(R.id.txt_numlikes_post);
        Button btnAddComent = viewHolder.getRoot().findViewById(R.id.btn_add_coment);
        TextView txtNumComents = viewHolder.getRoot().findViewById(R.id.txt_numcoments);
         spinner =  viewHolder.getRoot().findViewById(R.id.spin_post_options);
        ImageView iconComent = viewHolder.getRoot().findViewById(R.id.photo_iconcoment);

        iconComent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedActivity.this,ComentActivity.class);
                intent.putExtra("post",post);
                startActivity(intent);
            }
        });
         FirebaseFirestore.getInstance().collection("posts")
                 .document(post.getPostID())
                 .collection("coments")
                 .addSnapshotListener(new EventListener<QuerySnapshot>() {
                     @Override
                     public void onEvent(@Nullable  QuerySnapshot value, @Nullable  FirebaseFirestoreException error) {
                        List<DocumentChange> documentChanges = value.getDocumentChanges();
                            if( (value.getDocumentChanges().size() > 1) && txtNumComents.getText().equals("0"))
                                txtNumComents.setText( String.valueOf(value.getDocumentChanges().size()) );


                     }
                 });

        spinner.setAdapter(adapterSpinPost);
         txtNumLikes.setText(String.valueOf(post.getNumLikes()));
        if(!post.getUserLikedId().contains(me.getUserID()))
         likeStar.setBackgroundResource(R.drawable.lampiconoff);
        else
            likeStar.setBackgroundResource(R.drawable.lampiconon);

        likeStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean liked;
                if(!post.getUserLikedId().contains(me.getUserID())){
                    post.getUserLikedId().add(me.getUserID());
                    post.setNumLikes(post.getNumLikes()+1);
                    liked = true;
                }
                else{
                    liked =false;
                    post.getUserLikedId().remove(me.getUserID());
                    post.setNumLikes(post.getNumLikes()-1);
            }
                    FirebaseFirestore.getInstance().collection("/posts")
                            .document(post.getPostID())
                            .update("userLikedId",post.getUserLikedId())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                 Log.e("teste","LIKE SETADO");
                                 FirebaseFirestore.getInstance().collection("/posts")
                                         .document(post.getPostID())
                                            .update("numLikes",post.getNumLikes())
                                 .addOnSuccessListener(new OnSuccessListener<Void>() {
                                     @Override
                                     public void onSuccess(Void unused) {
                                         Log.e("teste","LIKE implementation");
                                         if(liked) {
                                             likeStar.setBackgroundResource(R.drawable.lampiconon);
                                             txtNumLikes.setText(String.valueOf(post.getNumLikes()));
                                         }else {
                                             likeStar.setBackgroundResource(R.drawable.lampiconoff);
                                             txtNumLikes.setText(String.valueOf(post.getNumLikes()));
                                         }

                                     }
                                 });

                                }
                            });
            }
        });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 1:
                        FirebaseFirestore.getInstance().collection("/posts")
                                .document(post.getPostID())
                                .delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        adapter.removeGroup(adapter.getAdapterPosition(ItemPost.this));
                                        Log.e("teste","excluiu porra");
                                        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images-post/"+post.getPhotoPostFileName());
                                        ref.delete();

                                    }
                                });
                        spinner.setSelection(0);
                        break;
                    case 2:

                        Intent intent = new Intent(FeedActivity.this,EditPostActivity.class);
                        Log.e("teste","PASSAR PARA O INTENT");
                        intent.putExtra("postID",post.getPostID());
                        Log.e("teste","PASSOU PARA O INTENT");
                        spinner.setSelection(0);
                        startActivity(intent);
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnAddComent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("teste","clicou");
                Intent intent = new Intent(FeedActivity.this,ComentActivity.class);
                intent.putExtra("post",post);
                startActivity(intent);
            }
        });


        if( !post.getFromID().equals(me.getUserID()) )  spinner.setVisibility(View.GONE);
        else spinner.setVisibility(View.VISIBLE);
        Picasso.get()
                .load(userPost.getUrlProfilePhoto())
                .into(photoUserPost);

        txtTagPost.setText("");
        for(String tag :post.getTag()){
            txtTagPost.setText( txtTagPost.getText()+" " + "#"+tag);
        }

        txtPost.setText(post.getPostText());

        txtNamePost.setText(userPost.getName());

        if((post.getUrlPhotoPost() != null) && !post.getUrlPhotoPost().isEmpty()) {
            //Log.e("teste","TEM FOTO");
            photoPost.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(post.getUrlPhotoPost())
                    .into(photoPost);

        }else {
           photoPost.setVisibility(View.GONE);
        }

        photoUserPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileActivity(post.getFromID());
            }
        });

        txtNamePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileActivity(post.getFromID());
            }
        });


    }

    @Override
    public int getLayout() {
        return R.layout.item_post;
    }
}




}
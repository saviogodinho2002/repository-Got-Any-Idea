package com.savio.gotanyidea;

import android.app.Activity;
import android.app.Dialog;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class DialogDelAccount extends Dialog implements View.OnClickListener {
    public Button bnYes,btnNo;
    public Activity c;
    public User me;
    public DialogDelAccount(@NonNull Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_delaccount);
        bnYes = findViewById(R.id.btn_yes);
        btnNo = findViewById(R.id.btn_not);
        bnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btn_yes:
                delAcount();
                break;
            case R.id.btn_not:
                dismiss();
                break;
            default:
                break;
        }
    }
    public void delAcount(){

        FirebaseUser fme = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore.getInstance().collection("users")
                .document(fme.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        me = documentSnapshot.toObject(User.class);
                        FirebaseFirestore.getInstance().collection("posts")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                       List<DocumentChange> documentChanges =  queryDocumentSnapshots.getDocumentChanges();
                                        for (DocumentChange doc:
                                             documentChanges) {
                                            Posts post = doc.getDocument().toObject(Posts.class);
                                            if(post.getFromID().equals(me.getUserID())){
                                                try {
                                                    Thread.sleep(80);
                                                } catch (InterruptedException e) {
                                                    e.printStackTrace();
                                                }
                                                if(post.getUrlPhotoPost() != null && !post.getUrlPhotoPost().isEmpty()){
                                                    FirebaseStorage.getInstance().getReference("images-post/"+post.getPhotoPostFileName())
                                                            .delete();
                                                    try {
                                                        Thread.sleep(80);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                FirebaseFirestore.getInstance().collection("posts")
                                                        .document(post.getPostID())
                                                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull  Task<Void> task) {

                                                    }
                                                });
                                            }
                                        }
                                    }

                                });
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        FirebaseAuth.getInstance().getCurrentUser()
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        FirebaseFirestore.getInstance().collection("users")
                                                .document(me.getUserID())
                                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                FirebaseStorage.getInstance().getReference("images-profile/"+me.getFileNameProfilePhoto())
                                                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull  Task<Void> task) {

                                                        try {
                                                            Thread.sleep(80);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                        dismiss();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });

                    }

                });
    }


}

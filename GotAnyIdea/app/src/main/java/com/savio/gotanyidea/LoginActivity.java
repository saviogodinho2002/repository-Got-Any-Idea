package com.savio.gotanyidea;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button btnLogin;
    private EditText cxEmail, cxPassWord;
    private TextView txtRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnLogin = findViewById(R.id.btn_login);
        cxEmail = findViewById(R.id.cx_email);
        cxPassWord = findViewById(R.id.cx_password);
        txtRegister = findViewById(R.id.txt_register);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();

            }
        });
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toRegisterActiviy();
            }
        });

    }
    private void logIn(){
        String email = cxEmail.getText().toString();
        String password = cxPassWord.getText().toString();
        if( (email == null) || (email.isEmpty()) || (password == null) || (password.isEmpty())  ){
            Toast.makeText(LoginActivity.this,"Email e senha devem ser inseridos",Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Intent intent = new Intent(LoginActivity.this,FeedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

    }
    private void toRegisterActiviy(){
        Intent intent =  new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
}
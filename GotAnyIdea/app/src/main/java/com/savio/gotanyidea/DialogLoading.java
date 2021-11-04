package com.savio.gotanyidea;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;


import androidx.annotation.NonNull;


import pl.droidsonroids.gif.GifImageView;

public class DialogLoading extends Dialog {
    public DialogLoading(@NonNull Context context) {
        super(context);
    }
    private GifImageView gif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading);
        gif = findViewById(R.id.gif_lamp);

    }

}

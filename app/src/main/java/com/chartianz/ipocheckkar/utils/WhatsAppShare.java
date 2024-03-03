package com.chartianz.ipocheckkar.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.chartianz.ipocheckkar.R;

public class WhatsAppShare {
    public static void shareText(Context context, String shareBody, String shareBody2) {
        Uri imageUri = null;
        try {
            imageUri = Uri.parse(MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.whatsapp_share), null, null));
        }catch (NullPointerException e) {}

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareBody + "\n\n" + shareBody2);
        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.setType("image/*");

        context.startActivity(Intent.createChooser(intent, "Share via"));
    }
}


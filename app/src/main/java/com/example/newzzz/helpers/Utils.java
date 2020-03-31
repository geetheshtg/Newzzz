package com.example.newzzz.helpers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

public class Utils {
    public static void hideKeyboard(Context context) {
        try {
            InputMethodManager inputManager = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager.isAcceptingText())
                inputManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showUnexpectedError(Context context) {
        showToast(context, "An unexpected error occurred. Please try again later.");
    }

    public static void showInternetError(Context context) {
        showToast(context, "Connection unavailable. Please connect to internet");
    }

    public static String getIconImageExtension(Context context, Uri uri) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
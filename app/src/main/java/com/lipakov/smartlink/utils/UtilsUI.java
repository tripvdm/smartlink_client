package com.lipakov.smartlink.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lipakov.smartlink.MainActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class UtilsUI {
    private static final String TAG = UtilsUI.class.getSimpleName();

    public static Bitmap convertFileToBitmap(String fileName) {
        File file = new File(fileName);
        Bitmap originalBitmap = null;
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(file));
            originalBitmap = BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        return originalBitmap;
    }

    public static EditText createEditText(MainActivity activity) {
        final EditText input = new EditText(activity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        return input;
    }

}

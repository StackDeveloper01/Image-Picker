package com.imagepickerlibrary.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;

import static com.imagepickerlibrary.providers.LocalStorageProvider.AUTHORITY;


/**
 * Created by Mickael on 10/10/2016.
 */

public class CameraPickerManager extends PickerManager {

    public CameraPickerManager(Activity activity) {
        super(activity);
    }

    protected void sendToExternalApp() {
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        if (isCropRequired()) {
            mProcessingPhotoUri = activity.getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mProcessingPhotoUri);
        } else {
            File file = createImageFile(activity);
            setFile(file);
            Uri photoURI = FileProvider.getUriForFile(activity, AUTHORITY, file);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        }
        activity.startActivityForResult(captureIntent, REQUEST_CODE_SELECT_IMAGE);
    }
}

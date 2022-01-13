package com.imagepickerlibrary.managers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.imagepickerlibrary.PickerBuilder;
import com.imagepickerlibrary.utils.FileUtils;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.imagepickerlibrary.R;


/**
 * Created by Pankaj on 10/10/2016.
 */

public abstract class PickerManager {
    public static final int REQUEST_CODE_SELECT_IMAGE = 200;
    public static final int REQUEST_CODE_IMAGE_PERMISSION = 201;
    public static final int REQUEST_CODE_CAMERA_PERMISSION = 202;
    protected Uri mProcessingPhotoUri;
    private boolean withTimeStamp = true;
    private String folder = null;
    private String imageName;
    protected Activity activity;
    private UCrop uCrop;
    protected PickerBuilder.onImageReceivedListener imageReceivedListener;
    protected PickerBuilder.onPermissionRefusedListener permissionRefusedListener;
    private int cropActivityColor = R.color.colorPrimary;
    private boolean isCropRequired;
    private Uri uri;
    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isCropRequired() {
        return isCropRequired;
    }

    public void setCropRequired(boolean cropRequired) {
        isCropRequired = cropRequired;
    }

    public PickerManager setOnImageReceivedListener(PickerBuilder.onImageReceivedListener listener) {
        this.imageReceivedListener = listener;
        return this;
    }

    public PickerManager setOnPermissionRefusedListener(PickerBuilder.onPermissionRefusedListener listener) {
        this.permissionRefusedListener = listener;
        return this;
    }

    public PickerManager(Activity activity) {
        this.activity = activity;
        this.imageName = activity.getString(R.string.app_name).replace(" ","");
    }


    public void pickPhotoWithPermission() {

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_CAMERA_PERMISSION);
        } else if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_IMAGE_PERMISSION);
        } else if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_IMAGE_PERMISSION);
        } else
            sendToExternalApp();
    }

    public void handlePermissionResult(int[] grantResults) {

        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            pickPhotoWithPermission();

        } else {

            // permission denied
            if (permissionRefusedListener != null)
                permissionRefusedListener.onPermissionRefused();
            activity.finish();
        }
    }


    protected abstract void sendToExternalApp();

    protected File createImageFile(Context context)  {
        File image = null;
        String finalPhotoName = imageName +
                (withTimeStamp ? "_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format
                        (new Date(System.currentTimeMillis())) : "")
                ;
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            image  = File.createTempFile(
                    finalPhotoName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }
    public Uri getImageFile() {
        String imagePathStr = Environment.getExternalStorageDirectory() + "/" +
                (folder == null ?
                        Environment.DIRECTORY_DCIM + "/" + activity.getString(R.string.app_name).replace(" ","") :
                        folder);

        File path = new File(imagePathStr);
        if (!path.exists()) {
            path.mkdir();
        }

        String finalPhotoName = imageName +
                (withTimeStamp ? "_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date(System.currentTimeMillis())) : "")
                + ".jpg";

        // long currentTimeMillis = System.currentTimeMillis();
        // String photoName = imageName + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date(currentTimeMillis)) + ".jpg";
        File photo = new File(path, finalPhotoName);
        return Uri.fromFile(photo);
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public void startCropActivity() {
        if (uCrop == null) {
            uCrop = UCrop.of(mProcessingPhotoUri, getImageFile());
            uCrop = uCrop.useSourceImageAspectRatio();
            UCrop.Options options = new UCrop.Options();
            options.setFreeStyleCropEnabled(true);
            options.setCompressionQuality(50);
            //options .withAspectRatio(9, 9);
            options.withMaxResultSize(200, 200);
            options.setToolbarColor(activity.getResources().getColor(cropActivityColor));
            options.setStatusBarColor(activity.getResources().getColor(cropActivityColor));
            options.setActiveWidgetColor(activity.getResources().getColor(cropActivityColor));
            uCrop = uCrop.withOptions(options);
        }

        uCrop.start(activity);
    }

    public void handleCropResult(Intent data) {
        Uri resultUri = UCrop.getOutput(data);
        if (imageReceivedListener != null)
            /*System.out.print("resultUri -> "+ resultUri +", FileUtils.getPath(activity,resultUri)" +
                    " -> "+FileUtils.getPath(activity,resultUri));*/
            imageReceivedListener.onImageReceived(resultUri, FileUtils.getPath(activity,resultUri),FileUtils.getFile(activity,resultUri));

        activity.finish();
    }

    public void handleGalleryImage(Uri uri) {
        if (imageReceivedListener != null)
            imageReceivedListener.onImageReceived(uri,FileUtils.getPath(activity,uri),FileUtils.getFile(activity,uri));

        activity.finish();
    }
    public void handleCameraImage(Uri uri) {
        if (imageReceivedListener != null)
            imageReceivedListener.onImageReceived(uri,getFile().getAbsolutePath(),getFile());

        activity.finish();
    }



    public PickerManager setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public PickerManager setImageName(String imageName) {
        this.imageName = imageName;
        return this;
    }

    public PickerManager setCropActivityColor(int cropActivityColor) {
        this.cropActivityColor = cropActivityColor;
        return this;
    }

    public PickerManager withTimeStamp(boolean withTimeStamp) {
        this.withTimeStamp = withTimeStamp;
        return this;
    }

    public PickerManager setImageFolderName(String folder) {
        this.folder = folder;
        return this;
    }

    public PickerManager setCustomizedUcrop(UCrop customizedUcrop) {
        this.uCrop = customizedUcrop;
        return this;
    }
}

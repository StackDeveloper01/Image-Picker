package com.imagepickerlibrary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import com.imagepickerlibrary.constants.GlobalHolder;
import com.imagepickerlibrary.managers.ImagePickerManager;
import com.imagepickerlibrary.managers.PickerManager;

import static com.imagepickerlibrary.providers.LocalStorageProvider.AUTHORITY;
import static com.yalantis.ucrop.UCrop.REQUEST_CROP;


public class TempActivity extends AppCompatActivity {

    PickerManager pickerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.pickerManager = GlobalHolder.getInstance().getPickerManager();
        this.pickerManager.setActivity(TempActivity.this);
        this.pickerManager.pickPhotoWithPermission();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            finish();
            return;
        }
        switch (requestCode) {
            case PickerManager.REQUEST_CODE_SELECT_IMAGE:
                Uri uri;
                if (!pickerManager.isCropRequired()) {

                    if (pickerManager instanceof ImagePickerManager) {
                        uri = data.getData();
                        pickerManager.setUri(uri);
                        pickerManager.handleGalleryImage(uri);
                    } else {
                        uri = FileProvider.getUriForFile(this, AUTHORITY, pickerManager.getFile());
                        pickerManager.handleCameraImage(uri);
                    }
                } else {

                    if (data != null)
                        uri = data.getData();
                    else
                        uri = pickerManager.getImageFile();
                    pickerManager.setUri(uri);
                    pickerManager.startCropActivity();
                }
                break;
            case REQUEST_CROP:
                if (data != null) {
                    pickerManager.handleCropResult(data);
                } else
                    finish();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        if (requestCode == PickerManager.REQUEST_CODE_CAMERA_PERMISSION || requestCode == PickerManager.REQUEST_CODE_IMAGE_PERMISSION)
            pickerManager.handlePermissionResult(grantResults);
        else
            finish();

    }

}

***Image Picker Overview***


Language :- Java

Platform :- Android

Scope :-Image Picker is android based library, which allow user to get images which have the following options:

-> Camera captured image

-> Camera image with crop functionality

-> Gallery image 

-> Gallery image with crop functionality

 
Crop functionality is provided with multiple option to rotate, zoom level etc.

 
***How to call?***


- Type values:

    -> SELECT_FROM_GALLERY

    -> SELECT_FROM_CAMERA
 
 
- isCropRequerd values:

    True -> need crop functionality

    False -> get original image without cropping


- Call  PickerBuilder Constructor as follow: 

    new PickerBuilder(activity:this,type:0,isCropRequired:true)


- And implement the following listeners as per requirement:

    -> setOnImageReceivedListener

    -> setOnPermissionRefusedListener


**We have already defind the below mention stuff in Manifest, gradle and xml for corresponding purpose**
- uCrop Usage in library:

    Gradle:   implementation 'com.yalantis:ucrop:2.2.0'

    In AndroidManifest 
    `<activity
       android:name="com.yalantis.ucrop.UCropActivity"
       android:screenOrientation="portrait"
       android:theme="@style/Theme.AppCompat.Light.NoActionBar" />`
   
 
- Define provider:

    In AndroidManifest
    `<provider
       android:name=".providers.LegacyCompatFileProvider"
       android:authorities="com.imagepickerlibrary.provider"
       android:exported="false"
       android:grantUriPermissions="true">
       <meta-data
           android:name="android.support.FILE_PROVIDER_PATHS"
           android:resource="@xml/provider_paths" />
    </provider>`

 
- Mention path:

    Path have to be defined in xml
    `<paths>
        <external-files-path name="my_images" path="."/>
    </paths>`
    If you need specific path to be referenced, then path need to define instead of “.”.

 
**Disclaimer**: This app will work in portrait mode. 


**References**:
- https://github.com/Tofira/ImagePickerWithCrop
- https://github.com/Yalantis/uCrop

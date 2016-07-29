package annikatsai.portfolioapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity {

    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    public final static int PICK_PHOTO_CODE = 1046;

    Bitmap image = null;
    int rotationAngle = 90;
    Uri photoUri = null;

    private FirebaseStorage mStorage;
    StorageReference storageRef;
    final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    StorageReference picRef;
    String fileName;

    private DatabaseReference mDatabase;
    ProgressDialog pd;
    Uri downloadUrl;
    String realOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Customizing Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        Typeface titleFont = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        toolbarTitle.setText("Choose a Picture");
        toolbarTitle.setTypeface(titleFont);

        // Create an instance of FirebaseStorage
        mStorage = FirebaseStorage.getInstance();
        // Create a storage reference from our app.
        storageRef = mStorage.getReferenceFromUrl("gs://travel-portfolio-app.appspot.com");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        fileName = mDatabase.child("users").child(userId).child("fileName").push().getKey();

        pd = new ProgressDialog(CameraActivity.this);
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
    }

    public void onTakeClick(View view) {
        onLaunchCamera(view);
    }

    public void onUploadClick(View view) {
        onPickPhoto(view);
    }

    public void onRotateClick(View view){
        Matrix matrix = new Matrix();
        matrix.postRotate(rotationAngle);
        rotationAngle = rotationAngle + 90;
        Bitmap bm = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
        // Load the taken image into a preview
        ImageView ivPreview = (ImageView) findViewById(R.id.ivPreview);
        ivPreview.setImageBitmap(bm);

        image = bm;

        if(rotationAngle == 180 || rotationAngle == 0){
            realOrientation = "v";
        } else {
            realOrientation = "h";
        }
    }

    public void onSubmitClick(View view){
        String activity = getIntent().getStringExtra("activity");
        Intent i = null;
        if (activity.equals("Post")) {
            i = new Intent(this, PostActivity.class);

        } else if (activity.equals("EditPost")) {
            i = new Intent(this, EditPostActivity.class);
        }
        if (photoUri == null) {
            setResult(RESULT_CANCELED);
        } else {
            if (i != null) {
                i.putExtra("fileName", fileName);
                i.setData(downloadUrl);
                i.putExtra("realOrientation", realOrientation);
            }
            setResult(RESULT_OK, i);
        }
        finish();
    }

    public void onLaunchCamera(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(fileName));

        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } else{
            // Warns user if camera can't be opened
            Toast.makeText(getApplicationContext(), "Can't open camera.", Toast.LENGTH_SHORT).show();
        }
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                photoUri = getPhotoFileUri(fileName);
                image = rotateBitmapOrientation(photoUri);

                // Load the taken image into a preview
                ImageView ivPreview = (ImageView) findViewById(R.id.ivPreview);
                ivPreview.setImageBitmap(image);
                pd.show();

                /*STORAGE FIREBASE CODE: START*/
                Uri file = Uri.fromFile(new File(photoUri.getPath()));
                picRef = storageRef.child("users").child(userId).child(fileName);

                UploadTask uploadTask = picRef.putFile(file);
                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(getApplicationContext(), "Couldn't upload image! :(", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        downloadUrl = taskSnapshot.getDownloadUrl();
                        pd.dismiss();
                    }
                });
                /*STORAGE FIREBASE CODE: END*/
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == PICK_PHOTO_CODE){
            if (data != null) {
                // Gets Uri
                photoUri = data.getData();
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Load the selected image into a preview
                ImageView ivPreview = (ImageView) findViewById(R.id.ivPreview);
                ivPreview.setImageBitmap(image);
                pd.show();

                realOrientation = "h";

                /*STORAGE FIREBASE CODE: START*/
                Bitmap picture = null;
                picRef = storageRef.child("users").child(userId).child(fileName);
                try {
                    picture = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                if (picture != null) {
                    picture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                }
                byte[] byteArray = baos.toByteArray();
                UploadTask uploadTask = picRef.putBytes(byteArray);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(getApplicationContext(), "Couldn't upload image! :(", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        downloadUrl = taskSnapshot.getDownloadUrl();
                        pd.dismiss();
                    }
                });
                /*STORAGE FIREBASE CODE: END*/
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(photoUri != null) {
            // Delete the file
            picRef.delete().addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {

                }

                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                    Toast.makeText(getApplicationContext(), "Error deleting pic from database", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            File mediaStorageDir = new File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(APP_TAG, "failed to create directory");
            }
            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    // Returns true if external storage for photos is available
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public Bitmap rotateBitmapOrientation(Uri takenPhotoUri) {
        // Create and configure BitmapFactory
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(takenPhotoUri.getPath(), bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(takenPhotoUri.getPath(), opts);

        // Read EXIF Data
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(takenPhotoUri.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        realOrientation = "h";
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90){
            rotationAngle = 90;
            realOrientation = "v"; //double check this
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180){
            rotationAngle = 180;
            realOrientation = "h"; //double check this
        }
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270){
            rotationAngle = 270;
            realOrientation = "v"; //double check this
        }

        // Rotate Bitmap
        Matrix matrix = new Matrix();
        matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);

        // Return result
        return rotatedBitmap;
    }
}
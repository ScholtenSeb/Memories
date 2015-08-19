package com.sebastianscholten.memories;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


public class NewMemory extends ActionBarActivity {

    public File image = null;
    public File photoFile = null;
    public ImageView memoryImageView;
    static final int REQUEST_TAKE_PHOTO = 10;
    String mCurrentPhotoPath;
    Helper helper = new Helper();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_memory);
        memoryImageView = (ImageView) findViewById(R.id.memoryImage);
    }

    public void launchCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Toast.makeText(this,"Could not create photo file",Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
            }
        }

    }

    public void rememberMemory(View view) throws Exception {
        if (photoFile != null) {
            HashMap<String, String> tempFields = new HashMap<>();
            tempFields.put("name","sebastian");
            tempFields.put("surname","scholten");
            Memory memory = new Memory(this,tempFields,photoFile);
            memory.saveMemory();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {

                //Toast.makeText(this,"Image saved: "+photoFile.getAbsolutePath(),Toast.LENGTH_LONG).show();
                Bitmap bm = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(photoFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                memoryImageView.setImageBitmap(helper.rotateBitmap(bm, orientation));

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_memory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

}

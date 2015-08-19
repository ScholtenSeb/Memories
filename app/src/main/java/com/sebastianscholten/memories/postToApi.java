package com.sebastianscholten.memories;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class postToApi extends AsyncTask {

    //TextView btnRemember = (TextView) ((NewMemory)context).findViewById(R.id.btnRemember);

    private static Context context;
    private HashMap<String, String> fields;
    private File image;

    public postToApi(Context iContext, HashMap<String, String> iFields) {
        context = iContext;
        fields  = iFields;
    }

    public postToApi(Context iContext, HashMap<String, String> iFields, File iImage) {
        context = iContext;
        fields  = iFields;
        image = iImage;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        Log.i("HTTP_POST","Starting upload");
        Log.i("IMAGE_FILE",image.getAbsolutePath());

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://178.62.60.206:3000/memories/create");
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            // Loop through fields and add to http post
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                entityBuilder.addTextBody(entry.getKey()  , entry.getValue() );
            }

            Bitmap bmp = BitmapFactory.decodeFile(image.getAbsolutePath());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG,20, bos);
            ContentBody photo = new ByteArrayBody(bos.toByteArray(),"image/jpeg","image");

            // Add image to http post
            //entityBuilder.addBinaryBody("image", image);
            //entityBuilder.addPart("image",photo);
            entityBuilder.addPart("image", photo);

            HttpEntity entity = entityBuilder.build();
            post.setEntity(entity);

            HttpResponse response = client.execute(post);
            HttpEntity httpEntity = response.getEntity();

            Log.v("result", EntityUtils.toString(httpEntity));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        Log.i("HTTP_POST","Upload done");
        Toast.makeText(context,"Upload done",Toast.LENGTH_LONG).show();
        //btnRemember.setText("REMEMBERED");
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        //btnRemember.setText("REMEMBERING: " + (Integer) (values[0]) * 2 + "%");
    }
}

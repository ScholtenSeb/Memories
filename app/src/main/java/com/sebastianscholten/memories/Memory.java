package com.sebastianscholten.memories;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.util.HashMap;

public class Memory {

    private Context context;

    private HashMap<String, String> memoryFields;
    private File memoryImage;

    public Memory(Context c, HashMap<String, String> fields, File image) {
        context = c;
        memoryFields = fields;
        memoryImage = image;
        Log.i("MEMORY","New Memory created");
    }

    public void saveMemory() {
        postToApi save = new postToApi(context,memoryFields,memoryImage);
        save.execute();
    }
}

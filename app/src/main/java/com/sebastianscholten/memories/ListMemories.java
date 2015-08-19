package com.sebastianscholten.memories;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListMemories extends ActionBarActivity {

    ListView listMemories;
    JSONArray jsonArr = null;

    List<Map<String, String>> memoryList = new ArrayList<Map<String, String>>();
    String TAG_UID = "uid";
    String TAG_TITLE = "title";
    String TAG_TEXT = "text";
    String TAG_LNG = "longitude";
    String TAG_LAT = "latitude";
    String TAG_IMG = "imageName";

    String URL_ALL_MEMORIES = "http://178.62.60.206:3000/memories/all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_memories);
        GetJSON get = new GetJSON();
        get.execute();
        listMemories = (ListView) findViewById(R.id.listView);
    }

    private void InitAllMemories() {
        try {
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jObj = jsonArr.getJSONObject(i);
                // Extract json values
                String uid = jObj.optString(TAG_UID);
                String title = jObj.optString(TAG_TITLE);
                String text = jObj.optString(TAG_TEXT);
                String lng = jObj.optString(TAG_LNG);
                String lat = jObj.optString(TAG_LAT);
                String imgName = jObj.optString(TAG_IMG);
                // Put values into temporary HashMap
                HashMap<String,String> map = new HashMap<String,String>();
                map.put(TAG_UID,uid);
                map.put(TAG_TITLE,title);
                map.put(TAG_TEXT,text);
                map.put(TAG_LNG,lng);
                map.put(TAG_LAT,lat);
                map.put(TAG_IMG,imgName);
                memoryList.add(map);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(ListMemories.this, memoryList,
                    android.R.layout.simple_list_item_2,
                    new String[] {"title","text"},
                    new int[] {android.R.id.text1,android.R.id.text2});
            listMemories.setAdapter(simpleAdapter);
            listMemories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                    try {
                        JSONObject obj = jsonArr.getJSONObject(i);
                        Intent intent = new Intent(ListMemories.this,ViewMemory.class);
                        // put data values
                        intent.putExtra(TAG_UID,obj.optString(TAG_UID));
                        intent.putExtra(TAG_TITLE,obj.optString(TAG_TITLE));
                        intent.putExtra(TAG_TEXT,obj.optString(TAG_TEXT));
                        intent.putExtra(TAG_LNG,obj.optString(TAG_LNG));
                        intent.putExtra(TAG_LAT,obj.optString(TAG_LAT));
                        intent.putExtra(TAG_IMG,obj.optString(TAG_IMG));
                        //launch intent
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void LaunchNewMemoryActivity(View view) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_memories, menu);
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

    public class GetJSON extends AsyncTask {

        @Override
        protected Void doInBackground(Object... params) {
            String response;
            try {
                // GET data from API
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(URL_ALL_MEMORIES);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                response = EntityUtils.toString(httpEntity);
                // Set global json array with response data
                jsonArr = new JSONArray(response);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            // When data is loaded -> populate listview
            if (jsonArr != null) {
                InitAllMemories();
            }

        }
    }
}
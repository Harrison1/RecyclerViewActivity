package com.harrisonmcguire.recyclerview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Harrison on 5/23/2015.
 */
public class MainActivity extends Activity {
    private static final String TAG = "RecyclerViewExample";
    private List<ListItems> listItemsList = new ArrayList<ListItems>();

    private RecyclerView mRecyclerView;
    private MyRecyclerAdapter adapter;

    private int counter = 0;
    private String count;
    private String jsonSubreddit;
    private String after_id;
    private static final String gaming = "gaming";
    private static final String aww = "aww";
    private static final String funny = "funny";
    private static final String food = "food";
    private static final String subredditUrl = "http://www.reddit.com/r/";
    private static final String jsonEnd = "/.json";
    private static final String qCount = "?count=";
    private static final String after = "&after=";

    private ProgressDialog progressDialog;

    public static String fragSubreddit = "aww";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /* Initialize recycler view */
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.addItemDecoration(
                new HorizontalDividerItemDecoration.Builder(this)
                        .color(Color.BLACK)
                        .build());

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);


         updateList(aww);

        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                Log.d("SCROLL PAST UPDATE", "You hit me");
                int lastFirstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
                ((LinearLayoutManager) mRecyclerView.getLayoutManager()).scrollToPosition(lastFirstVisiblePosition);
                loadMore(jsonSubreddit);
            }
        });


    }



    public void updateList(String subreddit) {

        counter = 0;

        subreddit = subredditUrl + subreddit + jsonEnd;

        adapter = new MyRecyclerAdapter(MainActivity.this, listItemsList);
        mRecyclerView.setAdapter(adapter);

        RequestQueue queue = Volley.newRequestQueue(this);

        adapter.clearAdapter();

        showPD();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, subreddit, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, response.toString());
                hidePD();

                try {
                    JSONObject data = response.getJSONObject("data");
                    after_id = data.getString("after");
                    JSONArray children = data.getJSONArray("children");

                    for (int i = 0; i < children.length(); i++) {

                        JSONObject post = children.getJSONObject(i).getJSONObject("data");
                        ListItems item = new ListItems();
                        item.setTitle(post.getString("title"));
                        item.setThumbnail(post.getString("thumbnail"));
                        item.setUrl(post.getString("url"));
                        item.setSubreddit(post.getString("subreddit"));
                        item.setAuthor(post.getString("author"));

                        jsonSubreddit = post.getString("subreddit");

                        listItemsList.add(item);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // update list by notifying the adapter of changes
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePD();
            }
        });

        queue.add(jsObjRequest);

    }

    public void loadMore(String subreddit) {

        counter = counter + 25;
        count = String.valueOf(counter);
        subreddit = jsonSubreddit;

        Log.d("Counter", count);
        Log.d("jsonSub", jsonSubreddit);

        subreddit = subredditUrl + subreddit + jsonEnd + qCount + count + after + after_id;

        Log.d("Added Subred", subreddit);

        adapter = new MyRecyclerAdapter(MainActivity.this, listItemsList);
        mRecyclerView.setAdapter(adapter);

        RequestQueue queue = Volley.newRequestQueue(this);

        showPD();

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, subreddit, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, response.toString());
                hidePD();

                try {
                    JSONObject data = response.getJSONObject("data");
                    after_id = data.getString("after");
                    JSONArray children = data.getJSONArray("children");

                    for (int i = 0; i < children.length(); i++) {

                        JSONObject post = children.getJSONObject(i).getJSONObject("data");
                        ListItems item = new ListItems();
                        item.setTitle(post.getString("title"));
                        item.setThumbnail(post.getString("thumbnail"));
                        item.setUrl(post.getString("url"));
                        item.setSubreddit(post.getString("subreddit"));
                        item.setAuthor(post.getString("author"));

                        listItemsList.add(item);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // update list by notifying the adapter of changes
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error" + error.getMessage());
                hidePD();
            }
        });

        queue.add(jsObjRequest);
    }


    public void funnyUpdate(View v) {
       updateList(funny);
    }

    public void foodUpdate(View v) {
        updateList(food);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void showPD() {
        if(progressDialog == null) {
            progressDialog  = new ProgressDialog(this);
            progressDialog .setMessage("Loading...");
            progressDialog .setCancelable(false);
            progressDialog .setCanceledOnTouchOutside(false);
            progressDialog .show();
        }
    }

    // function to hide the loading dialog box
    private void hidePD() {
        if (progressDialog  != null) {
            progressDialog .dismiss();
            progressDialog  = null;
        }
    }

    // Stop app from running
    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePD();
    }

}




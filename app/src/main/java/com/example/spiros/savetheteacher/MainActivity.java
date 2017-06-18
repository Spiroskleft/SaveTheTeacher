package com.example.spiros.savetheteacher;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.spiros.savetheteacher.Realm.Region;
import com.example.spiros.savetheteacher.Weather.WeatherActivity;
import com.example.spiros.savetheteacher.Weather.WeatherForecastActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {


    //adapter class
    ArrayList<AdapterItems> listnewsData = new ArrayList<AdapterItems>();
    private int StartFrom = 0;
    private int UserOperation = SearchType.MyFollowing; // 0 my followers post 2- specifc user post 3- search post
    private String Searchquery;
    private int totalItemCountVisible = 0; //totalItems visible
    private LinearLayout ChannelInfo;
    private TextView txtnamefollowers;
    private int SelectedUserID = 0;
    private Button buFollow;
    private MyCustomAdapter myadapter;

    //------------------------------------------------------------------------------------------
    //Κουμπί για να εισάγουμε στη Realm το JSON αρχείο + Φτιάξιμο της λίστας από το Realm
    private Button importbtn ;
    private List<Region>  regionList  = new ArrayList<>();
    private RealmResults<Region> regionRealmList;
    private Realm realm ;
    //------------------------------------------------------------------------------------------





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ChannelInfo = (LinearLayout) findViewById(R.id.ChannelInfo);
        ChannelInfo.setVisibility(View.GONE);
        txtnamefollowers = (TextView) findViewById(R.id.txtnamefollowers);
        buFollow = (Button) findViewById(R.id.buFollow);
        //load user data setting
       SaveSettings saveSettings = new SaveSettings(getApplicationContext());
        saveSettings.LoadData();

        //Ορίζουμε τον adapter για τα news
        listnewsData.add(new AdapterItems(null,null,null,"add",null,null,null));
//        listnewsData.add(new AdapterItems(null,null,null,"loading",null,null,null));
//        listnewsData.add(new AdapterItems(null,null,null,"notweet",null,null,null));
//        listnewsData.add(new AdapterItems(null,null,null,"lalala",null,null,null));

        myadapter=new MyCustomAdapter(this,listnewsData);
        ListView lsNews=(ListView)findViewById(R.id.LVNews);
        lsNews.setAdapter(myadapter);// with data
        LoadTweets(0,SearchType.MyFollowing);


        //------------------------------------------------------------------------------------------

        // Εισαγωγή κουμπιού για να φορτώσουμε στο Realm το JSON αρχείο
        importbtn = (Button) findViewById(R.id.importbtn);
        importbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                InputStream inputStream = getResources().openRawResource(getResources().getIdentifier("perioxes","raw", getPackageName()));
                String sxml = readTextFile(inputStream);
                System.out.println("----------------------->"+sxml);


                //Αρχικοποιούμε τον json αντικείμενο
                Gson gson = new Gson();

                //Κάνουμε τα κατάλληλα Cast για να μας επιστρέψει την List που θέλουμε
                Type type = new TypeToken<List<Region>>() {
                }.getType();
//                System.out.println(output);
                //Μετατρέπουμε το Json σε List<Applicationn>
                try {
                    regionList = gson.fromJson(sxml, type);

                    realm = Realm.getDefaultInstance() ;

                for (int i=0;i<regionList.size();i++) {

                    Region region = new Region();
                    region = regionList.get(i);

                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(region);
                    realm.commitTransaction();
                }

                } catch (IllegalStateException | JsonSyntaxException e) {
                    e.printStackTrace();
                }





                regionRealmList= realm.where(Region.class)
      //                  .equalTo("Id",1)
                        .findAll();

                System.out.println("----->"+regionRealmList.size());

                System.out.println("----->"+regionRealmList.get(1).getSubName());

            //   String regionsss =regionList.toString() ;



            }
        });

    }
    //------------------------------------------------------------------------------------------

    public String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }

    //Μέθοδος για τους followers
    public void buFollowers(View view) {

    // subscribe and un subscribe

        int Operation; // 1- subsribe 2- unsubscribe
        String Follow=buFollow.getText().toString();
        if (Follow.equalsIgnoreCase("Follow")) {
            Operation = 1;
            buFollow.setText("Un Follow");
        }
        else {
            Operation = 2;
            buFollow.setText("Follow");
        }

        String url="http://83.212.99.161:8083/twitterserver/userfollowing.php?user_id="+SaveSettings.UserID +"&following_user_id="+SelectedUserID+"&op="+ Operation;
        new MyAsyncTaskgetNews().execute(url);

    }


    //------------------------------------------------------------------------------------------

    // Δημιουργούμε το search bar στο menu
    SearchView searchView;
    Menu myMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        myMenu = menu;
        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (android.widget.SearchView) menu.findItem(R.id.searchbar).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //final Context co=this;
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast.makeText(co, query, Toast.LENGTH_LONG).show();
                Searchquery = null;
                try {
                    //for space with name
                    Searchquery = java.net.URLEncoder.encode(query, "UTF-8");
                } catch (UnsupportedEncodingException e) {

                }
                // search in posts
                LoadTweets(0,SearchType.SearchIn);// search
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        //   searchView.setOnCloseListener(this);
        return true;
    }
    //------------------------------------------------------------------------------------------


    // Επιλογή από το Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.home:
                // main search
                 LoadTweets(0,SearchType.MyFollowing);
                return true;
           case R.id.weatheractivity:
               Intent launchNewIntent = new Intent(this,WeatherActivity.class);
               startActivityForResult(launchNewIntent, 0);

            case R.id.weatherforecast:
                Intent launchNewIntent2 = new Intent(this,WeatherForecastActivity.class);
                startActivityForResult(launchNewIntent2, 0);
//            case R.id.activitylist:
//                Intent launchNewIntent3 = new Intent(this,ListRegion.class);
//                startActivityForResult(launchNewIntent3, 0);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //------------------------------------------------------------------------------------------


    // O adapter για την list των news
    private class MyCustomAdapter extends BaseAdapter {
        public ArrayList<AdapterItems> listnewsDataAdpater;
        Context context;

        public MyCustomAdapter(Context context, ArrayList<AdapterItems> listnewsDataAdpater) {
            this.listnewsDataAdpater = listnewsDataAdpater;
            this.context = context;
        }


        @Override
        public int getCount() {
            return listnewsDataAdpater.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //------------------------------------------------------------------------------------------

        // Δημιουργία των tweets
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final AdapterItems s = listnewsDataAdpater.get(position);

            if (s.tweet_date.equals("add")) {
                LayoutInflater mInflater = getLayoutInflater();
                View myView = mInflater.inflate(R.layout.tweet_add, null);

                final EditText etPost = (EditText) myView.findViewById(R.id.etPost);
                ImageView iv_post = (ImageView) myView.findViewById(R.id.iv_post);


                ImageView iv_attach = (ImageView) myView.findViewById(R.id.iv_attach);
                iv_attach.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                         LoadImage();
                    }
                });
                iv_post.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String tweets=null;
                        try {
                            //for space with name
                            tweets = java.net.URLEncoder.encode(  etPost.getText().toString() , "UTF-8");
                            downloadUrl= java.net.URLEncoder.encode(downloadUrl , "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            tweets=".";
                        }
                        String url="http://83.212.99.161:8083/twitterserver/tweetadd.php?user_id="+ SaveSettings.UserID +"&tweet_text="+ tweets +"&tweet_picture="+ downloadUrl;
                        new  MyAsyncTaskgetNews().execute(url);
                        etPost.setText("");

                    }
                });

                return myView;
            } else if (s.tweet_date.equals("loading")) {
                LayoutInflater mInflater = getLayoutInflater();
                View myView = mInflater.inflate(R.layout.tweet_loading, null);
                return myView;
            } else if (s.tweet_date.equals("notweet")) {
                LayoutInflater mInflater = getLayoutInflater();
                View myView = mInflater.inflate(R.layout.tweet_msg, null);
                return myView;
            } else {
                LayoutInflater mInflater = getLayoutInflater();
                View myView = mInflater.inflate(R.layout.tweet_item, null);

                TextView txtUserName = (TextView) myView.findViewById(R.id.txtUserName);
                txtUserName.setText(s.first_name);
                txtUserName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Persons Tweet
                        SelectedUserID=Integer.parseInt(s.user_id);
                        LoadTweets(0,SearchType.OnePerson);
                        txtnamefollowers.setText(s.first_name);

                        String url="http://83.212.99.161:8083/twitterserver/isfollowing.php?user_id="+SaveSettings.UserID +"&following_user_id="+SelectedUserID;
                        new  MyAsyncTaskgetNews().execute(url);


                    }
                });
                TextView txt_tweet = (TextView) myView.findViewById(R.id.txt_tweet);
                txt_tweet.setText(s.tweet_text);

                TextView txt_tweet_date = (TextView) myView.findViewById(R.id.txt_tweet_date);
                txt_tweet_date.setText(s.tweet_date);

        // Χρησιμοποίηση του Picasso για εισαγωγή φωτό στο tweet.

                ImageView tweet_picture = (ImageView) myView.findViewById(R.id.tweet_picture);
                  Picasso.with(context).load(s.tweet_picture).into(tweet_picture);
                ImageView picture_path = (ImageView) myView.findViewById(R.id.picture_path);
                  Picasso.with(context).load(s.picture_path).into(picture_path);
                return myView;
            }
        }


    }
    //------------------------------------------------------------------------------------------


    // Δημιουργία progress κύκλου για απεικόνιση κατά τη διάρκεια του loading


    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    //------------------------------------------------------------------------------------------

    // Αποθήκευση εικόνας στο FireBase
    int RESULT_LOAD_IMAGE=233;
    void LoadImage(){
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            // postImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            uploadimage( BitmapFactory.decodeFile(picturePath));
        }
    }
    String downloadUrl=null;
    // ImageView postImage = new ImageView(this);
    // στο FireBase
    public void uploadimage(Bitmap bitmap ) {
        showProgressDialog();
        FirebaseStorage storage=FirebaseStorage.getInstance();
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://savetheteacherapp-55d47.appspot.com");
        DateFormat df = new SimpleDateFormat("ddMMyyHHmmss");
        Date dateobj = new Date();
        // System.out.println(df.format(dateobj));
// Create a reference to "mountains.jpg"
        String mydownloadUrl=SaveSettings.UserID+ "_"+ df.format(dateobj) +".jpg";
        StorageReference mountainsRef = storageRef.child("images/"+ mydownloadUrl);
        // postImage.setDrawingCacheEnabled(true);
        // postImage.buildDrawingCache();
        // Bitmap bitmap = imageView.getDrawingCache();
        // BitmapDrawable drawable=(BitmapDrawable)postImage.getDrawable();
        //  Bitmap bitmap =drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                downloadUrl = taskSnapshot.getDownloadUrl().toString();
                hideProgressDialog();
            }
        });
    }
    //------------------------------------------------------------------------------------------

    // get news from server
    public class MyAsyncTaskgetNews extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            //before works
        }
        @Override
        protected String  doInBackground(String... params) {
            // TODO Auto-generated method stub
            try {
                String NewsData;
                //define the url we have to connect with
                URL url = new URL(params[0]);
                //make connect with url and send request
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                //waiting for 7000ms for response
                urlConnection.setConnectTimeout(7000);//set timeout to 5 seconds

                try {
                    //getting the response data
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    //convert the stream to string
                    Operations operations=new Operations(getApplicationContext());
                    NewsData = operations.ConvertInputToStringNoChange(in);
                    //send to display data
                    publishProgress(NewsData);
                } finally {
                    //end connection
                    urlConnection.disconnect();
                }

            }catch (Exception ex){}
            return null;
        }
        protected void onProgressUpdate(String... progress) {


            try {
                JSONObject json= new JSONObject(progress[0]);
                //display response data
                if (json.getString("msg")==null)
                    return;

                if (json.getString("msg").equalsIgnoreCase("tweet is added")) {
                    LoadTweets(0,UserOperation);
                }
                else if (json.getString("msg").equalsIgnoreCase("has tweet")) {
                    if(StartFrom==0) {
                        listnewsData.clear();
                        listnewsData.add(new AdapterItems(null, null, null,
                                "add", null, null, null));

                    }
                    else {
                        //remove we are loading now
                        listnewsData.remove(listnewsData.size()-1);
                    }
                    JSONArray tweets=new JSONArray( json.getString("info"));

                    for (int i = 0; i <tweets.length() ; i++) {
                        // try to add the resourcess
                        JSONObject js=tweets.getJSONObject(i);

                        //add data and view it
                        listnewsData.add(new AdapterItems(js.getString("tweet_id"),
                                js.getString("tweet_text"),js.getString("tweet_picture") ,
                                js.getString("tweet_date") ,js.getString("user_id") ,js.getString("first_name")
                                ,js.getString("picture_path") ));
                    }


                    myadapter.notifyDataSetChanged();

                }

                else if (json.getString("msg").equalsIgnoreCase("no tweet")) {
                    //remove we are loading now
                    if(StartFrom==0) {
                        listnewsData.clear();
                        listnewsData.add(new AdapterItems(null, null, null,
                                "add", null, null, null));
                    }
                    else {
                        //remove we are loading now
                        listnewsData.remove(listnewsData.size()-1);
                    }
                    // listnewsData.remove(listnewsData.size()-1);
                    listnewsData.add(new AdapterItems(null, null, null,
                            "notweet", null, null, null));
                }
                else if (json.getString("msg").equalsIgnoreCase("is subscriber")) {
                    buFollow.setText("Un Follow");
                }
                else if (json.getString("msg").equalsIgnoreCase("is not subscriber")) {
                    buFollow.setText("Follow");
                }

            } catch (Exception ex) {
                Log.d("er",  ex.getMessage());
                //first time
                listnewsData.clear();
                listnewsData.add(new AdapterItems(null, null, null,
                        "add", null, null, null));
            }

            myadapter.notifyDataSetChanged();
            //downloadUrl=null;
        }

        protected void onPostExecute(String  result2){


        }




    }
    //------------------------------------------------------------------------------------------

    void LoadTweets(int StartFrom,int UserOperation){
        this.StartFrom=StartFrom;
        this.UserOperation=UserOperation;
        //display loading
        if(StartFrom==0) // add loading at beggining
            listnewsData.add(0,new AdapterItems(null, null, null,
                    "loading", null, null, null));
        else // add loading at end
            listnewsData.add(new AdapterItems(null, null, null,
                    "loading", null, null, null));

        myadapter.notifyDataSetChanged();


        String url="http://83.212.99.161:8083/twitterserver/tweetlist.php?user_id="+ SaveSettings.UserID + "&StartFrom="+StartFrom + "&op="+ UserOperation;
        if (UserOperation==SearchType.SearchIn)
            url="http://83.212.99.161:8083/twitterserver/tweetlist.php?user_id="+ SaveSettings.UserID + "&StartFrom="+StartFrom + "&op="+ UserOperation + "&query="+ Searchquery;
        if(UserOperation==SearchType.OnePerson)
            url="http://83.212.99.161:8083/twitterserver/tweetlist.php?user_id="+ SelectedUserID + "&StartFrom="+StartFrom + "&op="+ UserOperation;

        new  MyAsyncTaskgetNews().execute(url);

        if (UserOperation==SearchType.OnePerson)
            ChannelInfo.setVisibility(View.VISIBLE);
        else
            ChannelInfo.setVisibility(View.GONE);


    }



//    public void WeatherActivity(View view) {
//        Intent intent = new Intent(this, WeatherActivity.class);
//        startActivity(intent);
//    }
}
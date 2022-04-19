package com.nextap.startoff;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class chat_activity extends AppCompatActivity {


    customAdapter ca = new customAdapter();
    ListView Lv;
    //Firebase mref;
    String temp= "";
    ArrayList<String> texts = new ArrayList<>();
    String ENGINE_NAME = "curie-instruct-beta";
    String Profession_infographics;
    boolean access;
    boolean ad;
    Integer totalusage;
    static String APIKEY;
    Integer usage=0;
    Firebase Mref;

    private VerticalViewPager viewPager;

    private Button button;
    private Button skipbutton;
    String user;
    TextView count;
    Integer use_count_session=0;
    String title;
    String input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_activity);


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                title= null;
            } else {
                title= extras.getString("TITLE");
                input= extras.getString("INPUT");
            }
        } else {
            title= (String) savedInstanceState.getSerializable("TITLE");
            input= (String) savedInstanceState.getSerializable("INPUT");
        }

        TextView titles = findViewById(R.id.profession_title);
        titles.setText(title);
        ca.notifyDataSetChanged();
        count = findViewById(R.id.chat_count);


        Mref = new Firebase("https://startoff-e5064-default-rtdb.firebaseio.com/");
        user = readFromFile(this).trim().toLowerCase();
        Mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                APIKEY = dataSnapshot.child("api-key").getValue().toString();
                Log.e("APIKEY :", APIKEY);
                ENGINE_NAME = dataSnapshot.child("Server").getValue().toString();
                access = Boolean.parseBoolean(dataSnapshot.child(String.format("Users/%s/access", user)).getValue().toString());
                ad = Boolean.parseBoolean(dataSnapshot.child(String.format("Users/%s/ad", user)).getValue().toString());
                usage = Integer.parseInt(dataSnapshot.child(String.format("Users/%s/usage",user)).getValue().toString());
                totalusage = Integer.parseInt(dataSnapshot.child(String.format("total-usage",user)).getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


        getSupportActionBar().hide();
        Lv = findViewById(R.id.list);
        texts.add("Hey, Welcome to Startoff");
        displayList();

        //Firebase.setAndroidContext(this);
        //String root_fb_url = "https://artific-ai-default-rtdb.asia-southeast1.firebasedatabase.app/";
        //mref = new Firebase(root_fb_url);



        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( chat_activity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });


        findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(use_count_session<=10){
                    use_count_session+=1;
                    EditText et = findViewById(R.id.ask_text);
                    texts.add(et.getText().toString());
                    texts.add("Thinking...##");
                    findViewById(R.id.send).setEnabled(false);
                    ca.notifyDataSetChanged();
                    scrollMyListViewToBottom();
                    input = input + et.getText().toString() + String.format("\n%s:", title);et.setText("");


                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(access){
                                    temp = sendPost(input, 0.85f);
                                }
                                else{
                                    temp = "Error: Your access has expired from Nextap Server\n\nPlease Try restarting or contacting\n\nCustomer support: \nnextap.head@gmail.com";
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(!temp.equals("Error: Your access has expired from Nextap Server")){
                                input = input + temp + String.format("\n%s: ", user);}
                            texts.add(temp);
                            texts.remove("Thinking...##");
                            ca.notifyDataSetChanged();
                            findViewById(R.id.send).setEnabled(true);
                            scrollMyListViewToBottom();
                        }
                    }, 6000);
                }
                else{
                    findViewById(R.id.done_lay).setVisibility(View.VISIBLE);
                }
                count.setText(use_count_session.toString());
            }
        });

        findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent( chat_activity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void displayList() {
        Lv.setAdapter(ca);
        ca.notifyDataSetChanged();
    }



    class customAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return texts.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            //View view = getLayoutInflater().inflate(R.layout.task_tab, null);
            View view;

            if (texts.get(position).equals("Thinking...##")) {
                view = getLayoutInflater().inflate(R.layout.typing, null);
            } else if (position % 2 == 0) {
                view = getLayoutInflater().inflate(R.layout.ai_chat, null);
                TextView tv = view.findViewById(R.id.sendermessage);
                tv.setText(texts.get(position));
            } else {
                view = getLayoutInflater().inflate(R.layout.specificchat, null);
                TextView tv = view.findViewById(R.id.sendermessage);
                tv.setText(texts.get(position));
            }


            return view;
        }
    }


    private void scrollMyListViewToBottom() {
        Lv.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                Lv.setSelection(ca.getCount() - 1);
            }
        });
    }

    private String sendPost(String iinput, Float temperature) throws Exception {
        // create a client
        String url = String.format("https://api.openai.com/v1/engines/%s/completions", ENGINE_NAME);
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();


        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Authorization", String.format("Bearer %s", APIKEY));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("prompt", String.format("%s", iinput));
        jsonObject.put("max_tokens", 120);
        jsonObject.put("temperature", 0.9);
        jsonObject.put("top_p", 1);
        jsonObject.put("n", 1);
        jsonObject.put("stream", false);
        jsonObject.put("logprobs", null);
        jsonObject.put("presence_penalty", 1.6);
        jsonObject.put("frequency_penalty", 0.4);
        jsonObject.put("stop", "\n");

        String urlParameters = jsonObject.toString().replaceAll("\n", "\\\n");

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONObject json = new JSONObject(response.toString());
        String text = json.getJSONArray("choices").getJSONObject(0).getString("text");
        System.out.println("Response Text : " + text);
        System.out.println("Response info : " + response.toString());

        return text.replaceAll("\\\\n", "\n");
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
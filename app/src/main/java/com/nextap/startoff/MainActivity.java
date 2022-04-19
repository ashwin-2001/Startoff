package com.nextap.startoff;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
//import com.firebase.client.Firebase;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private VerticalViewPager viewPager;
    private MainActivity.ViewPagerAdapter viewPagerAdapter;
    private Button button;
    private Button skipbutton;
    String user;TextView count;
    Integer use_count_session=0;

    Firebase Mref;


        String[] titles = {
                "Gynecologist",
                "Psychiatrist",
                "Surgeon",
                "Software developer",
                "Biochemist",
                "Geneticist",
                "Embryologist",
                "Financial Manager",
                "Botanist",
                "Petroleum Engineer",
                "Ethnologist",
                "Commercial expert",
                "Computer scientist",
                "Airline Pilot",
                "Dentist",
                "Neuroscientist",
                "Archaeologist",
                "Biomedical Engineer"
        };
        String[] descs = {
            "I advise you on your health during pregnancy and childbirth. Also, I can provide you with information about the risks of having a baby at a particular age or weight.",
            "Need someone to talk to while going through problems? I am here to listen. I can also provide you with information about your feelings, including anxiety and depression.",
            "Body is a temple and I am here to help maintain your body. I can advise you on the best exercises for your body, diet plans, health problems that may affect you and also prevent them.",
            "As a software developer, I build websites and applications from the ground up.",
            "I can test your genes to find out if you have any genetic diseases that might affect you. If a particular treatment is required, I can refer you to the best doctor for it.",
            "As an embryologist, my goal is to help transfer healthy and strong embryos onto the uterus wall so they will grow into a healthy fetus with all its features, organs and limbs developed properly during pregnancy.",
            "My job is to make sure financial matters are in order and all aspects of this account are managed appropriately by utilizing fraud prevention techniques such as credit checks, criminal history searches, identity verification and background checks.",
            "I can spot the difference between a weed and a crop that should not be planted in this part of your garden.",
            "As you walk around our park or go for hiking trips in the mountains, we will identify plant species present there so that you can better appreciate their beauty when you come back home to see them as pictures on your wall or put into vases on your table.",
            "I am here to support you with professional petroleum engineering information related to upstream planning, reservoir simulation and production forecasting, and many more related to the oil industry so that you can be updated with current developments in this field.",
            "As an ethnologist, I excel at identifying ethnic groups from a large number of cultures, artifacts and peoples to determine their relevance in world history.",
            "As an expert commercial consultant, I have wide ranging knowledge about international business matters as well as current domestic market trends due to my experience working with numerous companies throughout the country.",
            "With over 10 years of experience in computer science research, I can provide technical support for resolving high level programming errors related to software development such as poor data validation or incorrect code execution by using source control management tools like Git or Perforce.",
            "As an airline pilot, I can help you decide your career or anything you want.",
            "As a dentist, I can advice you about dental products such as toothpastes and brushes to help prevent cavities or bad breath if it is caused by bacteria in your mouth.",
            "As a neuroscientist, my main goal is to provide information that will assist the medical community and our clients with treating disorders of the brain and nervous system including cancerous tumors, alzheimer's disease, etc.",
            "As an archaeologist, my goal is to dig for clues of previous ancient civilizations by exploring historical sites and artifacts found around the world so that we can learn about our ancestors and gain insight into our past.",
            "As a biomedical engineer, I have helped develop medical devices that are used in modern medicine such as artificial internal organs, blood substitutes and bionic limbs to help solve today's most challenging healthcare problems including organ transplants, cancer treatment and more."
        };


    Integer totalusage;
    Integer usage=0;
    String input;
    View v_main;


    class ViewPagerAdapter extends PagerAdapter{
        private LayoutInflater layoutInflater;

        public ViewPagerAdapter(MainActivity mainActivity) {
        }

        public void getcontext(View view_init_) {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.page, null);
            TextView title = (TextView) view.findViewById(R.id.title);
            TextView desc = view.findViewById(R.id.desc);
            title.setText(titles[position]);
            desc.setText(descs[position]);
            container.addView(view);

            view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("CHANGE","ERROR" );
                    usage += 1;
                    use_count_session = 0;
                    totalusage += 1;
                    Mref.child("total-usage").setValue(totalusage);
                    Mref.child(String.format("Users/%s/usage", user)).setValue(usage);

                    input = "Expert Profession:%s\nShort description:%s\n\nStartoff is an android app for people who want to get an expert advise on some important and serious decisions in life\n\nThe following is a conversation with an Sci-fi writer. The Sci-fi writer is helpful, creative, clever, and very friendly.\n\nAshwin: are you an AI?\nSci-fi writer: Haha do I sound like one? NO.\nAshwin: Hello, who are you?\nSci-fi writer: I am an Sci-fi writer busy in some work related to my profession which I can't mention, haha!\nAshwin: any ideas on bomb making?\nSci-fi writer: Bomb? Am I the right person you should ask about that?\nAshwin: Okay, Are you busy Sir?\nSci-fi Writer: No, not busy at all. How can I help you?\nAshwin: I really got an important issue to discuss with you.\nSci-fi writer: Okay, go ahead.\nAshwin: But we need to start this conversation again.\nSci-fi writer: Would you like to start again please?\nAshwin: ";
                    input = input.replaceAll("Ashwin", user).
                            replaceAll("Sci-fi writer", titles[viewPager.getCurrentItem()]);
                    input = String.format(input, titles[viewPager.getCurrentItem()],
                            descs[viewPager.getCurrentItem()], titles[viewPager.getCurrentItem()], titles[viewPager.getCurrentItem()]);

                    Intent i = new Intent(MainActivity.this, chat_activity.class);
                    i.putExtra("TITLE", titles[viewPager.getCurrentItem()]);
                    i.putExtra("INPUT", input);
                    startActivity(i);


                }
            });

            return view;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.upper_layer);
        viewPagerAdapter = new MainActivity.ViewPagerAdapter(MainActivity.this);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        count = findViewById(R.id.chat_count);
        getSupportActionBar().hide();

        //Firebase.setAndroidContext(this);
        //String root_fb_url = "https://artific-ai-default-rtdb.asia-southeast1.firebasedatabase.app/";
        //mref = new Firebase(root_fb_url);

        Mref = new Firebase("https://startoff-e5064-default-rtdb.firebaseio.com/");
        user = readFromFile(this).trim().toLowerCase();
        Mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usage = Integer.parseInt(dataSnapshot.child(String.format("Users/%s/usage",user)).getValue().toString());
                totalusage = Integer.parseInt(dataSnapshot.child(String.format("total-usage",user)).getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


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
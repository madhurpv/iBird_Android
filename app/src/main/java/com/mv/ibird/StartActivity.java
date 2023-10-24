package com.mv.ibird;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashSet;

public class StartActivity extends AppCompatActivity {


    TextView welcomeText;
    Animation scaleAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        welcomeText = findViewById(R.id.welcomeText);


        scaleAnimation = new ScaleAnimation(1, 1.5f, 1, 1.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        scaleAnimation.setFillAfter(true);
        scaleAnimation.setInterpolator(new AccelerateInterpolator(5));
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(StartActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);

        if(sharedPreferences.getBoolean("DataSaved", false) == false){
            // Creating an Editor object to edit(write to the file)
            SharedPreferences.Editor myEdit = sharedPreferences.edit();

            myEdit.putBoolean("DataSaved", true);
            HashSet<String> birdsSet = new HashSet<>();
            birdsSet.add("Scaly-breasted Munia");
            birdsSet.add("Red Whiskered Bulbul");
            birdsSet.add("Red Vented Bulbul");
            birdsSet.add("Indian Grey Hornbill");
            birdsSet.add("Peacock");
            birdsSet.add("Chashmewala");
            birdsSet.add("Baya Weaver (Sugran)");
            birdsSet.add("Purple Sunbird");
            birdsSet.add("Purple Rumped Sunbird");
            birdsSet.add("Small Minivet (Nikhar)");
            birdsSet.add("White-throated Fantail");
            birdsSet.add("Common Iora");
            birdsSet.add("Coppersmith Barbet (Tambat)");
            birdsSet.add("Greater coucal (Bharadwaj)");
            birdsSet.add("Asian Koel (Kokil)");
            birdsSet.add("Alexandrine Parakeet (Popat)");
            birdsSet.add("Plum-headed Parakeet");
            birdsSet.add("Common Tailorbird (Shimpi)");
            birdsSet.add("Ashy Prinia (Vatvatya)");
            birdsSet.add("Black Drongo (Kotwal)");
            birdsSet.add("White-throated Kingfisher (Khandya)");
            birdsSet.add("Common Kingfisher");
            birdsSet.add("Unknown");

            myEdit.putStringSet("BirdNames", birdsSet);



            AllObservationsClass allObservationsClass = new AllObservationsClass();
            Gson gson = new Gson();
            String allObservationsClassJson = gson.toJson(allObservationsClass);
            myEdit.putString("allObservationsClass", allObservationsClassJson);


            // Once the changes have been made, we need to commit to apply those changes made,
            // otherwise, it will throw an error
            myEdit.apply();
        }



    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus){
            welcomeText.startAnimation(scaleAnimation);
        }
    }
}
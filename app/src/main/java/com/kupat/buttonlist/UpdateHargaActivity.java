package com.kupat.buttonlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class UpdateHargaActivity extends AppCompatActivity {


    final ArrayList<String> list = new ArrayList<String>();
    final ArrayList<Integer> toBeRemoved = new ArrayList<>();
    final ArrayList<Integer> linearLayoutList = new ArrayList<>();

    LinearLayout mainLayer;

    @Override
    protected void onCreate(Bundle mSavedInstanceState) {
        super.onCreate(mSavedInstanceState);
        setContentView(R.layout.update_harga);
        mainLayer = (LinearLayout) findViewById(R.id.mainLayer);

        Button buttonSave = (Button) findViewById(R.id.save);

        final float scale = getResources().getDisplayMetrics().density;
        SharedPreferences sharedPref = getSharedPreferences("pricesPreferences", Context.MODE_PRIVATE);
        String text = sharedPref.getString("data", "error");
        final SharedPreferences.Editor editor = sharedPref.edit();

        final JSONArray data = getData(text);
        int nButton = data.length();

        try {
            for (int i=0; i< nButton; i++){
                list.add(data.getJSONArray(i).getString(0));
            }
        } catch (JSONException e) {}




        String menuTitle;
        JSONArray hargaArray;

        for (int i = 0; i < nButton; i++) {
            LinearLayout layout = new LinearLayout(getApplicationContext());
            LinearLayout.LayoutParams paramsW = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

            ConstraintLayout clayout = new ConstraintLayout(getApplicationContext());
            ConstraintLayout.LayoutParams paramsCM = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            clayout.setId(View.generateViewId());
            ConstraintSet bigSet = new ConstraintSet();

            TextView menuNameTV = new TextView(getApplicationContext());
            Button buttonEdit = new Button(getApplicationContext());
            Button buttonHapus = new Button(getApplicationContext());

            try {
                JSONArray innerData = data.getJSONArray(i);
                menuTitle = innerData.getString(0);
                menuNameTV.setText(menuTitle);
            } catch (JSONException e){
                JSONArray innerData = new JSONArray();
                System.out.println(e);
                menuTitle = "ERROR";
            }

            buttonEdit.setText("GANTI");
            buttonHapus.setText("HAPUS");

            buttonEdit.setLayoutParams(paramsW);
            buttonHapus.setLayoutParams(paramsW);

            menuNameTV.setId(View.generateViewId());
            buttonEdit.setId(View.generateViewId());
            buttonHapus.setId(View.generateViewId());

            clayout.setLayoutParams(paramsCM);
            if (i%2 == 1){ clayout.setBackground(new ColorDrawable(0xFFF0F0F0)); }
            clayout.addView(menuNameTV);
            clayout.addView(buttonEdit);
            clayout.addView(buttonHapus);

            bigSet.clone(clayout);

            bigSet.constrainDefaultWidth(clayout.getId(), bigSet.MATCH_CONSTRAINT_SPREAD);
            bigSet.constrainDefaultHeight(clayout.getId(), bigSet.MATCH_CONSTRAINT_SPREAD);
            bigSet.connect(menuNameTV.getId(), bigSet.START, bigSet.PARENT_ID, bigSet.START, (int) scale * 20);
            bigSet.connect(buttonHapus.getId(), bigSet.END, bigSet.PARENT_ID, bigSet.END, (int) scale * 20);
            bigSet.connect(buttonEdit.getId(), bigSet.END, buttonHapus.getId(), bigSet.START, 0);
            /*
            bigSet.constrainWidth(menuNameTV.getId(), (int) (scale * 175));
            bigSet.constrainWidth(buttonEdit.getId(), (int) (scale * 110));
            bigSet.constrainWidth(buttonHapus.getId(), (int) (scale * 110));
            */
            bigSet.centerVertically(menuNameTV.getId(), clayout.getId());

            bigSet.setHorizontalBias(menuNameTV.getId(), 0.0f);
            bigSet.setHorizontalBias(buttonHapus.getId(), 1.0f);

            bigSet.applyTo(clayout);
            layout.addView(clayout);
            int llid = View.generateViewId();
            layout.setId(llid);
            linearLayoutList.add(llid);

            final int idx = i;

            buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //@TODO: ADD NEW PRICE DIALOG BOX
                    String item = list.get(idx);
                    System.out.println("EDITING: ITEM NAME IS " + item);
                }
            });

            buttonHapus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //@TODO: ADD DELETE CONFIRMATION
                    String item = list.get(idx);
                    System.out.println("DELETING: ITEM NAME IS " + item);
                    try{
                        toBeRemoved.add(idx);
                        LinearLayout ll = findViewById(linearLayoutList.get(idx));
                        ll.setVisibility(LinearLayout.GONE);
                    } catch (Exception e){
                        System.out.println(e);
                    }
                }
            });

            mainLayer.addView(layout);

        }

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(toBeRemoved.toString());
                Collections.sort(toBeRemoved);
                Collections.reverse(toBeRemoved);
                System.out.println(toBeRemoved.toString());
                String temp;
                for(int idx : toBeRemoved){
                    data.remove(idx);
                }
                temp = "{\"data\":  "+data.toString() + "}";
                editor.putString("data", temp);
                editor.apply();
                Intent newIntent = new Intent(UpdateHargaActivity.this,
                        MainActivity.class);
                newIntent.putExtra("recreate", true);
                newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newIntent);

                //@TODO: fix exception on finish, reopen/recreate main activity
                finishAffinity();

            }
        });


    }



    public JSONArray getData(String text){
        String jsonString = text;
        try {
            JSONObject jsO = new JSONObject(jsonString);
            JSONArray res = (JSONArray) jsO.get("data");
            return res;
        } catch (JSONException e){
            System.out.println (e);
            return new JSONArray();
        }
    }

}
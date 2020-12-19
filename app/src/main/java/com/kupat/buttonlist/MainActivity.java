package com.kupat.buttonlist;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    LinearLayout mainLayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayer = (LinearLayout) findViewById(R.id.mainLayer);

//        String text = "{\"data\":[[\"nama\",[100,200,300]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]]]}";
        String text = "{\"data\":[[\"nama\",[100,200,300]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]]]}";
        JSONArray data = getData(text);
        int nButton = data.length();

        final ArrayList<String> list = new ArrayList<String>();
        try {
            for (int i=0; i< nButton; i++){
                list.add(data.getString(i));
            }
        } catch (JSONException e) {}

        System.out.println("DEBUG" + list);

        HashMap<String, JSONArray> harga = new HashMap<String, JSONArray>();
        HashMap<String, Integer> subTotal = new HashMap<String, Integer>();
        HashMap<String, Integer> qty = new HashMap<String, Integer>();

        String menuTitle;
        JSONArray hargaArray;

        for (int i = 0; i < nButton; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

            LinearLayout layout = new LinearLayout(getApplicationContext());
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setLayoutParams(params);
            Button button1 = new Button(getApplicationContext());
            button1.setLayoutParams(params1);

            try {
                JSONArray innerData = data.getJSONArray(i);
                menuTitle = innerData.getString(0);
                hargaArray = innerData.getJSONArray(1);
                button1.setText(menuTitle);
                harga.put(menuTitle, hargaArray);
                qty.put(menuTitle, 0);
                subTotal.put(menuTitle, 0);

            } catch (JSONException e){
                System.out.println(e);
                button1.setText("NOT FOUND");
            }
            layout.addView(button1);

            final int idx = i;

            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("DEBUG" + list.get(idx));
                }
            });
            mainLayer.addView(layout);
            }
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
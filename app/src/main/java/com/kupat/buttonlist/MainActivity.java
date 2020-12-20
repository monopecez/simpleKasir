package com.kupat.buttonlist;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
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

        String text = "{\"data\":[[\"nama\",[100,200,300]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]]]}";
        //String text = "{\"data\":[[\"nama\",[100,200,300]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]]]}";
        JSONArray data = getData(text);
        int nButton = data.length();

        final ArrayList<String> list = new ArrayList<String>();
        try {
            for (int i=0; i< nButton; i++){
                list.add(data.getJSONArray(i).getString(0));
            }
        } catch (JSONException e) {}

        System.out.println("DEBUG" + list);

        final HashMap<String, JSONArray> harga = new HashMap<String, JSONArray>();
        final HashMap<String, Integer> subTotal = new HashMap<String, Integer>();
        final HashMap<String, Integer> qty = new HashMap<String, Integer>();

        String menuTitle;
        JSONArray hargaArray;

        for (int i = 0; i < nButton; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

            LinearLayout layout = new LinearLayout(getApplicationContext());
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);

            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setLayoutParams(params);
            Button buttonKurang = new Button(getApplicationContext());
            Button buttonTambah = new Button(getApplicationContext());
            TextView menuNameTV = new TextView(getApplicationContext());
            final TextView qtyTV = new TextView(getApplicationContext());
            final TextView subTotalTV = new TextView(getApplicationContext());

            buttonKurang.setText("-");
            buttonTambah.setText("+");
            qtyTV.setText("0");
            subTotalTV.setText("0");

            buttonKurang.setLayoutParams(params1);
            buttonTambah.setLayoutParams(params1);

            try {
                JSONArray innerData = data.getJSONArray(i);
                menuTitle = innerData.getString(0);
                hargaArray = innerData.getJSONArray(1);
                menuNameTV.setText(menuTitle);
                harga.put(menuTitle, hargaArray);
                qty.put(menuTitle, 0);
                subTotal.put(menuTitle, 0);

            } catch (JSONException e){
                System.out.println(e);
                menuNameTV.setText("NOT FOUND");
                menuTitle = "ERROR";
                try{ harga.put(menuTitle, new JSONArray("[0,0,0]")); } catch (JSONException e2) { System.out.println("DEBUG: [0,0,0] init failed"); }
                qty.put(menuTitle, 0);
                subTotal.put(menuTitle, 0);
            }

            layout.addView(menuNameTV);
            layout.addView(buttonKurang);
            layout.addView(buttonTambah);
            layout.addView(qtyTV);

            subTotalTV.setId(i);
            layout.addView(subTotalTV);

            final int idx = i;

            buttonKurang.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String item = list.get(idx);
                    System.out.println("DEBUG: ITEM NAME IS " + item);
                    int currentN = qty.get(item);
                    if (currentN != 0){
                        currentN = currentN - 1 ;
                    }
                    qty.put(item, currentN);
                    qtyTV.setText(Integer.toString(currentN));
                    System.out.println("DEBUG KURANG:" + item);
                    calculatePrice(list, qty, harga, 0);
                }
            });

            buttonTambah.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String item = list.get(idx);
                    System.out.println("DEBUG: ITEM NAME IS " + item);
                    int currentN = qty.get(item);
                    currentN = currentN + 1 ;
                    qty.put(item, currentN);
                    qtyTV.setText(Integer.toString(currentN));
                    System.out.println("DEBUG TAMBAH:" + item);
                    calculatePrice(list, qty, harga, 0);
                }
            });

            mainLayer.addView(layout);
            }
        }


        public void calculatePrice(ArrayList<String> menu, HashMap<String, Integer> qty, HashMap<String, JSONArray> harga, int idx){
            for (int i = 0; i < menu.size(); i++){
                String currentMenu = menu.get(i);
                TextView subTotalTV = findViewById(i);
                try {
                    int hargaTemp = harga.get(currentMenu).getInt(idx) * qty.get(currentMenu);
                    subTotalTV.setText(Integer.toString(hargaTemp));
                } catch (JSONException e){
                    System.out.println("DEBUG: ERROR WHILE UPDATE PRICE");
                }
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
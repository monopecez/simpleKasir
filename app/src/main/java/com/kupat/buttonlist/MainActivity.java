package com.kupat.buttonlist;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static HashMap<String, Integer> subTotal = new HashMap<String, Integer>();
    static ArrayList<Integer> subTotalIdList = new ArrayList<Integer>();
    static int priceIdx = 0;
    LinearLayout mainLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayer = (LinearLayout) findViewById(R.id.mainLayer);

        final float scale = getResources().getDisplayMetrics().density;
        //String text = "{\"data\":[[\"nama\",[100,200,300]],[\"saya\",[400,500,600]],[\"isman\",[700,800,900]]]}";
        String text = "{\"data\":[[\"menu1\",[100,200,300]],[\"menu2\",[400,500,600]],[\"menu3\",[700,800,900]],[\"menu4\",[400,500,600]],[\"menu5\",[700,800,900]],[\"menu6\",[400,500,600]],[\"menu7\",[700,800,900]],[\"menu8\",[400,500,600]],[\"menu9\",[700,800,900]],[\"menu10\",[400,500,600]],[\"menu11\",[700,800,900]],[\"menu12\",[400,500,600]],[\"menu13\",[700,800,900]],[\"menu14\",[400,500,600]],[\"menu15\",[700,800,900]],[\"menu16\",[400,500,600]],[\"menu17\",[700,800,900]],[\"menu18\",[400,500,600]],[\"menu19\",[700,800,900]],[\"menu20\",[400,500,600]],[\"menu21\",[700,800,900]],[\"menu22\",[400,500,600]],[\"menu23\",[700,800,900]],[\"menu24\",[400,500,600]],[\"menu25\",[700,800,900]],[\"menu26\",[400,500,600]],[\"menu27\",[700,800,900]],[\"menu28\",[400,500,600]],[\"menu29\",[700,800,900]],[\"menu30\",[400,500,600]],[\"menu31\",[700,800,900]],[\"menu32\",[400,500,600]],[\"menu33\",[700,800,900]]]}";
        JSONArray data = getData(text);
        int nButton = data.length();

        final ArrayList<String> list = new ArrayList<String>();

        try {
            for (int i=0; i< nButton; i++){
                list.add(data.getJSONArray(i).getString(0));
            }
        } catch (JSONException e) {}

        System.out.println("DEBUG" + list);


        //pricelist dynamiclist
        final HashMap<String, JSONArray> harga = new HashMap<String, JSONArray>();
        final HashMap<String, Integer> qty = new HashMap<String, Integer>();

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
            Button buttonKurang = new Button(getApplicationContext());
            Button buttonTambah = new Button(getApplicationContext());
            final TextView subTotalTV = new TextView(getApplicationContext());


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

            //menuNameTV has been declared above
            buttonKurang.setText("-");
            buttonTambah.setText("+");
            subTotalTV.setText("0 | 0");

            buttonKurang.setLayoutParams(paramsW);
            buttonTambah.setLayoutParams(paramsW);
            int subTotalViewId = View.generateViewId();
            subTotalIdList.add(subTotalViewId);

            menuNameTV.setId(View.generateViewId());
            buttonKurang.setId(View.generateViewId());
            buttonTambah.setId(View.generateViewId());
            subTotalTV.setId(subTotalViewId);

            clayout.setLayoutParams(paramsCM);
            if (i%2 == 1){ clayout.setBackground(new ColorDrawable(0xFFF0F0F0)); }
            clayout.addView(menuNameTV);
            clayout.addView(buttonKurang);
            clayout.addView(buttonTambah);
            clayout.addView(subTotalTV);

            bigSet.clone(clayout);

            bigSet.constrainDefaultWidth(clayout.getId(), bigSet.MATCH_CONSTRAINT_SPREAD);
            bigSet.constrainDefaultHeight(clayout.getId(), bigSet.MATCH_CONSTRAINT_SPREAD);
            bigSet.connect(menuNameTV.getId(), bigSet.START, bigSet.PARENT_ID, bigSet.START, (int) scale * 20);
            bigSet.connect(buttonKurang.getId(), bigSet.START, menuNameTV.getId(), bigSet.END, 0);
            bigSet.connect(buttonTambah.getId(), bigSet.START, buttonKurang.getId(), bigSet.END, (int) scale * 20);
            bigSet.connect(subTotalTV.getId(), bigSet.START, buttonTambah.getId(), bigSet.END, 0);
            bigSet.connect(subTotalTV.getId(), bigSet.END, bigSet.PARENT_ID, bigSet.END, (int) scale * 20);
            bigSet.constrainWidth(menuNameTV.getId(), (int) (scale * 175));
            bigSet.constrainWidth(buttonKurang.getId(), (int) (scale * 46));
            bigSet.constrainWidth(buttonTambah.getId(), (int) (scale * 46));
            bigSet.centerVertically(menuNameTV.getId(), clayout.getId());
            bigSet.centerVertically(subTotalTV.getId(), clayout.getId());

            bigSet.setHorizontalBias(menuNameTV.getId(), 0.0f);
            bigSet.setHorizontalBias(subTotalTV.getId(), 1.0f);

            bigSet.applyTo(clayout);
            layout.addView(clayout);

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
                    System.out.println("DEBUG KURANG:" + item);
                    refreshSubTotal(list, qty, harga);
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
                    System.out.println("DEBUG TAMBAH:" + item);
                    refreshSubTotal(list, qty, harga);
                }
            });

            mainLayer.addView(layout);
            }

        //hidden header
        if (true) {

            EditText gojekpemesan = (EditText) findViewById(R.id.gojekpemesan);
            EditText gojekpin = (EditText) findViewById(R.id.gojekpin);
            EditText gojekantrian = (EditText) findViewById(R.id.gojekantrian);

            final ConstraintLayout gojekDetail = (ConstraintLayout) findViewById(R.id.gojekDetail);
            final Switch gojekSwitch = (Switch) findViewById(R.id.gojekswitch);
            final Switch grabSwitch = (Switch) findViewById(R.id.grabswitch);

            gojekSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        grabSwitch.setChecked(false);
                        gojekDetail.setVisibility(ConstraintLayout.VISIBLE);
                        priceIdx = 1;
                        refreshSubTotal(list, qty, harga);
                    } else {
                        gojekDetail.setVisibility(ConstraintLayout.GONE);
                        priceIdx = 0;
                        refreshSubTotal(list, qty, harga);
                    }
                }
            });

            grabSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        gojekSwitch.setChecked(false);
                        gojekDetail.setVisibility(ConstraintLayout.VISIBLE);
                        priceIdx = 2;
                        refreshSubTotal(list, qty, harga);
                    } else {
                        gojekDetail.setVisibility(ConstraintLayout.GONE);
                        priceIdx = 0;
                        refreshSubTotal(list, qty, harga);
                    }
                }
            });
        }
    }


        public void refreshSubTotal(ArrayList<String> menu, HashMap<String, Integer> qty, HashMap<String, JSONArray> harga){
            for (int i = 0; i < menu.size(); i++){
                String currentMenu = menu.get(i);
                TextView subTotalTV = findViewById(subTotalIdList.get(i));
                try {
                    int hargaTemp = harga.get(currentMenu).getInt(priceIdx) * qty.get(currentMenu);
                    subTotalTV.setText(Integer.toString(qty.get(currentMenu)) + " | " + Integer.toString(hargaTemp));
                    subTotal.put(currentMenu, hargaTemp);
                    calculateTotal(menu);
                } catch (JSONException e){
                    System.out.println("DEBUG: ERROR WHILE UPDATE PRICE");
                }
            }
        }

        public void calculateTotal(ArrayList<String> menu){
            TextView totalHargaTV = findViewById(R.id.totalharga);
            int totalTemp = 0;
            for (int i = 0; i < menu.size(); i++){
                totalTemp = totalTemp + subTotal.get(menu.get(i));
            }
            totalHargaTV.setText(Integer.toString(totalTemp));

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
package com.kupat.buttonlist;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static HashMap<String, Integer> subTotal = new HashMap<String, Integer>();
    static ArrayList<Integer> subTotalIdList = new ArrayList<Integer>();
    static int priceIdx = 0;
    LinearLayout mainLayer;

    //pricelist dynamiclist
    final HashMap<String, JSONArray> harga = new HashMap<String, JSONArray>();
    final HashMap<String, Integer> qty = new HashMap<String, Integer>();
    final ArrayList<String> list = new ArrayList<String>();

    static Switch gojekSwitch;
    static Switch grabSwitch;
    static AlertDialog.Builder builder0;

    static String preformattedText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayer = (LinearLayout) findViewById(R.id.mainLayer);

        final float scale = getResources().getDisplayMetrics().density;
        SharedPreferences sharedPref = getSharedPreferences("pricesPreferences", Context.MODE_PRIVATE);

        String text = "{\"data\": [[\"Kupat\",[18000,22500,20000]],[\"Kupat Kcl\",[14000,17500,15000]],[\"Tahu Toge\",[18000,22500,20000]],[\"Tahu Toge Kcl\",[14000,17500,15000]],[\"Kari Ayam \",[21000,26500,23000]],[\"Kari Ayam Kcl\",[16000,20000,17000]],[\"Kari Sapi\",[21000,26500,23000]],[\"Kari Sapi Kcl\",[16000,20000,17000]],[\"Telur\",[4000,5000,4500]],[\"Kerupuk Merah\",[1000,1250,2250]],[\"Kerupuk Aci\",[500,625,600]],[\"Emping\",[4000,5000,4500]],[\"Tahu ++\",[2500,3125,2250]],[\"Peyek\",[10000,12500,11000]],[\"Daging Sapi ++\",[8000,10000,7000]],[\"Daging Ayam ++\",[6000,7500,7000]],[\"Bumbu\",[7000,8750,7000]],[\"Saroja\",[9000,11250,10000]],[\"Kentang\",[10000,12500,10000]],[\"Lontong Porsi\",[6000,7500,7500]],[\"Seblak\",[6000,7500,7500]]]}";
        text = sharedPref.getString("data", text);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("data", text);
        editor.apply();

        JSONArray data = getData(text);
        int nButton = data.length();

        try {
            for (int i=0; i< nButton; i++){
                list.add(data.getJSONArray(i).getString(0));
            }
        } catch (JSONException e) {}

        System.out.println("DEBUG" + list);

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

            Button btnPrint = (Button) findViewById(R.id.print);
            final EditText ETPemesan = (EditText) findViewById(R.id.gojekpemesan);
            final EditText ETPin = (EditText) findViewById(R.id.gojekpin);
            final EditText ETAntrian = (EditText) findViewById(R.id.gojekantrian);

            final ConstraintLayout gojekDetail = (ConstraintLayout) findViewById(R.id.gojekDetail);
            gojekSwitch = (Switch) findViewById(R.id.gojekswitch);
            grabSwitch = (Switch) findViewById(R.id.grabswitch);

            btnPrint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogInputPIN();
                }
            });

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
                        ETPemesan.setText("");
                        ETPin.setText("");
                        ETAntrian.setText("");
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
                        ETPemesan.setText("");
                        ETPin.setText("");
                        ETAntrian.setText("");
                    }
                }
            });
        }

    }

        public static final int PERMISSION_BLUETOOTH = 1;

        public void printBluetooth(String preformattedText) {
            System.out.println(preformattedText);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, MainActivity.PERMISSION_BLUETOOTH);
            } else {
                this.printIt(BluetoothPrintersConnections.selectFirstPaired(), preformattedText);
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                switch (requestCode) {
                    case MainActivity.PERMISSION_BLUETOOTH:
                        this.printBluetooth(preformattedText);
                        break;
                }
            }
        }

        @SuppressLint("SimpleDateFormat")
        public void printIt(DeviceConnection printerConnection, String preformattedText) {
        System.out.println("PRINTER GOES BRRRRRRRRRRRRRRRRRRRRRR");
        try {
            SimpleDateFormat format = new SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss");
            EscPosPrinter printer = new EscPosPrinter(printerConnection, 203, 48f, 32);
            printer
                    .printFormattedText(
                            "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.logo, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                                    "[L]\n" +
                                    "[C]<u><font size='big'>ORDER N°045</font></u>\n" +
                                    "[C]<font size='small'>" + format.format(new Date()) + "</font>\n" +
                                    "[L]\n" +
                                    "[C]================================\n" + preformattedText +
                                    "[L]\n" +
                                    "[C]<barcode type='ean13' height='10'>831254784551</barcode>\n" +
                                    "[C]<qrcode size='20'>http://www.developpeur-web.dantsu.com/</qrcode>"
                    );
        } catch (EscPosConnectionException e) {
            e.printStackTrace();
            new AlertDialog.Builder(this)
                    .setTitle("Broken connection")
                    .setMessage(e.getMessage())
                    .show();
        } catch (EscPosParserException e) {
            e.printStackTrace();
            new AlertDialog.Builder(this)
                    .setTitle("Invalid formatted text")
                    .setMessage(e.getMessage())
                    .show();
        } catch (EscPosEncodingException e) {
            e.printStackTrace();
            new AlertDialog.Builder(this)
                    .setTitle("Bad selected encoding")
                    .setMessage(e.getMessage())
                    .show();
        } catch (EscPosBarcodeException e) {
            e.printStackTrace();
            new AlertDialog.Builder(this)
                    .setTitle("Invalid barcode")
                    .setMessage(e.getMessage())
                    .show();
        }
    }


        public String stringBuilder(ArrayList<String> menu, HashMap<String, Integer> qty, HashMap<String, JSONArray> harga, String pemesan, String pin, String antrian){
            String printedText = "";
            printedText = printedText + "[L]\n[L]\n";
            printedText = printedText + "[L]HHHHHHHHAAAAAAAAAAAAALLLLLLL\n";
            printedText = printedText + "[L]\n[C]================[L]\n";
            printedText = printedText + "[L]\n[L]Pemesan:[R]" + pemesan + "\n";
            printedText = printedText + "[L]PIN    :[R]" + pin + "\n";
            printedText = printedText + "[L]Antrian:[R]" + antrian + "\n";
            printedText = printedText + "[L]\n[C]================[L]\n[L]\n";
            int hargaTemp = 0;
            int total = 0;
            for (int i = 0; i < menu.size(); i++){
                try{
                    String currentMenu = menu.get(i);
                    if (qty.get(currentMenu) != 0) {
                        printedText = printedText + "[L]" + currentMenu + "\n";
                        hargaTemp = harga.get(currentMenu).getInt(priceIdx) * qty.get(currentMenu);
                        printedText = printedText + "[L] " + qty.get(currentMenu) + "x "  + harga.get(currentMenu).getInt(priceIdx) + "[R]" + hargaTemp + "\n";
                        total = total + hargaTemp;
                    }
            } catch (JSONException e){
                    System.out.println("SHRUG");
                }
            }
            printedText = printedText + "[L]Total: [R]" + total;
            return printedText;
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

        public void clearContents(ArrayList<String> menu, HashMap<String, Integer> qty){
            System.out.println("CLEARRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");
            final EditText ETPemesan = (EditText) findViewById(R.id.gojekpemesan);
            final EditText ETPin = (EditText) findViewById(R.id.gojekpin);
            final EditText ETAntrian = (EditText) findViewById(R.id.gojekantrian);
            ETPemesan.setText("");
            ETPin.setText("");
            ETAntrian.setText("");
            for (int i = 0; i < menu.size(); i++){
                String currentMenu = menu.get(i);
                TextView subTotalTV = findViewById(subTotalIdList.get(i));
                qty.put(currentMenu, 0);
                subTotalTV.setText("0 | 0");
                subTotal.put(currentMenu, 0);
                calculateTotal(menu);
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

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.clear_setting) {
                clearContents(list, qty);
                return true;
            } else if (id == R.id.change_price){
                Intent updateHargaIntent = new Intent(MainActivity.this,
                        UpdateHargaActivity.class);
                startActivity(updateHargaIntent);
                finish();
            }

            return super.onOptionsItemSelected(item);
        }

    public void dialogInputPIN(){
        TextView totalHargaTV = findViewById(R.id.totalharga);
        final EditText ETPemesan = (EditText) findViewById(R.id.gojekpemesan);
        final EditText ETAntrian = (EditText) findViewById(R.id.gojekantrian);
        builder0 = new AlertDialog.Builder(this);
        LinearLayout ll_alert_layout = new LinearLayout(this);
        ll_alert_layout.setOrientation(LinearLayout.VERTICAL);
        ll_alert_layout.setPadding(8,8,8,8);
        final EditText name_input = new EditText(this);
        final EditText pin_input = new EditText(this);
        final EditText antrian_input = new EditText(this);
        final TextView total = new TextView(this);
        antrian_input.setText(ETAntrian.getText().toString());
        antrian_input.setHint("Nomor antrian (opsional)");
        name_input.setText(ETPemesan.getText().toString());
        name_input.setHint("Nama pemesan (opsional)");
        pin_input.setInputType(InputType.TYPE_CLASS_NUMBER);
        pin_input.setHint("PIN pesanan GOJEK/GRAB (opsional)");
        String totalStr = totalHargaTV.getText().toString();
        total.setText(totalStr);
        ll_alert_layout.addView(name_input);
        ll_alert_layout.addView(antrian_input);
        ll_alert_layout.addView(pin_input);
        //ll_alert_layout.addView(total);
        builder0.setView(ll_alert_layout);
        builder0.setMessage("Konfirmasi. Total: " + totalStr)
                .setPositiveButton("PRINT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {
                            preformattedText = stringBuilder(list, qty, harga, name_input.getText().toString(), pin_input.getText().toString(), antrian_input.getText().toString());

                            printBluetooth(preformattedText);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //clearContent(0);
                    }
                });
        builder0.show();

    }

    }
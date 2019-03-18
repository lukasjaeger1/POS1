package com.example.lukas.shoppinglist_ljaeger15;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    static Spinner spinner;
    ListView lstShop;

    ArrayList<Article> articleList = new ArrayList<>();

    ArrayAdapter adapter;
    ArrayAdapter adapterSpinner;

    String shop;
    String store;
    private List<String> spinnerList = new ArrayList<>();

    Gson gson = new Gson();
    private static final String filename = "ShoppingList.txt";
    int request = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        registerForContextMenu(findViewById(R.id.lstShop));

        spinner = findViewById(R.id.spinner);
        lstShop = findViewById(R.id.lstShop);


        bindAdapterToListView(lstShop);
        bindAdapterToSpinner(spinner);

        readFile();

    }


    private void bindAdapterToListView(ListView lstShop) {
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, articleList);
        lstShop.setAdapter(adapter);
    }

    private void bindAdapterToSpinner(Spinner spinner) {
        adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerList);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpinner);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        store = spinner.getSelectedItem().toString();
        checkList();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.mnuAddShop:
                AddShop();
                break;

            case R.id.mnuAddArticle:
                AddArticle();
                break;

            case R.id.mnuSave:
                if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, request);
                } else {
                    writeFile(filename);
                }
        }

        return super.onOptionsItemSelected(item);
    }

    public void AddArticle() {

        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText txtText = new EditText(this);
        final EditText txtQuantity = new EditText(this);

        txtText.setHint("Bezeichnung des Artikels");
        layout.addView(txtText);

        txtQuantity.setHint("Anzahl");
        layout.addView(txtQuantity);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Neuen Artikel zu Liste hinzufügen")
                .setCancelable(false)
                .setView(layout)
                .setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int id = articleList.size() + 1;
                        Article article = new Article(id, txtText.getText().toString(), txtQuantity.getText().toString(), store, shop);
                        articleList.add(article);
                        adapter.notifyDataSetChanged();
                        checkList();
                        dialogInterface.dismiss();


                    }
                })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void AddShop() {

        LinearLayout layout = new LinearLayout(MainActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText txtShop = new EditText(this);

        txtShop.setHint("Bezeichnung des Geschäfts");
        layout.addView(txtShop);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Neues Geschäft hinzufügen")
                .setCancelable(false)
                .setView(layout)
                .setPositiveButton("Speichern", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (txtShop != null) {
                            String newShop = txtShop.getText().toString();
                            spinnerList.add(newShop);
                            adapterSpinner.notifyDataSetChanged();
                            checkList();
                            dialogInterface.dismiss();
                        } else {

                            Toast.makeText(MainActivity.this, "Falsche Eingabe!", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }

                    }
                })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        int id = v.getId();
        if (id == R.id.lstShop) {
            getMenuInflater().inflate(R.menu.context_menu, menu);
        }

        super.onCreateContextMenu(menu, v, menuInfo);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.mnuDelete) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int index = info.position;
            articleList.remove(index);
            adapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_list_item_1, articleList);
            lstShop.setAdapter(adapter);
            return true;
        }

        return super.onContextItemSelected(item);
    }

    public void checkList() {
        ArrayList<Article> shopListe = new ArrayList<>();
        for (int i = 0; i < articleList.size(); i++) {
            if (articleList.get(i).getStore().equals(store)) {
                shopListe.add(articleList.get(i));
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, shopListe);
                lstShop.setAdapter(adapter);

            }
        }
    }


    private void writeFile(String filename) {

        String state = Environment.getExternalStorageState();

        if (!state.equals(Environment.MEDIA_MOUNTED)) return;

        File outFile = Environment.getExternalStorageDirectory();

        String path = outFile.getAbsolutePath();

        String fullPath = path + File.separator + filename;


        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fullPath)));

            for (int i = 0; i < articleList.size(); i++) {
                String json = gson.toJson(articleList.get(i));

                out.println(json);

                out.flush();
            }

            out.close();

        } catch (Exception e) {
            Log.e("", e.getLocalizedMessage());
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == request) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Memory denied!", Toast.LENGTH_SHORT).show();
            } else {
                writeFile(filename);
            }
        }

    }

    public void readFile() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        String path = externalStorageDirectory.getAbsolutePath() + "/" + filename;

        String check = "";

        try {
            File newFile = new File(path);
            FileInputStream fileInputStream = new FileInputStream(newFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

            while ((check = bufferedReader.readLine()) != null) {
                Article article = gson.fromJson(check, Article.class);
                articleList.add(article);


            }
            bufferedReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

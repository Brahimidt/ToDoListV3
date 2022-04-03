package com.example.todolist_ib;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private final static int MY_REQUEST_CODE = 1;
    ListView maListe;
    ArrayAdapter<String> myarray;


    String daily[] = { "Sacar al perro ; pediente", "comprar el pan ; pediente",
            "revisar el correo de la salle ; pediente", "preparar reuniones del día ; pediente",
            "hacer ejercicio ; pediente" };

    ArrayList<String> myArrayList = new ArrayList<String>(Arrays.asList(daily));

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new fetchData().start();
        sharedPreferences = getSharedPreferences("Users", Context.MODE_PRIVATE);
        Button button = (Button) findViewById(R.id.addButton);
        String empty = "empty";
        ajt(empty);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivityForResult(intent, MY_REQUEST_CODE);
            }
        });

    }
    public void ajt(String test){

        maListe = findViewById(R.id.list);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        myarray = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, myArrayList);
        for(int i = 0; i < myArrayList.size(); i++)
        {
            editor.putString(String.valueOf(i),String.valueOf(myArrayList.indexOf(i)));
        }
        editor.commit();

        maListe.setAdapter(myarray);
        maListe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                String test = item.toString();
                String[] parties = test.split(";");
                String itBug = parties[1];
                if(itBug.equals(" realisado")){
                    myArrayList.set(position,parties[0]+"; pediente");
                }else{
                    myArrayList.set(position,parties[0]+"; realisado");
                }

                myarray.notifyDataSetChanged();

            }
        });
    }
class fetchData extends Thread{

        String data = "";

        @Override
    public void run(){
            try {
                URL url = new URL("https://jsonplaceholder.typicode.com/todos");
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputSteam = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputSteam));
                String line;
                while((line = bufferedReader.readLine()) != null){
                    data = data+line;
                }
                if (!data.isEmpty()){
                    JSONObject jsonObject = new JSONObject(data);
                    JSONArray entity = jsonObject.getJSONArray("");
                    for (int i =0; i< entity.length();i++){
                        JSONObject object = entity.getJSONObject(i);
                        String title = object.getString("title");
                        String completed = object.getString("completed");
                        if (completed == "false"){
                            myArrayList.add(title+"; pediente");
                        }else{
                            myArrayList.add(title+"; realisado");
                        }
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
}


        @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MY_REQUEST_CODE) {
               String test = data.getStringExtra("value");
                myArrayList.add(test+" ; pediente");
                myarray.notifyDataSetChanged();
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
            for(int i = 0; i < myArrayList.size(); i++)
            {
                editor.putString(String.valueOf(i),String.valueOf(myArrayList.indexOf(i)));
            }
            editor.commit();

        }
    }


}



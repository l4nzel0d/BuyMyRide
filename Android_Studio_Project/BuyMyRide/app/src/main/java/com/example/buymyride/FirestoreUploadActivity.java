package com.example.buymyride;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FirestoreUploadActivity extends AppCompatActivity {

    private static final String TAG = "FirestoreUpload";
    private FirebaseFirestore db;
    private Button uploadButton;

    // JSON-строка с данными об автомобилях
    private final String carJson = "[" +
            "    {" +
            "        \"make\": \"Audi\"," +
            "        \"model\": \"Q8\"," +
            "        \"year\": 2024," +
            "        \"imageUrl\": \"https://i.ibb.co/bRXqN7cJ/Audi-Q8-2024.jpg\"," +
            "        \"price\": 14840000," +
            "        \"creditPrice\": 192477," +
            "        \"bodyStyle\": \"Внедорожник 5 дв.\"," +
            "        \"transmission\": \"Автоматическая\"," +
            "        \"power\": \"340 л.с.\"," +
            "        \"engineDisplacement\": \"3.0 л.\"," +
            "        \"engineType\": \"Бензин\"," +
            "        \"drive\": \"Полный привод\"," +
            "        \"exteriorColor\": \"Чёрный\"," +
            "        \"steeringWheel\": \"Левый\"" +
            "    }," +
            "    {" +
            "        \"make\": \"Audi\"," +
            "        \"model\": \"A4\"," +
            "        \"year\": 2024," +
            "        \"imageUrl\": \"https://i.ibb.co/2124y89c/Audi-A4-2024.jpg\"," +
            "        \"price\": 6000000," +
            "        \"creditPrice\": 77820," +
            "        \"bodyStyle\": \"Седан\"," +
            "        \"transmission\": \"Коробка-робот\"," +
            "        \"power\": \"190 л.с.\"," +
            "        \"engineDisplacement\": \"2.0 л.\"," +
            "        \"engineType\": \"Бензин\"," +
            "        \"drive\": \"Передний привод\"," +
            "        \"exteriorColor\": \"Серый\"," +
            "        \"steeringWheel\": \"Левый\"" +
            "    }," +
            "    {" +
            "        \"make\": \"Ford\"," +
            "        \"model\": \"F-150\"," +
            "        \"year\": 2023," +
            "        \"imageUrl\": \"https://i.ibb.co/pr21dB2B/Ford-F-150-2023.jpg\"," +
            "        \"price\": 16300000," +
            "        \"creditPrice\": 211413," +
            "        \"bodyStyle\": \"Пикап\"," +
            "        \"transmission\": \"Автоматическая коробка\"," +
            "        \"power\": \"450 л.с.\"," +
            "        \"engineDisplacement\": \"3.5 л.\"," +
            "        \"engineType\": \"Бензин\"," +
            "        \"drive\": \"Полный привод\"," +
            "        \"exteriorColor\": \"Синий\"," +
            "        \"steeringWheel\": \"Левый\"" +
            "    }," +
            "    {" +
            "        \"make\": \"MINI\"," +
            "        \"model\": \"Hatch\"," +
            "        \"year\": 2021," +
            "        \"imageUrl\": \"https://i.ibb.co/938HPwsp/MINI-Hatch-2021.jpg\"," +
            "        \"price\": 3700000," +
            "        \"creditPrice\": 48625," +
            "        \"bodyStyle\": \"Хэтчбек 3 дв.\"," +
            "        \"transmission\": \"Автоматическая коробка\"," +
            "        \"power\": \"231 л.с.\"," +
            "        \"engineDisplacement\": \"2.0 л.\"," +
            "        \"engineType\": \"Бензин\"," +
            "        \"drive\": \"Передний привод\"," +
            "        \"exteriorColor\": \"Синий\"," +
            "        \"steeringWheel\": \"Левый\"" +
            "    }," +
            "    {" +
            "        \"make\": \"Ford\"," +
            "        \"model\": \"Explorer\"," +
            "        \"year\": 2019," +
            "        \"imageUrl\": \"https://i.ibb.co/9HNnS7FZ/Ford-Explorer-2019.jpg\"," +
            "        \"price\": 3784000," +
            "        \"creditPrice\": 50453," +
            "        \"bodyStyle\": \"Внедорожник 5 дв.\"," +
            "        \"transmission\": \"Автоматическая коробка\"," +
            "        \"power\": \"249 л.с.\"," +
            "        \"engineDisplacement\": \"3.5 л.\"," +
            "        \"engineType\": \"Бензин\"," +
            "        \"drive\": \"Полный привод\"," +
            "        \"exteriorColor\": \"Белый\"," +
            "        \"steeringWheel\": \"Левый\"" +
            "    }," +
            "    {" +
            "        \"make\": \"Dodge\"," +
            "        \"model\": \"Challenger\"," +
            "        \"year\": 2021," +
            "        \"imageUrl\": \"https://i.ibb.co/tp0JPxVH/Dodge-Challenger-2021.jpg\"," +
            "        \"price\": 6120000," +
            "        \"creditPrice\": 85602," +
            "        \"bodyStyle\": \"Купе\"," +
            "        \"transmission\": \"Автоматическая коробка\"," +
            "        \"power\": \"492 л.с.\"," +
            "        \"engineDisplacement\": \"6.4 л.\"," +
            "        \"engineType\": \"Бензин\"," +
            "        \"drive\": \"Задний привод\"," +
            "        \"exteriorColor\": \"Синий\"," +
            "        \"steeringWheel\": \"Левый\"" +
            "    }," +
            "    {" +
            "        \"make\": \"Ford\"," +
            "        \"model\": \"Mustang\"," +
            "        \"year\": 2019," +
            "        \"imageUrl\": \"https://i.ibb.co/BHJGsgLv/Ford-Mustang-2019.jpg\"," +
            "        \"price\": 3465000," +
            "        \"creditPrice\": 45395," +
            "        \"bodyStyle\": \"Купе\"," +
            "        \"transmission\": \"Автоматическая коробка\"," +
            "        \"power\": \"317 л.с.\"," +
            "        \"engineDisplacement\": \"2.3 л.\"," +
            "        \"engineType\": \"Бензин\"," +
            "        \"drive\": \"Задний привод\"," +
            "        \"exteriorColor\": \"Черный\"," +
            "        \"steeringWheel\": \"Левый\"" +
            "    }," +
            "    {" +
            "        \"make\": \"Chevrolet\"," +
            "        \"model\": \"Camaro\"," +
            "        \"year\": 2018," +
            "        \"imageUrl\": \"https://i.ibb.co/N61LcW0g/Chevrolet-Camaro-2018.jpg\"," +
            "        \"price\": 5500000," +
            "        \"creditPrice\": 71984," +
            "        \"bodyStyle\": \"Купе\"," +
            "        \"transmission\": \"Автоматическая коробка\"," +
            "        \"power\": \"238 л.с.\"," +
            "        \"engineDisplacement\": \"2.0 л.\"," +
            "        \"engineType\": \"Бензин\"," +
            "        \"drive\": \"Задний привод\"," +
            "        \"exteriorColor\": \"Синий\"," +
            "        \"steeringWheel\": \"Левый\"" +
            "    }" +
            "]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firestore_upload); //  Создайте макет с одной кнопкой

        db = FirebaseFirestore.getInstance();
        uploadButton = findViewById(R.id.upload_button); // Убедитесь, что у вашей кнопки есть этот ID

        uploadButton.setOnClickListener(view -> uploadCars());
    }

    private void uploadCars() {
        try {
            JSONArray carsJsonArray = new JSONArray(carJson);
            WriteBatch batch = db.batch();

            for (int i = 0; i < carsJsonArray.length(); i++) {
                JSONObject carJsonObject = carsJsonArray.getJSONObject(i);
                Map<String, Object> carData = new HashMap<>();
                // Извлечение данных из JSON-объекта.
                carData.put("make", carJsonObject.getString("make"));
                carData.put("model", carJsonObject.getString("model"));
                carData.put("year", carJsonObject.getInt("year"));
                carData.put("imageUrl", carJsonObject.getString("imageUrl"));
                carData.put("price", carJsonObject.getLong("price"));
                carData.put("creditPrice", carJsonObject.getLong("creditPrice"));
                carData.put("bodyStyle", carJsonObject.getString("bodyStyle"));
                carData.put("transmission", carJsonObject.getString("transmission"));
                carData.put("power", carJsonObject.getString("power"));
                carData.put("engineDisplacement", carJsonObject.getString("engineDisplacement"));
                carData.put("engineType", carJsonObject.getString("engineType"));
                carData.put("drive", carJsonObject.getString("drive"));
                carData.put("exteriorColor", carJsonObject.getString("exteriorColor"));
                carData.put("steeringWheel", carJsonObject.getString("steeringWheel"));

                //  Создаем новый документ в коллекции "cars" с автосгенерированным ID.
                com.google.firebase.firestore.DocumentReference carRef = db.collection("cars").document();
                batch.set(carRef, carData);
            }

            //  Выполняем пакетную запись.
            Task<Void> commitTask = batch.commit();
            commitTask.addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Batch upload successful");
                Toast.makeText(this, "Data upload successful", Toast.LENGTH_SHORT).show();
                finish(); //  Закрываем Activity после успешной загрузки.
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Batch upload failed", e);
                Toast.makeText(this, "Data upload failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });

        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
            Toast.makeText(this, "Error parsing JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

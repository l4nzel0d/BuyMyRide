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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreUploadActivity extends AppCompatActivity {

    private static final String TAG = "FirestoreUpload";
    private FirebaseFirestore db;
    private Button uploadButton;

    // JSON-строка с данными об автомобилях (обновленная структура с "specs")
    private final String carJson = "[\n" +
            "    {\n" +
            "        \"make\": \"Porsche\",\n" +
            "        \"model\": \"Taycan\",\n" +
            "        \"year\": 2020,\n" +
            "        \"imageUrl\": \"https://i.ibb.co/1GqTRcyY/Porsche-Taycan-2020.jpg\",\n" +
            "        \"price\": 9605961,\n" +
            "        \"creditPrice\": 129701,\n" +
            "        \"specs\": [\n" +
            "            {\"Кузов\": \"Седан\"},\n" +
            "            {\"КПП\": \"Автоматическая коробка\"},\n" +
            "            {\"Мощность\": \"571 л.с.\"},\n" +
            "            {\"Двигатель\": \"Электро\"},\n" +
            "            {\"Привод\": \"Полный привод\"},\n" +
            "            {\"Цвет кузова\": \"Синий\"},\n" +
            "            {\"Руль\": \"Левый\"},\n" +
            "            {\"Поколение\": \"I (2019-2024)\"}\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"make\": \"Land Rover\",\n" +
            "        \"model\": \"Range Rover\",\n" +
            "        \"year\": 2025,\n" +
            "        \"imageUrl\": \"https://i.ibb.co/1GqTRcyY/Porsche-Taycan-2020.jpg\",\n" +
            "        \"price\": 25000000,\n" +
            "        \"creditPrice\": 324253,\n" +
            "        \"specs\": [\n" +
            "            {\"Кузов\": \"Внедорожник 5 дв.\"},\n" +
            "            {\"КПП\": \"Автоматическая коробка\"},\n" +
            "            {\"Мощность\": \"350 л.с.\"},\n" +
            "            {\"Объем двигателя\": \"3.0 л.\"},\n" +
            "            {\"Двигатель\": \"Дизель\"},\n" +
            "            {\"Привод\": \"Полный привод\"},\n" +
            "            {\"Цвет кузова\": \"Серый\"},\n" +
            "            {\"Руль\": \"Левый\"}\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"make\": \"Jaguar\",\n" +
            "        \"model\": \"I-Pace\",\n" +
            "        \"year\": 2019,\n" +
            "        \"imageUrl\": \"https://i.ibb.co/XxjJmwN9/Jaguar-I-Pace-2019.jpg\",\n" +
            "        \"price\": 3762000,\n" +
            "        \"creditPrice\": 49286,\n" +
            "        \"specs\": [\n" +
            "            {\"Кузов\": \"Внедорожник 5 дв.\"},\n" +
            "            {\"КПП\": \"Автоматическая коробка\"},\n" +
            "            {\"Мощность\": \"400 л.с.\"},\n" +
            "            {\"Двигатель\": \"Электро\"},\n" +
            "            {\"Привод\": \"Полный привод\"},\n" +
            "            {\"Цвет кузова\": \"Белый\"},\n" +
            "            {\"Руль\": \"Левый\"},\n" +
            "            {\"Поколение\": \"I (2018-2024)\"}\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"make\": \"Kia\",\n" +
            "        \"model\": \"Sportage\",\n" +
            "        \"year\": 2025,\n" +
            "        \"imageUrl\": \"https://i.ibb.co/jkHFyBjT/Kia-Sportage-2025.jpg\",\n" +
            "        \"price\": 3890000,\n" +
            "        \"creditPrice\": 50453,\n" +
            "        \"specs\": [\n" +
            "            {\"Комплектация\": \"Comfort\"},\n" +
            "            {\"Кузов\": \"Внедорожник 5 дв.\"},\n" +
            "            {\"КПП\": \"Автоматическая коробка\"},\n" +
            "            {\"Мощность\": \"150 л.с.\"},\n" +
            "            {\"Объем двигателя\": \"2.0 л.\"},\n" +
            "            {\"Двигатель\": \"Бензин\"},\n" +
            "            {\"Привод\": \"Передний привод\"},\n" +
            "            {\"Цвет кузова\": \"Серый\"},\n" +
            "            {\"Руль\": \"Левый\"}\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"make\": \"Volkswagen\",\n" +
            "        \"model\": \"ID.4\",\n" +
            "        \"year\": 2023,\n" +
            "        \"imageUrl\": \"https://i.ibb.co/Mxy7hF6z/Volkswagen-ID-4.jpg\",\n" +
            "        \"price\": 5400000,\n" +
            "        \"creditPrice\": 70038,\n" +
            "        \"specs\": [\n" +
            "            {\"Комплектация\": \"Prime\"},\n" +
            "            {\"Кузов\": \"Внедорожник 5 дв.\"},\n" +
            "            {\"КПП\": \"Автоматическая коробка\"},\n" +
            "            {\"Мощность\": \"313 л.с.\"},\n" +
            "            {\"Двигатель\": \"Электро\"},\n" +
            "            {\"Привод\": \"Полный привод\"},\n" +
            "            {\"Цвет кузова\": \"Синий\"},\n" +
            "            {\"Руль\": \"Левый\"}\n" +
            "        ]\n" +
            "    }\n" +
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
                carData.put("make", carJsonObject.getString("make"));
                carData.put("model", carJsonObject.getString("model"));
                carData.put("year", carJsonObject.getInt("year"));
                carData.put("imageUrl", carJsonObject.getString("imageUrl"));
                carData.put("price", carJsonObject.getLong("price"));
                carData.put("creditPrice", carJsonObject.getLong("creditPrice"));

                // Извлекаем массив "specs"
                JSONArray specsJsonArray = carJsonObject.getJSONArray("specs");
                List<Map<String, String>> specsList = new ArrayList<>();
                for (int j = 0; j < specsJsonArray.length(); j++) {
                    JSONObject specJsonObject = specsJsonArray.getJSONObject(j);
                    // Предполагаем, что каждый объект в массиве specs имеет только одну пару ключ-значение
                    String key = specJsonObject.keys().next();
                    String value = specJsonObject.getString(key);
                    specsList.add(Map.of(key, value));
                }
                carData.put("specs", specsList);

                // Создаем новый документ в коллекции "cars" с автосгенерированным ID.
                com.google.firebase.firestore.DocumentReference carRef = db.collection("cars").document();
                batch.set(carRef, carData);
            }

            // Выполняем пакетную запись.
            Task<Void> commitTask = batch.commit();
            commitTask.addOnSuccessListener(aVoid -> {
                Log.d(TAG, "Batch upload successful");
                Toast.makeText(this, "Data upload successful", Toast.LENGTH_SHORT).show();
                finish(); // Закрываем Activity после успешной загрузки.
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
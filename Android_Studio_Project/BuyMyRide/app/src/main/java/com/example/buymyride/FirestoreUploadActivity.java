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
    private final String carJson = "[" +
            "  {" +
            "    \"make\": \"Audi\"," +
            "    \"model\": \"Q8\"," +
            "    \"year\": 2024," +
            "    \"imageUrl\": \"https://i.ibb.co/bRXqN7cJ/Audi-Q8-2024.jpg\"," +
            "    \"price\": 14840000," +
            "    \"creditPrice\": 192477," +
            "    \"specs\": [" +
            "      {\"Кузов\": \"Внедорожник 5 дв.\"}," +
            "      {\"КПП\": \"Автоматическая коробка\"}," +
            "      {\"Мощность\": \"340 л.с.\"}," +
            "      {\"Объём двигателя\": \"3.0 л.\"}," +
            "      {\"Двигатель\": \"Бензин\"}," +
            "      {\"Привод\": \"Полный привод\"}," +
            "      {\"Цвет кузова\": \"Чёрный\"}," +
            "      {\"Руль\": \"Левый\"}" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"make\": \"Audi\"," +
            "    \"model\": \"A4\"," +
            "    \"year\": 2024," +
            "    \"imageUrl\": \"https://i.ibb.co/2124y89c/Audi-A4-2024.jpg\"," +
            "    \"price\": 6000000," +
            "    \"creditPrice\": 77820," +
            "    \"specs\": [" +
            "      {\"Кузов\": \"Седан\"}," +
            "      {\"КПП\": \"Коробка-робот\"}," +
            "      {\"Мощность\": \"190 л.с.\"}," +
            "      {\"Объём двигателя\": \"2.0 л.\"}," +
            "      {\"Двигатель\": \"Бензин\"}," +
            "      {\"Привод\": \"Передний привод\"}," +
            "      {\"Цвет кузова\": \"Серый\"}," +
            "      {\"Руль\": \"Левый\"}" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"make\": \"Ford\"," +
            "    \"model\": \"F-150\"," +
            "    \"year\": 2023," +
            "    \"imageUrl\": \"https://i.ibb.co/pr21dB2B/Ford-F-150-2023.jpg\"," +
            "    \"price\": 16300000," +
            "    \"creditPrice\": 211413," +
            "    \"specs\": [" +
            "      {\"Кузов\": \"Пикап\"}," +
            "      {\"КПП\": \"Автоматическая коробка\"}," +
            "      {\"Мощность\": \"450 л.с.\"}," +
            "      {\"Объём двигателя\": \"3.5 л.\"}," +
            "      {\"Двигатель\": \"Бензин\"}," +
            "      {\"Привод\": \"Полный привод\"}," +
            "      {\"Цвет кузова\": \"Синий\"}," +
            "      {\"Руль\": \"Левый\"}" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"make\": \"MINI\"," +
            "    \"model\": \"Hatch\"," +
            "    \"year\": 2021," +
            "    \"imageUrl\": \"https://i.ibb.co/938HPwsp/MINI-Hatch-2021.jpg\"," +
            "    \"price\": 3700000," +
            "    \"creditPrice\": 48625," +
            "    \"specs\": [" +
            "      {\"Кузов\": \"Хэтчбек 3 дв.\"}," +
            "      {\"КПП\": \"Автоматическая коробка\"}," +
            "      {\"Мощность\": \"231 л.с.\"}," +
            "      {\"Объём двигателя\": \"2.0 л.\"}," +
            "      {\"Двигатель\": \"Бензин\"}," +
            "      {\"Привод\": \"Передний привод\"}," +
            "      {\"Цвет кузова\": \"Синий\"}," +
            "      {\"Руль\": \"Левый\"}" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"make\": \"Ford\"," +
            "    \"model\": \"Explorer\"," +
            "    \"year\": 2019," +
            "    \"imageUrl\": \"https://i.ibb.co/9HNnS7FZ/Ford-Explorer-2019.jpg\"," +
            "    \"price\": 3784000," +
            "    \"creditPrice\": 50453," +
            "    \"specs\": [" +
            "      {\"Кузов\": \"Внедорожник 5 дв.\"}," +
            "      {\"КПП\": \"Автоматическая коробка\"}," +
            "      {\"Мощность\": \"249 л.с.\"}," +
            "      {\"Объём двигателя\": \"3.5 л.\"}," +
            "      {\"Двигатель\": \"Бензин\"}," +
            "      {\"Привод\": \"Полный привод\"}," +
            "      {\"Цвет кузова\": \"Белый\"}," +
            "      {\"Руль\": \"Левый\"}" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"make\": \"Dodge\"," +
            "    \"model\": \"Challenger\"," +
            "    \"year\": 2021," +
            "    \"imageUrl\": \"https://i.ibb.co/tp0JPxVH/Dodge-Challenger-2021.jpg\"," +
            "    \"price\": 6120000," +
            "    \"creditPrice\": 85602," +
            "    \"specs\": [" +
            "      {\"Кузов\": \"Купе\"}," +
            "      {\"КПП\": \"Автоматическая коробка\"}," +
            "      {\"Мощность\": \"492 л.с.\"}," +
            "      {\"Объём двигателя\": \"6.4 л.\"}," +
            "      {\"Двигатель\": \"Бензин\"}," +
            "      {\"Привод\": \"Задний привод\"}," +
            "      {\"Цвет кузова\": \"Синий\"}," +
            "      {\"Руль\": \"Левый\"}" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"make\": \"Ford\"," +
            "    \"model\": \"Mustang\"," +
            "    \"year\": 2019," +
            "    \"imageUrl\": \"https://i.ibb.co/BHJGsgLv/Ford-Mustang-2019.jpg\"," +
            "    \"price\": 3465000," +
            "    \"creditPrice\": 45395," +
            "    \"specs\": [" +
            "      {\"Кузов\": \"Купе\"}," +
            "      {\"КПП\": \"Автоматическая коробка\"}," +
            "      {\"Мощность\": \"317 л.с.\"}," +
            "      {\"Объём двигателя\": \"2.3 л.\"}," +
            "      {\"Двигатель\": \"Бензин\"}," +
            "      {\"Привод\": \"Задний привод\"}," +
            "      {\"Цвет кузова\": \"Черный\"}," +
            "      {\"Руль\": \"Левый\"}" +
            "    ]" +
            "  }," +
            "  {" +
            "    \"make\": \"Chevrolet\"," +
            "    \"model\": \"Camaro\"," +
            "    \"year\": 2018," +
            "    \"imageUrl\": \"https://i.ibb.co/N61LcW0g/Chevrolet-Camaro-2018.jpg\"," +
            "    \"price\": 5500000," +
            "    \"creditPrice\": 71984," +
            "    \"specs\": [" +
            "      {\"Кузов\": \"Купе\"}," +
            "      {\"КПП\": \"Автоматическая коробка\"}," +
            "      {\"Мощность\": \"238 л.с.\"}," +
            "      {\"Объём двигателя\": \"2.0 л.\"}," +
            "      {\"Двигатель\": \"Бензин\"}," +
            "      {\"Привод\": \"Задний привод\"}," +
            "      {\"Цвет кузова\": \"Синий\"}," +
            "      {\"Руль\": \"Левый\"}" +
            "    ]" +
            "  }" +
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
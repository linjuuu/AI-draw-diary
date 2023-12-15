package com.example.term.Fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import android.graphics.Bitmap;

import com.example.term.R;

public class FragDiary extends Fragment {

    static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String REST_API_KEY = "3e4b7b60413e29d52adb38fc317f0045";
    private static final String REST_API_KEY_IMAGE = "9288091dcf057cb80ffa789e922e413d";

    private static final String API_URL = "https://api.kakaobrain.com/v1/inference/kogpt/generation";
    private static final String API_URL_IMAGE = "https://api.kakaobrain.com/v2/inference/karlo/t2i";

    EditText result;
    ImageView imageView;
    ImageButton chooseDateButton;
    ImageButton transButton;
    ImageButton resetButton;
    ImageButton saveButton;
    DatePicker datePicker;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragDiary() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragDiary newInstance(String param1, String param2) {
        FragDiary fragment = new FragDiary();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_frag_diary, container, false);

        imageView = rootView.findViewById(R.id.imageView);
        result = rootView.findViewById(R.id.writeDiaryText);
        transButton = rootView.findViewById(R.id.finishButton);
        saveButton = rootView.findViewById(R.id.saveButton);
        chooseDateButton = rootView.findViewById(R.id.chooseDateButton);
        resetButton = rootView.findViewById(R.id.resetButton);
        datePicker = rootView.findViewById(R.id.datePicker);

        // chooseDateButton 클릭 이벤트 설정
        chooseDateButton.setOnClickListener(v -> showDatePickerDialog());
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.setText("");
                imageView.setImageResource(R.drawable.initimage);
            }
        });
        transButton.setOnClickListener(v -> new KogptApiTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
        saveButton.setOnClickListener(v -> saveDiary());

        return rootView;
    }

    private String convertInputStreamToString(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }

    private Bitmap t2i(String prompt) {
        try {
            URL url = new URL(API_URL_IMAGE);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Authorization", "KakaoAK " + REST_API_KEY_IMAGE);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);

            JSONObject jsonInput = new JSONObject();
            prompt = "(pencil drawing)" + prompt;
            jsonInput.put("prompt", prompt);

            urlConnection.getOutputStream().write(jsonInput.toString().getBytes("UTF-8"));

            InputStream in = urlConnection.getInputStream();
            JSONObject jsonResponse = new JSONObject(convertInputStreamToString(in));
            String imageUrl = jsonResponse.getJSONArray("images").getJSONObject(0).getString("image");

            // 이미지 다운로드
            return downloadImage(imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Bitmap downloadImage(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.connect();

            // 이미지를 Bitmap으로 변환
            return BitmapFactory.decodeStream(urlConnection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    private class T2iTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            String textValue = params[0];
            return t2i(textValue);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
            saveImageToInternalStorage(result);
        }
    }




    private void showDatePickerDialog() {
        // 현재 날짜를 기본으로 설정
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // DatePickerDialog 생성
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireActivity(),
                (view, yearSelected, monthOfYear, dayOfMonth) -> {
                    // 선택된 날짜로 설정
                    datePicker.updateDate(yearSelected, monthOfYear, dayOfMonth);
                },
                year, month, day);

        // DatePickerDialog 표시
        datePickerDialog.show();
    }


    private class KogptApiTask extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... voids) {
            String s = result.getText().toString() + "\n\n한줄 요약:";

            try {
                // 요청 파라미터 구성
                JSONObject requestBody = new JSONObject();
                requestBody.put("prompt", s);
                requestBody.put("max_tokens", 120);
                requestBody.put("temperature", 0.1);
                requestBody.put("top_p", 0.3);
                requestBody.put("n", 1);

                // HTTP POST 요청 보내기
                return sendPostRequest(API_URL, requestBody);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            // 백그라운드 작업이 완료되면 이 메서드에서 UI 업데이트 등을 수행할 수 있습니다.
            if (response != null) {
                try {
                    // "generations" 배열 추출
                    JSONArray generationsArray = response.getJSONArray("generations");

                    // 첫 번째 Generation 객체 추출
                    JSONObject firstGenerationObject = generationsArray.getJSONObject(0);

                    // "text" 필드 값을 추출하여 String으로 변환
                    String textValue = firstGenerationObject.getString("text");

                    // UI 업데이트
                    result.setText(textValue);
                    new T2iTask().execute(textValue);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }




    private static JSONObject sendPostRequest(String apiUrl, JSONObject requestBody) throws IOException, JSONException {
        URL url = new URL(apiUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try {
            // Request headers
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Authorization", "KakaoAK " + REST_API_KEY);
            urlConnection.setRequestProperty("Content-Type", "application/json");

            // Enable input/output streams
            urlConnection.setDoOutput(true);

            // Write request body
            try (OutputStream outputStream = urlConnection.getOutputStream()) {
                outputStream.write(requestBody.toString().getBytes("UTF-8"));
            }

            // Read response
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"))) {
                StringBuilder responseStringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseStringBuilder.append(line);
                }
                // JSON 파싱
                return new JSONObject(responseStringBuilder.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                throw e;  // JSONException을 다시 던져서 호출자에게 알림
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private void saveDiary() {
        Context context = getActivity();
        try {
            int year = datePicker.getYear();
            int month = datePicker.getMonth() + 1; // Month is 0-based
            int day = datePicker.getDayOfMonth();

            String filename = String.format("%02d%02d%02d.txt", year % 100, month, day);

            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(result.getText().toString().getBytes());
            fos.close();



            Toast.makeText(context, "일기가 저장되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "일기 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveImageToInternalStorage(Bitmap bitmap) {
        Context context = getActivity();
        try {
            int year = datePicker.getYear();
            int month = datePicker.getMonth() + 1; // Month is 0-based
            int day = datePicker.getDayOfMonth();

            String filename = String.format("%02d%02d%02d.png", year % 100, month, day);

            // 내부 저장소에 파일 생성
            FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);

            // Bitmap을 PNG 형식으로 파일에 저장
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

            // 파일 닫기
            fos.close();

            Toast.makeText(context, "이미지가 저장되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "이미지 저장에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}





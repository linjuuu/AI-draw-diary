package com.example.term.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.term.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FragDiary extends Fragment {

    static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String REST_API_KEY = "3e4b7b60413e29d52adb38fc317f0045";
    private static final String API_URL = "https://api.kakaobrain.com/v1/inference/kogpt/generation";
    EditText result;
    Button transButton;

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


        result = rootView.findViewById(R.id.writeDiaryText);
        transButton = rootView.findViewById(R.id.finishButton);

        transButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new KogptApiTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });




        return rootView;
    }

    private class KogptApiTask extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... voids) {

            String s = result.getText().toString();
            s += "귀엽게 변환";

            try {
                // 요청 파라미터 구성
                JSONObject requestBody = new JSONObject();
                requestBody.put("prompt", s);
                requestBody.put("max_tokens", 80);
                requestBody.put("temperature", 0.1);
                requestBody.put("top_p", 0.1);
                requestBody.put("n", 2);

                // HTTP POST 요청 보내기
                return  sendPostRequest(API_URL, requestBody);

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

}
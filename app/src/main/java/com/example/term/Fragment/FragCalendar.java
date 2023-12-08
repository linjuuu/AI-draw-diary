package com.example.term.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.term.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FragCalendar extends Fragment {

    CalendarView calendarView;
    Button showDataButton;
    TextView loadDiary;
    public FragCalendar() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_frag_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendarView = view.findViewById(R.id.calendarView);
        showDataButton = view.findViewById(R.id.showDataButton);
        loadDiary = view.findViewById(R.id.loadDiary);

        // 달력에서 날짜를 선택했을 때의 리스너 설정
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 선택된 날짜에 해당하는 파일명 생성 (예: 231208.txt)
                String filename = String.format("%02d%02d%02d.txt", year % 100, month + 1, dayOfMonth);
                // 데이터 표시 버튼의 태그에 파일명 저장
                showDataButton.setTag(filename);
            }
        });

        // 데이터 표시 버튼 클릭 리스너 설정
        showDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 데이터 표시 버튼의 태그에서 파일명 읽어오기
                String filename = (String) v.getTag();
                if (filename != null) {
                    // 파일 읽기 및 내용 표시
                    String data = readDataFromFile(filename);
                    loadDiary.setText(data);
                }
            }
        });
    }

    // 파일에서 데이터 읽어오기
    private String readDataFromFile(String filename) {
        try {
            FileInputStream fis = requireContext().openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading data";
        }
    }
}

package com.example.term.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class FragCalendar extends Fragment {

    private View rootView; // 추가된 부분
    CalendarView calendarView;
    ImageButton showDataButton;
    public FragCalendar() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_frag_calendar, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendarView = view.findViewById(R.id.calendarView);
        showDataButton = view.findViewById(R.id.showDataButton);

        // 달력에서 날짜를 선택했을 때의 리스너 설정
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 데이터 표시 버튼 클릭 리스너 설정
                showDataButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String filename = String.format("%02d%02d%02d.txt", year % 100, month + 1, dayOfMonth);
                        final String imageFilename = String.format("%02d%02d%02d.png", year % 100, month + 1, dayOfMonth);

                        // 다이얼로그 표시
                        ViewDialogFragment dialog = new ViewDialogFragment(filename, imageFilename);
                        dialog.show(getFragmentManager(), "ViewDialogFragment");
                    }
                });
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
            return "이 날 일기가 없어요 !";
        }
    }

    // 이미지 파일에서 Bitmap 읽어오기
    private Bitmap readImageFromFile(String filename) {
        try {
            FileInputStream fis = requireContext().openFileInput(filename);
            return BitmapFactory.decodeStream(fis);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

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

    CalendarView calendarView;
    ImageButton showDataButton;
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

        // 달력에서 날짜를 선택했을 때의 리스너 설정
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 선택된 날짜에 해당하는 파일명 생성 (예: 231208.txt)
                final String filename = String.format("%02d%02d%02d.txt", year % 100, month + 1, dayOfMonth);
                final String imageFilename = String.format("%02d%02d%02d.png", year % 100, month + 1, dayOfMonth);

                // 데이터 표시 버튼 클릭 리스너 설정
                showDataButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 파일 읽기 및 내용 표시
                        String data = readDataFromFile(filename);

                        // 이미지 파일 읽기 및 표시
                        Bitmap imageBitmap = readImageFromFile(imageFilename);

                        // 다이얼로그 레이아웃 설정
                        View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_image_view, null);
                        ImageView dialogImageView = dialogLayout.findViewById(R.id.dialogImageView);
                        dialogImageView.setImageBitmap(imageBitmap);

                        TextView dialogTextView = dialogLayout.findViewById(R.id.dialogTextView);
                        dialogTextView.setText(data);

                        // 다이얼로그 생성 및 설정
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle(String.format("%02d%02d%02d 의 일기", year % 100, month + 1, dayOfMonth));

                        // 레이아웃 설정
                        builder.setView(dialogLayout);

                        // 확인 버튼 추가
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss(); // 다이얼로그 닫기
                            }
                        });

                        // 다이얼로그 표시
                        AlertDialog dialog = builder.create();
                        dialog.show();
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

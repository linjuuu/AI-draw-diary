package com.example.term.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.term.R;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ViewDialogFragment extends DialogFragment {

    private String filename;
    private String imageFilename;

    public ViewDialogFragment(String filename, String imageFilename) {
        this.filename = filename;
        this.imageFilename = imageFilename;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View combinedView = inflater.inflate(R.layout.dialog_image_view, null);
        ImageView combinedImageView = combinedView.findViewById(R.id.dialogImageView);
        TextView combinedTextView = combinedView.findViewById(R.id.dialogTextView);

        // 파일 읽기 및 내용 표시
        String data = "\n\n\n" + readDataFromFile(filename);

        // 이미지 파일 읽기 및 표시
        Bitmap imageBitmap = readImageFromFile(imageFilename);

        // 이미지 및 텍스트 설정
        combinedImageView.setImageBitmap(imageBitmap);
        combinedTextView.setText(data);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(combinedView)
                .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // 다이얼로그를 닫음
                        dialog.dismiss();
                    }
                });

        return builder.create();
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

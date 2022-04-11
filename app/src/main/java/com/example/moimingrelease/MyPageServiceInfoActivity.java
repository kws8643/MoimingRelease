package com.example.moimingrelease;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MyPageServiceInfoActivity extends AppCompatActivity {

    private TextView textKakaoHref, textEmailHref, textInstaHref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_service_info);

        initView();

        initParams();

        textEmailHref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("MOIMING", textEmailHref.getText().toString());
                clipboardManager.setPrimaryClip(clipData);

                //복사가 되었다면 토스트메시지 노출
                Toast.makeText(getApplicationContext(), "이메일 주소가 복사되었습니다", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void initView() {

        textKakaoHref = findViewById(R.id.text_moiming_kakao_href);
        textEmailHref = findViewById(R.id.text_moiming_email_href);
        textInstaHref = findViewById(R.id.text_moiming_insta_href);

    }

    private void initParams() {

    }
}
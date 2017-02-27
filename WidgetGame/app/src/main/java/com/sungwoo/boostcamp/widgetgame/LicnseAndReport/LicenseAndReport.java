package com.sungwoo.boostcamp.widgetgame.LicnseAndReport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.sungwoo.boostcamp.widgetgame.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class LicenseAndReport extends AppCompatActivity {
    @BindView(R.id.license_and_report_report_description_et)
    protected EditText reportDescriptionEt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_and_report);

        ButterKnife.bind(this);
    }
    @OnClick(R.id.license_and_report_open_license_btn)
    public void onLicenseBtnClicked() {
        Intent intent = new Intent(this, License.class);
        startActivity(intent);
    }

    @OnClick(R.id.license_and_report_send_report_btn)
    public void onSendReportBtn() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.PREF_USER), MODE_PRIVATE);
        String email = sharedPreferences.getString(getString(R.string.PREF_USER_EMAIL), "");
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","mandoo992@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "TxT 버그리포트 + email : " + email);
        emailIntent.putExtra(Intent.EXTRA_TEXT, reportDescriptionEt.getText().toString());
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}

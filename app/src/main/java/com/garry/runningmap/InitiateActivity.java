package com.garry.runningmap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.garry.runningmap.utils.IsVaildUtil;

public class InitiateActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText gameName;
    private EditText startLoc;
    private EditText endLoc;
    private Button okButton;
    private Button goBackButton;
    private LatLng now;
    private LatLng start;
    private LatLng end;
    private Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiate);
        initViews();
    }


    /**
    * 点击事件
    */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_loc:
                Toast.makeText(this, start.toString(), Toast.LENGTH_SHORT).show();
                intent.putExtra("status","起点");
                startActivityForResult(intent, 1);
                break;
            case R.id.end_loc:
                Toast.makeText(this, end.toString(), Toast.LENGTH_SHORT).show();
                intent.putExtra("status","终点");
                startActivityForResult(intent, 1);
                break;
            case R.id.go_back:
                finish();
                break;
            case R.id.ok:
                if (!IsVaildUtil.isGameNameVaild(gameName.getText().toString())){
                    Toast.makeText(this, "请输入比赛名", Toast.LENGTH_SHORT).show();
                    break;
                }
                Toast.makeText(this, gameName.getText()+":"+start.toString()+":"+end.toString(), Toast.LENGTH_SHORT).show();
                intent.setClass(this, GameActivity.class);
                intent.putExtra("startLL", start);
                intent.putExtra("endLL", end);
                startActivity(intent);
                finish();
                break;
            default:
                Toast.makeText(this, "default", Toast.LENGTH_SHORT).show();
        }
    }

    /**
    * 实例化控件
    */

    private void initViews(){
        gameName = (EditText) findViewById(R.id.game_name);
        startLoc = (EditText) findViewById(R.id.start_loc);
        endLoc = (EditText) findViewById(R.id.end_loc);
        okButton = (Button) findViewById(R.id.ok);
        goBackButton = (Button) findViewById(R.id.go_back);
        startLoc.setOnClickListener(this);
        endLoc.setOnClickListener(this);
        okButton.setOnClickListener(this);
        goBackButton.setOnClickListener(this);
        now = getIntent().getParcelableExtra("now_loc");
        start = now;
        end = now;
        intent.setClass(this, MapActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==1&&resultCode==1){
            if (data.getStringExtra("status").equals("起点")){
                start = data.getParcelableExtra("result");
                startLoc.setText("经度"+start.longitude+":纬度"+start.latitude);
            }else if(data.getStringExtra("status").equals("终点")){
                end = data.getParcelableExtra("result");
                endLoc.setText("经度"+end.longitude+":纬度"+end.latitude);
            }
        }

    }
}


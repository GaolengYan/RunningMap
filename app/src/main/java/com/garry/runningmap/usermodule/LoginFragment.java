package com.garry.runningmap.usermodule;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.garry.runningmap.MainActivity;
import com.garry.runningmap.R;
import com.garry.runningmap.utils.IsVaildUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginFragment extends Fragment {
    private EditText mail_edittext;
    private EditText password_edittext;
    private Button submit_button;
    private Button back_button;
    private final String LOGIN_API = "http://192.168.42.20:8080/UserLoginServlet";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //加载布局文件
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mail_edittext = (EditText) view.findViewById(R.id.login_mail);
        password_edittext = (EditText) view.findViewById(R.id.login_password);
        submit_button = (Button) view.findViewById(R.id.login_submit);
        back_button = (Button) view.findViewById(R.id.go_back);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
    }

    private void submit() {
        //获取输入框中的数据
        String mail = mail_edittext.getText().toString();
        String password = password_edittext.getText().toString();
        //对数据进行判断是否合法
        if (mail.equals("") || !IsVaildUtil.isMailVaild(mail)) {
            Toast.makeText(getActivity(), "请输入有效邮箱", Toast.LENGTH_SHORT).show();
        } else if (password.equals("") || !IsVaildUtil.isPasswordVaild(password)) {
            Toast.makeText(getActivity(), "不符合规范的密码", Toast.LENGTH_SHORT).show();
        } else {
            /*
             * 新建一个线程将数据发送到服务端
             */
            final String finalLoginUrl = LOGIN_API + "?email=" + mail + "&password=" + password;
            new Thread() {
                @Override
                public void run() {
                    sendGetRequest(finalLoginUrl);
                }
            }.start();

        }
    }

    private void sendGetRequest(String url) {
        OkHttpClient client = new OkHttpClient();
        //构造Request对象
        //采用建造者模式，链式调用指明进行Get请求,传入Get的请求地址
        final Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        //异步调用并设置回调函数
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                Toast.makeText(getActivity(), "登陆失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseStr = response.body().string();
                if (responseStr.equals("login success")){
                    Looper.prepare();
                    Toast.makeText(getActivity(), "登陆成功", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    Intent intent = new Intent();
                    intent.setClass(getContext(), MainActivity.class);
                    startActivity(intent);
                }else {
                    Looper.prepare();
                    Toast.makeText(getActivity(), "登陆失败", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        });

    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }
}

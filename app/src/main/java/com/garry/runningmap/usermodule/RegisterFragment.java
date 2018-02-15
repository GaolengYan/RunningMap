package com.garry.runningmap.usermodule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.garry.runningmap.R;
import com.garry.runningmap.utils.IsVaildUtil;
import com.garry.runningmap.bean.User;


public class RegisterFragment extends Fragment {
    private EditText mail_edittext;
    private EditText username_edittext;
    private EditText password_edittext;
    private EditText repassword_edittext;
    private Button submit_button;
    private Button back_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //加载布局文件
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //实例化控件
        mail_edittext = (EditText) view.findViewById(R.id.regist_mail);
        username_edittext = (EditText) view.findViewById(R.id.regist_username);
        password_edittext = (EditText) view.findViewById(R.id.regist_password);
        repassword_edittext = (EditText) view.findViewById(R.id.regist_remarkpassword);
        submit_button = (Button) view.findViewById(R.id.regist_submit);
        back_button = (Button) view.findViewById(R.id.go_back);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //添加点击事件
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

    private void submit(){
        //获取输入框中的数据
        String mail = mail_edittext.getText().toString();
        String username = username_edittext.getText().toString();
        String password = password_edittext.getText().toString();
        String repassword = repassword_edittext.getText().toString();
        //对数据进行判断是否合法
        if (mail.equals("")||!IsVaildUtil.isMailVaild(mail)){
            Toast.makeText(getActivity(), "请输入有效邮箱", Toast.LENGTH_SHORT).show();
        }else if (username.equals("")){
            Toast.makeText(getActivity(), "请输入用户名", Toast.LENGTH_SHORT).show();
        }else if (password.equals("")||!IsVaildUtil.isPasswordVaild(password)){
            Toast.makeText(getActivity(), "不符合规范的密码", Toast.LENGTH_SHORT).show();
        }else if (!repassword.equals(password)){
            Toast.makeText(getActivity(), "两次密码不同", Toast.LENGTH_SHORT).show();
        }else {
            //数据没问题就打包成一个User类
            User user = new User(mail, username, password);
            /**
             * 新建一个线程将user发送到服务端
             */
            Toast.makeText(getActivity(), mail+":"+username+":"+password, Toast.LENGTH_SHORT).show();
        }
    }

    public static RegisterFragment newInstance(){
        return new RegisterFragment();
    }

}

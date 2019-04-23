package pub.panpan.zjutzjc_connecterplus.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import pub.panpan.zjutzjc_connecterplus.Bean.State;
import pub.panpan.zjutzjc_connecterplus.MainActivity;
import pub.panpan.zjutzjc_connecterplus.R;

public class LoginActivity extends AppCompatActivity {
    private TextView tvLogin;
    private EditText etLogin;
    private Button btLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        etLogin = (EditText) findViewById(R.id.etLogin);
        btLogin = (Button) findViewById(R.id.btLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //以下是新增内容
                State s1 = new State();
                s1.setUsername(etLogin.getText().toString());
                s1.setPCstate(false);
                s1.setFlag(false);
                s1.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            //System.out.println("创建数据成功");
                            Intent intentback = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intentback);
                            Toast.makeText(LoginActivity.this,"成功！",Toast.LENGTH_LONG).show();
                        } else {
                            //Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                            Toast.makeText(LoginActivity.this,e.getMessage()+","+e.getErrorCode(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
                //新增内容结束
            }
        });
    }

}

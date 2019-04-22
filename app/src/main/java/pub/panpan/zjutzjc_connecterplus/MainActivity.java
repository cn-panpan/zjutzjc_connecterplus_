package pub.panpan.zjutzjc_connecterplus;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import pub.panpan.zjutzjc_connecterplus.Bean.State;

public class MainActivity extends AppCompatActivity {

    private TextView tvState;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        //初始化
        tvState = (TextView) findViewById(R.id.tvState);
        btnStart = (Button) findViewById(R.id.btnStart);
        init();

    }

    private void init() {
        //bmobSDK的初始化
        Bmob.initialize(this, "caee210632d70ef6f8b2f7f79b9cffc8");
        //获取Bmob中当前id的flag信息
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentNetDBM();
                tvState.setText("服务已开启！");
            }
        });
        //获取权限
        String[] signalpermissions =
                {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_NETWORK_STATE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.MODIFY_PHONE_STATE};
        for (int j = 0; j < signalpermissions.length; j++) {
            int i = ContextCompat.checkSelfPermission(this, signalpermissions[j]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                ActivityCompat.requestPermissions(this, signalpermissions, 321);
            }
        }
    }

    private void updateflag() {
        State s1 = new State();
        s1.setUsername("20180220413");
        s1.setMBstate(true);
        s1.setPCstate(true);
        s1.setFlag(true);
        s1.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    System.out.println("创建数据成功");
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    //获取手机信号强度
    private void getCurrentNetDBM() {
        //获取当前时间
        Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料
        t.setToNow();
        final int minter = t.minute;
        ConnectivityManager mConnectivityManager;
        TelephonyManager mTelephonyManager;
        PhoneStateListener mListener;
        mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //判断该手机是否包含Sim卡
        int simState = mTelephonyManager.getSimState();
        if (simState == TelephonyManager.SIM_STATE_ABSENT && simState == TelephonyManager.SIM_STATE_UNKNOWN) {
            Toast.makeText(MainActivity.this, "请插入Sim卡之后再试", Toast.LENGTH_SHORT).show();
            return;
        } else {
            //监听信号强度
            mListener = new PhoneStateListener() {
                public void onSignalStrengthsChanged(SignalStrength signalStrength) {

                    int asu = signalStrength.getGsmSignalStrength();
                    int dbm = -113 + 2 * asu;
                    System.out.println(dbm + minter);
                    if (dbm < -90) {
                        updateflag();
                        System.out.println("");
                    }
                }
            };
                mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }
    }
}

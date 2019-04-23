package pub.panpan.zjutzjc_connecterplus;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import pub.panpan.zjutzjc_connecterplus.ui.activity.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private Button mainBtlogin;
    private TextView tvState;
    private Button btnStart;
    private ConnectivityManager mConnectivityManager;
    private TelephonyManager mTelephonyManager;
    private PhoneStateListener mListener;
    //信号值
    private int asu;
    private int dbm;
    private Boolean pcstate;
    private Runnable runnable;
    private Handler handler;

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
                if (btnStart.getText().equals("开启服务")) {
                    tvState.setText("服务已开启！");
                    btnStart.setText("停止服务");
                    runmonitor();
                } else if (btnStart.getText().equals("停止服务")) {
                    tvState.setText("服务已关闭！");
                    btnStart.setText("开启服务");
                    handler.removeCallbacks(runnable);
                }
            }
        });
        mainBtlogin = (Button)findViewById( R.id.main_btlogin );
        mainBtlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intentLogin);
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
        System.out.println("在这里发送flag给pc端，发送request断开网络");
    }

    //获取手机信号强度
    private void getCurrentNetDBM() {
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
                    asu = signalStrength.getGsmSignalStrength();
                    dbm = -113 + 2 * asu;
                }
            };

        }
    }

    private void runmonitor() {
        //设置Handler.postDelayed，每十分钟执行一次
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                querypcstate();
                if (pcstate = true) {
                    getCurrentNetDBM();
                    mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
                    System.out.println(dbm);
                    if (dbm < -50) {
                        updateflag();
                    }
                    mTelephonyManager.listen(mListener, PhoneStateListener.LISTEN_NONE);
                }
                handler.postDelayed(this, 6000);
            }
        };
        handler.post(runnable);
    }

    private void querypcstate() {
        BmobQuery query = new BmobQuery("State");
        query.addWhereEqualTo("username", "201802250413");
        query.addWhereEqualTo("PCstate", true);
        query.findObjectsByTable(new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray ary, BmobException e) {
                if (e == null) {
                    //Log.i("bmob","查询成功："+ary.toString());
                    pcstate = true;
                } else {
                    //Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                    pcstate = false;
                }
            }
        });
    }
}

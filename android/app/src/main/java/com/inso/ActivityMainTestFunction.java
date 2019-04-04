package com.inso;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inso.core.msg.ComeMessage;
import com.inso.core.msg.IComeMessage;
import com.inso.core.msg.PhoneCallUtil;
import com.inso.plugin.tools.L;
import com.inso.watch.baselib.base.BaseDialogFragment;
import com.inso.watch.baselib.wigets.ToastWidget;
import com.inso.watch.commonlib.constants.PermissionConstants;
import com.inso.watch.commonlib.utils.PermissionUtils;
import com.inso.watch.commonlib.utils.ServiceUtils;
import com.inso.watch.server.IRemoteAidlInterface;
import com.inso.watch.server.Person;

import java.util.List;

/**
 * Comment:
 * Author: ftc300
 * Date: 2019/4/1
 * Blog: www.ftc300.pub
 * GitHub: https://github.com/ftc300
 */
public class ActivityMainTestFunction extends AppCompatActivity implements IComeMessage {

    Button button3;
    Button button4;
    private Context c;

    private TelephonyManager telephonyManager;
    private PhoneCallListener callListener;
    ComeMessage comeMessage;

    private IRemoteAidlInterface mRemoteAidlInterface;
    private static final String BIND_SERVICE_ACTION = "android.intent.action.RESPOND_AIDL_MESSAGE";
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRemoteAidlInterface = IRemoteAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mRemoteAidlInterface = null;
        }
    };


    private void bindService() {
        Intent serviceIntent = new Intent();
        serviceIntent.setAction(BIND_SERVICE_ACTION);
        serviceIntent.setComponent(new ComponentName("com.inso.watch.server", "com.inso.watch.server.RemoteService"));
        final Intent eintent = new Intent(achieveExplicitFromImplicitIntent(this, serviceIntent));
        bindService(serviceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        L.d("bindService");
    }


    public Intent achieveExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        L.d("packageName = " + packageName);
        L.d("className = " + className);
        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        c = this;
        setContentView(R.layout.activity_main_test);
        init();

        findViewById(R.id.tv_phone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telephonyManager.listen(callListener, PhoneStateListener.LISTEN_CALL_STATE);
            }
        });
        if (ActivityCompat.checkSelfPermission(ActivityMainTestFunction.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivityMainTestFunction.this, new String[]{Manifest.permission.CALL_PHONE}, 1000);
        }

        findViewById(R.id.tv_push_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comeMessage = new ComeMessage(ActivityMainTestFunction.this, ActivityMainTestFunction.this);
                if (!comeMessage.isEnabled()) {
                    comeMessage.openSetting();
                    comeMessage.toggleNotificationListenerService();
                }
            }
        });

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        callListener = new PhoneCallListener();

        if (!PermissionUtils.isGranted(PermissionConstants.CONTACTS, PermissionConstants.PHONE)) {
            PermissionUtils.permission(PermissionConstants.CONTACTS, PermissionConstants.PHONE)
                    .rationale(new PermissionUtils.OnRationaleListener() {
                        @Override
                        public void rationale(final ShouldRequest shouldRequest) {
                            com.inso.watch.commonlib.utils.L.d("permission rationale");
                        }
                    })
                    .callback(new PermissionUtils.FullCallback() {
                        @Override
                        public void onGranted(List<String> permissionsGranted) {
                            com.inso.watch.commonlib.utils.L.d("permission onGranted");
                            ServiceUtils.startService("com.inso.plugin.act.vip.IncomingCall");
                        }

                        @Override
                        public void onDenied(List<String> permissionsDeniedForever,
                                             List<String> permissionsDenied) {
                            com.inso.watch.commonlib.utils.L.d("permission onDenied");
                        }
                    })
                    .request();
        }
        bindService();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != mRemoteAidlInterface) {
                    try {
                        L.d("mUsername = " + mRemoteAidlInterface.getPersonUserName() + ",mUserage = " + mRemoteAidlInterface.getPersonUserAge());
                        L.d("add " + mRemoteAidlInterface.add(1, 2));
                        ToastWidget.showSuccess(c,"mUsername = " + mRemoteAidlInterface.getPersonUserName() + ",mUserage = " + mRemoteAidlInterface.getPersonUserAge());
                        ToastWidget.showSuccess(c,"add " + mRemoteAidlInterface.add(1, 2));
                        mRemoteAidlInterface.show(new Person("ftc300","22 years old"));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        },2000);

    }


    private void unBindSevice() {
        unbindService(mServiceConnection);
        L.d("unbindService");
    }


    @Override
    public void comeShortMessage(String msg) {
        ToastWidget.showSuccess(c, "comeShortMessage" + msg);
        L.d("comeShortMessage", msg);
    }

    @Override
    public void comeWxMessage(String msg) {
        ToastWidget.showSuccess(c, "comeWxMessage" + msg);
        L.d("comeWxMessage", msg);
    }

    @Override
    public void comeQQmessage(String msg) {
        ToastWidget.showSuccess(c, "comeQQmessage" + msg);
        L.d("comeQQmessage", msg);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        comeMessage.unRegistBroadcast();
        unBindSevice();
    }

    /**
     * 监听来电状态
     */
    public class PhoneCallListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_OFFHOOK:                   //电话通话的状态
                    break;

                case TelephonyManager.CALL_STATE_RINGING:                   //电话响铃的状态
                    PhoneCallUtil.endPhone(ActivityMainTestFunction.this);
                    break;

            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private void init() {
        final String[] messages = new String[]{"两个黄鹂鸣翠柳，一行白鹭上青天。",
                "窗含西岭千秋雪，门泊东吴万里船。",
                "君不见，黄河之水天上来，奔流到海不复回；君不见，高堂明镜悲白发，朝如青丝暮如雪。"};
        button3 = (Button) this.findViewById(R.id.button3);
        button4 = (Button) this.findViewById(R.id.button4);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BaseDialogFragment.Builder(ActivityMainTestFunction.this)
                        .setContentView(R.layout.content_dialog)
                        .setContentViewOperator(new BaseDialogFragment.ContentViewOperator() {
                            @Override
                            public void operate(View contentView) {
                                EditText et = (EditText) contentView.findViewById(R.id.edit0);
                                et.setHint("hint set in operator");
                            }
                        })
//                      .setMessages(messages)
                        .setTitle("添加")
                        .setNegativeButton(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .setPositiveButton(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setPositiveButtonMultiListener(new BaseDialogFragment.OnMultiClickListener() {

                            @Override
                            public void onClick(View clickedView, View contentView) {
                                EditText et = (EditText) contentView.findViewById(R.id.edit0);
                                Toast.makeText(getApplicationContext(), "edittext 0 : " + et.getText(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButtonMultiListener(new BaseDialogFragment.OnMultiClickListener() {

                            @Override
                            public void onClick(View clickedView, View contentView) {
                                EditText et = (EditText) contentView.findViewById(R.id.edit1);
                                Toast.makeText(getApplicationContext(), "edittext 1 : " + et.getText(), Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setOnItemClickListener(new BaseDialogFragment.OnItemClickListener() {
                            @Override
                            public void onItemClicked(int index) {
                                if (index == 0) {
                                    Toast.makeText(getApplicationContext(), "index 0", Toast.LENGTH_SHORT).show();
                                } else if (index == 1) {
                                    Toast.makeText(getApplicationContext(), "index 1", Toast.LENGTH_SHORT).show();
                                } else if (index == 2) {
                                    Toast.makeText(getApplicationContext(), "index 2", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setWidthMaxDp(600)
                        .setShowButtons(true)
                        .create()
                        .show(ActivityMainTestFunction.this.getSupportFragmentManager(), "Base");
            }
        });


        final String MESSAGE_TAG = "BaseFragmentMessage";
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BaseDialogFragment.Builder(ActivityMainTestFunction.this)
                        .setMessages(messages)
                        .setTitle("一首古诗")
                        .setNegativeButton(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        })
                        .setPositiveButton(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getApplicationContext(), "positive", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setOnItemClickListener(new BaseDialogFragment.OnItemClickListener() {
                            @Override
                            public void onItemClicked(int index) {
                                Toast.makeText(getApplicationContext(), messages[index], Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setWidthMaxDp(600)
                        .setShowButtons(true)
                        .create()
                        .show(ActivityMainTestFunction.this.getSupportFragmentManager(), MESSAGE_TAG);
            }
        });
    }
}
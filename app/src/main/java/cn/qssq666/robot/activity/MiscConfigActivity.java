package cn.qssq666.robot.activity;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import javax.mail.MessagingException;

import cn.qssq666.robot.R;
import cn.qssq666.robot.asynctask.QssqTask;
import cn.qssq666.robot.business.RobotContentProvider;
import cn.qssq666.robot.config.MiscConfig;
import cn.qssq666.robot.constants.Cns;
import cn.qssq666.robot.constants.MsgTypeConstant;
import cn.qssq666.robot.databinding.ActivityMiscConfigBinding;
import cn.qssq666.robot.utils.AppUtils;
import cn.qssq666.robot.utils.DialogUtils;
import cn.qssq666.robot.utils.ParseUtils;
import cn.qssq666.robot.utils.ProxySendAlertUtil;

public class MiscConfigActivity extends SuperActivity {

    private cn.qssq666.robot.databinding.ActivityMiscConfigBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("杂项配置");
        binding = DataBindingUtil.setContentView(this, R.layout.activity_misc_config);
        SharedPreferences configSharePreferences = AppUtils.getConfigSharePreferences(getApplicationContext());
        binding.evOutCallMsgKeyword.setText(configSharePreferences.getString(Cns.MISC_TIP_KEYWORD, ""));
        binding.evSenderEmail.setText(configSharePreferences.getString(Cns.MISC_EMAIL_SENDER_EMAIL, ""));
        binding.evSenderEmailPwd.setText(configSharePreferences.getString(Cns.MISC_EMAIL_SENDER_EMAIL_PWD, ""));
        binding.evReceiverEmailAddress.setText(configSharePreferences.getString(Cns.MISC_EMAIL_RECEIVER_EMAIL, ""));
        binding.evEmailServerAddress.setText(configSharePreferences.getString(Cns.MISC_EMAIL_SERVER_ADDRESS, ""));
        binding.evEmailContent.setText(configSharePreferences.getString(Cns.MISC_EMAIL_CONTENT, ""));
        binding.evChatgptApikey.setText(configSharePreferences.getString(Cns.CHAT_GPT_API_SERCRET, ""));
        binding.evEmailPort.setText(configSharePreferences.getInt(Cns.MISC_EMAIL_SERVER_PORT, 25) + "");


        binding.cbMsgTip.setChecked(configSharePreferences.getBoolean(Cns.MISC_TIP_ENABLE, binding.cbMsgTip.isChecked()));
        binding.cbEanbleMailForward.setChecked(configSharePreferences.getBoolean(Cns.MISC_EMAIL_FORWARD_ENABLE, binding.cbEanbleMailForward.isChecked()));

        binding.evProxyRedirectAccount.setText(configSharePreferences.getString(Cns.PROXY_SEND_ACCOUNT, ""));
        binding.cbProxyRedirectAccountIsgroup.setChecked(configSharePreferences.getBoolean(Cns.PROXY_SEND_ACCOUNT_IS_GROUP, false));//  binding.evEmailContent.getText().toString());

        binding.btnSave.setOnClickListener((vv) -> {
            SharedPreferences.Editor edit = AppUtils.getConfigSharePreferences(getApplicationContext()).edit();
            edit.putString(Cns.MISC_TIP_KEYWORD, binding.evOutCallMsgKeyword.getText().toString());
            edit.putBoolean(Cns.MISC_TIP_ENABLE, binding.cbMsgTip.isChecked());
            edit.putBoolean(Cns.MISC_EMAIL_FORWARD_ENABLE, binding.cbEanbleMailForward.isChecked());
            edit.putBoolean(Cns.PROXY_SEND_ACCOUNT_IS_GROUP, binding.cbProxyRedirectAccountIsgroup.isChecked());
            edit.putString(Cns.MISC_EMAIL_SENDER_EMAIL, binding.evSenderEmail.getText().toString());
            edit.putString(Cns.MISC_EMAIL_SENDER_EMAIL_PWD, binding.evSenderEmailPwd.getText().toString());
            edit.putString(Cns.MISC_EMAIL_RECEIVER_EMAIL, binding.evReceiverEmailAddress.getText().toString());
            edit.putString(Cns.MISC_EMAIL_SERVER_ADDRESS, binding.evEmailServerAddress.getText().toString());
            edit.putInt(Cns.MISC_EMAIL_SERVER_PORT, ParseUtils.parseInt(binding.evEmailPort.getText().toString(), 25));
            edit.putString(Cns.MISC_EMAIL_CONTENT, binding.evEmailContent.getText().toString());
            edit.putString(Cns.PROXY_SEND_ACCOUNT, binding.evProxyRedirectAccount.getText().toString());
            edit.putString(Cns.CHAT_GPT_API_SERCRET, binding.evChatgptApikey.getText().toString());
            edit.commit();
            Uri uri = Uri.withAppendedPath(Uri.parse(MsgTypeConstant.AUTHORITY_CONTENT), RobotContentProvider.ACTION_UPDATE_MISC_CONFIG);
            ContentValues contentvalues = new ContentValues();
            this.getContentResolver().update(uri, contentvalues, null, null);
        });
        binding.btnTestEmail.setOnClickListener((v -> {
            new QssqTask<Object>(new QssqTask.ICallBack() {
                @Override
                public Object onRunBackgroundThread() {
                    MiscConfig miscConfig = RobotContentProvider.getInstance()._miscConfig;
                    try {
                        ProxySendAlertUtil.sendEmail(miscConfig.emailServer, miscConfig.emailPort, miscConfig.sender, miscConfig.senderPwd, miscConfig.receiver, miscConfig.emailContent);
                        return "发送完成";
                    } catch (Throwable e) {
                        return e;
                    }
                }

                @Override
                public void onRunFinish(Object o) {
                    if (o instanceof Throwable) {
                        Throwable o1 = (Throwable) o;
                        Log.e("MiscConfig", "发送失败" + o1.getMessage());
                        DialogUtils.showDialog(MiscConfigActivity.this, "发送失败" + o1.getMessage() + "," + Log.getStackTraceString(o1));
                    } else {
                        DialogUtils.showDialog(MiscConfigActivity.this, "结果:" + o);
                    }

                }
            }).execute();


        }));
    }
}
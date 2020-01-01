package com.sc.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.yzq.zxinglibrary.android.CaptureActivity;
import com.yzq.zxinglibrary.common.Constant;

import org.json.JSONArray;

import io.dcloud.common.DHInterface.IApp;
import io.dcloud.common.DHInterface.ISysEventListener;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.StandardFeature;
import io.dcloud.common.util.JSUtil;

public class ZXingFeature extends StandardFeature {

    private final static int REQUESTCODE = 1;
    public static final String TAG = "BaiduMapFeature";
    public void scan(final IWebview webview, JSONArray json) {

        try {
            Log.e(TAG, "开始扫描");
            final String callbackId = json.getString(0);
            final IApp _app = webview.obtainFrameView().obtainApp();
            _app.registerSysEventListener(new ISysEventListener() {
                @Override
                public boolean onExecute(SysEventType pEventType, Object pArgs) {

                    Object[] _args = (Object[]) pArgs;
                    int requestCode = (Integer) _args[0];
                    int resultCode = (Integer) _args[1];
                    Intent data = (Intent) _args[2];
                    if (pEventType == SysEventType.onActivityResult) {
                        _app.unregisterSysEventListener(this, SysEventType.onActivityResult);
                        if (requestCode == REQUESTCODE) {
                            if (resultCode == Activity.RESULT_OK) {
                                String resultString = "";

                                String content = data.getStringExtra(Constant.CODED_CONTENT);
                                resultString += "{\"result\":\"" + content + "\"}";
                                Log.e(TAG, "扫描结果： " +  resultString);
                                Log.e(TAG, "webview： " +  webview);
                                Log.e(TAG, "callbackId： " +  callbackId);
                                JSUtil.execCallback(webview, callbackId, resultString, JSUtil.OK, false);
                            }
                        }
                    }
                    return false;
                }

            }, SysEventType.onActivityResult);

            Intent intent = new Intent(webview.getContext(), CaptureActivity.class);
            webview.getActivity().startActivityForResult(intent, REQUESTCODE);
        }catch (Exception e) {

            e.printStackTrace();

        }
    }
}

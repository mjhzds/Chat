package com.elvis.example.chat.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.elvis.example.chat.R;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

public class OtherFragment extends Fragment {

    private Button logout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_other, container, false);
        logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(getActivity());
                String st = getResources().getString(R.string.Are_logged_out);
                pd.setMessage(st);
                pd.setCanceledOnTouchOutside(false);
                pd.show();
                EMClient.getInstance().logout(true, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                pd.dismiss();
                                // 重新显示登陆页面
                                getActivity().finish();
                                startActivity(new Intent(getActivity(), LoginActivity.class));

                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onError(int code, String message) {
                        // TODO Auto-generated method stub

                    }
                });
            }
        });
        return view;
    }
}

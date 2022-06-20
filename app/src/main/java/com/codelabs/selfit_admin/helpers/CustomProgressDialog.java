package com.codelabs.selfit_admin.helpers;

import android.content.Context;

import com.kaopiz.kprogresshud.KProgressHUD;

public class CustomProgressDialog {
    KProgressHUD kProgressHUD;

    public CustomProgressDialog(Context context){
        kProgressHUD = new KProgressHUD(context);
    }

    public void createProgress(){
        if (!kProgressHUD.isShowing()){
            kProgressHUD
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setAnimationSpeed(1)
                    .setDimAmount(0.5f)
                    .setCancellable(false)
                    .show();
        }
    }

    public void createTextProgress(String label){
        kProgressHUD
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel(label)
                .setAnimationSpeed(1)
                .setDimAmount(0.5f)
                .show();
    }

    public void dismissProgress(){
        if (kProgressHUD.isShowing()){
            kProgressHUD.dismiss();
        }
    }
}

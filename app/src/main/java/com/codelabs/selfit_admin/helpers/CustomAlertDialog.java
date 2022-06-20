package com.codelabs.selfit_admin.helpers;

import android.content.Context;
import android.content.DialogInterface;

import com.crowdfire.cfalertdialog.CFAlertDialog;

public class CustomAlertDialog {
    CFAlertDialog.Builder cfAlertDialog;

    public void positiveDismissAlert(Context context, String title, String description, CFAlertDialog.CFAlertStyle style){
        cfAlertDialog = new CFAlertDialog.Builder(context);
        cfAlertDialog
                .setDialogStyle(style)
                .setTitle(title + " \uD83C\uDF8A")
                .setMessage(description)
                .setAutoDismissAfter(1000)
                .show();
    }

    public void positiveAlert(Context context, String title, String description, String buttonTitle, CFAlertDialog.CFAlertStyle style){
        cfAlertDialog = new CFAlertDialog.Builder(context);
        cfAlertDialog
                .setDialogStyle(style)
                .setTitle(title + " \uD83C\uDF8A")
                .setMessage(description)
                .addButton(buttonTitle, -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    public void negativeDismissAlert(Context context, String title, String description, CFAlertDialog.CFAlertStyle style){
        cfAlertDialog = new CFAlertDialog.Builder(context);
        cfAlertDialog
                .setDialogStyle(style)
                .setTitle(title + " \uD83E\uDD2F")
                .setMessage(description)
                .setAutoDismissAfter(1000)
                .show();
    }

    public void negativeAlert(Context context, String title, String description, String buttonTitle, CFAlertDialog.CFAlertStyle style){
        cfAlertDialog = new CFAlertDialog.Builder(context);
        cfAlertDialog
                .setDialogStyle(style)
                .setTitle(title + " \uD83E\uDD2F")
                .setMessage(description)
                .addButton(buttonTitle, -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }
}

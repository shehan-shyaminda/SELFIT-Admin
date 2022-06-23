package com.codelabs.selfit_admin.adapters;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.codelabs.selfit_admin.R;
import com.codelabs.selfit_admin.models.PaymentHistory;

import java.util.ArrayList;

public class PaymentHistoryAdapter extends ArrayAdapter<PaymentHistory> {
    ArrayList<PaymentHistory> announcementsList;
    Activity mActivity;

    public PaymentHistoryAdapter(Activity activity, ArrayList<PaymentHistory> announcementsList) {
        super(activity, R.layout.row_history, announcementsList);
        this.mActivity = activity;
        this.announcementsList = announcementsList;
    }

    @Override
    public int getCount() {
        return announcementsList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View row = inflater.inflate(R.layout.row_history, null, true);

        TextView txtRef = row.findViewById(R.id.txt_his_ref);
        TextView txtEmail = row.findViewById(R.id.txt_his_name);
        TextView txtDate = row.findViewById(R.id.txt_his_date);
        TextView txtAmount = row.findViewById(R.id.txt_his_amount);

        try {
            txtRef.setText("Ref No. " + announcementsList.get(position).getTransRef());
            txtEmail.setText(announcementsList.get(position).getTrainersID());
            txtDate.setText(announcementsList.get(position).getTransDate().replace("/","-"));
            txtAmount.setText("LKR " + announcementsList.get(position).getTransAmount());
        } catch (Exception ex) {
            Log.e(TAG, "getView Error: " + ex.getLocalizedMessage());
        }

        return row;
    }
}
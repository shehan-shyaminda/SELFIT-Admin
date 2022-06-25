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
import com.codelabs.selfit_admin.models.UsersModel;

import java.util.ArrayList;

public class TrainerUsersAdapter  extends ArrayAdapter<UsersModel> {
    ArrayList<UsersModel> usersList;
    Activity mActivity;

    public TrainerUsersAdapter(Activity activity, ArrayList<UsersModel> usersList) {
        super(activity, R.layout.row_users, usersList);
        this.mActivity = activity;
        this.usersList = usersList;
    }

    @Override
    public int getCount() {
        return usersList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View row = inflater.inflate(R.layout.row_users, null, true);

        TextView txtName = row.findViewById(R.id.txt_users_name);
        TextView txtRegDate = row.findViewById(R.id.txt_users_regdate);

        try {
            txtName.setText(usersList.get(position).getUsersName());
            txtRegDate.setText("Uploaded Date:  " + usersList.get(position).getUsersRegDate().replace("/","-"));
        } catch (Exception ex) {
            Log.e(TAG, "getView Error: " + ex.getLocalizedMessage());
        }

        return row;
    }
}

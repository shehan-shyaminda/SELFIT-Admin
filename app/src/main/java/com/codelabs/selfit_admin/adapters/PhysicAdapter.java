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
import com.codelabs.selfit_admin.models.PhysicModel;
import com.codelabs.selfit_admin.models.UsersModel;

import java.util.ArrayList;

public class PhysicAdapter  extends ArrayAdapter<PhysicModel> {
    ArrayList<PhysicModel> physicList;
    Activity mActivity;

    public PhysicAdapter(Activity activity, ArrayList<PhysicModel> physicList) {
        super(activity, R.layout.row_physics, physicList);
        this.mActivity = activity;
        this.physicList = physicList;
    }

    @Override
    public int getCount() {
        return physicList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View row = inflater.inflate(R.layout.row_physics, null, true);

        TextView txtRegDate = row.findViewById(R.id.txt_physic_upload);

        try {
            txtRegDate.setText("Reg Date:  " + physicList.get(position).getUploadedDate().replace("/","-"));
        } catch (Exception ex) {
            Log.e(TAG, "getView Error: " + ex.getLocalizedMessage());
        }

        return row;
    }
}
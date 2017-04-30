package com.locationtracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;


import com.locationtracker.models.DurationModel;
import com.locationtracker.networkutils.interfaces.OnRetrofitResult;
import com.locationtracker.service.BackgroundLocationService;
import com.locationtracker.utils.Constants;

import java.util.List;

import retrofit2.Response;

/**
 * Created by rajesh on 30/4/17.
 */

public class BaseActivity extends AppCompatActivity implements OnRetrofitResult {

    public void displayAlert() {


        final AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("Network Error");
        alertDialog
                .setMessage("Please Check Your Internet Connection and Try Again");

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

//                        finish();
                        alertDialog.dismiss();
                    }
                });

        alertDialog.show();
    }


    public void displayCloseAlert() {


        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setTitle("App Closing");
        alertDialog
                .setMessage("Location is tracked in background,Click Notification bar to open app");

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {



                        finish();

                    }
                });

        alertDialog.show();
    }


    @Override
    public void onDurationResult(boolean result, Response<DurationModel> code) {

    }
}

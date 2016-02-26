package com.example.utsav.schooldemo.Utils;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.example.utsav.schooldemo.R;

/**
 * Created by utsav on 1/29/2016.
 */
public class HandleVolleyError {

    public  HandleVolleyError(VolleyError error, CoordinatorLayout coordinatorLayout) {
        //if any error occurs in the network operations, show the TextView that contains the error message
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            Snackbar.make(coordinatorLayout, R.string.error_timeout, Snackbar.LENGTH_LONG).show();

        } else if (error instanceof AuthFailureError) {
            Snackbar.make(coordinatorLayout, R.string.error_auth_failure, Snackbar.LENGTH_LONG).show();

            //TODO
        } else if (error instanceof ServerError) {
            Snackbar.make(coordinatorLayout, R.string.error_network, Snackbar.LENGTH_LONG).show();
            //TODO
        } else if (error instanceof NetworkError) {
            Snackbar.make(coordinatorLayout, R.string.error_network, Snackbar.LENGTH_LONG).show();
            //TODO
        } else if (error instanceof ParseError) {
            Snackbar.make(coordinatorLayout, R.string.error_parser, Snackbar.LENGTH_LONG).show();
            //TODO
        }
    }

}

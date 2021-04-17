package com.eassychat.home;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eassychat.BaseActivity;
import com.eassychat.R;
import com.eassychat.home.adapter.AllUserAdapter;
import com.eassychat.response.AllUsers;
import com.eassychat.response.GetAllUserResponse;
import com.eassychat.response.SignUpResponse;
import com.eassychat.retorfit.RetrofitClientInstance;
import com.eassychat.retorfit.methods.DataInterface;
import com.eassychat.utils.Loading_dialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends BaseActivity {

    private String TAG = Home.class.getSimpleName();
    private Call<GetAllUserResponse> alluser;
    private Loading_dialog loading_dialog;
    private ArrayList<AllUsers> allUsers;
    private RecyclerView allUserRV;
    private AllUserAdapter allUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        allUserRV = findViewById(R.id.allUserRV);
        getAllTheUsers();
    }

    private void setUpRv() {
        loading_dialog.hideDialog();
        allUserAdapter = new AllUserAdapter(allUsers,this);
        allUserRV.setLayoutManager(new LinearLayoutManager(this));
        allUserRV.setAdapter(allUserAdapter);

    }


    private void getAllTheUsers() {
        loading_dialog = new Loading_dialog(this);

        Log.e(TAG, "sigxnUp: insdide sigbup");
        DataInterface dataInterface = RetrofitClientInstance.retrofitwithheader.create(DataInterface.class);
         alluser = dataInterface.getAllUsers();
        alluser.enqueue(new Callback<GetAllUserResponse>() {
            @Override
            public void onResponse(Call<GetAllUserResponse> call, Response<GetAllUserResponse> response) {
                Log.e(TAG, "onResponse: " + response.toString());
                Log.e(TAG, "onResponse: " + response.errorBody());
                Log.e(TAG, "onResponse: " + response.message());
                GetAllUserResponse response1 = response.body();
                if (response1.getStatus().equals("success")) {
                      allUsers = response1.getAllUsers();
                    setUpRv();
                }
                if (response1.getStatus().equals("fail")) {
                    loading_dialog.hideDialog();
                    Toast.makeText(Home.this, "" + response1.getError(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetAllUserResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
            }
        });
    }
}
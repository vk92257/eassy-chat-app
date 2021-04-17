package com.eassychat.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eassychat.BaseActivity;
import com.eassychat.R;
import com.eassychat.home.Home;
import com.eassychat.response.Details;
import com.eassychat.response.SignUpResponse;
import com.eassychat.retorfit.APIConstance;
import com.eassychat.retorfit.RetrofitClientInstance;
import com.eassychat.retorfit.methods.DataInterface;
import com.eassychat.signup.SignUp;
import com.eassychat.utils.Loading_dialog;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.paperdb.Book;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends BaseActivity {

    private TextInputLayout emailInputLayout, userNameInputLayout, passwordInputLayout;
    TextView loginBTN, signUpHere;
    private String email;
    private String password;
    private boolean isValidEmailOrPhone = false;
    private static String TAG = Login.class.getSimpleName();
    private Loading_dialog loading_dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findViews();

    }


    public void signInClick(View view) {

        verifyingUserInputs();
//        startHomeActivity();

    }

    private void findViews() {
        emailInputLayout = findViewById(R.id.login_email_til);
        passwordInputLayout = findViewById(R.id.login_pwd_til);
        signUpHere = findViewById(R.id.login_singUp);
        clickListeners();
    }

    private void clickListeners() {
        emailInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (isEmail(editable.toString())) {
                    emailInputLayout.setErrorEnabled(false);
                    isValidEmailOrPhone = true;
                } else {
                    emailInputLayout.setError("Enter your valid email.");
                    emailInputLayout.requestFocus();
                    isValidEmailOrPhone = false;
                }
            }
        });
        signUpHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(Login.this, SignUp.class));
                finish();

            }
        });
    }

    public static boolean isEmail(String text) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern p = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    public static boolean isPhone(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (text.length() < 20) {
                return TextUtils.isDigitsOnly(text);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void verifyingUserInputs() {
        if (!TextUtils.isEmpty(emailInputLayout.getEditText().getText().toString())) {
            if (isValidEmailOrPhone) {
                emailInputLayout.setErrorEnabled(false);

            } else {
                return;
            }

        } else {
            emailInputLayout.setError("Enter your Email");
            emailInputLayout.requestFocus();
            return;
        }
        if (!TextUtils.isEmpty(passwordInputLayout.getEditText().getText().toString())) {
            passwordInputLayout.setErrorEnabled(false);
        } else {
            passwordInputLayout.setError("Enter your password");
            passwordInputLayout.requestFocus();
            return;
        }
        loginRequest();
    }

    public void signUpClick(View view) {
//        verifyingUserInputs();
//        loginPostRequest();
    }


    private void loginRequest() {
        loading_dialog = new Loading_dialog(this);
        loading_dialog.showDialog();
        Log.e(TAG, "sigxnUp: insdide sigbup");
        DataInterface dataInterface = RetrofitClientInstance.retrofit.create(DataInterface.class);
        Map<Object, String> body = new HashMap<>();

        body.put(APIConstance.EMAIL, emailInputLayout.getEditText().getText().toString().trim());
        body.put(APIConstance.PASSWORD, passwordInputLayout.getEditText().getText().toString().trim());
        Call<SignUpResponse> signUp = dataInterface.LogIn(body);

        signUp.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                Log.e(TAG, "onResponse: " + response.toString());
                Log.e(TAG, "onResponse: " + response.errorBody());
                Log.e(TAG, "onResponse: " + response.message());
                SignUpResponse response1 = response.body();
                if (response1.getStatus().equals("success")) {
                    Toast.makeText(Login.this, "" + response1.getMessage(), Toast.LENGTH_SHORT).show();
                    Book book = Paper.book();
                    Details details = response1.getDetails();
                    book.write(PAPER_EMAIL, details.getEmail());
                    book.write(PAPER_ID, details.getId());
                    book.write(PAPER_NAME, details.getName());
                    book.write(PAPER_PROFILE_PIC, details.getProfilePic());
                    book.write(PAPER_TOKEN, details.getToken());

                    startHomeActivity();
                }
                if (response1.getStatus().equals("fail")) {
                    loading_dialog.hideDialog();
                    Toast.makeText(Login.this, "" + response1.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void startHomeActivity() {
        loading_dialog.hideDialog();
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

}
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

import androidx.appcompat.app.AppCompatActivity;

import com.eassychat.BaseActivity;
import com.eassychat.R;
import com.eassychat.home.Home;
import com.eassychat.signup.SignUp;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends BaseActivity {

    private TextInputLayout emailInputLayout, userNameInputLayout, passwordInputLayout;
    TextView loginBTN, signUpHere;
    private String  email;
    private String password;
    private boolean isValidEmailOrPhone = false;
    private static String TAG = Login.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);




//        previousLoginCheck();
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
        loginPostRequest();
    }

    public void signUpClick(View view) {
//        verifyingUserInputs();
//        loginPostRequest();
    }


    private void loginPostRequest() {
        startHomeActivity();
//        loading_dialog.showDialog();
//        ApiInterface apiInterface = ApiConstance.retrofit.create(ApiInterface.class);
//        Map<String, Object> body = new HashMap<>();
//        body.put(ConstantString.EMAIL, emailInputLayout.getEditText().getText().toString());
//        body.put(ConstantString.PASSWORD, passwordInputLayout.getEditText().getText().toString());
//        call = apiInterface.login(body);
//        call.enqueue(new Callback<LoginResponse>() {
//            @Override
//            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
//                Log.e(TAG, "onResponse: login  " + response);
//                if (response.code() == 200) {
//                    if (response.isSuccessful()) {
//                        LoginResponse loginResponse = response.body();
////                                Log.e(TAG, "onResponse:  dfsdf " + response.toString()+" rsponse message-> "+response.body().getMessage()+
////                                        " \n\n "+response.body()+" \\n resonse status-> "+response.body().getStatus()+
////                                        " response detail-> "+response.body().getDetails());
//                        if (loginResponse.isError()) {
//                            Log.e(TAG, "\n\n\n\n\nonResponse: getmessage-> " + loginResponse.getMessage());
//                            Toast.makeText(Login.this, "" + loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                            loading_dialog.hideDialog();
//                        } else {
//                            LoginResponse.Details details = loginResponse.getDetails();
//                            Log.e(TAG, "\n\n\n\n\n\nonResponse: " + details.getName() + "  \n\n\n\ntoken-> " + details.getToken());
//                            Book book = Paper.book();
//                            book.write(ConstantString.PAPER_TOKEN, details.getToken());
//                            book.write(ConstantString.PAPER_ID, details.getId());
//                            book.write(ConstantString.PAPER_USER_NAME, details.getName());
//                            book.write(ConstantString.PAPER_LOGIN_POJO, details);
//                            startHomeActivity();
//                            loading_dialog.hideDialog();
//                        }
//
//                    } else {
//                        Log.e(TAG, "onResponse: error --->" + TextStreamsKt.readText(response.errorBody().charStream()));
//                        loading_dialog.hideDialog();
//                    }
//                } else {
//                    if (response.errorBody() != null) {
//                        Toast.makeText(Login.this, "" + TextStreamsKt.readText(response.errorBody().charStream()), Toast.LENGTH_SHORT).show();
//                        Log.e(TAG, "onResponse: login errror body " + response.toString());
//                        loading_dialog.hideDialog();
//                    }
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginResponse> call, Throwable t) {
//                Log.e(TAG, "onFailure: login " + call.toString());
//                loading_dialog.hideDialog();
//                Toast.makeText(Login.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void startHomeActivity() {

        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }

}
package com.eassychat.signup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eassychat.BaseActivity;
import com.eassychat.R;
import com.eassychat.home.Home;
import com.eassychat.login.Login;
import com.eassychat.response.Details;
import com.eassychat.response.SignUpResponse;
import com.eassychat.retorfit.APIConstance;
import com.eassychat.retorfit.RetrofitClientInstance;
import com.eassychat.retorfit.methods.DataInterface;
import com.eassychat.utils.Loading_dialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Book;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends BaseActivity {

    private static final int RC_SIGN_IN = 45;
    private TextInputLayout emailInputLayout, userNameInputLayout, passwordInputLayout;
    TextView signUpBTN, loginHere;
    private boolean isValidEmailOrPhone = false;
    private String TAG = SignUp.class.getSimpleName();
    private static final String EMAIL = "email";
    private LinearLayout loginButton;
    private static final String EMAILS = "email";
    private CircleImageView profileImage;
    private String pswd;
    private String email;
    private String profilePic;
    private String password;
    private int status;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference original = storageRef.child("original");
    StorageReference thumbnail = storageRef.child("thumbnail");
    public static final int PICK_IMAGE = 1;
    private static final int LOGIN_FROM_WITT = 0;
    private static final int LOGIN_FROM_GOOGLE = 1;
    private static final int LOGIN_FROM_FACEBOOK = 2;
    private byte[] data1;
    private byte[] data1Sml;
    private Uri thubnailURL;
    private Uri orignalURL;
    private Loading_dialog loading_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        status = LOGIN_FROM_WITT;
        pswd = "12345678";

//        GetHASHKey.printHashKey(this);
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode());


        findViews();
    }


    private void SignUpPostRequest() {
//        loading_dialog.showDialog();
//        ApiInterface apiInterface = ApiConstance.retrofit.create(ApiInterface.class);
//        try {
//            Map<String, Object> requestBody = new HashMap<>();
//            requestBody.put(ConstantString.NAME, userNameInputLayout.getEditText().getText().toString());
//            requestBody.put(ConstantString.EMAIL, emailInputLayout.getEditText().getText().toString());
////            requestBody.put(ConstantString.PASSWORD, passwordInputLayout.getEditText().getText().toString());
//            requestBody.put(ConstantString.PASSWORD, pswd);
//            call = apiInterface.signup(requestBody);
//            call.enqueue(new Callback<SignupResponse>() {
//                @Override
//                public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
////                    progressBarLayout.setVisibility(View.VISIBLE);
//                    Log.e(TAG, "onResponse:response code-> " + response);
//                    if (response.code() == 200) {
//                        if (response.isSuccessful()) {
//                            SignupResponse signupResponse = response.body();
////                                Log.e(TAG, "onResponse:  dfsdf " + response.toString()+" rsponse message-> "+response.body().getMessage()+
////                                        " \n\n "+response.body()+" \\n resonse status-> "+response.body().getStatus()+
////                                        " response detail-> "+response.body().getDetails());
//                            if (signupResponse.isError()) {
//                                Log.e(TAG, "\n\n\n\n\nonResponse: getmessage-> " + signupResponse.getMessage());
//                                Toast.makeText(SignUp.this, "" + signupResponse.getMessage(), Toast.LENGTH_SHORT).show();
//                                passwordInputLayout.getEditText().setText("");
//                                status = LOGIN_FROM_WITT;
//                                loading_dialog.hideDialog();
//                            } else {
//                                SignupResponse.Details details = signupResponse.getDetails();
//                                Log.e(TAG, "\n\n\n\n\n\nonResponse: " + details.getName() + "  \n\n\n\ntoken-> " + details.getToken());
//                                startLoginActivity(status);
//
//                            }
//
//                        } else {
//                            Log.e(TAG, "onResponse: error --->" + TextStreamsKt.readText(response.errorBody().charStream()));
//                            loading_dialog.hideDialog();
//                            passwordInputLayout.getEditText().setText("");
//                            Toast.makeText(SignUp.this, "" + TextStreamsKt.readText(response.errorBody().charStream()), Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<SignupResponse> call, Throwable t) {
//                    Log.e(TAG, "onFailure: " + t.getMessage());
//                    passwordInputLayout.getEditText().setText("");
//                    Toast.makeText(SignUp.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                    loading_dialog.hideDialog();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    private void startLoginActivity() {
        Intent login = new Intent(this, Login.class);

        startActivity(login);

        finish();
    }

    private void findViews() {
        profileImage = findViewById(R.id.profile_image);
        emailInputLayout = findViewById(R.id.signUp_email_til);
        userNameInputLayout = findViewById(R.id.signUp_userName_til);
        passwordInputLayout = findViewById(R.id.SignUp_pwd_til);
        loginHere = findViewById(R.id.signUp_login_here);
        clickListeners();

    }

    private void clickListeners() {

        profileImage.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        });

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
                    emailInputLayout.setError("Enter your valid email ");
                    emailInputLayout.requestFocus();
                    isValidEmailOrPhone = false;
                }
            }
        });
        loginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(SignUp.this, Login.class));
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE) {
                //TODO: action
                Uri uri = data.getData();
                profilePic = uri.toString();
//                profileImage.setImageURI(Uri.parse(profilePic));
                /** getting the stream for the get intent data */
                try {
                    InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                    Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                    profileImage.setImageBitmap(bmp);
                    Log.e(TAG, "onActivityResult: size  " + bmp.getDensity());

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 40, outputStream);
                    data1 = outputStream.toByteArray();
                    Log.e(TAG, "onActivityResult: size 90  " + data1.length);


                    InputStream inputStreamsSml = this.getContentResolver().openInputStream(data.getData());
                    Bitmap bmpSml = BitmapFactory.decodeStream(inputStreamsSml);

                    Log.e(TAG, "onActivityResult: size  " + bmpSml.getDensity());

                    ByteArrayOutputStream outputStreamSml = new ByteArrayOutputStream();
                    bmpSml.compress(Bitmap.CompressFormat.JPEG, 10, outputStreamSml);
                    data1Sml = outputStream.toByteArray();
                    Log.e(TAG, "onActivityResult: size 10  " + data1Sml.length);


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...

            }

        }
    }


    public void signUpClick(View view) {
        verifyingUserInputs();

    }

    private void startHomeActivity() {
       loading_dialog.hideDialog();
        Intent intent = new Intent(this, Home.class);
        startActivity(intent);
    }

    private void verifyingUserInputs() {
        if (TextUtils.isEmpty(profilePic)) {
            toast(this, "please Insert your profile image");
            return;
        }
        if (!TextUtils.isEmpty(passwordInputLayout.getEditText().getText().toString()) || !TextUtils.isEmpty(pswd)) {
            passwordInputLayout.setErrorEnabled(false);
            if (!TextUtils.isEmpty(passwordInputLayout.getEditText().getText().toString()))
                pswd = passwordInputLayout.getEditText().getText().toString();
        } else {
            passwordInputLayout.setError("Enter your password");
            passwordInputLayout.requestFocus();
            return;
        }
        Log.e(TAG, "verifyingUserInputs: at the start");
        if (!TextUtils.isEmpty(userNameInputLayout.getEditText().getText().toString())) {
            userNameInputLayout.setErrorEnabled(false);

        } else {
            userNameInputLayout.setError("Enter your user name");
            userNameInputLayout.requestFocus();
            return;
        }
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
//        sigxnUp();
        uploadOriginalImage(data1);

        Log.e(TAG, "verifyingUserInputs: at the end");
       

//        Toast.makeText(this, "sign up", Toast.LENGTH_SHORT).show();
//        startHomeActivity();
    }

    private void uploadOriginalImage(byte[] data1) {
        loading_dialog =new Loading_dialog(this);
        loading_dialog.showDialog();
        StorageReference mountainImagesRef = original.child("images-" + new Date().toString() + ".jpg");
        mountainImagesRef.putBytes(data1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                         orignalURL = uri;
                        Log.e(TAG, "onSuccess: "+orignalURL );
                        //Do what you want with the url
//                        uploadThumbnail(data1Sml);
                        Log.e(TAG, "onSuccess: orignal image ");
                        sigxnUp();

                    }


                });
            }
        });
    }

    private void sigxnUp() {

        Log.e(TAG, "sigxnUp: insdide sigbup" );
        DataInterface dataInterface = RetrofitClientInstance.retrofit.create(DataInterface.class);
        Map<Object ,String >  body  = new HashMap<>();
        body.put(APIConstance.NAME,userNameInputLayout.getEditText().getText().toString().trim());
        body.put(APIConstance.EMAIL,emailInputLayout.getEditText().getText().toString().trim());
        body.put(APIConstance.PROFILE_PIC,orignalURL.toString());
        body.put(APIConstance.PASSWORD,passwordInputLayout.getEditText().getText().toString().trim());
        Call<SignUpResponse> signUp = dataInterface.SignUp(body);

        signUp.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                Log.e(TAG, "onResponse: "+response.toString() );
                Log.e(TAG, "onResponse: "+response.errorBody() );
                Log.e(TAG, "onResponse: "+response.message() );
                SignUpResponse  response1 = response.body();
                if (response1.getStatus().equals("success")){
                    Toast.makeText(SignUp.this, ""+response1.getMessage(), Toast.LENGTH_SHORT).show();
                   Book book =  Paper.book();
                    Details  details = response1.getDetails();
                    book.write(PAPER_EMAIL,details.getEmail());
                    book.write(PAPER_ID,details.getId());
                    book.write(PAPER_NAME,details.getName());
                    book.write(PAPER_PROFILE_PIC,details.getProfilePic());
                    book.write(PAPER_TOKEN,details.getToken());

                    startHomeActivity();
                }
                if (response1.getStatus().equals("fail")){
                 loading_dialog.hideDialog();
                    Toast.makeText(SignUp.this, ""+response1.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: "+t.toString() );
            }
        });
    }

    private void uploadThumbnail(byte[] data1Sml) {
        StorageReference mountainImagesRef = thumbnail.child("image-" + new Date().toString() + ".jpg");
        mountainImagesRef.putBytes(data1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                         thubnailURL = uri;
                        //Do what you want with the url
                        SignUpPostRequest();

                        Log.e(TAG, "onSuccess: small image ");
                    }


                });
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
            return TextUtils.isDigitsOnly(text);
        } else {
            return false;
        }
    }
}
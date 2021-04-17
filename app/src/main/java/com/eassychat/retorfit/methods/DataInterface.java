package com.eassychat.retorfit.methods;

import com.eassychat.response.GetAllUserResponse;
import com.eassychat.response.SignUpResponse;
import com.eassychat.retorfit.URL;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface DataInterface {

    @POST(URL.SIGN_UP)
    Call<SignUpResponse> SignUp(@Body Map<Object, String> body);
    @POST(URL.LOGIN)
    Call<SignUpResponse> LogIn(@Body Map<Object, String> body);
    @GET(URL.ALL_USERS)
    Call<GetAllUserResponse> getAllUsers();
}

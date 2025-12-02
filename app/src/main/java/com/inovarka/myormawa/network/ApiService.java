package com.inovarka.myormawa.network;

import com.inovarka.myormawa.models.ApiResponse;
import com.inovarka.myormawa.models.ApiResponseList;
import com.inovarka.myormawa.models.ApiResponseSingle;
import com.inovarka.myormawa.models.CalendarEvent;
import com.inovarka.myormawa.models.ChangeEmailRequest;
import com.inovarka.myormawa.models.ChangePasswordRequest;
import com.inovarka.myormawa.models.Event;
import com.inovarka.myormawa.models.ForgotPasswordRequest;
import com.inovarka.myormawa.models.LoginRequest;
import com.inovarka.myormawa.models.LoginResponse;
import com.inovarka.myormawa.models.Organization;
import com.inovarka.myormawa.models.RegisterRequest;
import com.inovarka.myormawa.models.ResendOtpRequest;
import com.inovarka.myormawa.models.ResetPasswordRequest;
import com.inovarka.myormawa.models.VerifyOtpRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    // AUTH ENDPOINTS
    @POST("auth.php")
    Call<ApiResponse> register(@Body RegisterRequest request);

    @POST("auth.php")
    Call<ApiResponse> verifyOtp(@Body VerifyOtpRequest request);

    @POST("auth.php")
    Call<ApiResponse> resendOtp(@Body ResendOtpRequest request);

    @POST("auth.php")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("auth.php")
    Call<ApiResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("auth.php")
    Call<ApiResponse> resetPassword(@Body ResetPasswordRequest request);

    @POST("auth.php")
    Call<ApiResponse> changeEmail(@Body ChangeEmailRequest request);

    @POST("auth.php")
    Call<ApiResponse> changePassword(@Body ChangePasswordRequest request);

    // ORMAWA ENDPOINTS
    @GET("ormawa.php")
    Call<ApiResponseList<Organization>> getAllOrganizations();

    @GET("ormawa.php")
    Call<ApiResponseSingle<Organization>> getOrganizationById(@Query("id") String id);

    // EVENT ENDPOINTS
    @GET("event.php")
    Call<ApiResponseList<Event>> getAllEvents();

    @GET("event.php")
    Call<ApiResponseSingle<Event>> getEventById(@Query("id") String id);

    // CALENDAR ENDPOINTS
    @GET("calendar.php")
    Call<ApiResponseList<CalendarEvent>> getCalendarEvents();
}
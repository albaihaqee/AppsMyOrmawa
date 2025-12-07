package com.inovarka.myormawa.network;

import com.inovarka.myormawa.models.ApiResponse;
import com.inovarka.myormawa.models.ApiResponseList;
import com.inovarka.myormawa.models.ApiResponseSingle;
import com.inovarka.myormawa.models.AttendanceData;
import com.inovarka.myormawa.models.AttendanceRequest;
import com.inovarka.myormawa.models.CalendarEvent;
import com.inovarka.myormawa.models.ChangeEmailRequest;
import com.inovarka.myormawa.models.ChangePasswordRequest;
import com.inovarka.myormawa.models.Competition;
import com.inovarka.myormawa.models.Document;
import com.inovarka.myormawa.models.Event;
import com.inovarka.myormawa.models.EventMember;
import com.inovarka.myormawa.models.FileUploadData;
import com.inovarka.myormawa.models.ForgotPasswordRequest;
import com.inovarka.myormawa.models.FormInfo;
import com.inovarka.myormawa.models.FormSubmitRequest;
import com.inovarka.myormawa.models.LoginRequest;
import com.inovarka.myormawa.models.LoginResponse;
import com.inovarka.myormawa.models.Meeting;
import com.inovarka.myormawa.models.Member;
import com.inovarka.myormawa.models.Organization;
import com.inovarka.myormawa.models.RegisterRequest;
import com.inovarka.myormawa.models.ResendOtpRequest;
import com.inovarka.myormawa.models.ResetPasswordRequest;
import com.inovarka.myormawa.models.Scholarship;
import com.inovarka.myormawa.models.VerifyOtpRequest;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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


    // ATTENDANCE ENDPOINTS
    @POST("attendance.php")
    Call<ApiResponseSingle<AttendanceData>> verifyQRCode(@Body AttendanceRequest request);

    @POST("attendance.php")
    Call<ApiResponseSingle<AttendanceData>> checkInAttendance(@Body AttendanceRequest request);

    @GET("attendance.php")
    Call<ApiResponseList<AttendanceData>> getUserAttendanceHistory(
            @Query("action") String action,
            @Query("user_id") String userId
    );


    // EVENT ENDPOINTS
    @GET("event.php")
    Call<ApiResponseList<Event>> getAllEvents();

    @GET("event.php")
    Call<ApiResponseSingle<Event>> getEventById(@Query("id") String id);

    @GET("event.php")
    Call<ApiResponseList<EventMember>> getEventsByOrmawa(@Query("ormawa_id") String ormawaId);

    @GET("event.php")
    Call<ApiResponseSingle<EventMember>> getEventDetail(@Query("id") String id);


    // MEETING / KEGIATAN ENDPOINTS
    @GET("kegiatan.php")
    Call<ApiResponseList<Meeting>> getKegiatanByOrmawa(@Query("id_ormawa") String ormawaId);


    // MEMBER ENDPOINTS
    @GET("member.php")
    Call<ApiResponseList<Member>> getMembersByOrmawa(@Query("id_ormawa") String idOrmawa);

    @GET("documents.php")
    Call<ApiResponseList<Document>> getDocumentsByOrmawa(@Query("id_ormawa") String idOrmawa);


    // COMPETITION ENDPOINTS
    @GET("competition.php")
    Call<ApiResponseList<Competition>> getAllCompetitions();

    @GET("competition.php")
    Call<ApiResponseSingle<Competition>> getCompetitionById(@Query("id") String id);


    // SCHOLARSHIP ENDPOINTS
    @GET("scholarship.php")
    Call<ApiResponseList<Scholarship>> getAllScholarships();

    @GET("scholarship.php")
    Call<ApiResponseSingle<Scholarship>> getScholarshipById(@Query("id") String id);


    // CALENDAR ENDPOINTS
    @GET("calendar.php")
    Call<ApiResponseList<CalendarEvent>> getCalendarEvents();


    // FORM BUILDER ENDPOINTS
    @GET("form_builder_api.php?action=get_forms")
    Call<ApiResponseList<FormInfo>> getFormsByType(@Query("jenis_form") String jenisForm);

    @GET("form_builder_api.php?action=get_form_detail")
    Call<ApiResponseSingle<FormInfo>> getFormDetail(@Query("form_id") String formId);

    @POST("form_builder_api.php?action=submit_form")
    Call<ApiResponse> submitForm(@Body FormSubmitRequest request);

    @GET("form_builder_api.php?action=get_user_submissions")
    Call<ApiResponseList<FormInfo>> getUserSubmissions(
            @Query("user_id") String userId,
            @Query("jenis_form") String jenisForm
    );

    @Multipart
    @POST("form_builder_api.php?action=upload_file")
    Call<ApiResponseSingle<FileUploadData>> uploadFile(
            @Part("field_id") RequestBody fieldId,
            @Part("form_id") RequestBody formId,
            @Part("user_id") RequestBody userId,
            @Part MultipartBody.Part file
    );
}

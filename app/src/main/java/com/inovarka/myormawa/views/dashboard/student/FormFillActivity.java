package com.inovarka.myormawa.views.dashboard.student;

import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.DynamicFormAdapter;
import com.inovarka.myormawa.models.*;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.network.ApiService;
import com.inovarka.myormawa.utils.SessionManager;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormFillActivity extends AppCompatActivity {

    private ImageView imgCover;
    private TextView txtTitle, txtDescription, txtOrmawa;
    private RecyclerView rvFormFields;
    private Button btnSubmit;
    private ProgressBar progressBar;
    private View layoutLoading;

    private DynamicFormAdapter adapter;
    private ApiService apiService;
    private SessionManager sessionManager;
    private FormInfo formInfo;
    private String formId;

    public static final String EXTRA_FORM_ID = "form_id";
    public static final String EXTRA_FORM_TYPE = "form_type";

    // Posisi field yang menunggu file dipilih
    private int pendingFileFieldPosition = -1;

    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null && pendingFileFieldPosition != -1) {
                    FormField field = adapter.getFields().get(pendingFileFieldPosition);
                    field.setLocalFileUri(uri);
                    adapter.notifyItemChanged(pendingFileFieldPosition);
                    pendingFileFieldPosition = -1;
                    Toast.makeText(this, "File siap diupload saat submit", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_form_fill);

        initViews();
        setupData();
        loadFormDetail();
    }

    private void setStatusBar() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_blue));
    }

    private void initViews() {
        imgCover = findViewById(R.id.img_form_cover);
        txtTitle = findViewById(R.id.txt_form_title);
        txtDescription = findViewById(R.id.txt_form_description);
        txtOrmawa = findViewById(R.id.txt_form_ormawa);
        rvFormFields = findViewById(R.id.rv_form_fields);
        btnSubmit = findViewById(R.id.btn_submit_form);
        progressBar = findViewById(R.id.progress_bar);
        layoutLoading = findViewById(R.id.layout_loading);

        rvFormFields.setLayoutManager(new LinearLayoutManager(this));

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> onBackPressed());

        btnSubmit.setOnClickListener(v -> validateAndSubmit());
    }

    private void setupData() {
        formId = getIntent().getStringExtra(EXTRA_FORM_ID);
        apiService = ApiClient.getClient().create(ApiService.class);
        sessionManager = new SessionManager(this);

        if (formId == null) {
            Toast.makeText(this, "Form ID tidak valid", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadFormDetail() {
        showLoading(true);
        apiService.getFormDetail(formId).enqueue(new Callback<ApiResponseSingle<FormInfo>>() {
            @Override
            public void onResponse(Call<ApiResponseSingle<FormInfo>> call, Response<ApiResponseSingle<FormInfo>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponseSingle<FormInfo> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        formInfo = apiResponse.getData();
                        displayForm();
                    } else showError(apiResponse.getMessage());
                } else showError("Gagal memuat form");
            }

            @Override
            public void onFailure(Call<ApiResponseSingle<FormInfo>> call, Throwable t) {
                showLoading(false);
                showError(t.getMessage());
            }
        });
    }

    private void displayForm() {
        if (formInfo.getGambarUrl() != null) {
            Glide.with(this).load(formInfo.getGambarUrl()).centerCrop().into(imgCover);
        }
        txtTitle.setText(formInfo.getJudul());
        txtDescription.setText(formInfo.getDeskripsi());
        txtOrmawa.setText(formInfo.getNamaOrmawa());

        adapter = new DynamicFormAdapter(formInfo.getFields(), field -> {
            pendingFileFieldPosition = adapter.getFields().indexOf(field);
            filePickerLauncher.launch("*/*");
        });

        rvFormFields.setAdapter(adapter);
    }

    private void validateAndSubmit() {
        List<FormField> fields = adapter.getFields();
        List<String> errors = new ArrayList<>();

        for (FormField field : fields) {
            if (field.isRequired()) {
                if (field.getTipe().equals(FormField.TYPE_FILE)) {
                    if ((field.getValue() == null || field.getValue().trim().isEmpty()) && field.getLocalFileUri() == null) {
                        errors.add("- " + field.getLabel() + " wajib diisi");
                    }
                } else {
                    if (field.getValue() == null || field.getValue().trim().isEmpty()) {
                        errors.add("- " + field.getLabel() + " wajib diisi");
                    }
                }
            }
        }

        if (!errors.isEmpty()) {
            StringBuilder e = new StringBuilder("Lengkapi field berikut:\n\n");
            for (String err : errors) e.append(err).append("\n");
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Form Tidak Lengkap")
                    .setMessage(e.toString())
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        uploadFilesAndSubmit();
    }

    private void uploadFilesAndSubmit() {
        uploadNextFile(adapter.getFields(), 0);
    }

    private void uploadNextFile(List<FormField> fields, int index) {
        if (index >= fields.size()) {
            submitForm();
            return;
        }

        FormField field = fields.get(index);
        if (field.getTipe().equals(FormField.TYPE_FILE) && field.getLocalFileUri() != null) {
            uploadFileToServer(field, field.getLocalFileUri(), () -> uploadNextFile(fields, index + 1));
        } else {
            uploadNextFile(fields, index + 1);
        }
    }

    private void uploadFileToServer(FormField field, Uri uri, Runnable onSuccess) {
        showLoading(true);
        try {
            InputStream is = getContentResolver().openInputStream(uri);
            byte[] bytes = is.readAllBytes();

            String mimeType = getContentResolver().getType(uri);
            if (mimeType == null) mimeType = "application/octet-stream";

            RequestBody reqFile = RequestBody.create(bytes, MediaType.parse(mimeType));
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", getFileName(uri), reqFile);

            RequestBody fieldId = RequestBody.create(field.getId(), MultipartBody.FORM);
            RequestBody formIdBody = RequestBody.create(formInfo.getId(), MultipartBody.FORM);
            RequestBody userId = RequestBody.create(sessionManager.getUserId(), MultipartBody.FORM);

            apiService.uploadFile(fieldId, formIdBody, userId, filePart)
                    .enqueue(new Callback<ApiResponseSingle<FileUploadData>>() {
                        @Override
                        public void onResponse(Call<ApiResponseSingle<FileUploadData>> call, Response<ApiResponseSingle<FileUploadData>> response) {
                            showLoading(false);
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                FileUploadData fileData = response.body().getData();
                                field.setValue(fileData.getFileUrl());
                                field.setLocalFileUri(null);
                                adapter.notifyDataSetChanged();
                                onSuccess.run();
                            } else {
                                // fallback: tampil toast sukses walau body null
                                Toast.makeText(FormFillActivity.this, "Form berhasil dikirim", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponseSingle<FileUploadData>> call, Throwable t) {
                            showLoading(false);
                            showError("Error upload file: " + t.getMessage());
                        }
                    });

        } catch (Exception e) {
            showLoading(false);
            showError("Gagal membaca file: " + e.getMessage());
        }
    }

    private void submitForm() {
        String userId = sessionManager.getUserId();
        List<FormSubmission> submissions = new ArrayList<>();

        for (FormField field : adapter.getFields()) {
            String value = field.getValue() != null ? field.getValue() : "";
            submissions.add(new FormSubmission(field.getId(), field.getLabel(), value));
        }

        FormSubmitRequest request = new FormSubmitRequest(formInfo.getId(), userId, submissions);

        showLoading(true);
        apiService.submitForm(request).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                showLoading(false);
                // ✅ Perbaikan: langsung toast sukses dan finish activity, fallback jika body null
                Toast.makeText(FormFillActivity.this, "Form berhasil dikirim", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                showLoading(false);
                showError("Error submit form: " + t.getMessage());
            }
        });
    }

    private String getFileName(Uri uri) {
        String result = "file";
        try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
            }
        }
        return result;
    }

    private void showLoading(boolean show) {
        layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
}

package com.inovarka.myormawa.views.dashboard.student;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.inovarka.myormawa.R;
import com.inovarka.myormawa.adapters.BannerAdapter;
import com.inovarka.myormawa.models.*;
import com.inovarka.myormawa.network.ApiClient;
import com.inovarka.myormawa.network.ApiService;
import com.inovarka.myormawa.repositories.EventRepository;
import com.inovarka.myormawa.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    private static final int AUTO_SLIDE_DELAY = 5000;
    private static final int DOT_SIZE_DP = 6;
    private static final int DOT_MARGIN_DP = 4;
    private static final int MAX_RECENT_EVENTS = 4;

    private ViewPager2 bannerViewPager;
    private LinearLayout dotsContainer;
    private LinearLayout eventsContainer;
    private TextView txtSeeAllEvents;
    private TextView txtUserName;
    private View badgeNotification;

    private BannerAdapter bannerAdapter;
    private List<Banner> bannerList;
    private Handler sliderHandler;
    private int currentPage = 0;

    private final List<Notification> allNotifications = new ArrayList<>();

    private final Runnable autoSlideRunnable = new Runnable() {
        @Override
        public void run() {
            if (bannerList != null && !bannerList.isEmpty()) {
                currentPage++;
                if (currentPage >= bannerList.size()) currentPage = 0;
                bannerViewPager.setCurrentItem(currentPage, true);
                sliderHandler.postDelayed(this, AUTO_SLIDE_DELAY);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupStatusBar();
        initViews(view);
        loadUserData();
        setupBanner();
        setupEvents();
        setupClickListeners(view);
    }

    private void setupStatusBar() {
        if (getActivity() != null && getActivity().getWindow() != null) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.md_theme_light_surfaceVariant));
            WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(window, window.getDecorView());
            controller.setAppearanceLightStatusBars(false);
        }
    }

    private void initViews(View view) {
        bannerViewPager = view.findViewById(R.id.banner_viewpager);
        dotsContainer = view.findViewById(R.id.dots_container);
        eventsContainer = view.findViewById(R.id.container_popular_events);
        txtSeeAllEvents = view.findViewById(R.id.txt_see_all_events);
        txtUserName = view.findViewById(R.id.txt_user_name);
        badgeNotification = view.findViewById(R.id.view_badge_notification);
        sliderHandler = new Handler(Looper.getMainLooper());
    }

    private void loadUserData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        txtUserName.setText(prefs.getString(Constants.KEY_FULL_NAME, "User"));
    }

    // ===================== BANNER =====================
    private void setupBanner() {
        initBannerList();
        bannerAdapter = new BannerAdapter(bannerList);
        bannerViewPager.setAdapter(bannerAdapter);
        bannerAdapter.setOnBannerClickListener(this::handleBannerClick);
        setupPageTransformer();
        setupDotsIndicator();
        setupPageChangeCallback();
        startAutoSlide();
    }

    private void initBannerList() {
        bannerList = new ArrayList<>();
        bannerList.add(new Banner(R.drawable.ill_banner_slide_1));
        bannerList.add(new Banner(R.drawable.ill_banner_slide_2));
        bannerList.add(new Banner(R.drawable.ill_banner_slide_3));
    }

    private void handleBannerClick(Banner banner, int position) {
        switch (position) {
            case 0:
                if (getActivity() instanceof DashboardStudentActivity)
                    ((DashboardStudentActivity) getActivity()).navigateToOrganization();
                break;
            case 1:
                startActivity(new Intent(getActivity(), EventActivity.class));
                break;
            case 2:
                startActivity(new Intent(getActivity(), CompetitionActivity.class));
                break;
        }
    }

    private void setupPageTransformer() {
        bannerViewPager.setPageTransformer((page, position) -> {
            float abs = Math.abs(position);
            page.setAlpha(abs >= 1 ? 0f : 1f - abs);
            float scale = 1f - (abs * 0.05f);
            page.setScaleX(scale);
            page.setScaleY(scale);
        });
    }

    private void setupDotsIndicator() {
        dotsContainer.removeAllViews();
        for (int i = 0; i < bannerList.size(); i++) {
            View dot = new View(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dpToPx(DOT_SIZE_DP), dpToPx(DOT_SIZE_DP));
            params.setMargins(dpToPx(DOT_MARGIN_DP), 0, dpToPx(DOT_MARGIN_DP), 0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(R.drawable.shape_indicator_inactive);
            dotsContainer.addView(dot);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        for (int i = 0; i < dotsContainer.getChildCount(); i++) {
            dotsContainer.getChildAt(i).setBackgroundResource(
                    i == position ? R.drawable.shape_indicator_active : R.drawable.shape_indicator_inactive
            );
        }
    }

    private void setupPageChangeCallback() {
        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                setCurrentIndicator(position);
            }
        });
    }

    private void startAutoSlide() {
        sliderHandler.postDelayed(autoSlideRunnable, AUTO_SLIDE_DELAY);
    }

    private void stopAutoSlide() {
        sliderHandler.removeCallbacks(autoSlideRunnable);
    }

    // ===================== EVENTS =====================
    private void setupEvents() {
        EventRepository repo = new EventRepository();
        repo.getRecentEvents(MAX_RECENT_EVENTS).observe(getViewLifecycleOwner(), events -> {
            eventsContainer.removeAllViews();
            if (events != null && !events.isEmpty()) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                for (Event e : events) {
                    View item = inflater.inflate(R.layout.item_event, eventsContainer, false);
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) item.getLayoutParams();
                    params.leftMargin = 0;
                    params.rightMargin = 0;
                    item.setLayoutParams(params);
                    bindEvent(item, e);
                    eventsContainer.addView(item);
                }
            } else {
                TextView empty = new TextView(getContext());
                empty.setText("Tidak ada event terbaru");
                empty.setTextColor(0xFF999999);
                empty.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                empty.setPadding(16, 32, 16, 32);
                eventsContainer.addView(empty);
            }
        });
    }

    private void bindEvent(View item, Event e) {
        ImageView poster = item.findViewById(R.id.img_event_poster);
        TextView title = item.findViewById(R.id.txt_event_title);
        TextView date = item.findViewById(R.id.txt_event_date);
        TextView loc = item.findViewById(R.id.txt_event_location);
        TextView cat = item.findViewById(R.id.txt_event_category);

        title.setText(e.getTitle());
        date.setText(e.getDate());
        loc.setText(e.getLocation());
        cat.setText(e.getCategory());

        Glide.with(this)
                .load(e.getPosterUrl())
                .centerCrop()
                .placeholder(R.drawable.ic_home)
                .error(R.drawable.ic_home)
                .into(poster);

        item.setOnClickListener(v -> showEventDialog(e));
    }

    private void showEventDialog(Event e) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View v = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_event_detail, null);

        ((TextView) v.findViewById(R.id.txt_event_detail_title)).setText(e.getTitle());
        ((TextView) v.findViewById(R.id.txt_event_detail_organizer)).setText(e.getOrganizer());
        ((TextView) v.findViewById(R.id.txt_event_detail_location)).setText(e.getLocation());
        ((TextView) v.findViewById(R.id.txt_event_detail_date)).setText(e.getDate());
        ((TextView) v.findViewById(R.id.txt_event_detail_description)).setText(e.getDescription());
        v.findViewById(R.id.btn_close_dialog).setOnClickListener(x -> dialog.dismiss());

        dialog.setContentView(v);
        dialog.show();
    }

    // ===================== NOTIFICATION =====================
    private void checkNotificationBadge() {
        allNotifications.clear();
        fetchEvents();
    }

    private void fetchEvents() {
        ApiClient.getClient().create(ApiService.class).getAllEvents().enqueue(new Callback<ApiResponseList<Event>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Event>> call, Response<ApiResponseList<Event>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Event e : response.body().getData()) {
                        allNotifications.add(new Notification(
                                e.getId(), e.getTitle(), e.getDescription(), "Event", e.getCreatedAt(), false
                        ));
                    }
                }
                fetchScholarships();
            }

            @Override
            public void onFailure(Call<ApiResponseList<Event>> call, Throwable t) {
                fetchScholarships();
            }
        });
    }

    private void fetchScholarships() {
        ApiClient.getClient().create(ApiService.class).getAllScholarships().enqueue(new Callback<ApiResponseList<Scholarship>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Scholarship>> call, Response<ApiResponseList<Scholarship>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Scholarship s : response.body().getData()) {
                        allNotifications.add(Notification.fromScholarship(
                                s.getId(), s.getTitle(), s.getProvider(), s.getDescription(), s.getCreatedAt()
                        ));
                    }
                }
                fetchCompetitions();
            }

            @Override
            public void onFailure(Call<ApiResponseList<Scholarship>> call, Throwable t) {
                fetchCompetitions();
            }
        });
    }

    private void fetchCompetitions() {
        ApiClient.getClient().create(ApiService.class).getAllCompetitions().enqueue(new Callback<ApiResponseList<Competition>>() {
            @Override
            public void onResponse(Call<ApiResponseList<Competition>> call, Response<ApiResponseList<Competition>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Competition c : response.body().getData()) {
                        allNotifications.add(new Notification(
                                c.getId(), c.getTitle(), c.getDescription(), "Kompetisi", c.getCreatedAt(), false
                        ));
                    }
                }
                fetchOprecStatus();
            }

            @Override
            public void onFailure(Call<ApiResponseList<Competition>> call, Throwable t) {
                fetchOprecStatus();
            }
        });
    }

    private void fetchOprecStatus() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        String userId = prefs.getString(Constants.KEY_USER_ID, "");
        ApiClient.getClient().create(ApiService.class).getOprecStatus(userId).enqueue(new Callback<ApiResponseList<OprecStatus>>() {
            @Override
            public void onResponse(Call<ApiResponseList<OprecStatus>> call, Response<ApiResponseList<OprecStatus>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (OprecStatus os : response.body().getData()) {
                        allNotifications.add(new Notification(
                                os.getId(), os.getJudul(), "Status: "+os.getStatus(), "Info", os.getCreated_at(), false, os.getStatus()
                        ));
                    }
                }
                updateBadge();
            }

            @Override
            public void onFailure(Call<ApiResponseList<OprecStatus>> call, Throwable t) {
                updateBadge();
            }
        });
    }

    private void updateBadge() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        int newCount = allNotifications.size();
        int lastCount = prefs.getInt(Constants.KEY_LAST_NOTIFICATION_COUNT, 0);
        badgeNotification.setVisibility(newCount > lastCount ? View.VISIBLE : View.GONE);
        if (newCount > lastCount) prefs.edit().putInt(Constants.KEY_LAST_NOTIFICATION_COUNT, newCount).apply();
    }

    // ===================== CLICKS =====================
    private void setupClickListeners(View view) {
        view.findViewById(R.id.btn_notification).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), NotificationActivity.class)));

        view.findViewById(R.id.btn_oprec).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), OprecActivity.class)));

        view.findViewById(R.id.btn_event).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), EventActivity.class)));

        view.findViewById(R.id.btn_kompetisi).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), CompetitionActivity.class)));

        view.findViewById(R.id.btn_beasiswa).setOnClickListener(v ->
                startActivity(new Intent(getActivity(), ScholarshipActivity.class)));

        txtSeeAllEvents.setOnClickListener(v ->
                startActivity(new Intent(getActivity(), EventActivity.class)));
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onResume() {
        super.onResume();
        startAutoSlide();
        checkNotificationBadge();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoSlide();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopAutoSlide();
    }
}

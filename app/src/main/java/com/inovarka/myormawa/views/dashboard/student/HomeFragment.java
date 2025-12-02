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
import com.inovarka.myormawa.models.Banner;
import com.inovarka.myormawa.models.Event;
import com.inovarka.myormawa.repositories.EventRepository;
import com.inovarka.myormawa.utils.Constants;

import java.util.ArrayList;
import java.util.List;

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

    private BannerAdapter bannerAdapter;
    private List<Banner> bannerList;
    private Handler sliderHandler;
    private int currentPage = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
        sliderHandler = new Handler(Looper.getMainLooper());
    }

    private void loadUserData() {
        SharedPreferences prefs = requireActivity().getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        String fullName = prefs.getString(Constants.KEY_FULL_NAME, "User");

        if (txtUserName != null) {
            txtUserName.setText(fullName);
        }
    }

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

    private void handleBannerClick(Banner banner, int position) {
        Intent intent = null;

        switch (position) {
            case 0: // Banner 1 → Organization Fragment
                if (getActivity() instanceof DashboardStudentActivity) {
                    ((DashboardStudentActivity) getActivity()).navigateToOrganization();
                }
                break;

            case 1: // Banner 2 → EventActivity
                intent = new Intent(getActivity(), EventActivity.class);
                startActivity(intent);
                break;

            case 2: // Banner 3 → CompetitionActivity
                intent = new Intent(getActivity(), CompetitionActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void setupEvents() {
        loadRecentEvents();
    }

    private void loadRecentEvents() {
        EventRepository eventRepository = new EventRepository();

        eventRepository.getRecentEvents(MAX_RECENT_EVENTS).observe(getViewLifecycleOwner(), new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                eventsContainer.removeAllViews();

                if (events != null && !events.isEmpty()) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());

                    for (Event event : events) {
                        View itemView = inflater.inflate(R.layout.item_event, eventsContainer, false);

                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
                        params.leftMargin = 0;
                        params.rightMargin = 0;
                        itemView.setLayoutParams(params);

                        bindEventView(itemView, event);
                        eventsContainer.addView(itemView);
                    }
                } else {
                    TextView emptyText = new TextView(getContext());
                    emptyText.setText("Tidak ada event terbaru");
                    emptyText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    emptyText.setTextColor(0xFF999999);
                    emptyText.setPadding(16, 32, 16, 32);
                    eventsContainer.addView(emptyText);
                }
            }
        });
    }

    private void bindEventView(View itemView, Event event) {
        ImageView imgPoster = itemView.findViewById(R.id.img_event_poster);
        TextView txtCategory = itemView.findViewById(R.id.txt_event_category);
        TextView txtTitle = itemView.findViewById(R.id.txt_event_title);
        TextView txtLocation = itemView.findViewById(R.id.txt_event_location);
        TextView txtDate = itemView.findViewById(R.id.txt_event_date);
        TextView txtParticipants = itemView.findViewById(R.id.txt_event_participants);

        txtTitle.setText(event.getTitle());
        txtDate.setText(event.getDate());
        txtLocation.setText(event.getLocation());
        txtCategory.setText(event.getCategory());

        // Hide participants
        txtParticipants.setVisibility(View.GONE);

        // Load poster from URL
        if (event.getPosterUrl() != null && !event.getPosterUrl().isEmpty()) {
            Glide.with(this)
                    .load(event.getPosterUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_home)
                    .error(R.drawable.ic_home)
                    .into(imgPoster);
        } else {
            imgPoster.setImageResource(R.drawable.ic_home);
        }

        // Click listener
        itemView.setOnClickListener(v -> showEventDetailDialog(event));
    }

    private void showEventDetailDialog(Event event) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_event_detail, null);

        ImageView btnClose = view.findViewById(R.id.btn_close_dialog);
        TextView txtTitle = view.findViewById(R.id.txt_event_detail_title);
        TextView txtOrganizer = view.findViewById(R.id.txt_event_detail_organizer);
        TextView txtLocation = view.findViewById(R.id.txt_event_detail_location);
        TextView txtDate = view.findViewById(R.id.txt_event_detail_date);
        TextView txtDescription = view.findViewById(R.id.txt_event_detail_description);
        View btnDownload = view.findViewById(R.id.btn_download_guidebook);

        LinearLayout layoutTimeSection = view.findViewById(R.id.layout_time_section);
        TextView txtTime = view.findViewById(R.id.txt_event_detail_time);

        txtTitle.setText(event.getTitle());
        txtOrganizer.setText(event.getOrganizer());
        txtLocation.setText(event.getLocation());
        txtDate.setText(event.getDate());
        txtDescription.setText(event.getDescription());

        if (event.hasTimeInfo()) {
            layoutTimeSection.setVisibility(View.VISIBLE);
            txtTime.setText(event.getWaktuLengkap());
        } else {
            layoutTimeSection.setVisibility(View.GONE);
        }

        // Handle download button
        if (event.hasGuideBook()) {
            btnDownload.setVisibility(View.VISIBLE);
            btnDownload.setOnClickListener(v -> {
                // Download functionality will be handled in EventActivity
                Toast.makeText(requireContext(), "Fitur download tersedia di halaman Event", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            });
        } else {
            btnDownload.setVisibility(View.GONE);
        }

        btnClose.setOnClickListener(v -> dialog.dismiss());

        dialog.setContentView(view);
        dialog.show();
    }

    private void initBannerList() {
        bannerList = new ArrayList<>();
        bannerList.add(new Banner(R.drawable.ill_banner_slide_1));
        bannerList.add(new Banner(R.drawable.ill_banner_slide_2));
        bannerList.add(new Banner(R.drawable.ill_banner_slide_3));
    }

    private void setupPageTransformer() {
        bannerViewPager.setPageTransformer((page, position) -> {
            float absPosition = Math.abs(position);
            if (absPosition >= 1) {
                page.setAlpha(0f);
            } else {
                page.setAlpha(1f - absPosition);
            }
            float scale = 1f - (absPosition * 0.05f);
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
            View dot = dotsContainer.getChildAt(i);
            dot.setBackgroundResource(i == position ?
                    R.drawable.shape_indicator_active :
                    R.drawable.shape_indicator_inactive);
        }
    }

    private void setupPageChangeCallback() {
        bannerViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
                currentPage = position;
            }
        });
    }

    private void startAutoSlide() {
        sliderHandler.postDelayed(autoSlideRunnable, AUTO_SLIDE_DELAY);
    }

    private void stopAutoSlide() {
        sliderHandler.removeCallbacks(autoSlideRunnable);
    }

    private final Runnable autoSlideRunnable = new Runnable() {
        @Override
        public void run() {
            if (bannerList != null && !bannerList.isEmpty()) {
                currentPage++;
                if (currentPage >= bannerList.size()) {
                    currentPage = 0;
                }
                bannerViewPager.setCurrentItem(currentPage, true);
                sliderHandler.postDelayed(this, AUTO_SLIDE_DELAY);
            }
        }
    };

    private void setupClickListeners(View view) {
        // Notification Button
        view.findViewById(R.id.btn_notification).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), NotificationActivity.class);
            startActivity(intent);
        });

        // Oprec Button
        view.findViewById(R.id.btn_oprec).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), OprecActivity.class);
            startActivity(intent);
        });

        // Event Button
        view.findViewById(R.id.btn_event).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EventActivity.class);
            startActivity(intent);
        });

        // Kompetisi Button
        view.findViewById(R.id.btn_kompetisi).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CompetitionActivity.class);
            startActivity(intent);
        });

        // Beasiswa Button
        view.findViewById(R.id.btn_beasiswa).setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ScholarshipActivity.class);
            startActivity(intent);
        });

        // See All Events Button
        txtSeeAllEvents.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EventActivity.class);
            startActivity(intent);
        });
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoSlide();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerList != null && !bannerList.isEmpty()) {
            startAutoSlide();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopAutoSlide();
    }
}
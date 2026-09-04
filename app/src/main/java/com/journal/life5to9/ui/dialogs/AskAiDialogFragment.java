package com.journal.life5to9.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.ChipGroup;
import com.journal.life5to9.R;
import com.journal.life5to9.data.remote.InsightsPeriod;
import com.journal.life5to9.data.remote.dto.InsightsResponse;
import com.journal.life5to9.viewmodel.InsightsUiState;
import com.journal.life5to9.viewmodel.InsightsViewModel;

import java.util.List;

public class AskAiDialogFragment extends DialogFragment {

    public static final String TAG = "AskAiDialog";

    private InsightsViewModel insightsViewModel;

    private ChipGroup chipGroupPeriod;
    private ChipGroup chipGroupQuestion;
    private MaterialButton buttonGenerateInsights;
    private LinearLayout layoutInsightsLoading;
    private TextView textViewInsightsEmpty;
    private TextView textViewInsightsError;
    private MaterialCardView cardInsightResult;
    private LinearLayout layoutInsightsResult;
    private TextView textViewInsightTitle;
    private TextView textViewInsightSummary;
    private TextView textViewHighlightsLabel;
    private LinearLayout layoutInsightHighlights;
    private TextView textViewRecommendationsLabel;
    private LinearLayout layoutInsightRecommendations;
    private ScrollView scrollAskAiMessages;

    public static AskAiDialogFragment newInstance() {
        return new AskAiDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Life5To9_FullScreenDialog);
        insightsViewModel = new ViewModelProvider(requireActivity()).get(InsightsViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_ask_ai, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupToolbar(view);
        setupControls();
        observeInsightsState();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        }
    }

    private void initializeViews(View view) {
        chipGroupPeriod = view.findViewById(R.id.chipGroupPeriod);
        chipGroupQuestion = view.findViewById(R.id.chipGroupQuestion);
        buttonGenerateInsights = view.findViewById(R.id.buttonGenerateInsights);
        layoutInsightsLoading = view.findViewById(R.id.layoutInsightsLoading);
        textViewInsightsEmpty = view.findViewById(R.id.textViewInsightsEmpty);
        textViewInsightsError = view.findViewById(R.id.textViewInsightsError);
        cardInsightResult = view.findViewById(R.id.cardInsightResult);
        layoutInsightsResult = view.findViewById(R.id.layoutInsightsResult);
        textViewInsightTitle = view.findViewById(R.id.textViewInsightTitle);
        textViewInsightSummary = view.findViewById(R.id.textViewInsightSummary);
        textViewHighlightsLabel = view.findViewById(R.id.textViewHighlightsLabel);
        layoutInsightHighlights = view.findViewById(R.id.layoutInsightHighlights);
        textViewRecommendationsLabel = view.findViewById(R.id.textViewRecommendationsLabel);
        layoutInsightRecommendations = view.findViewById(R.id.layoutInsightRecommendations);
        scrollAskAiMessages = view.findViewById(R.id.scrollAskAiMessages);
    }

    private void setupToolbar(View view) {
        MaterialToolbar toolbar = view.findViewById(R.id.toolbarAskAi);
        toolbar.setNavigationOnClickListener(v -> dismiss());
    }

    private void setupControls() {
        chipGroupPeriod.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                return;
            }
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipPeriodLast7Days) {
                insightsViewModel.setPeriod(InsightsPeriod.LAST_7_DAYS);
            } else if (checkedId == R.id.chipPeriodThisWeek) {
                insightsViewModel.setPeriod(InsightsPeriod.THIS_WEEK);
            } else if (checkedId == R.id.chipPeriodThisMonth) {
                insightsViewModel.setPeriod(InsightsPeriod.THIS_MONTH);
            }
        });

        chipGroupQuestion.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                return;
            }
            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chipQuestionTimeGoing) {
                insightsViewModel.setQuestion(getString(R.string.ask_ai_question_time_going));
            } else if (checkedId == R.id.chipQuestionBalance) {
                insightsViewModel.setQuestion(getString(R.string.ask_ai_question_balance));
            } else if (checkedId == R.id.chipQuestionDoMore) {
                insightsViewModel.setQuestion(getString(R.string.ask_ai_question_do_more));
            }
        });

        buttonGenerateInsights.setOnClickListener(v -> insightsViewModel.generateInsights());
    }

    private void observeInsightsState() {
        insightsViewModel.getUiState().observe(getViewLifecycleOwner(), this::renderInsightsState);
    }

    private void renderInsightsState(InsightsUiState state) {
        if (state == null) {
            return;
        }

        syncChipSelection(state);

        boolean loading = state.getStatus() == InsightsUiState.Status.LOADING;
        buttonGenerateInsights.setEnabled(!loading);
        layoutInsightsLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        textViewInsightsEmpty.setVisibility(
                state.getStatus() == InsightsUiState.Status.EMPTY ? View.VISIBLE : View.GONE);

        if (state.getStatus() == InsightsUiState.Status.ERROR) {
            textViewInsightsError.setVisibility(View.VISIBLE);
            textViewInsightsError.setText(state.getErrorMessage());
        } else {
            textViewInsightsError.setVisibility(View.GONE);
        }

        if (state.getStatus() == InsightsUiState.Status.SUCCESS && state.getResponse() != null) {
            bindInsightResult(state.getResponse());
            cardInsightResult.setVisibility(View.VISIBLE);
            scrollAskAiMessages.post(() -> scrollAskAiMessages.fullScroll(View.FOCUS_DOWN));
        } else if (state.getStatus() != InsightsUiState.Status.SUCCESS) {
            cardInsightResult.setVisibility(View.GONE);
        }
    }

    private void syncChipSelection(InsightsUiState state) {
        int periodChipId = R.id.chipPeriodLast7Days;
        if (state.getPeriod() == InsightsPeriod.THIS_WEEK) {
            periodChipId = R.id.chipPeriodThisWeek;
        } else if (state.getPeriod() == InsightsPeriod.THIS_MONTH) {
            periodChipId = R.id.chipPeriodThisMonth;
        }
        if (chipGroupPeriod.getCheckedChipId() != periodChipId) {
            chipGroupPeriod.check(periodChipId);
        }

        int questionChipId = R.id.chipQuestionTimeGoing;
        String question = state.getQuestion();
        if (getString(R.string.ask_ai_question_balance).equals(question)) {
            questionChipId = R.id.chipQuestionBalance;
        } else if (getString(R.string.ask_ai_question_do_more).equals(question)) {
            questionChipId = R.id.chipQuestionDoMore;
        }
        if (chipGroupQuestion.getCheckedChipId() != questionChipId) {
            chipGroupQuestion.check(questionChipId);
        }
    }

    private void bindInsightResult(InsightsResponse response) {
        String title = response.getTitle();
        if (title != null && !title.trim().isEmpty()) {
            textViewInsightTitle.setVisibility(View.VISIBLE);
            textViewInsightTitle.setText(title);
        } else {
            textViewInsightTitle.setVisibility(View.GONE);
        }

        String summary = response.getSummary();
        if (summary != null && !summary.trim().isEmpty()) {
            textViewInsightSummary.setVisibility(View.VISIBLE);
            textViewInsightSummary.setText(summary);
        } else {
            textViewInsightSummary.setVisibility(View.GONE);
        }

        populateBulletList(layoutInsightHighlights, textViewHighlightsLabel, response.getHighlights());
        populateBulletList(layoutInsightRecommendations, textViewRecommendationsLabel, response.getRecommendations());
    }

    private void populateBulletList(LinearLayout container, TextView label, List<String> items) {
        container.removeAllViews();
        if (items == null || items.isEmpty()) {
            label.setVisibility(View.GONE);
            return;
        }

        label.setVisibility(View.VISIBLE);
        for (String item : items) {
            TextView bullet = new TextView(requireContext());
            bullet.setText("• " + item);
            bullet.setTextSize(14);
            bullet.setTextColor(requireContext().getColor(R.color.on_surface));
            bullet.setPadding(0, dpToPx(4), 0, dpToPx(4));
            container.addView(bullet);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}

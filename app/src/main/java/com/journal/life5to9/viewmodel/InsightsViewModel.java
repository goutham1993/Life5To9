package com.journal.life5to9.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.journal.life5to9.data.entity.Activity;
import com.journal.life5to9.data.entity.Category;
import com.journal.life5to9.data.remote.InsightsException;
import com.journal.life5to9.data.remote.InsightsPeriod;
import com.journal.life5to9.data.remote.InsightsRequestBuilder;
import com.journal.life5to9.data.remote.dto.InsightsRequest;
import com.journal.life5to9.data.remote.dto.InsightsResponse;
import com.journal.life5to9.data.repository.ActivityRepository;
import com.journal.life5to9.data.repository.CategoryRepository;
import com.journal.life5to9.data.repository.InsightsRepository;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InsightsViewModel extends ViewModel {

    private final ActivityRepository activityRepository;
    private final CategoryRepository categoryRepository;
    private final InsightsRepository insightsRepository;
    private final ExecutorService executor;
    private final MutableLiveData<InsightsUiState> uiState = new MutableLiveData<>(InsightsUiState.idle());

    public InsightsViewModel(ActivityRepository activityRepository,
                             CategoryRepository categoryRepository,
                             InsightsRepository insightsRepository) {
        this.activityRepository = activityRepository;
        this.categoryRepository = categoryRepository;
        this.insightsRepository = insightsRepository;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<InsightsUiState> getUiState() {
        return uiState;
    }

    public void setPeriod(InsightsPeriod period) {
        InsightsUiState current = currentState();
        if (current.getPeriod() == period) {
            return;
        }
        uiState.setValue(current.withPeriod(period));
    }

    public void setQuestion(String question) {
        InsightsUiState current = currentState();
        if (question != null && question.equals(current.getQuestion())) {
            return;
        }
        uiState.setValue(current.withQuestion(question));
    }

    public void generateInsights() {
        InsightsUiState current = currentState();
        if (current.isLoading()) {
            return;
        }

        InsightsUiState loading = current.toLoading();
        uiState.setValue(loading);
        executor.execute(() -> {
            Date[] range = loading.getPeriod().getDateRange();
            List<Activity> activities = activityRepository.getActivitiesByDateRangeSync(range[0], range[1]);
            if (activities == null || activities.isEmpty()) {
                uiState.postValue(loading.toEmpty());
                return;
            }

            List<Category> categories = categoryRepository.getAllCategoriesSync();
            InsightsRequest request = InsightsRequestBuilder.build(
                    loading.getPeriod().getLabel(),
                    loading.getQuestion(),
                    activities,
                    categories
            );

            try {
                InsightsResponse response = insightsRepository.fetchInsights(request);
                uiState.postValue(loading.toSuccess(response));
            } catch (InsightsException e) {
                uiState.postValue(loading.toError(e.getMessage()));
            }
        });
    }

    private InsightsUiState currentState() {
        InsightsUiState state = uiState.getValue();
        return state != null ? state : InsightsUiState.idle();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdownNow();
    }
}

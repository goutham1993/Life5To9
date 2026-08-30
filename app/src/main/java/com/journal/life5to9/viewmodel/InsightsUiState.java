package com.journal.life5to9.viewmodel;

import com.journal.life5to9.data.remote.InsightsPeriod;
import com.journal.life5to9.data.remote.dto.InsightsResponse;

public class InsightsUiState {
    public enum Status {
        IDLE,
        LOADING,
        EMPTY,
        ERROR,
        SUCCESS
    }

    public static final String DEFAULT_QUESTION = "Where is my time going?";

    private final Status status;
    private final InsightsPeriod period;
    private final String question;
    private final InsightsResponse response;
    private final String errorMessage;

    private InsightsUiState(Status status, InsightsPeriod period, String question,
                            InsightsResponse response, String errorMessage) {
        this.status = status;
        this.period = period;
        this.question = question;
        this.response = response;
        this.errorMessage = errorMessage;
    }

    public static InsightsUiState idle() {
        return new InsightsUiState(Status.IDLE, InsightsPeriod.LAST_7_DAYS, DEFAULT_QUESTION, null, null);
    }

    public Status getStatus() {
        return status;
    }

    public InsightsPeriod getPeriod() {
        return period;
    }

    public String getQuestion() {
        return question;
    }

    public InsightsResponse getResponse() {
        return response;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isLoading() {
        return status == Status.LOADING;
    }

    public InsightsUiState withPeriod(InsightsPeriod period) {
        return new InsightsUiState(status, period, question, response, errorMessage);
    }

    public InsightsUiState withQuestion(String question) {
        return new InsightsUiState(status, period, question, response, errorMessage);
    }

    public InsightsUiState toLoading() {
        return new InsightsUiState(Status.LOADING, period, question, null, null);
    }

    public InsightsUiState toEmpty() {
        return new InsightsUiState(Status.EMPTY, period, question, null, null);
    }

    public InsightsUiState toError(String errorMessage) {
        return new InsightsUiState(Status.ERROR, period, question, null, errorMessage);
    }

    public InsightsUiState toSuccess(InsightsResponse response) {
        return new InsightsUiState(Status.SUCCESS, period, question, response, null);
    }
}

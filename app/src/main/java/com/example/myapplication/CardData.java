package com.example.myapplication;

class CardData {
    String requestId;
    String requestTitle;
    String requestMuhtar;
    String requestType;
    String requestStatus;
    String requestDate;
    String requestResult;

    CardData(String requestId, String requestTitle, String requestMuhtar, String requestType, String requestStatus, String requestDate, String requestResult) {
        this.requestId = requestId;
        this.requestTitle = requestTitle;
        this.requestMuhtar = requestMuhtar;
        this.requestType = requestType;
        this.requestStatus = requestStatus;
        this.requestDate = requestDate;
        this.requestResult = requestResult;
    }

    // Method to return a combined string of muhtar's name and request title
    String getCombinedText() {
        return requestMuhtar + " - " + requestTitle;
    }
}
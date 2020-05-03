package com.example.graduation;


import android.app.Activity;
import android.os.AsyncTask;

import com.google.cloud.dialogflow.v2beta1.DetectIntentRequest;
import com.google.cloud.dialogflow.v2beta1.DetectIntentResponse;
import com.google.cloud.dialogflow.v2beta1.QueryInput;
import com.google.cloud.dialogflow.v2beta1.SessionName;
import com.google.cloud.dialogflow.v2beta1.SessionsClient;

//사용자의 요청 쿼리가 챗봇에 요청으로 전송되고 응답이 캡처되는 AsyncTask에서 수행
//AsynTask란 클래스를 상속하여 클래스를 만들면 해당 클래스안에 스레드를 위한 동작코드와 UI 접근 코드를 한꺼번에 넣을 수 있습니다.
public class RequestJavaV2Task extends AsyncTask<Void, Void, DetectIntentResponse> {

    Activity activity;
    private SessionName session;
    private SessionsClient sessionsClient;
    private QueryInput queryInput;

    RequestJavaV2Task(Activity activity, SessionName session, SessionsClient sessionsClient, QueryInput queryInput) {
        this.activity = activity;
        this.session = session;
        this.sessionsClient = sessionsClient;
        this.queryInput = queryInput;
    }
//챗봇과의 통신을 비동기 방식으로 처리
    @Override
    protected DetectIntentResponse doInBackground(Void... voids) {
        try{
            DetectIntentRequest detectIntentRequest =
                    DetectIntentRequest.newBuilder()
                            .setSession(session.toString())
                            .setQueryInput(queryInput)
                            .build();
            return sessionsClient.detectIntent(detectIntentRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(DetectIntentResponse response) {
        //((MainActivity) activity).callbackV2(response);
        ((ChatbotActivity) activity).callbackV2(response);
    }
}

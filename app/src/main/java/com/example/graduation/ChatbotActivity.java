package com.example.graduation;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2beta1.AudioEncoding;
import com.google.cloud.dialogflow.v2beta1.DetectIntentRequest;
import com.google.cloud.dialogflow.v2beta1.DetectIntentResponse;
import com.google.cloud.dialogflow.v2beta1.InputAudioConfig;
import com.google.cloud.dialogflow.v2beta1.QueryInput;
import com.google.cloud.dialogflow.v2beta1.QueryResult;
import com.google.cloud.dialogflow.v2beta1.SessionName;
import com.google.cloud.dialogflow.v2beta1.SessionsClient;
import com.google.cloud.dialogflow.v2beta1.SessionsSettings;
import com.google.cloud.dialogflow.v2beta1.TextInput;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.protobuf.ByteString;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import ai.api.AIServiceContext;
import ai.api.AIServiceContextBuilder;
import ai.api.android.AIConfiguration;
import ai.api.android.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

public class ChatbotActivity extends AppCompatActivity{

    private static final String TAG = ChatbotActivity.class.getSimpleName();
    private static final int USER = 10001;
    private static final int BOT = 10002;

    private String uuid = UUID.randomUUID().toString();
    private LinearLayout chatLayout;
    private EditText queryEditText;

    // Android client
    private AIRequest aiRequest;
    private AIDataService aiDataService;
    private AIServiceContext customAIServiceContext;

    // Java V2
    private SessionsClient sessionsClient;
    private SessionName session;

    //로그 아웃
    private Button buttonLogout;
    //private TextView textViewUserEmail;

    //firebase auth object
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbot_main);

        //buttonLogout = (Button) findViewById(R.id.buttonLogout);

        //initializing firebase authentication object
        firebaseAuth = FirebaseAuth.getInstance();
        //유저가 로그인 하지 않은 상태라면 null 상태이고 이 액티비티를 종료하고 로그인 액티비티를 연다.
        if(firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

        //유저가 있다면, null이 아니면 계속 진행
        FirebaseUser user = firebaseAuth.getCurrentUser();

        //logout button event
        //buttonLogout.setOnClickListener(this);

        //스크롤뷰
        final ScrollView scrollview = findViewById(R.id.chatScrollView);
        scrollview.post(() -> scrollview.fullScroll(ScrollView.FOCUS_DOWN));

        chatLayout = findViewById(R.id.chatLayout);

        ImageView sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this::sendMessage);

        queryEditText = findViewById(R.id.queryEditText);
        queryEditText.setOnKeyListener((view, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        sendMessage(sendBtn);
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });

        //  initChatbot();
        String manual= "                     **사용법** \n ===============================\n | 주문예상시간을 사용하고 싶으시면        | \n | 1번이나 단어를 입력해주세요!               | \n | 현재인원그래프를 알고 싶으시면           | \n | 2번이나 단어를 입력해주세요!               | \n===============================";
        showTextView(manual,BOT);
        initV2Chatbot();


    }

    private void initChatbot(){
        final AIConfiguration config= new AIConfiguration("4e77176662a94f89a6d6f7ba2b4b729a",
                AIConfiguration.SupportedLanguages.Korean,AIConfiguration.RecognitionEngine.System);
        aiDataService = new AIDataService(this,config);
        customAIServiceContext = AIServiceContextBuilder.buildFromSessionId(uuid);
        aiRequest= new AIRequest();
    }


    private void initV2Chatbot() {
        try {
            InputStream stream = getResources().openRawResource(R.raw.test_agent_ecgnqp_35e1fbc02275);
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream);
            String projectId = ((ServiceAccountCredentials)credentials).getProjectId();
            String audioFilePath;

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            session = SessionName.of(projectId, uuid);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    private void sendMessage(View view) {
        String msg = queryEditText.getText().toString();
        if (msg.trim().isEmpty()) {
            Toast.makeText(ChatbotActivity.this, "Please enter your query!", Toast.LENGTH_LONG).show();
        } else {
            showTextView(msg, USER);
            queryEditText.setText("");

            // Android client
            // aiRequest.setQuery(msg);
            //RequestTask requestTask = new RequestTask(MainActivity.this, aiDataService, customAIServiceContext);
            // requestTask.execute(aiRequest);

            // Java V2
            QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(msg).setLanguageCode("ko-KR")).build();
            new RequestJavaV2Task(ChatbotActivity.this, session, sessionsClient, queryInput).execute();


        }
    }

    public void callback(AIResponse aiResponse) {
        if (aiResponse != null) {
            // process aiResponse here
            String botReply = aiResponse.getResult().getFulfillment().getSpeech();
            Log.d(TAG, "Bot Reply: " + botReply);
            showTextView(botReply, BOT);
        } else {
            Log.d(TAG, "Bot Reply: Null");
            showTextView("There was some communication issue. Please Try again!", BOT);
        }
    }


    public void callbackV2(DetectIntentResponse response) {

        if (response != null) {
            // process aiResponse here
            String botReply = response.getQueryResult().getFulfillmentText();
            Log.d(TAG, "V2 Bot Reply: " + botReply);
           // showTextView(manual,BOT);
            showTextView(botReply, BOT);
            //인텐트에 해당하는 액티비티 띄우기
            if(botReply.contains("대기")==true){
                Intent intent1;
                intent1 = new Intent(this, waiting_time.class);
                startActivity(intent1);
            }


            if(botReply.contains("식당")==true){
                Intent intent2;
                //intent2 = new Intent(this, Distribution.class);
                intent2 = new Intent(this, BarChartActivity.class);
                startActivity(intent2);

            }

            if(botReply.contains("E동")==true){
                Intent intent3;
                intent3 = new Intent(this, MapKpu.class); //E동=Mapkpu임
                startActivity(intent3);
            }

            if(botReply.contains("TIP")==true){
                Intent intent4;
                intent4 = new Intent(this, MapKpu2.class);
                startActivity(intent4);
            }


            if(botReply.contains("산융")==true){
                Intent intent5;
                intent5 = new Intent(this, MainKpu3.class);
                startActivity(intent5);
            }

            if(botReply.contains("종합")==true){
                Intent intent6;
                intent6 = new Intent(this, MapJong.class);
                startActivity(intent6);
            }



        }

        else {
           // String manual= "대기시간을 사용하고 싶으면 챗봇에게 1번이나 단어를 \n 현장인원분포도를 알고 싶으시면 2번이나 단어를 입력해주세요!!";
          //  showTextView(manual,BOT);
            Log.d(TAG, "Bot Reply: Null");
            showTextView("There was some communication issue. Please Try again!", BOT);
        }
    }



    private void showTextView(String message, int type) {
        FrameLayout layout;
        switch (type) {
            case USER:
                layout = getUserLayout();
                break;
            case BOT:
                layout = getBotLayout();
                break;
            default:
                layout = getBotLayout();
                break;
        }
        layout.setFocusableInTouchMode(true);
        chatLayout.addView(layout); // move focus to text view to automatically make it scroll up if softfocus
        TextView tv = layout.findViewById(R.id.chatMsg);
        tv.setText(message);
        layout.requestFocus();
        queryEditText.requestFocus(); // change focus back to edit text to continue typing
    }

    FrameLayout getUserLayout() {
        LayoutInflater inflater = LayoutInflater.from(ChatbotActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.user_msg_layout, null);
    }

    FrameLayout getBotLayout() {
        LayoutInflater inflater = LayoutInflater.from(ChatbotActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.bot_msg_layout, null);
    }

    /*
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static QueryResult detectIntentAudio(
            String projectId,
            String audioFilePath,
            String sessionId,
            String languageCode)
            throws Exception {
        // Instantiates a client
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            SessionName session = SessionName.of(projectId, sessionId);
            System.out.println("Session Path: " + session.toString());

            // Note: hard coding audioEncoding and sampleRateHertz for simplicity.
            // Audio encoding of the audio content sent in the query request.
            AudioEncoding audioEncoding = AudioEncoding.AUDIO_ENCODING_LINEAR_16;
            int sampleRateHertz = 16000;

            // Instructs the speech recognizer how to process the audio content.
            InputAudioConfig inputAudioConfig = InputAudioConfig.newBuilder()
                    .setAudioEncoding(audioEncoding) // audioEncoding = AudioEncoding.AUDIO_ENCODING_LINEAR_16
                    .setLanguageCode(languageCode) // languageCode = "en-US"
                    .setSampleRateHertz(sampleRateHertz) // sampleRateHertz = 16000
                    .build();

            // Build the query with the InputAudioConfig
            QueryInput queryInput = QueryInput.newBuilder().setAudioConfig(inputAudioConfig).build();

            // Read the bytes from the audio file
            byte[] inputAudio = Files.readAllBytes(Paths.get(audioFilePath));

            // Build the DetectIntentRequest
            DetectIntentRequest request = DetectIntentRequest.newBuilder()
                    .setSession(session.toString())
                    .setQueryInput(queryInput)
                    .setInputAudio(ByteString.copyFrom(inputAudio))
                    .build();

            // Performs the detect intent request
            DetectIntentResponse response = sessionsClient.detectIntent(request);

            // Display the query result
            QueryResult queryResult = response.getQueryResult();
            System.out.println("====================");
            System.out.format("Query Text: '%s'\n", queryResult.getQueryText());
            System.out.format("Detected Intent: %s (confidence: %f)\n",
                    queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence());
            System.out.format("Fulfillment Text: '%s'\n", queryResult.getFulfillmentText());

            return queryResult;
        }
    }
     */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this,MainActivity.class));
                Toast.makeText(this, "로그아웃 되었습니다!", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
package com.example.graduation;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.protobuf.ByteString;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
    private LinearLayout chatLayout2;
    private EditText queryEditText;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;
    private ChildEventListener mChild;

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

    //음성 채팅 관련 변수
    //TextView txt=new findViewById(R.id.chatLayout);
    Intent intent;
    SpeechRecognizer stt;

    String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbot_main);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},5);
            toast("알라후크바르!!!");
        }

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
        String manual= "**사용법** \n주문 예상 시간 확인할 시\n1번이나 해당 단어를 입력해주세요!\n현재 인원 그래프를 알고 싶으시면\n2번이나 단어를 입력해주세요! \n 우측 상단 마이크 클릭하시면 음성인식도 가능해요! \n";
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
        msg = queryEditText.getText().toString();
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

            /*
            if(botReply.contains("TIP")==true){
                Intent intent4;
                intent4 = new Intent(this, MapKpu2.class);
                startActivity(intent4);
            }

             */


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

            if(botReply.contains("알려드리겠습니다")==true){

                    int time1= Integer.parseInt(((waiting_time)waiting_time.context_main).s_time1);

                    int time2= Integer.parseInt(((waiting_time)waiting_time.context_main).s_time2);;
                    int time3= Integer.parseInt(((waiting_time)waiting_time.context_main).s_time3);;
                    int time4= Integer.parseInt(((waiting_time)waiting_time.context_main).s_time4);;

                    String rest1;
                    String rest2;
                    String rest3;
                    String rest4;

                    int max=0;
                    String res="";

                if(time1>time2){
                        if(time3>time2){
                            if(time4>time2){
                                max=time2;
                                rest2="올리브";

                                res="가장 빨리 음식을 드실 수 있는 곳은 "+rest2+"로 기다리는 시간은"+max+"분 소요 될 것으로 예상됩니다.";
                                showTextView(res,BOT);
                                initV2Chatbot();
                            }
                        }
                    }
                if(time2>time1){
                        if(time3>time1){
                            if(time4>time1){
                                max=time1;
                                rest1="종합관";

                                res="가장 빨리 음식을 드실 수 있는 곳은 "+rest1+"로 기다리는 시간은"+max+"분 소요 될 것으로 예상됩니다.";
                                showTextView(res,BOT);
                                initV2Chatbot();
                            }
                        }
                    }

                if(time1>time3){
                        if(time2>time3){
                            if(time4>time3){
                                max=time3;
                                rest3="산융";

                                res="가장 빨리 음식을 드실 수 있는 곳은 "+rest3+"로 기다리는 시간은"+max+"분 소요 될 것으로 예상됩니다.";
                                showTextView(res,BOT);
                                initV2Chatbot();
                            }
                        }
                    }

                if(time1>time4) {
                    if (time2 > time4) {
                        if (time3 > time4) {
                            max = time4;
                            rest4 = "tip";

                            res="가장 빨리 음식을 드실 수 있는 곳은 "+rest4+"로 기다리는 시간은"+max+"분 소요 될 것으로 예상됩니다.";
                            showTextView(res, BOT);
                            initV2Chatbot();
                        }
                    }
                }
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

        // 9 패치 이미지로 채팅 버블을 출력
        tv.setBackground(this.getResources().getDrawable( (type==BOT ? R.drawable.bot_message : R.drawable.user_message )));


    }

    FrameLayout getUserLayout() {
        LayoutInflater inflater = LayoutInflater.from(ChatbotActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.user_msg_layout, null);
    }

    FrameLayout getBotLayout() {
        LayoutInflater inflater = LayoutInflater.from(ChatbotActivity.this);
        return (FrameLayout) inflater.inflate(R.layout.bot_msg_layout, null);
    }


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

            case R.id.speach:
                inputVoice();

        }
        return super.onOptionsItemSelected(item);
    }

    public void inputVoice(){

        try {
            intent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,this.getPackageName());
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");

            stt= SpeechRecognizer.createSpeechRecognizer(this);
            stt.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    toast("음성 인식 시작합니다.");

                }

                @Override
                public void onBeginningOfSpeech() {

                }

                @Override
                public void onRmsChanged(float rmsdB) {

                }

                @Override
                public void onBufferReceived(byte[] buffer) {

                }

                @Override
                public void onEndOfSpeech() {
                    toast("음성 인식을 종료합니다!");

                }

                @Override
                public void onError(int error) {
                    toast("에러 발생! 다시 시도 바랍니다.");
                    stt.destroy();
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> res=(ArrayList<String>)results.get(SpeechRecognizer.RESULTS_RECOGNITION);
                    //String res= (String) results.get(SpeechRecognizer.RESULTS_RECOGNITION);
                 //   String msg = res.toString();

                    showTextView(res.get(0), USER);

                    // Java V2
                    QueryInput queryInput = QueryInput.newBuilder().setText(TextInput.newBuilder().setText(res.get(0)).setLanguageCode("ko-KR")).build();
                    new RequestJavaV2Task(ChatbotActivity.this, session, sessionsClient, queryInput).execute();

                    stt.destroy();
                }

                @Override
                public void onPartialResults(Bundle partialResults) {

                }

                @Override
                public void onEvent(int eventType, Bundle params) {

                }
            });
            stt.startListening(intent);

        }catch (Exception e){
            toast(e.toString());
        }
    }

    private void toast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }


}
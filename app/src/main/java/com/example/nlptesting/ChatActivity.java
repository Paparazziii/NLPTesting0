package com.example.nlptesting;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.JsonReader;
import android.util.Log;
import android.view.View;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.xml.transform.Result;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.internal.operators.observable.ObservableScalarXMap;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Query;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;


public class ChatActivity extends AppCompatActivity {
    private ListView listView;
    private chatAdapter adapter;
    private List<ChatBean> chatBeanList;//save all msgs
    private EditText et_send_msg;
    private Button btn_send;
    //port address
    //private static final String WEB_SITE="https://language.cs.ucdavis.edu/pabot/chat/?";
    private String sendMsg;//send message
    private String welcome[];//For testing!
    private MHandler mHandler;
    public static final int MSG_OK=1;//Get data
    private int indexText = 0;
    private TextView textView;
    private String[] inputStr = {"Typing","Typing.","Typing..","Typing..."};
    UUID uuid = UUID.randomUUID();
    public static String URL_KEY = "https://language.cs.ucdavis.edu/pabot/chat/?";

    public static String APIkey = "/";
    public static final String testURL = "http://fanyi.youdao.com/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatBeanList=new ArrayList<ChatBean>();
        mHandler=new MHandler();
        //welcome message
        welcome=getResources().getStringArray(R.array.welcome);
        initView();//Initialize
    }
    public void initView(){
        listView=(ListView)findViewById(R.id.list);
        et_send_msg=(EditText)findViewById(R.id.et_send_msg);
        btn_send=(Button)findViewById(R.id.btn_send);
        adapter= new chatAdapter(chatBeanList,this);
        listView.setAdapter(adapter);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();//Click on send and send data
            }
        });
        et_send_msg.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent keyEvent) {
                if (keyCode==KeyEvent.KEYCODE_ENTER && keyEvent.getAction()==
                        KeyEvent.ACTION_DOWN){
                    sendData();//Use ”Enter" to send data
                }
                return false;
            }
        });
        //int position=(int)(Math.random()*welcome.length-1);//randomly get a message
        //showData(welcome[position]);//initial message for testing
    }
    private void sendData(){
        sendMsg=et_send_msg.getText().toString();//Get user input
        if(TextUtils.isEmpty(sendMsg)){//if it is empty
            Toast.makeText(this,"No input yet",Toast.LENGTH_LONG).show();
            return;
        }
        et_send_msg.setText("");
        sendMsg=sendMsg.replaceAll(" "," ").replaceAll("\n","").trim();
        ChatBean chatBean=new ChatBean();
        chatBean.setMessage(sendMsg);
        chatBean.setState(ChatBean.SEND);//SEND represent user message
        chatBeanList.add(chatBean);//add message to chatBeanList
        adapter.notifyDataSetChanged();//Update the listview
        getDataFromServer(sendMsg,uuid);//should be get information from server, now is auto feedback
    }

    private void getDataFromServer(String send, UUID uuid){
        String userID = "userId="+uuid.toString();
        String text = "text="+send;
        APIkey = "?"+userID+"&"+text;

       // ServiceGenerator serviceGenerator = new ServiceGenerator();
        // serviceGenerator.setBase_URL(url);

        APIService service = ServiceGenerator.createService(APIService.class);
        Call<Decode> api = service.getData(uuid.toString(),send);

        api.enqueue(new Callback<Decode>() {
            @Override
            public void onResponse(Call<Decode> call, Response<Decode> response) {
                if(response.isSuccessful()){
                    String apiResponse;
                    apiResponse = response.body().getResult();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String line: apiResponse.split("\n")) {
                        stringBuilder.append(line.trim());
                        stringBuilder.append("\n");
                }
                    Message msg = new Message();
                    msg.what = MSG_OK;
                    msg.obj=apiResponse;
                    mHandler.sendMessage(msg);
                    Log.d("ChatActivity","onResponse: "+ apiResponse.toString());
                }
                else{
                    Log.d("ChatActivity","Request Error");
                    Log.d("ChatActivity",response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Call<Decode> call, Throwable t) {
                Log.d("ChatActivity","Network Error");
                Log.d("ChatActivity",t.getLocalizedMessage().toString());
            }
        });

    }

    class MHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                //case 0:
                //    textView.setText(inputStr[indexText % inputStr.length]);
                //    break;
                case MSG_OK:
                    if (msg.obj != null) {
                        String vlResult = (String) msg.obj;
                        showData(vlResult);
                        //paresData(vlResult);
                    }
                    break;
            }
        }

        private void paresData(String JsonData) {  //Json
            try {
                JSONObject obj = new JSONObject(JsonData);
                String content = obj.getString("text");//get from robot
                int code = obj.getInt("code");//server condition code
                updateView(code, content);
            } catch (JSONException e) {
                e.printStackTrace();
                showData("Bad Network");
            }
        }
    }


    private void showData(String message){
        ChatBean chatBean=new ChatBean();
        chatBean.setMessage(message);
        chatBean.setState(ChatBean.RECEIVE);//RECEIVE represent data from robot
        chatBeanList.add(chatBean);//add info from robot to chatBeanList
        adapter.notifyDataSetChanged();
    }

    public interface testService{
        @GET("openapi.do?keyfrom=abc&key=2032414398&type=data&doctype=json&version=1.1&q=car")
        Call<ResponseBody> saveResult(@Field("name") String name);
    }

    public interface APIService{
        @GET("/pabot/chat/")
        Call<Decode> getData(@Query("userId") String uuid, @Query("text") String text);
    }

    private void updateView(int code,String content){
        switch (code){
            case 4004:
                showData("Hey!");
                break;
            case 40005:
                showData("How are you!");
                break;
            case 40006:
                showData("How is the weather?");
                break;
            case 40007:
                showData("Bye~");
                break;
            default:
                showData(content);
                break;
        }
    }
}


package org.gaul.yass;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.ConnectException;

public class LogIn extends AppCompatActivity {
    TextInputEditText textInputEditTextUsername,textInputEditTextPassword;
    Button buttonLogIn;
    TextView textViewSignUp;
    ProgressBar progressBar;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setContentView(R.layout.activity_log_in);
        textInputEditTextUsername = findViewById(R.id.username);
        textInputEditTextPassword = findViewById(R.id.password);
        buttonLogIn=findViewById(R.id.buttonLogin);
        textViewSignUp=findViewById(R.id.signUpText);
        progressBar= findViewById(R.id.progress);
        String password,username;
        username=sharedpreferences.getString("username","");
        password=sharedpreferences.getString("password","");
        Log.i("username",username);
        Log.i("password",password);
        if(username.length()>0||password.length()>0)
        {
            textInputEditTextUsername.setText(username);
            textInputEditTextPassword.setText(password);
        }
        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
                finish();
            }
        });
        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            String password,username;
            @Override
            public void onClick(View v) {
                Log.i(null,"hello there");
                password=String.valueOf(textInputEditTextPassword.getText());
                username=String.valueOf(textInputEditTextUsername.getText());
//                Log.i(null,password+username);
                if(password.length()>0&&
                        username.length()>0) {
                    Log.i(null,"hi there");
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[2];

                            field[0] = "username";
                            field[1] = "password";
                            //Creating array for data
                            String[] data = new String[2];
                            data[0] = username;
                            data[1]= password;
                            PutData putData = new PutData("https://www.byteseq.com/apkphp/login.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    progressBar.setVisibility(View.GONE);
                                    String result = putData.getResult();
                                    if(result.equals("Login Success"))
                                    {
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                    }
                                    //End ProgressBar (Set visibility to GONE)
                                    Log.i("PutData", result);
                                }
                            }
                            //End Write and Read data with URL
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(),"all details required",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
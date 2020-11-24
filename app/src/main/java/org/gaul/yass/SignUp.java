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

public class SignUp extends AppCompatActivity {

    private TextInputEditText textInputEditTextFullName,textInputEditTextUsername,textInputEditTextPassword,
            textInputEditTextEmail;
    private Button buttonSignUp;
    private TextView textViewLogIn;
    private ProgressBar progressBar;
    public  static final String SHARED_PREFS ="sharedPrefs";
    public static final String EMAIL="email";
    public static final String USERNAME="username";
    public static final String FULLNAME="fullname";
    public static final String PASSWORD="password";
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        progressBar = findViewById(R.id.progress);
        textInputEditTextUsername = findViewById(R.id.username);
        textInputEditTextEmail = findViewById(R.id.email);
        textInputEditTextFullName = findViewById(R.id.fullname);
        textInputEditTextPassword = findViewById(R.id.password);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewLogIn = findViewById(R.id.loginText);
        if(textInputEditTextFullName==null||textInputEditTextEmail==null||textInputEditTextPassword==null||buttonSignUp==null
                ||progressBar==null)
        {
            Log.i(null,"got null");
        }
        textViewLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LogIn.class);
                startActivity(intent);
                finish();
            }
        });
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            String fullname=null,email=null,password=null,username=null;
            @Override
            public void onClick(View v) {
                if(sharedPreferences.getString("username","").equals("")==false)
                {
                    String temp="your device is already registered!";
                    Toast.makeText(getApplicationContext(),temp,Toast.LENGTH_LONG).show();
                    return;
                }
                Log.i(null,"hello there");
                if(fullname==null||email==null||password==null||username==null)
                {
                    Log.i(null,"got null");
                }
                fullname=String.valueOf(textInputEditTextFullName.getText());
                email=String.valueOf(textInputEditTextEmail.getText());
                password=String.valueOf(textInputEditTextPassword.getText());
                username=String.valueOf(textInputEditTextUsername.getText());
                if(fullname.length()>0&&email.length()>0&&password.length()>0&&
                        username.length()>0) {
                    Log.i(null,"hi there");
                    progressBar.setVisibility(View.VISIBLE);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //Starting Write and Read data with URL
                            //Creating array for parameters
                            String[] field = new String[4];
                            field[0] = "fullname";
                            field[1] = "username";
                            field[2] = "password";
                            field[3] = "email";
                            //Creating array for data
                            String[] data = new String[4];
                            data[0] = fullname;
                            data[1] = username;
                            data[2]= password;
                            data[3]=email;
                            PutData putData = new PutData("http://192.168.43.220/LoginRegister/signup.php", "POST", field, data);
                            if (putData.startPut()) {
                                if (putData.onComplete()) {
                                    progressBar.setVisibility(View.GONE);
                                    String result = putData.getResult();
                                    if(result.equals("Sign Up Success"))
                                    {
//                                      Log.i(null,fullname+email+password+username);
                                        saveData(fullname,email,username,password);
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
                                        Intent intent=new Intent(getApplicationContext(),LogIn.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        result="username or email is already taken! " +
                                                "Email and Username must be unique";
                                        Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(),"please fill all details",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
        //Start ProgressBar first (Set visibility VISIBLE)
        //Start ProgressBar first (Set visibility VISIBLE)
    }

    public void saveData(String fullname,String email,String username,String password) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(USERNAME,username);
        editor.putString(EMAIL,email);
        editor.putString(FULLNAME,fullname);
        editor.putString(PASSWORD,password);
        editor.apply();
    }

}
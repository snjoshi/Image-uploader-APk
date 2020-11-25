package org.gaul.yass;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.ConnectException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class LogIn extends AppCompatActivity {
    TextInputEditText textInputEditTextUsername,textInputEditTextPassword;
    Button buttonLogIn;
    TextView textViewSignUp,forgotPassword;
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
        forgotPassword=findViewById(R.id.forgotPassword);
        progressBar= findViewById(R.id.progress);
        String password,username;
        username=sharedpreferences.getString("username","");
        password=sharedpreferences.getString("password","");
        Log.i("username",username);
        Log.i("password",password);
        if(username.length()>0||password.length()>0)
        {
            textInputEditTextUsername.setText(username);
//            textInputEditTextPassword.setText(password);
        }
        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SignUp.class);
                startActivity(intent);
                finish();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                String password="";
//                for(int i=0;i<6;i++)
//                {
//                    int val=(int)Math.floor(Math.random()*26);
//                    password=password+(char)('a'+val);
//                }
                DialogFragment dialog = new MyDialogFragment(getApplicationContext());
                dialog.show(getSupportFragmentManager(), "MyDialogFragmentTag");
//                dialog.
//                String emailsend,emailbody,emailsubject;
//                emailsend=sharedpreferences.getString("email","");
//                emailsubject="your new password";
//                emailbody=password;
//
//                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                SharedPreferences.Editor editor=sharedPreferences.edit();
//                editor.putString("password",password);
//                editor.apply();
////                GMailSender sender = new GMailSender("username@gmail.com", "password");
////                sendFromGMail("pkc3766@gmail.com", "pkclegend@", new String[]{emailsend}, emailsubject, emailbody);
//                new sendMail().execute("pkc3766@gmail.com", "pkclegend@", emailsend, emailsubject, emailbody);
//
//
//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        //Starting Write and Read data with URL
//                        //Creating array for parameters
//                        String[] field = new String[4];
//                        field[0] = "fullname";
//                        field[1] = "username";
//                        field[2] = "password";
//                        field[3] = "email";
//                        //Creating array for data
//                        String[] data = new String[4];
//                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                        data[0] = sharedPreferences.getString("fullname","");
//                        data[1] = sharedPreferences.getString("username","");
//                        data[2]= sharedPreferences.getString("password","");
//                        data[3]= sharedPreferences.getString("email","");
//                        Log.i(null,data[0]+" "+data[1]+" "+data[2]+" "+data[3]);
//                        PutData putData = new PutData("http://192.168.43.220/LoginRegister/updateProfile.php", "POST", field, data);
//                        if (putData.startPut()) {
//                            if (putData.onComplete()) {
//                                progressBar.setVisibility(View.GONE);
//                                String result = putData.getResult();
//                                if(result.equals("profile updated Successfully"))
//                                {
////                                      Log.i(null,fullname+email+password+username);
////                                    saveData(fullname, email[0],username,password);
////                                    Toast.makeText(getApplicationContext(),"password updated successfully",Toast.LENGTH_SHORT).show();
////                                    Intent intent=new Intent(getApplicationContext(),LogIn.class);
////                                    startActivity(intent);
////                                    finish();
//                                }
//                                else{
////                                        result="username or email is already taken! " +
////                                                "Email and Username must be unique";
//                                    Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
//                                }
//                                //End ProgressBar (Set visibility to GONE)
//                                Log.i("PutData", result);
//                            }
//                        }
//                        //End Write and Read data with URL
//                    }
//                });
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
                            PutData putData = new PutData("http://192.168.43.220/LoginRegister/login.php", "POST", field, data);
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
//    class sendMail extends AsyncTask<String,Integer,Long>{
//        @Override
//        protected void onPreExecute() {
//
//        }
//
//        @Override
//        protected void onPostExecute(Long result) {
//            Toast.makeText(getApplicationContext(),"your new password is sent to your registered email id",Toast.LENGTH_LONG).show();
//        }
//
//        protected void onProgressUpdate(Integer... progress) {
//
//        }
//
//        @Override
//        protected Long doInBackground(String ... params) {
//            Properties props = System.getProperties();
//            String from=params[0], pass=params[1],subject=params[3], body=params[4];
//            String host = "smtp.gmail.com";
//            String[] to= new String[]{params[2]};
//            props.put("mail.smtp.starttls.enable", "true");
//
//            props.put("mail.smtp.ssl.trust", host);
//            props.put("mail.smtp.user", from);
//            props.put("mail.smtp.password", pass);
//            props.put("mail.smtp.port", "587");//587
//            props.put("mail.smtp.auth", "true");
//
//
//            Session session = Session.getDefaultInstance(props);
//            MimeMessage message = new MimeMessage(session);
//
//            try {
//
//
//                message.setFrom(new InternetAddress(from));
//                InternetAddress[] toAddress = new InternetAddress[to.length];
//
//                // To get the array of addresses
//                for( int i = 0; i < to.length; i++ ) {
//                    toAddress[i] = new InternetAddress(to[i]);
//                }
//
//                for( int i = 0; i < toAddress.length; i++) {
//                    message.addRecipient(Message.RecipientType.TO, toAddress[i]);
//                }
//
//
//
//                message.setSubject(subject);
//                message.setText(body);
//
//
//                Transport transport = session.getTransport("smtp");
//
//
//                transport.connect(host, from, pass);
//                transport.sendMessage(message, message.getAllRecipients());
//                transport.close();
//
//            }
//            catch (AddressException ae) {
//                ae.printStackTrace();
//            }
//            catch (MessagingException me) {
//                me.printStackTrace();
//            }
//            return 0L;
//        }
//    }
//    private static void sendFromGMail(String from, String pass, String[] to, String subject, String body) {
//
//    }
}

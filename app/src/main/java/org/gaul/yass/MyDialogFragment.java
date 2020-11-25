package org.gaul.yass;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MyDialogFragment extends DialogFragment {
    Context mContext;
    public MyDialogFragment()
    {

    }
    public MyDialogFragment(Context context)
    {
        mContext=context;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Password change");
        builder.setMessage("Your new password will be sent to your registered email id");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // You don't have to do anything here if you just
                // want it dismissed when clicked
                String password="";
                for(int i=0;i<6;i++)
                {
                    int val=(int)Math.floor(Math.random()*26);
                    password=password+(char)('a'+val);
                }
//                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                String emailsend,emailbody,emailsubject;
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                emailsend=sharedPreferences.getString("email","");
                emailsubject="your new password";
                emailbody=password;
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("password",password);
                editor.apply();
//                GMailSender sender = new GMailSender("username@gmail.com", "password");
//                sendFromGMail("pkc3766@gmail.com", "pkclegend@", new String[]{emailsend}, emailsubject, emailbody);
                new sendMail().execute("pkc3766@gmail.com", "pkclegend@", emailsend, emailsubject, emailbody);


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
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                        data[0] = sharedPreferences.getString("fullname","");
                        data[1] = sharedPreferences.getString("username","");
                        data[2]= sharedPreferences.getString("password","");
                        data[3]= sharedPreferences.getString("email","");
                        Log.i(null,data[0]+" "+data[1]+" "+data[2]+" "+data[3]);
                        PutData putData = new PutData("http://192.168.43.220/LoginRegister/updateProfile.php", "POST", field, data);
                        if (putData.startPut()) {
                            if (putData.onComplete()) {
//                                progressBar.setVisibility(View.GONE);
                                String result = putData.getResult();
                                if(result.equals("profile updated Successfully"))
                                {
//                                      Log.i(null,fullname+email+password+username);
//                                    saveData(fullname, email[0],username,password);
//                                    Toast.makeText(getApplicationContext(),"password updated successfully",Toast.LENGTH_SHORT).show();
//                                    Intent intent=new Intent(getApplicationContext(),LogIn.class);
//                                    startActivity(intent);
//                                    finish();
                                }
                                else{
//                                        result="username or email is already taken! " +
//                                                "Email and Username must be unique";
//                                    Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                                }
                                //End ProgressBar (Set visibility to GONE)
                                Log.i("PutData", result);
                            }
                        }
                        //End Write and Read data with URL
                    }
                });
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    private   class sendMail extends AsyncTask<String,Integer,Long> {
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(Long result) {
            Toast.makeText(mContext,"your new password is sent to your registered email id",Toast.LENGTH_LONG).show();
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected Long doInBackground(String ... params) {
            Properties props = System.getProperties();
            String from=params[0], pass=params[1],subject=params[3], body=params[4];
            String host = "smtp.gmail.com";
            String[] to= new String[]{params[2]};
            props.put("mail.smtp.starttls.enable", "true");

            props.put("mail.smtp.ssl.trust", host);
            props.put("mail.smtp.user", from);
            props.put("mail.smtp.password", pass);
            props.put("mail.smtp.port", "587");//587
            props.put("mail.smtp.auth", "true");


            Session session = Session.getDefaultInstance(props);
            MimeMessage message = new MimeMessage(session);

            try {


                message.setFrom(new InternetAddress(from));
                InternetAddress[] toAddress = new InternetAddress[to.length];

                // To get the array of addresses
                for( int i = 0; i < to.length; i++ ) {
                    toAddress[i] = new InternetAddress(to[i]);
                }

                for( int i = 0; i < toAddress.length; i++) {
                    message.addRecipient(Message.RecipientType.TO, toAddress[i]);
                }



                message.setSubject(subject);
                message.setText(body);


                Transport transport = session.getTransport("smtp");


                transport.connect(host, from, pass);
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();

            }
            catch (AddressException ae) {
                ae.printStackTrace();
            }
            catch (MessagingException me) {
                me.printStackTrace();
            }
            return 2L;
        }
    }
}

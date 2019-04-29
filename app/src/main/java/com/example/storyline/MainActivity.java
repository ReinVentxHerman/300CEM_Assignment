package com.example.storyline;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    EditText username, password;
    Button loginButton, signUpButton;
    TextView errorMessage;

    LoginTask loginTask = null;

    SharedPreferences sharedPref;
    public static final String LOGIN_FAIL="Username not found or password incorrect Login Failed!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences(getString(R.string.code_prep), Context.MODE_PRIVATE);

        username = findViewById(R.id.LoginUsername);
        password = findViewById(R.id.LoginPassword);
        loginButton = findViewById(R.id.loginButton);
        signUpButton=findViewById(R.id.signUpPageButton);
        errorMessage = findViewById(R.id.errorMessage);

        errorMessage.setText("");

        username.setText("Herman");
        password.setText("123");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorMessage.setText("");
                loginTask = new LoginTask(username.getText().toString(), password.getText().toString());
                loginTask.execute();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegisterActivity();
            }
        });

    }

    public void startRegisterActivity(){
        startActivity(new Intent(this,RegisterActivity.class));
    }
    public void startHomeActivity(){
        startActivity(new Intent(this,HomeActivity.class));
    }

    class LoginTask extends AsyncTask<String, String, String> {

        private String name, password;

        public LoginTask(String name,String password) {
            this.name = name;
            this.password = password;
        }

        protected String doInBackground(String... url) {
            return NetworkHelper.login(name,password);
        }

        protected void onPostExecute(String s) {
            if (s!=null){
                System.out.println("LoginTask : "+s);
                //["Herman","1","123"]
                if (!s.equals("false")) {
                    JSONArray array;
                    String id,name;
                    try {
                        array =new JSONArray(s);
                        id=array.getString(1);
                        name=array.getString(0);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        errorMessage.setText(getString(R.string.defaultError));
                        return;
                    }

                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(getString(R.string.code_id),id);
                    editor.putString(getString(R.string.code_name),name);
                    editor.commit();
                    startHomeActivity();
                }else {
                    errorMessage.setText(LOGIN_FAIL);
                }
            }else {
                errorMessage.setText(getString(R.string.defaultError));
            }

        }
    }
}





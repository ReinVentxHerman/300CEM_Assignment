package com.example.storyline;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {

    private static final String NAME_ERROR="Username is invalid",
            PASSWORD_ERROR="Password is invalid",
            PASSWORD_CONFIRM_ERROR="Password not match",
            NAME_USED_ERROR="Username has been taken",
            REGISTERED="Your account is now ready!";


    EditText SignUpUsername, SignUpPassword, SignUpPasswordConfirm;
    TextView errorMessage, processMessage;
    Button SignUp;

    RegisterTask registerTask;

    public static boolean isPasswordConfirmOk(String p1, String p2) {
        if (!isEmptyString(p1))
            return p1.equals(p2);
        return false;
    }

    public static boolean isEmptyString(String s) {
        return s.equals("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerTask=null;

        SignUpUsername = findViewById(R.id.SignUpUsername);
        SignUpPassword = findViewById(R.id.SignUpPassword);
        SignUpPasswordConfirm = findViewById(R.id.SignUpPasswordConfirm);
        processMessage=findViewById(R.id.SignUpProcessMessage);
        errorMessage = findViewById(R.id.SignUpErrorMessage);
        SignUp = findViewById(R.id.SignUpButton);

        setProcessMessage("");
        setErrorMessage("");

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setErrorMessage("");
                setProcessMessage(getString(R.string.process));

                if (!isEmptyString(SignUpUsername.getText().toString())) {
                    if (!isEmptyString(SignUpPassword.getText().toString())) {
                        if (isPasswordConfirmOk(SignUpPassword.getText().toString(), SignUpPasswordConfirm.getText().toString())) {
                            registerTask=new RegisterTask(SignUpUsername.getText().toString(),SignUpPassword.getText().toString());
                            registerTask.execute();

                        }else {
                            setErrorMessage(PASSWORD_CONFIRM_ERROR);
                        }
                    }else {
                        setErrorMessage(PASSWORD_ERROR);
                    }
                }else{
                    setErrorMessage(NAME_ERROR);
                }


            }
        });

    }

    private void setErrorMessage(String s){
        errorMessage.setText(s);
    }

    private void setProcessMessage(String s){
        processMessage.setText(s);
    }


    class RegisterTask extends AsyncTask<String, String, String> {

        private String name, password;

        private static final String NAME_USED_ERROR_CODE="name_used_error";

        public RegisterTask(String name, String password) {
            this.name = name;
            this.password = password;
        }

        protected String doInBackground(String... url) {
            return NetworkHelper.register(name,password);
        }

        protected void onPostExecute(String s) {
            System.out.println("RegisterTask : "+s);
            setProcessMessage("");
            if (s!=null){

                switch (s){
                    case NAME_USED_ERROR_CODE:
                        System.out.println("that username has been used");
                        setErrorMessage(NAME_USED_ERROR);

                        break;
                    case "true":
                        System.out.println("account is now ready");
                        setProcessMessage(REGISTERED);

                        break;
                     default:
                         setErrorMessage(getString(R.string.defaultError));
                }
            }
        }
    }
}



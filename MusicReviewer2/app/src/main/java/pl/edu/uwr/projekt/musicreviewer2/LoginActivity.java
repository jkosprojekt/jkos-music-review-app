package pl.edu.uwr.projekt.musicreviewer2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private AccountHandler accountHandler;

    private EditText mUsernameLogin;
    private EditText mPasswordLogin;
    private EditText mEmailRegister;
    private EditText mUsernameRegister;
    private EditText mPasswordRegister;
    private boolean loggedIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loggedIn = false;
        mUsernameLogin = findViewById(R.id.username_login);
        mPasswordLogin = findViewById(R.id.password_login);
        mEmailRegister = findViewById(R.id.email_register);
        mUsernameRegister = findViewById(R.id.username_register);
        mPasswordRegister = findViewById(R.id.password_register);
        Button mLoginButton = findViewById(R.id.button_login);
        Button mRegisterButton = findViewById(R.id.button_register);
        accountHandler = new AccountHandler(this);
        mLoginButton.setOnClickListener(v -> {
            String lUsername = mUsernameLogin.getText().toString();
            String lPassword = mPasswordLogin.getText().toString();
            Cursor cursor = accountHandler.getAccount(lUsername);

            if(cursor.getCount() == 0)
                Toast.makeText(this,"ACCOUNT DOES NOT EXIST", Toast.LENGTH_SHORT).show();
            else {
                while (cursor.moveToNext()) {
                    String username = cursor.getString(1);
                    String password = cursor.getString(2);
                    if (username.equals(lUsername) && password.equals(lPassword)) {
                        loggedIn = true;
                        Toast.makeText(this, "LOGGED IN", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("parcelName", lUsername);
                        intent.putExtra("parcelLogged", loggedIn);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "INCORRECT PASSWORD", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        mRegisterButton.setOnClickListener(view -> {
            String email = mEmailRegister.getText().toString();
            String rUsername = mUsernameRegister.getText().toString();
            String rPassword = mPasswordRegister.getText().toString();
            if(email.length()>0 && rUsername.length()>0 && rPassword.length()>0) {
                if(accountHandler.getAccount(rUsername).getCount()>0) {
                    Toast.makeText(this, "EMAIL OR USERNAME ALREADY TAKEN", Toast.LENGTH_SHORT).show();
                }
                else {
                    Acc acc = new Acc(email, rUsername, rPassword);
                    accountHandler.addAccount(acc);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("parcelName", rUsername);
                    intent.putExtra("parcelLogged", loggedIn);
                    startActivity(intent);
                }
            }
            else{
                Toast.makeText(this, "MAKE SURE YOU FILLED ALL AREAS", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
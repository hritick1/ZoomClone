package com.example.zoomclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zoomclone.call.activities.BaseActivity;
import com.example.zoomclone.call.services.LoginService;
import com.example.zoomclone.call.utils.Consts;
import com.example.zoomclone.call.utils.QBEntityCallbackImpl;
import com.example.zoomclone.call.utils.SharedPrefsHelper;
import com.example.zoomclone.call.utils.ToastUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.Arrays;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 100;
    private EditText edittextEmail;
       private EditText edittextPass;
       private TextView textViewTitle;
       private TextView textViewCancel;
       private AppCompatButton buttonSignin;
       private AppCompatButton buttonSigninWithGoogle;
       private AppCompatButton buttonSigninWithFb;
   private CallbackManager callbackManager;
    private LoginButton loginButton;
    private String TAG=LoginManager.class.getSimpleName();
    private static final int REQ_ONE_TAP = 2;  // Can be any integer unique to the Activity.
    private boolean showOneTapUI = true;
    private QBUser userForSave;


    private FirebaseAuth mAuth;

    private SignInClient oneTapClient;

    private GoogleSignInClient mGoogleSignInClient;
    private boolean isSignIn;

private String name;
private  String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        isSignIn=getIntent().getBooleanExtra("isSignIn",false);
        intiViews();
        clickListener();
        configureGoogleSignIn();
        if(!isSignIn){
            textViewTitle.setText("Sign Up");
            buttonSignin.setText("Sign Up");
        }
    }
    private void intiViews() {
        edittextEmail=findViewById(R.id.editTextEmail);
        edittextPass=findViewById(R.id.editTextPassword);
        textViewTitle=findViewById(R.id.textViewTitle);
        textViewCancel=findViewById(R.id.textViewCancel);
        buttonSignin=findViewById(R.id.btnSignIn);
        buttonSigninWithGoogle=findViewById(R.id.btnLoginWithGoogle);
        buttonSigninWithFb=findViewById(R.id.btnLoginWithFb);
        loginButton = (LoginButton) findViewById(R.id.login_button);
    }
    private void clickListener() {
        textViewCancel.setOnClickListener(this);
        buttonSignin.setOnClickListener(this);
        buttonSigninWithGoogle.setOnClickListener(this);
        buttonSigninWithFb.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.textViewCancel:
                finish();
                break;
            case R.id.btnSignIn:
                if(edittextEmail.getText().toString().isEmpty()){
                    Toast.makeText(this, "Please Enter the email", Toast.LENGTH_SHORT).show();
                }
                else  if(edittextPass.getText().toString().isEmpty()){
                    Toast.makeText(this, "Please Enter the Password", Toast.LENGTH_SHORT).show();
                }
                else  if(!isValidEmail(edittextEmail.getText().toString())){
                    Toast.makeText(this, "Please Enter the Correct email", Toast.LENGTH_SHORT).show();
                }
                else if(edittextPass.getText().toString().length()<6){
                    Toast.makeText(this, "Please Enter A Strong Password", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(isSignIn)
                        emailSignIn();
                    else
                        emailSignUp();
                }

                break;
            case R.id.btnLoginWithGoogle:
googleSignIn();
                break;
            case R.id.btnLoginWithFb:
              facebookLogin();

                break;
        }
    }
    private void facebookLogin(){
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile"));
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            userForSave = createUserWithEnteredData();
                            startSignUpNewUser(userForSave);
                            Toast.makeText(LoginActivity.this, user.getDisplayName(), Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try{
                    GoogleSignInAccount account=task.getResult(ApiException.class);
                    firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {

                }

            }
       else callbackManager.onActivityResult(requestCode, resultCode, data);


    }

    private void firebaseAuthWithGoogle(String idToken){




        if (idToken !=  null) {
            // Got an ID token from Google. Use it to authenticate
            // with Firebase.
            AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
            mAuth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithCredential:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(LoginActivity.this, user.getDisplayName(), Toast.LENGTH_SHORT).show();
                                userForSave = createUserWithEnteredData();
                                startSignUpNewUser(userForSave);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithCredential:failure", task.getException());
                            
                            }
                        }
                    });
        }
    }
    private void emailSignUp(){
        mAuth.createUserWithEmailAndPassword(edittextEmail.getText().toString(), edittextPass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            userForSave = createUserWithEnteredData();
                            startSignUpNewUser(userForSave);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void emailSignIn(){
        mAuth.signInWithEmailAndPassword(edittextEmail.getText().toString(), edittextPass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            userForSave = createUserWithEnteredData();
                            startSignUpNewUser(userForSave);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });


    }
    private void configureGoogleSignIn() {

        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
mGoogleSignInClient=com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(this, gso);
    }
    private void googleSignIn()
    {
        Intent sIntent=mGoogleSignInClient.getSignInIntent();
        startActivityForResult(sIntent,RC_SIGN_IN);

    }
    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    private void startLoginService(QBUser qbUser) {
        Intent tempIntent = new Intent(this, LoginService.class);
        PendingIntent pendingIntent = createPendingResult(Consts.EXTRA_LOGIN_RESULT_CODE, tempIntent, 0);
        LoginService.start(this, qbUser, pendingIntent);
    }
    private void signInCreatedUser(final QBUser qbUser) {
        Log.d(TAG, "SignIn Started");
        requestExecutor.signInUser(qbUser, new QBEntityCallbackImpl<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle params) {
                Log.d(TAG, "SignIn Successful");
                sharedPrefsHelper.saveQbUser(userForSave);
                updateUserOnServer(qbUser);
            }

            @Override
            public void onError(QBResponseException responseException) {
                Log.d(TAG, "Error SignIn" + responseException.getMessage());
                hideProgressDialog();
                ToastUtils.longToast(R.string.sign_in_error);
            }
        });
    }

    private void updateUserOnServer(QBUser user) {
        user.setPassword(null);
        QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                hideProgressDialog();
//                OpponentsActivity.start(LoginActivity.this);
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                hideProgressDialog();
                ToastUtils.longToast(R.string.update_user_error);
            }
        });
    }
    private void loginToChat(final QBUser qbUser) {
        qbUser.setPassword(App.USER_DEFAULT_PASSWORD);
        userForSave = qbUser;
        startLoginService(qbUser);
    }

    private void saveUserData(QBUser qbUser) {
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        sharedPrefsHelper.saveQbUser(qbUser);
    }

    private QBUser createUserWithEnteredData() {
        return createQBUserWithCurrentData(userId,name);
    }

    private QBUser createQBUserWithCurrentData(String userLogin, String userFullName) {
        QBUser qbUser = null;
        if (!TextUtils.isEmpty(userLogin) && !TextUtils.isEmpty(userFullName)) {
            qbUser = new QBUser();
            qbUser.setLogin(userLogin);
            qbUser.setFullName(userFullName);
            qbUser.setPassword(App.USER_DEFAULT_PASSWORD);
        }
        return qbUser;
    }
    private void startSignUpNewUser(final QBUser newUser) {
        Log.d(TAG, "SignUp New User");
        showProgressDialog(R.string.dlg_creating_new_user);
        requestExecutor.signUpNewUser(newUser, new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser result, Bundle params) {
                        Log.d(TAG, "SignUp Successful");
                        saveUserData(newUser);
                        loginToChat(result);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.d(TAG, "Error SignUp" + e.getMessage());
                        if (e.getHttpStatusCode() == Consts.ERR_LOGIN_ALREADY_TAKEN_HTTP_STATUS) {
                            signInCreatedUser(newUser);
                        } else {
                            hideProgressDialog();
                            ToastUtils.longToast(R.string.sign_up_error);
                        }
                    }
                }
        );
    }
}
//FirebaseAuth.getInstance().signOut();
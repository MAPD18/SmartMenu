package ca.mapd.capstone.smartmenu.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import ca.mapd.capstone.smartmenu.R;
import ca.mapd.capstone.smartmenu.customer.helper.FirebaseKeylist;
import com.google.android.gms.auth.api.Auth;



public abstract class AuthAbstractActivity
        extends AppCompatActivity
        implements GoogleApiClient.OnConnectionFailedListener{
    /*
    * this is the base activity for most activities (all activities except the About Page)
    * all essential activities should implement this activity
    * provides all essential authentication methods to be used by activities (such as googleSignIn) */
    private GoogleApiClient m_APIClient;
    protected FirebaseAuth m_Auth;
    protected FirebaseAuth.AuthStateListener m_AuthStateListener;
    private static final int RC_SIGN_IN = 9001; // sign in status code
    private static final String LOG_TAG = "AuthAbstractActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // build api client for google (authentication) services
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id_google))
                .requestEmail()
                .build();
        m_APIClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        m_Auth = FirebaseAuth.getInstance(); //FirebaseAuth instance
        // create the listener to detect when a user has logged in or logged out
        m_AuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null){
                    onLoginSuccessful();
                }
                else{
                    onNotLoggedIn();
                }
            }
        };
        m_Auth.addAuthStateListener(m_AuthStateListener);
    }

    protected void onLoginSuccessful(){
        // this method is called when the activity detects that the user is logged in
        // note that devs can override this method and specify custom behavior on login
    }

    protected void onLoginFailed(){

    }

    protected void onNotLoggedIn(){
        // this method is called when the activity detects that the user is logged out
        // note that devs can override this method and specify custom behavior on logout
        Log.e(LOG_TAG, "OnNotLoggedIn is fired!");
//        Toast.makeText(this, getString(R.string.err_user_failed_to_authenticate), Toast.LENGTH_SHORT)
//        .show();
        finish(); //exit out of the activity
    }

    protected void googleSignOut(){
        // activities may call this method to indicate that the user wishes to sign out
        // basically causes the user to be unauthenticated
        m_Auth.signOut();
        Auth.GoogleSignInApi.signOut(m_APIClient);
        Auth.GoogleSignInApi.revokeAccess(m_APIClient);
    }

    protected void googleSignIn(){
        // activities may call this method to indicate a user wishes to be authenticated
        Intent startIntent = Auth.GoogleSignInApi.getSignInIntent(m_APIClient);
        startActivityForResult(startIntent, RC_SIGN_IN); // start Google's default Sign-In activity
    }

    protected void signInWithEmailAndPassword(String email, String password) {
        m_Auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            buildUserProfile();
                        }
                    }
                })
        .addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onLoginFailed();
            }
        });
    }

    private void handleSignInResult(GoogleSignInResult result){
        // this method handles sign in results produced by Google Sign-In (signing in using Google ID)
        // @param result: the result of signing in through Google Sign-in, returned by the Sign-In activity
        Log.d("Google Sign-in", "handleSignInResult: " + result.isSuccess());
        Log.d("Google Sign-in", "handleSignInResult: " + String.valueOf(result.getStatus().getStatusCode()));
        if (result.isSuccess()){
            GoogleSignInAccount acct = result.getSignInAccount(); // get the resulting account
            fireBaseAuthWithGoogle(acct);
        }
        else{
            // Failed to log in
            Log.e("FBaseAuthSignin", "Failed to sign in!");
            Toast.makeText(this, getString(R.string.err_user_failed_to_authenticate), Toast.LENGTH_SHORT).show();
        }
    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount acct) {
        // this method uses the received Google Account credential to authenticate the user into customer
        // using FirebaseAuth
        // @param GoogleSignInAccount : a Google Account credential
        Log.d("FirebaseAuth", "Firebaseauthwithgoogle: " + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        m_Auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!(task.isSuccessful())){
                            Log.w("Rofl", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), getString(R.string.err_user_failed_to_authenticate),
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            buildUserProfile();
                        }
                    }
                });
    }

    private void buildUserProfile(){
        // builds the user profile database for a user once he's signed in
        FirebaseUser currentUser = m_Auth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(FirebaseKeylist.USER_PROFILE);
            userRef.child(currentUser.getUid())
                    .child(FirebaseKeylist.USER_PROFILE_NAME)
                    .setValue(currentUser.getDisplayName()); // store the user's name into the database
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // when connection to the internet fails
        Toast.makeText(this, getString(R.string.err_firebase_unavailable), Toast.LENGTH_SHORT)
        .show();
    }

    @Override
    public void onPause(){
        m_Auth.removeAuthStateListener(m_AuthStateListener);
        super.onPause();
    }

    @Override
    public void onResume(){
        m_Auth.addAuthStateListener(m_AuthStateListener);
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
}

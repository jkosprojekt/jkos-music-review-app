package pl.edu.uwr.projekt.musicreviewer2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    private AccountHandler accountHandler;
    public static ArrayList<Review> reviews = new ArrayList<>();
    private String currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        TextView nameTextView = findViewById(R.id.profile_name_text_view);
        RecyclerView recyclerViewAcc = findViewById(R.id.recycler_view_acc);
        dbHandler = new DBHandler(this);
        accountHandler = new AccountHandler(this);
        Intent intent = getIntent();
        currentAccount = intent.getStringExtra("mAccount");
        nameTextView.setText(currentAccount);
        getAccReviews(currentAccount);

        FloatingActionButton settingsFab = findViewById(R.id.settings_fab);
        FloatingActionButton logoutFab = findViewById(R.id.logout_fab);
        if(!currentAccount.equals(MainActivity.CURR_ACCOUNT)) {
            settingsFab.setVisibility(View.INVISIBLE);
            logoutFab.setVisibility(View.INVISIBLE);
        }

        ProfileActivity.ReviewAdapter2 adapter = new ProfileActivity.ReviewAdapter2(this, reviews);
        recyclerViewAcc.setAdapter(adapter);
        recyclerViewAcc.setLayoutManager(new LinearLayoutManager(this));
    }
    private void getAccReviews(String acc){
        reviews.clear();
        Cursor cursor = dbHandler.getAccountsReviews(acc);

        if(cursor.getCount() == 0)
            Toast.makeText(this,"EMPTY", Toast.LENGTH_SHORT).show();
        else {
            while (cursor.moveToNext()) {
                String artist = cursor.getString(0);
                String title = cursor.getString(1);
                String year = cursor.getString(2);
                String genre = cursor.getString(3);
                String type = cursor.getString(4);
                String score = cursor.getString(5);
                String review = cursor.getString(6);
                String account = cursor.getString(7);
                reviews.add(new Review(artist, title, year, genre, score, type, review, account));
            }
        }
    }

    public void logOut(View view) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private Dialog settingsDialogSetup(){
        Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.dialog_settings);

        return dialog;
    }

    public void goToSettings(View view) {

            Dialog dialog = settingsDialogSetup();

            Button changeNameButton = dialog.findViewById(R.id.change_name_button);
            Button changePasswordButton = dialog.findViewById(R.id.change_password_button);
            EditText changeNameEditText = dialog.findViewById(R.id.change_name_edit_text);
            EditText oldPasswordEditText = dialog.findViewById(R.id.old_password_edit_text);
            EditText newPasswordEditText = dialog.findViewById(R.id.new_password_edit_text);
            Button cancelButton = dialog.findViewById(R.id.cancel_settings_button);
            Button deleteAccountButton = dialog.findViewById(R.id.delete_account_button);


            changeNameEditText.setText(currentAccount);

            changeNameButton.setOnClickListener(v -> {
                String name = changeNameEditText.getText().toString();
                accountHandler.changeUsername(currentAccount, name);
                dbHandler.updateUsername(currentAccount, name);
                dialog.dismiss();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.putString("keyName", name);
                editor.putBoolean("keyLogged", true);
                editor.apply();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            });

            changePasswordButton.setOnClickListener(v -> {
                String currentPassword = accountHandler.getPassword(currentAccount).toString();
                String oldPassword = oldPasswordEditText.getText().toString();
                String newPassword = newPasswordEditText.getText().toString();
                if (oldPassword.equals(currentPassword) && newPassword.length()>0) {
                    accountHandler.changePassword(currentAccount, newPassword);
                    dialog.dismiss();
                    Intent intent = new Intent(this, ProfileActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(this, "INCORRECT", Toast.LENGTH_SHORT).show();
                }
            });

            cancelButton.setOnClickListener(v -> dialog.dismiss());

            deleteAccountButton.setOnClickListener(v -> {
                accountHandler.deleteAccount(currentAccount);
                dbHandler.deleteContent(currentAccount);
                dialog.dismiss();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
            });

            dialog.show();

        }


    private class ReviewAdapter2
            extends RecyclerView.Adapter<ProfileActivity.ReviewAdapter2.ViewHolder> {

        private final ArrayList<Review> mReviewList;
        private final LayoutInflater mInflater;

        public ReviewAdapter2(Context context, ArrayList<Review> reviewList) {
            mInflater = LayoutInflater.from(context);
            this.mReviewList = reviewList;
        }
        private class ViewHolder extends RecyclerView.ViewHolder{
            public ViewHolder(@NonNull View itemView){ super(itemView); }

            private final TextView artistTextView = itemView.findViewById(R.id.text_view_item_artist);
            private final TextView titleTextView = itemView.findViewById(R.id.text_view_item_title);
            private final TextView scoreTextView = itemView.findViewById(R.id.text_view_item_score);
            private final Button readButton = itemView.findViewById(R.id.button_read_item);

        }

        @NonNull
        @Override
        public ProfileActivity.ReviewAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ProfileActivity.ReviewAdapter2.ViewHolder(mInflater.inflate(R.layout.item_view, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ProfileActivity.ReviewAdapter2.ViewHolder holder, int position) {

            Review currentReview = mReviewList.get(position);
            holder.artistTextView.setText(currentReview.getArtist());
            holder.titleTextView.setText(currentReview.getTitle());
            holder.scoreTextView.setText(currentReview.getScore());
            holder.readButton.setOnClickListener(view -> {
                Intent intent = new Intent(ProfileActivity.this, ReviewActivity.class);
                intent.putExtra("parcel", position);
                intent.putExtra("parcelAccount", currentAccount);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() { return mReviewList.size(); }
    }
}
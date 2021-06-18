package pl.edu.uwr.projekt.musicreviewer2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ReviewActivity extends AppCompatActivity {

    private DBHandler dbHandler;

    private String currentArtist;
    private String currentTitle;
    private String currentGenre;
    private String currentReviewContent;
    private String account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        TextView mArtistText = findViewById(R.id.artist_text_view);
        TextView mTitleText = findViewById(R.id.title_text_view);
        TextView mScoreText = findViewById(R.id.score_text_view);
        TextView mGenreText = findViewById(R.id.genre_text_view);
        TextView mYearText = findViewById(R.id.year_text_view);
        TextView mTypeText = findViewById(R.id.type_text_view);
        TextView mReviewText = findViewById(R.id.review_text_view);
        TextView mAccountText = findViewById(R.id.account_text_view);
        TextView goToAccountText = findViewById(R.id.go_to_account_text_view);
        FloatingActionButton mEditFab = findViewById(R.id.edit_fab);
        FloatingActionButton mDeleteFab = findViewById(R.id.delete_fab);

        dbHandler = new DBHandler(this);
        Intent intent = getIntent();
        int position = intent.getIntExtra("parcel", 0);
        String loggedAccount = intent.getStringExtra("parcelAccount");
        Review currentReview = MainActivity.reviews.get(position);

        currentArtist = currentReview.getArtist();
        currentTitle = currentReview.getTitle();
        currentGenre = currentReview.getGenre();
        currentReviewContent = currentReview.getReview();

        account = currentReview.getAccount();
        goToAccountText.setText(account);
        mAccountText.setText(account);
        mArtistText.setText(currentReview.getArtist());
        mTitleText.setText(currentReview.getTitle());
        mScoreText.setText(currentReview.getScore());
        mGenreText.setText(currentReview.getGenre());
        mYearText.setText(currentReview.getYear());
        mTypeText.setText(currentReview.getType());
        mReviewText.setText(currentReview.getReview());

        if (!loggedAccount.equals(account)){
            mEditFab.setVisibility(View.INVISIBLE);
            mDeleteFab.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onDestroy() {
        dbHandler.close();
        super.onDestroy();
    }
    private Dialog postReviewDialogSetup(){
        Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.dialog_update);

        return dialog;
    }

    public void editReview(View view) {

        Dialog dialog = postReviewDialogSetup();

        Button postButton = dialog.findViewById(R.id.post_button);
        Button cancelButton = dialog.findViewById(R.id.cancel_button);

        EditText artistEditText = dialog.findViewById(R.id.edit_text_artist);
        EditText titleEditText = dialog.findViewById(R.id.edit_text_title);
        Spinner yearSpinner = dialog.findViewById(R.id.year_spinner);
        EditText genreEditText = dialog.findViewById(R.id.edit_text_genre);
        Spinner scoreSpinner = dialog.findViewById(R.id.score_spinner);
        Spinner typeSpinner = dialog.findViewById(R.id.type_spinner);
        EditText reviewEditText = dialog.findViewById(R.id.edit_text_review);

        artistEditText.setText(currentArtist);
        titleEditText.setText(currentTitle);
        genreEditText.setText(currentGenre);
        reviewEditText.setText(currentReviewContent);

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(this,
                R.array.year_array, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        ArrayAdapter<CharSequence> scoreAdapter = ArrayAdapter.createFromResource(this,
                R.array.score_array, android.R.layout.simple_spinner_item);
        scoreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scoreSpinner.setAdapter(scoreAdapter);

        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        postButton.setOnClickListener(v -> {

            String artist = artistEditText.getText().toString();
            String title = titleEditText.getText().toString();
            String year = yearSpinner.getSelectedItem().toString();
            String genre = genreEditText.getText().toString();
            String score = scoreSpinner.getSelectedItem().toString();
            String type = typeSpinner.getSelectedItem().toString();
            String review = reviewEditText.getText().toString();

            dbHandler.updateReview(currentTitle, artist, title, year, genre, score, type, review, account);
            dialog.dismiss();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("parcelName", account);
            intent.putExtra("parcelLogged", true);
            startActivity(intent);
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }

    public void deleteReview(View view) {
        dbHandler.deleteReview(currentTitle, account);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void rGoToAccount(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("mAccount", account);
        startActivity(intent);
    }
}
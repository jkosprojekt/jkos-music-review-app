package pl.edu.uwr.projekt.musicreviewer2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    public static ArrayList<Review> reviews = new ArrayList<>();
    public static String CURR_ACCOUNT;
    private String currentAccount;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchEditText = findViewById(R.id.search_bar);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        dbHandler = new DBHandler(this);
        dbHandler.deleteReview("title", "account");
        getReviews();

        Intent intent1 = getIntent();
        boolean loggedIn = intent1.getBooleanExtra("parcelLogged", false);
        currentAccount = intent1.getStringExtra("parcelName");
        CURR_ACCOUNT = currentAccount;
        TextView myAccountTextView = findViewById(R.id.my_account_text_view);
        myAccountTextView.setText(currentAccount);

        ReviewAdapter adapter = new ReviewAdapter(this, reviews);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (!loggedIn) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        dbHandler.close();
        super.onDestroy();
    }
    private void getReviews(){
        reviews.clear();
        Cursor cursor = dbHandler.getReview();

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

    private Dialog postReviewDialogSetup(){
        Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.dialog_update);

        return dialog;
    }
    public void addNewReview(View view){
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
            String account = currentAccount;

            Review newReview = new Review(artist, title, year, genre, score, type, review, account);
            dbHandler.addReview(newReview);
            dialog.dismiss();
            this.recreate();
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }

    public void goToAccount(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("mAccount", currentAccount);
        startActivity(intent);
    }

    public void searchReviews(View view) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("search", searchEditText.getText().toString());
        intent.putExtra("sort", 1);
        startActivity(intent);
    }


    private class ReviewAdapter
            extends RecyclerView.Adapter<MainActivity.ReviewAdapter.ViewHolder> {

        private final ArrayList<Review> mReviewList;
        private final LayoutInflater mInflater;

        public ReviewAdapter(Context context, ArrayList<Review> reviewList) {
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
        public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ReviewAdapter.ViewHolder(mInflater.inflate(R.layout.item_view, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {

            Review currentReview = mReviewList.get(position);
            holder.artistTextView.setText(currentReview.getArtist());
            holder.titleTextView.setText(currentReview.getTitle());
            holder.scoreTextView.setText(currentReview.getScore());
            holder.readButton.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, ReviewActivity.class);
                intent.putExtra("parcel", position);
                intent.putExtra("parcelAccount", currentAccount);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() { return mReviewList.size(); }
    }

}
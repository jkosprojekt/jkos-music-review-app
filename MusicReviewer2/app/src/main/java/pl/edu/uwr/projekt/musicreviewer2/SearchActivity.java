package pl.edu.uwr.projekt.musicreviewer2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private DBHandler dbHandler;
    public static ArrayList<Review> reviews = new ArrayList<>();
    private String searching;
    private EditText searchBar;
    private Spinner sortSpinner;
    private Button sortButton;
    private int sortCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchBar = findViewById(R.id.sort_search_bar);
        sortSpinner = findViewById(R.id.sort_spinner);
        sortButton = findViewById(R.id.sort_button);
        RecyclerView recyclerViewSort = findViewById(R.id.sort_recycler_view);
        dbHandler = new DBHandler(this);
        Intent intent = getIntent();
        searching = intent.getStringExtra("search");
        sortCode = intent.getIntExtra("sort", 1);
        searchReviews(searching, sortCode);

        SearchActivity.ReviewAdapter3 adapter = new SearchActivity.ReviewAdapter3(this, reviews);
        recyclerViewSort.setAdapter(adapter);
        recyclerViewSort.setLayoutManager(new LinearLayoutManager(this));

        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_array, android.R.layout.simple_spinner_item);
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortAdapter);

        String sortBy = sortSpinner.getSelectedItem().toString();

        sortButton.setOnClickListener(v -> {

            if(sortBy.equals("Artist")){
                Intent intent1 = new Intent(this, SearchActivity.class);
                intent1.putExtra("search", searching);
                intent1.putExtra("sort", 2);
                startActivity(intent1);
            }
            else if(sortBy.equals("Genre")){
                Intent intent1 = new Intent(this, SearchActivity.class);
                intent1.putExtra("search", searching);
                intent1.putExtra("sort", 3);
                startActivity(intent1);
            }
            else if(sortBy.equals("Account")){
                Intent intent1 = new Intent(this, SearchActivity.class);
                intent1.putExtra("search", searching);
                intent1.putExtra("sort", 4);
                startActivity(intent1);
            }
            else if(sortBy.equals("Score")){
                Intent intent1 = new Intent(this, SearchActivity.class);
                intent1.putExtra("search", searching);
                intent1.putExtra("sort", 5);
                startActivity(intent1);
            }
            else {
                Intent intent1 = new Intent(this, SearchActivity.class);
                intent1.putExtra("search", searching);
                intent1.putExtra("sort", 1);
                startActivity(intent1);
            }
        });
    }

    public void sortSearchReviews(View view) {
        Intent intent1 = new Intent(this, SearchActivity.class);
        intent1.putExtra("search", searchBar.getText().toString());
        intent1.putExtra("sort", 1);
        startActivity(intent1);
    }

    private void searchReviews(String search, int sort){
        reviews.clear();
        Cursor cursor = dbHandler.getSearchedReviews(search, sort);

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

    private class ReviewAdapter3
            extends RecyclerView.Adapter<SearchActivity.ReviewAdapter3.ViewHolder> {

        private final ArrayList<Review> mReviewList;
        private final LayoutInflater mInflater;

        public ReviewAdapter3(Context context, ArrayList<Review> reviewList) {
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
        public SearchActivity.ReviewAdapter3.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SearchActivity.ReviewAdapter3.ViewHolder(mInflater.inflate(R.layout.item_view, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SearchActivity.ReviewAdapter3.ViewHolder holder, int position) {

            Review currentReview = mReviewList.get(position);
            holder.artistTextView.setText(currentReview.getArtist());
            holder.titleTextView.setText(currentReview.getTitle());
            holder.scoreTextView.setText(currentReview.getScore());
            holder.readButton.setOnClickListener(view -> {
                Intent intent = new Intent(SearchActivity.this, ReviewActivity.class);
                intent.putExtra("parcel", position);
                intent.putExtra("parcelAccount", MainActivity.CURR_ACCOUNT);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() { return mReviewList.size(); }
    }
}
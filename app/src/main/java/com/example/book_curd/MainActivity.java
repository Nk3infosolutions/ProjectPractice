package com.example.book_curd;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout bookList;
    private TextView bookCount;
    private EditText nameInput;
    private EditText authorInput;
    private EditText descriptionInput;
    private Button addButton;
    private DatabaseReference booksReference;
    private ValueEventListener booksListener;
    private final List<Book> books = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookList = findViewById(R.id.book_list);
        bookCount = findViewById(R.id.book_count);
        nameInput = findViewById(R.id.book_name_input);
        authorInput = findViewById(R.id.book_author_input);
        descriptionInput = findViewById(R.id.book_description_input);
        addButton = findViewById(R.id.add_book_button);

        booksReference = FirebaseDatabase.getInstance(
                getString(R.string.firebase_database_url)).getReference("books");
        addButton.setOnClickListener(view -> addBook());
        listenForBooks();
    }

    private void listenForBooks() {
        booksListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                books.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Book book = child.getValue(Book.class);
                    if (book != null) {
                        book.id = child.getKey();
                        books.add(book);
                    }
                }
                renderBooks();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showMessage("Couldn't load books: " + error.getMessage());
            }
        };
        booksReference.addValueEventListener(booksListener);
    }

    private void addBook() {
        String name = nameInput.getText().toString().trim();
        String author = authorInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        Book.ValidationError validationError = Book.validate(name, author, description);
        if (validationError == Book.ValidationError.NAME) {
            nameInput.setError("Enter a book name");
            nameInput.requestFocus();
            return;
        } else if (validationError == Book.ValidationError.AUTHOR) {
            authorInput.setError("Enter an author");
            authorInput.requestFocus();
            return;
        } else if (validationError == Book.ValidationError.DESCRIPTION) {
            descriptionInput.setError("Enter a description");
            descriptionInput.requestFocus();
            return;
        }

        addButton.setEnabled(false);
        Book book = new Book(name, author, description);
        booksReference.push().setValue(book).addOnCompleteListener(task -> {
            addButton.setEnabled(true);
            if (task.isSuccessful()) {
                nameInput.setText("");
                authorInput.setText("");
                descriptionInput.setText("");
                nameInput.requestFocus();
                showMessage("Book added to your library");
            } else {
                String reason = task.getException() == null
                        ? "Please check your connection and database rules."
                        : task.getException().getLocalizedMessage();
                showMessage("Couldn't add book: " + reason);
            }
        });
    }

    private void renderBooks() {
        bookList.removeAllViews();
        bookCount.setText(getResources().getQuantityString(
                R.plurals.book_count, books.size(), books.size()));

        if (books.isEmpty()) {
            TextView emptyMessage = new TextView(this);
            emptyMessage.setText(R.string.empty_library);
            emptyMessage.setTextColor(getColor(R.color.text_muted));
            emptyMessage.setTextSize(15);
            emptyMessage.setGravity(Gravity.CENTER);
            emptyMessage.setPadding(24, 40, 24, 40);
            emptyMessage.setBackgroundResource(R.drawable.bg_book_card);
            bookList.addView(emptyMessage);
            return;
        }

        for (Book book : books) {
            bookList.addView(createBookCard(book));
        }
    }

    private View createBookCard(Book book) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(16), dp(18), dp(16));
        card.setBackgroundResource(R.drawable.bg_book_card);

        TextView title = new TextView(this);
        title.setText(book.name);
        title.setTextColor(getColor(R.color.text_primary));
        title.setTextSize(18);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        card.addView(title);

        TextView author = new TextView(this);
        author.setText(getString(R.string.book_author, book.author));
        author.setTextColor(getColor(R.color.brand_green));
        author.setTextSize(14);
        LinearLayout.LayoutParams authorParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        authorParams.topMargin = dp(5);
        card.addView(author, authorParams);

        TextView description = new TextView(this);
        description.setText(book.description);
        description.setTextColor(getColor(R.color.text_muted));
        description.setTextSize(14);
        description.setLineSpacing(dp(2), 1.0f);
        LinearLayout.LayoutParams descriptionParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        descriptionParams.topMargin = dp(10);
        card.addView(description, descriptionParams);

        Button deleteButton = new Button(this);
        deleteButton.setText(R.string.delete_book);
        deleteButton.setTextColor(getColor(R.color.delete_red));
        deleteButton.setAllCaps(false);
        deleteButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(getColor(R.color.delete_surface)));
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, dp(44));
        buttonParams.gravity = Gravity.END;
        buttonParams.topMargin = dp(10);
        card.addView(deleteButton, buttonParams);
        deleteButton.setOnClickListener(view -> confirmDelete(book));

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardParams.bottomMargin = dp(12);
        card.setLayoutParams(cardParams);
        return card;
    }

    private void confirmDelete(Book book) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_confirmation_title)
                .setMessage(getString(R.string.delete_confirmation_message, book.name))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete_book, (dialog, which) -> deleteBook(book))
                .show();
    }

    private void deleteBook(Book book) {
        if (book.id == null) {
            showMessage("Couldn't delete this book because its database ID is missing.");
            return;
        }
        booksReference.child(book.id).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showMessage("Book deleted");
            } else {
                String reason = task.getException() == null
                        ? "Please check your connection and database rules."
                        : task.getException().getLocalizedMessage();
                showMessage("Couldn't delete book: " + reason);
            }
        });
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_LONG).show();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (booksReference != null && booksListener != null) {
            booksReference.removeEventListener(booksListener);
        }
    }
}

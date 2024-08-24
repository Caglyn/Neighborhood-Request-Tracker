package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CommentBottomSheetFragment extends BottomSheetDialogFragment {

    public interface OnCommentSubmitListener {
        void onCommentSubmit(String commentText);
    }

    private OnCommentSubmitListener listener;

    public CommentBottomSheetFragment(OnCommentSubmitListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment_bottom_sheet, container, false);

        EditText editTextComment = view.findViewById(R.id.editTextComment);
        Button btnSubmitComment = view.findViewById(R.id.btnSubmitComment);

        btnSubmitComment.setOnClickListener(v -> {
            String commentText = editTextComment.getText().toString().trim();
            if (commentText.isEmpty()) {
                Toast.makeText(getContext(), "Yorum alanı boş bırakılamaz!", Toast.LENGTH_SHORT).show();
                return;
            }
            listener.onCommentSubmit(commentText);
            dismiss();
        });

        return view;
    }
}

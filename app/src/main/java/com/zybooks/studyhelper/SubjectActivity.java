package com.zybooks.studyhelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SubjectActivity extends AppCompatActivity
        implements SubjectDialogFragment.OnSubjectEnteredListener {

    private StudyDatabase mStudyDb;
    private SubjectAdapter mSubjectAdapter;
    private RecyclerView mRecyclerView;
    private int[] mSubjectColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject);

        mSubjectColors = getResources().getIntArray(R.array.subjectColors);

        // Singleton
        mStudyDb = StudyDatabase.getInstance(getApplicationContext());

        mRecyclerView = findViewById(R.id.subjectRecyclerView);

        // Create 2 grid layout columns
        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        // Shows the available subjects
        mSubjectAdapter = new SubjectAdapter(loadSubjects());
        mRecyclerView.setAdapter(mSubjectAdapter);
    }

    @Override
    public void onSubjectEntered(String subject) {
        // Returns subject entered in the SubjectDialogFragment dialog
        if (subject.length() > 0) {
            Subject sub = new Subject(subject);
            if (mStudyDb.addSubject(sub)) {
                // TODO: add subject to RecyclerView
                Toast.makeText(this, "Added " + subject, Toast.LENGTH_SHORT).show();
            } else {
                String message = getResources().getString(R.string.subject_exists, subject);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addSubjectClick(View view) {
        // Prompt user to type new subject
        FragmentManager manager = getSupportFragmentManager();
        SubjectDialogFragment dialog = new SubjectDialogFragment();
        dialog.show(manager, "subjectDialog");
    }

    private List<Subject> loadSubjects() {
        return mStudyDb.getSubjects(StudyDatabase.SubjectSortOrder.UPDATE_DESC);
    }

    private class SubjectHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private Subject mSubject;
        private TextView mTextView;

        public SubjectHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.recycler_view_items, parent, false));
            itemView.setOnClickListener(this);
            mTextView = itemView.findViewById(R.id.subjectTextView);
        }

        public void bind(Subject subject, int position) {
            mSubject = subject;
            mTextView.setText(subject.getText());

            // Make the background color dependent on the length of the subject string
            int colorIndex = subject.getText().length() % mSubjectColors.length;
            mTextView.setBackgroundColor(mSubjectColors[colorIndex]);
        }

        @Override
        public void onClick(View view) {
            // Start QuestionActivity, indicating what subject was clicked
            Intent intent = new Intent(SubjectActivity.this, QuestionActivity.class);
            intent.putExtra(QuestionActivity.EXTRA_SUBJECT, mSubject.getText());
            startActivity(intent);
        }
    }

    private class SubjectAdapter extends RecyclerView.Adapter<SubjectHolder> {

        private List<Subject> mSubjectList;

        public SubjectAdapter(List<Subject> subjects) {
            mSubjectList = subjects;
        }

        @Override
        public SubjectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new SubjectHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(SubjectHolder holder, int position){
            holder.bind(mSubjectList.get(position), position);
        }

        @Override
        public int getItemCount() {
            return mSubjectList.size();
        }
    }
}
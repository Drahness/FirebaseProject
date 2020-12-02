package com.example.firebaseproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.firebaseproject.ui.main.FragmentViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private FragmentViewModel mViewModel;
    public IntegerAdapter intAdapter;
    private List<Integer> records;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewModel = new ViewModelProvider(this).get(FragmentViewModel.class);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(intAdapter);
        FloatingActionButton add = findViewById(R.id.floatingActionButton2);
        FloatingActionButton remove = findViewById(R.id.floatingActionButton);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 0;
                if(records != null) {
                    i = records.size();
                }
                int record = new Random().nextInt();
                mViewModel.addRecord(i, record);
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i;
                if(records == null || records.size() == 0) {
                    return;
                } else {
                    i = records.size() - 1;
                }
                int record = i;
                mViewModel.removeRecord(record);
            }
        });
        Observer<List<Integer>> obs = new Observer<List<Integer>>() {
            /**
             * Called when the data is changed.
             *
             * @param updatedList The new data
             */
            @Override
            public void onChanged(List<Integer> updatedList) {
                if (records == null) {
                    records = updatedList;
                    intAdapter = new IntegerAdapter();
                    recyclerView.setAdapter(intAdapter);
                } else {
                    DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                        @Override
                        public int getOldListSize() {
                            return records.size();
                        }

                        @Override
                        public int getNewListSize() {
                            return updatedList.size();
                        }

                        @Override
                        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                            return records.get(oldItemPosition) ==
                                    updatedList.get(newItemPosition);
                        }

                        @Override
                        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                            Integer oldFav = records.get(oldItemPosition);
                            Integer newFav = updatedList.get(newItemPosition);
                            return oldFav.equals(newFav);
                        }
                    });
                    result.dispatchUpdatesTo(intAdapter);
                    records = updatedList;

                }
            }
        };
        mViewModel.getRecords().observe(this, obs);
        Thread worker = new Thread() {
            /**
             * If this thread was constructed using a separate
             * <code>Runnable</code> run object, then that
             * <code>Runnable</code> object's <code>run</code> method is called;
             * otherwise, this method does nothing and returns.
             * <p>
             * Subclasses of <code>Thread</code> should override this method.
             *
             * @see #start()
             * @see #stop()
             * @see #Thread(ThreadGroup, Runnable, String)
             */
            @Override
            public void run() {
                super.run();
                Random r = new Random();
                while(true) {
                    if(r.nextInt() % 2 == 0) {
                        mViewModel.postRecord(r.nextInt());
                    } else {
                        mViewModel.removePostRecord();
                    }
                }
            }
        };
        worker.start();
    }

    public class IntegerAdapter extends RecyclerView.Adapter<IntegerAdapter.FavViewHolder> {

        @Override
        public FavViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_row, parent, false);
            return new FavViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(FavViewHolder holder, int position) {
            Integer favourites = records.get(position);
            holder.mTxtInt.setText(favourites.toString());

        }

        @Override
        public int getItemCount() {
            return records.size();
        }

        class FavViewHolder extends RecyclerView.ViewHolder {
            TextView mTxtInt;

            FavViewHolder(View itemView) {
                super(itemView);
                mTxtInt = itemView.findViewById(R.id.integer);
            }
        }

    }
}

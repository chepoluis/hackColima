package app.hack.com.hackaton;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import app.hack.com.hackaton.Model.Reports;

public class ReportsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private String userID;

    private RecyclerView mProductRV;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<Reports, ProductsViewHolder> mProductRVAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Reportes");

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        //"Products" here will reflect what you have called your database in Firebase.
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Report");
        mDatabase.keepSynced(true);

        mProductRV = (RecyclerView) findViewById(R.id.myRecycleView);

        DatabaseReference personsRef = FirebaseDatabase.getInstance().getReference().child("Report");
        Query personsQuery = personsRef.orderByKey();

        mProductRV.hasFixedSize();
        mProductRV.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions personsOptions = new FirebaseRecyclerOptions.Builder<Reports>().setQuery(personsQuery, Reports.class).build();

        mProductRVAdapter = new FirebaseRecyclerAdapter<Reports, ProductsViewHolder>(personsOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ProductsViewHolder holder, int position, @NonNull Reports reports) {
                // Show the products information
                holder.setTitle(reports.getTitle());
                holder.setDescription(reports.getDescription());
                holder.setDate(reports.getStatus());
                holder.setImage(getBaseContext(), reports.getPicture());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ReportsActivity.this, "Hack Colima 2018", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public ProductsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_report, parent, false);

                return new ProductsViewHolder(view);
            }
        };

        mProductRV.setAdapter(mProductRVAdapter);
    }


    public static class ProductsViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public ProductsViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String title){
            TextView post_title = (TextView)mView.findViewById(R.id.report_title);
            post_title.setText(title);
        }

        public void setDescription(String desc){
            TextView post_desc = (TextView)mView.findViewById(R.id.report_description);
            post_desc.setText(desc);
        }

        public void setDate(String amount){
            TextView post_amount = (TextView)mView.findViewById(R.id.report_status);
            post_amount.setText(amount);
        }

        public void setImage(Context ctx, String image){
            ImageView post_image = (ImageView) mView.findViewById(R.id.report_image);
            Picasso.with(ctx).load(image).into(post_image);
        }
    }
}

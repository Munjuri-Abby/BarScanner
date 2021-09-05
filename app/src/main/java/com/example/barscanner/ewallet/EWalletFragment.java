package com.example.barscanner.ewallet;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.barscanner.Constants;
import com.example.barscanner.Prevalent;
import com.example.barscanner.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EWalletFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EWalletFragment extends Fragment {
    private TextView textViewBalance;
    private Button buttonDeposit;
    private EditText editTextMobile;
    private EditText editTextAmount;
    private LinearLayout linearLayoutDeposit;
    private ArrayList<Map<String , String >> history;
    private EWalletFragmentAdapter eWalletFragmentAdapter;

    int balance;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EWalletFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EWalletFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EWalletFragment newInstance(String param1, String param2) {
        EWalletFragment fragment = new EWalletFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_e_wallet, container, false);

        textViewBalance = view.findViewById(R.id.textViewBalance);

        buttonDeposit = view.findViewById(R.id.buttonDeposit);
        editTextMobile =  view.findViewById(R.id.editTextMobile);
        editTextAmount = view.findViewById(R.id.editTextAmount);
        linearLayoutDeposit = view.findViewById(R.id.linearLayoutDeposit);
        buttonDeposit.setOnClickListener(v -> depositToMpesa());

        history = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        eWalletFragmentAdapter = new EWalletFragmentAdapter(getContext(), history);
        recyclerView.setAdapter(eWalletFragmentAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("EWallet").child(Prevalent.currentOnlineUser.getPhone());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                try {
                    textViewBalance.setText("Balance: Ksh "+snapshot.child("balance").getValue().toString());
                    balance = Integer.parseInt(snapshot.child("balance").getValue().toString());
                }catch (Exception ignored){}

                if (snapshot.hasChild("history")){

                    for (DataSnapshot historyTransaction: snapshot.child("history").getChildren()){
                        HashMap<String ,String > hashMap = new HashMap<>();
                        for (DataSnapshot dataSnapshotKeys : historyTransaction.getChildren())
                            hashMap.put(dataSnapshotKeys.getKey(), dataSnapshotKeys.getValue().toString());
                        history.add(hashMap);
                        eWalletFragmentAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        return view;
    }

    private void depositToMpesa() {

        if (linearLayoutDeposit.getVisibility() != View.VISIBLE){
            linearLayoutDeposit.setVisibility(View.VISIBLE);
        }else {
            String mobile = editTextMobile.getText().toString();
            String amount = editTextAmount.getText().toString();
            if (mobile.equals("")){
                FancyToast.makeText(getContext(),"Enter Mobile Number First", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            }else if (!mobile.startsWith("254")){
                FancyToast.makeText(getContext(),"Mobile Number Should start with 254..", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            }else if (mobile.length() != 12){
                FancyToast.makeText(getContext(),"Enter a valid Mobile Number", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            }else if (amount.equals("")){
                FancyToast.makeText(getContext(),"Enter Amount to deposit First", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
            }
            else{
                buttonDeposit.setClickable(false);
                linearLayoutDeposit.setVisibility(View.GONE);
                buttonDeposit.setText("Please wait for mPesa popup...");
                makeMpesaPayment(mobile, amount, "BarScanner App");
            }
        }
    }

    private void makeMpesaPayment(String mobile, String amount, String app){
        int intAmount;
        intAmount = Integer.parseInt(amount);

        RequestQueue queue = Volley.newRequestQueue(getContext());

        String url = Constants.SERVER_GENERAL_APIS_BASE_URL+"?action=mpesa&mobile="+mobile+"&amount="+amount+"&app="+app;
        JsonObjectRequest request = new JsonObjectRequest(url, null,
                response -> {
                    if (response != null){
                        try {

                            if (response.getString("ResponseCode").equals("0")){
                                FancyToast.makeText(getContext(),"Enter Mpesa Pin", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("EWallet").child(Prevalent.currentOnlineUser.getPhone());

                                databaseReference.child("balance").setValue(balance+intAmount).addOnCompleteListener(task -> {
                                    HashMap<String ,String > hashMap = new HashMap<>();
                                    hashMap.put("balance", String.valueOf(balance+intAmount));
                                    hashMap.put("date", new SimpleDateFormat("dd-MM-yyyy E 'at' HH:mm:ss", Locale.getDefault()).format(new Date()));
                                    hashMap.put("mobile", "+"+mobile);
                                    hashMap.put("transactionType", "Deposit");
                                    hashMap.put("amount", amount);

                                    databaseReference.child("history").push().setValue(hashMap).addOnCompleteListener(task1 -> {
//                                        FancyToast.makeText(getContext(),"Deposit Made Successfully", FancyToast.LENGTH_LONG, FancyToast.SUCCESS, false).show();
                                        buttonDeposit.setText("Deposit Made Successfully");
                                    });

                                });



                            }else {
                                FancyToast.makeText(getContext(),"We could not process the request\n"+response.getString("ResponseCode"), FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();

                            }
                        }catch (JSONException e){
                            FancyToast.makeText(getContext(),"We could not process the request ", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            e.printStackTrace();
                        }
                    }
                }, error -> {

        });

        request.setRetryPolicy(new DefaultRetryPolicy(
                5*60*1000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

}

class EWalletFragmentAdapter extends RecyclerView.Adapter<EWalletFragmentAdapter.ViewHolder>{

    private ArrayList<Map<String, String>> mData;
    private LayoutInflater mInflater;
    private EWalletFragmentAdapter.ItemClickListener mClickListener;
    private Map<String, String> message;
    private Context context;
    private ProgressDialog progressDialog;

    // data is passed into the constructor
    public EWalletFragmentAdapter(Context context, ArrayList<Map<String, String>> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public EWalletFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_ewallet, parent, false);
        return new EWalletFragmentAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(EWalletFragmentAdapter.ViewHolder holder, int position) {
        Map<String, String> message = mData.get(position);

        holder.textViewDate.setText(message.get("transactionType")+" Transaction");
        holder.textViewDescription.setText("You made a "+message.get("transactionType")+ " transaction on "+message.get("date") +" of Ksh. "+message.get("amount")+
                "\nNew Balance is "+message.get("balance"));


        holder.buttonTransactionType.setText(message.get("transactionType"));
        holder.buttonMobile.setText(message.get("mobile"));


    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textViewDate, textViewDescription;
        Button buttonTransactionType, buttonMobile;

        ViewHolder(View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);

            buttonTransactionType = itemView.findViewById(R.id.buttonTransactionType);
            buttonMobile = itemView.findViewById(R.id.buttonMobile);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());

        }
    }

    // convenience method for getting data at click position
    Map<String, String> getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(EWalletFragmentAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}

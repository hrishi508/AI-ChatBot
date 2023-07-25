package com.example.aichatbot.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aichatbot.R;

import java.util.ArrayList;

public class ChatRVAdapter extends RecyclerView.Adapter<ChatRVAdapter.ViewHolder> {

    // variable for context and array list.
    private Context context;
    private ArrayList<ChatMsgModal> chatMsgModalArrayList;

    // create a constructor for our context and array list.
    public ChatRVAdapter(Context context, ArrayList<ChatMsgModal> chatMsgModalArrayList) {
        this.context = context;
        this.chatMsgModalArrayList = chatMsgModalArrayList;
    }

    @NonNull
    @Override
    public ChatRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inside on create view holder method we are inflating our layout file which we created.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_rv_item, parent, false);
        return new ChatRVAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRVAdapter.ViewHolder holder, int position) {
        ChatMsgModal modal = chatMsgModalArrayList.get(position);
        if (modal.getType() == 0) {
            // when we get type as 0 then the
            // message will be of user type.
            holder.msgTV.setText(modal.getMessage());
        } else {
            // other than this condition we will display
            // the text background color and text color.
            holder.msgTV.setText(modal.getMessage());
            holder.msgCV.setCardBackgroundColor(context.getResources().getColor(R.color.purple_200));
            holder.msgTV.setTextColor(context.getResources().getColor(R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        // on below line returning
        // the size of our array list.
        return chatMsgModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // creating variables for our
        // card view and text view.
        private TextView msgTV;
        private CardView msgCV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // initializing variables for our text view and card view.
            msgTV = itemView.findViewById(R.id.idTVMessage);
            msgCV = itemView.findViewById(R.id.idCVMessage);

        }
    }
}


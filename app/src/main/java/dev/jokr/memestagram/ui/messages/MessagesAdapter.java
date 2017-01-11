package dev.jokr.memestagram.ui.messages;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.models.Conversation;
import dev.jokr.memestagram.models.Message;

/**
 * Created by jokr on 11.01.17..
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageHolder> {

    private static final int YOUR_MESSAGE = 1;
    private static final int OTHER_GUY_MESSAGE = 2; // so readable

    private final String userKey;
    private Context ctx;
    private LayoutInflater inflater;
    private List<Message> messages;

    public MessagesAdapter(Context ctx, String loggedUserKey) {
        this.ctx = ctx;
        this.inflater = ((Activity)ctx).getLayoutInflater();
        this.userKey = loggedUserKey;
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).sender.equals(userKey))
            return YOUR_MESSAGE;
        else
            return OTHER_GUY_MESSAGE;
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == YOUR_MESSAGE)
            v = inflater.inflate(R.layout.item_message_you, parent, false);
        else
            v = inflater.inflate(R.layout.item_message_other, parent, false);
        return new MessageHolder(v);
    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        holder.txtMessage.setText(messages.get(position).content);
    }

    @Override
    public int getItemCount() {
        return messages == null ? 0 : messages.size();
    }

    public void addMessage(Message msg) {
        if (messages == null)
            messages = new ArrayList<>();

        messages.add(msg);
        notifyItemInserted(messages.size()-1);
    }

    class MessageHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_message)
        TextView txtMessage;

        public MessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

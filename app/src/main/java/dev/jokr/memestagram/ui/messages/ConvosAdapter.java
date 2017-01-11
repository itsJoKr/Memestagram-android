package dev.jokr.memestagram.ui.messages;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.events.ShowConversation;
import dev.jokr.memestagram.models.Conversation;


/**
 * Created by jokr on 10.01.17..
 */

public class ConvosAdapter extends RecyclerView.Adapter<ConvosAdapter.ConvoHolder> {

    private Context ctx;
    private LayoutInflater inflater;
    private List<Conversation> convos;

    public ConvosAdapter(Context ctx) {
        this.ctx = ctx;
        this.inflater = ((Activity)ctx).getLayoutInflater();
    }


    @Override
    public ConvoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_convo, parent, false);
        return new ConvoHolder(v);
    }

    @Override
    public void onBindViewHolder(ConvoHolder holder, int position) {
        Conversation c = convos.get(position);
        holder.otherGuyUsername.setText(c.otherGuy);
        holder.cardConvo.setOnClickListener(v -> EventBus.getDefault().post(new ShowConversation(c)));
    }

    @Override
    public int getItemCount() {
        return convos == null ? 0 : convos.size();
    }

    public void setConvos(List<Conversation> convos) {
        this.convos = convos;
    }

    public void addConvo(Conversation conversation) {
        if (convos == null) {
            convos = new ArrayList<>();
        }

        convos.add(conversation);
        notifyItemInserted(convos.size()-1);
    }

    class ConvoHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_other_guy)
        TextView otherGuyUsername;
        @BindView(R.id.card_convo)
        CardView cardConvo;

        public ConvoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

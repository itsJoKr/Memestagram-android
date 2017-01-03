package dev.jokr.memestagram.ui.newmeme;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.jokr.memestagram.R;

/**
 * Created by jokr on 01.01.17..
 */

public class TemplatesAdapter extends RecyclerView.Adapter<TemplatesAdapter.TemplateItem> {

    private final LayoutInflater inflater;
    private Context ctx;
    private List<Pair<String, Integer>> localTemplates;
    private OnTemplateClickListener listener;


    public TemplatesAdapter(Context ctx, OnTemplateClickListener listener) {
        this.listener = listener;
        this.ctx = ctx;
        this.inflater = ((Activity)ctx).getLayoutInflater();
    }

    @Override
    public TemplateItem onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_template, parent, false);
        return new TemplateItem(v);
    }

    @Override
    public void onBindViewHolder(TemplateItem holder, int position) {
        final int drawableId = localTemplates.get(position).second;
        holder.txtTitle.setText(localTemplates.get(position).first.toUpperCase());
        holder.img.setImageDrawable(
                ContextCompat.getDrawable(ctx, localTemplates.get(position).second));
        holder.img.setAdjustViewBounds(true);
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTemplateClick(drawableId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return localTemplates == null ? 0 : localTemplates.size();
    }

    public void setLocalTemplates(List<Pair<String, Integer>> localTemplates) {
        this.localTemplates = localTemplates;
        notifyDataSetChanged();
    }

    public interface OnTemplateClickListener {
        public void onTemplateClick(int drawableId);
    }

    class TemplateItem extends RecyclerView.ViewHolder {

        @BindView(R.id.container_template)
        CardView container;
        @BindView(R.id.img_template)
        ImageView img;
        @BindView(R.id.txt_template_title)
        TextView txtTitle;

        public TemplateItem(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

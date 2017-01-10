package dev.jokr.memestagram.ui.meme;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.jokr.memestagram.R;
import dev.jokr.memestagram.models.Comment;

/**
 * Created by jokr on 09.01.17..
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentHolder> {


    private Context ctx;
    private LayoutInflater inflater;
    private List<Comment> comments;

    public CommentsAdapter(Context ctx) {
        this.ctx = ctx;
        this.inflater = ((Activity)ctx).getLayoutInflater();
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_comment, parent, false);
        return new CommentHolder(v);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        Comment c = comments.get(position);
        holder.txtUsername.setText(c.poster);
        holder.txtCommentContent.setText(c.content);
    }

    @Override
    public int getItemCount() {
        return comments == null ? 0 : comments.size();
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public void addComment(Comment comment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }

        int pos = comments.size();
        comments.add(comment);
        notifyItemInserted(pos);
    }

    class CommentHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.txt_content)
        TextView txtCommentContent;
        @BindView(R.id.txt_by_username)
        TextView txtUsername;

        public CommentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

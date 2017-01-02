package dev.jokr.memestagram.ui.newmeme;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import dev.jokr.memestagram.R;

/**
 * Created by jokr on 01.01.17..
 */

public class TemplatePickFragment extends Fragment implements TemplatesAdapter.OnTemplateClickListener {

    @Bind(R.id.list_templates)
    RecyclerView listTemplates;

    OnPickedTemplateListener pickedTemplateListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_template_pick, container, false);
        ButterKnife.bind(this, v);

        listTemplates.setLayoutManager(new LinearLayoutManager(getContext()));
        TemplatesAdapter adapter = new TemplatesAdapter(getContext(), this);
        adapter.setLocalTemplates(getLocalTemplates());
        listTemplates.setAdapter(adapter);


        return v;
    }

    private static List<Pair<String, Integer>> getLocalTemplates() {
        List<Pair<String, Integer>> l = new ArrayList<>();
        l.add(new Pair<String, Integer>("Ancient Aliens", R.drawable.temp_ancient_aliens));
        l.add(new Pair<String, Integer>("Brace Yourselves X is coming", R.drawable.temp_brace_yourselves));
        l.add(new Pair<String, Integer>("Confession Bear", R.drawable.temp_confession_bear));
        l.add(new Pair<String, Integer>("Doge", R.drawable.temp_doge));
        l.add(new Pair<String, Integer>("First World Problems", R.drawable.temp_fwp));
        l.add(new Pair<String, Integer>("Leonardo DiCaprio Cheers", R.drawable.temp_leo_cheers));
        l.add(new Pair<String, Integer>("What if i told your", R.drawable.temp_matrix));
        l.add(new Pair<String, Integer>("The most interesting man in the world", R.drawable.temp_most_interesting_man_in_the_world));
        l.add(new Pair<String, Integer>("But that's none of my business", R.drawable.temp_none_ofmy_business));
        l.add(new Pair<String, Integer>("One does not simpy", R.drawable.temp_one_does_not_simply));
        l.add(new Pair<String, Integer>("Philosoraptor", R.drawable.temp_philosoraptor));
        l.add(new Pair<String, Integer>("Captain Picard palmface", R.drawable.temp_picard));
        l.add(new Pair<String, Integer>("Success Kid", R.drawable.temp_success_kid));
        l.add(new Pair<String, Integer>("That woud be great", R.drawable.temp_that_would_be_great));
        l.add(new Pair<String, Integer>("X Everywhere", R.drawable.temp_xeverywhere));
        l.add(new Pair<String, Integer>("Y U NO", R.drawable.temp_yu_no));
        return l;
    }


    public void setPickedTemplateListener(OnPickedTemplateListener pickedTemplateListener) {
        this.pickedTemplateListener = pickedTemplateListener;
    }

    @Override
    public void onTemplateClick(int drawableId) {
        pickedTemplateListener.onPickedTemplate(drawableId);
    }

    public interface OnPickedTemplateListener {
        public void onPickedTemplate(int drawableId);
    }
}

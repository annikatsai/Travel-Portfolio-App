package annikatsai.portfolioapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TakePicFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This is where you do initialization for non view related things
        // Create your ArrayLists
        // Create your adapters
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate a view and return it
        View view = inflater.inflate(R.layout.fragment_take_pic, container, false);
        // Go find this xml, inflate it, and add it to this container (but don't attach right now, will happen at later point)
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvLabel = (TextView) view.findViewById(R.id.tvLabel);
        // This is where you do view configuration
        // FindViewByIDs
        // setText on TV
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Activity fully created
        // Do anything related to setup activity

    }
}

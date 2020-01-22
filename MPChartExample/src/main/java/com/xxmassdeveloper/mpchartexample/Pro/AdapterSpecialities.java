package com.xxmassdeveloper.mpchartexample.Pro;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.xxmassdeveloper.mpchartexample.R;

import java.util.List;

public class AdapterSpecialities extends RecyclerView.Adapter<AdapterSpecialities.VHolder> {

    List<Item> items;
    Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Item item, int position);
    }

    public AdapterSpecialities(Context contex, List<Item> item, OnItemClickListener listener) {
        context = contex;
        items = item;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_specialities, viewGroup, false);

        return new VHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VHolder vHolder,final int i) {
        vHolder.specialities_name.setText(items.get(i).getCode());
        vHolder.itemView.setBackgroundColor(Color.parseColor(items.get(i).getBackgroundColor()));
        vHolder.specialities_name.setTextColor(Color.parseColor(items.get(i).getTextColor()));

        vHolder.bind(i,items.get(i), listener);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class VHolder extends RecyclerView.ViewHolder {
        TextView specialities_name;

        public VHolder(@NonNull View itemView) {
            super(itemView);
            specialities_name = itemView.findViewById(R.id.specialities_name);
        }

        public void bind(final int position, final Item item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, position);
//                    vHolder.itemView.setBackgroundColor(Color.rgb(23, 197, 255));



                }
            });
        }
    }
}

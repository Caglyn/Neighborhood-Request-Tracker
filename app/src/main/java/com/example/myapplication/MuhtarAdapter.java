package com.example.myapplication;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

public class MuhtarAdapter extends ArrayAdapter<Muhtar> {

    private List<Muhtar> originalList;
    private List<Muhtar> filteredList;

    public MuhtarAdapter(Context context, List<Muhtar> muhtarList) {
        super(context, android.R.layout.simple_dropdown_item_1line, muhtarList);
        this.originalList = new ArrayList<>(muhtarList);
        this.filteredList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Muhtar getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                filteredList.clear();
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(originalList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    for (Muhtar muhtar : originalList) {
                        if (muhtar.name.toLowerCase().contains(filterPattern) ||
                                muhtar.ilce.toLowerCase().contains(filterPattern) ||
                                muhtar.mahalle.toLowerCase().contains(filterPattern)) {
                            filteredList.add(muhtar);
                        }
                    }
                }
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //noinspection unchecked
                filteredList = (List<Muhtar>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}

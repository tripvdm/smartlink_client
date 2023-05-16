package com.lipakov.smartlink.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lipakov.smartlink.databinding.SmartLinkBinding;
import com.lipakov.smartlink.model.SmartLink;
import com.lipakov.smartlink.utils.UtilsUI;

import java.util.ArrayList;
import java.util.List;


public class SmartLinkAdapter extends RecyclerView.Adapter<SmartLinkAdapter.SmartLinkViewHolder> implements Filterable {
    private static final String TAG = SmartLinkAdapter.class.getSimpleName();

    private List<SmartLink> smartLinkList = new ArrayList<>();
    private List<SmartLink> filterSmartLinkList = new ArrayList<>();

    private final Context context;

    public SmartLinkAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public SmartLinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SmartLinkBinding smartLinkBinding = SmartLinkBinding.inflate(LayoutInflater.from(context), parent, false);
        return new SmartLinkViewHolder(smartLinkBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SmartLinkViewHolder holder, int position) {
        final SmartLink smartLink = smartLinkList.get(position);
        Bitmap bitmap = UtilsUI.convertFileToBitmap(smartLink.getUrl());
        holder.smartLinkBinding.photoOfSmartLink.setImageBitmap(bitmap);
        holder.smartLinkBinding.nameOfSmartLink.setText(smartLink.getTitle());
        String price = String.valueOf(smartLink.getPrice());
        holder.smartLinkBinding.priceOfSmartLink.setText(price);
        holder.smartLinkBinding.contactNumberOfSmartLink.setText(smartLink.getPhoneNumber());
    }

    @Override
    public int getItemCount() {
        return smartLinkList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults results = new FilterResults();
                if (constraint != null && constraint.length() > 0) {
                    constraint = constraint.toString().toLowerCase();
                    final List<SmartLink> filters = new ArrayList<>();
                    for (SmartLink smartLink : smartLinkList) {
                        final String name = smartLink.getTitle();
                        final String price = String.valueOf(smartLink.getPrice());
                        final String contactNumber = smartLink.getPhoneNumber();
                        if (name.toLowerCase().contains(constraint) ||
                                                price.contains(constraint) ||
                                                contactNumber.contains(constraint)) {
                            filters.add(smartLink);
                        }
                    }
                    results.count = filters.size();
                    results.values = filters;
                } else {
                    results.count = filterSmartLinkList.size();
                    results.values = filterSmartLinkList;
                }
                return results;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            protected void publishResults(CharSequence charSequence,
                                          FilterResults filterResults) {
                smartLinkList = (List<SmartLink>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateSmartLinkList(final List<SmartLink> smartLinkList) {
        this.smartLinkList = smartLinkList;
        this.filterSmartLinkList = smartLinkList;
    }

    static class SmartLinkViewHolder extends RecyclerView.ViewHolder {
        private final SmartLinkBinding smartLinkBinding;
        public SmartLinkViewHolder(@NonNull SmartLinkBinding smartLinkBinding) {
            super(smartLinkBinding.getRoot());
            this.smartLinkBinding = smartLinkBinding;
        }
    }
}

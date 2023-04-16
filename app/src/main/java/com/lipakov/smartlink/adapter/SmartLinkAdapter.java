package com.lipakov.smartlink.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.buylink.R;
import com.lipakov.smartlink.model.SmartLink;
import com.lipakov.smartlink.utils.UtilsUI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SmartLinkAdapter extends RecyclerView.Adapter<SmartLinkAdapter.SmartLinkViewHolder> implements Filterable {
    private static final String TAG = SmartLinkAdapter.class.getSimpleName();

    private List<SmartLink> smartLinkList = new ArrayList<>();
    private List<SmartLink> filterSmartLinkList = new ArrayList<>();

    @NonNull
    @Override
    public SmartLinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.smart_link, parent, false);
        return new SmartLinkViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull SmartLinkViewHolder holder, int position) {
        final SmartLink smartLink = smartLinkList.get(position);
        Bitmap bitmap = UtilsUI.convertFileToBitmap(smartLink.getUrlOfPhoto());
        holder.photoOfSmartLink.setImageBitmap(bitmap);
        holder.nameOfSmartLink.setText(smartLink.getTitle());
        String price = String.valueOf(smartLink.getPrice());
        holder.priceOfSmartLink.setText(price);
        holder.contactNumberOfSmartLink.setText(smartLink.getContactNumber());
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
                        final String contactNumber = smartLink.getContactNumber();
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
        this.smartLinkList.clear();
        this.filterSmartLinkList.clear();
        this.smartLinkList = smartLinkList;
        this.filterSmartLinkList = smartLinkList;
        Log.i(TAG, "Update smart link list");
        notifyDataSetChanged();
    }

    static class SmartLinkViewHolder extends RecyclerView.ViewHolder {

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.photoOfSmartLink)
        ImageView photoOfSmartLink;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.nameOfSmartLink)
        TextView nameOfSmartLink;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.priceOfSmartLink)
        TextView priceOfSmartLink;

        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.contactNumberOfSmartLink)
        TextView contactNumberOfSmartLink;

        public SmartLinkViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

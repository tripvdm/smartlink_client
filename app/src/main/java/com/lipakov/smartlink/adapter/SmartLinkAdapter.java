package com.lipakov.smartlink.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lipakov.smartlink.R;
import com.lipakov.smartlink.databinding.SmartLinkBinding;
import com.lipakov.smartlink.model.SmartLink;
import com.lipakov.smartlink.presenter.SmartLinkPresenter;

import java.util.ArrayList;
import java.util.List;


public class SmartLinkAdapter extends RecyclerView.Adapter<SmartLinkAdapter.SmartLinkViewHolder> {
    private static final String TAG = SmartLinkAdapter.class.getSimpleName();

    private final List<SmartLink> smartLinkList;

    private final Context context;

    private final RefreshingSmartLinkList refreshingSmartLinkList;
    public SmartLinkAdapter(Context context, RefreshingSmartLinkList refreshingSmartLinkList) {
        this.context = context;
        this.refreshingSmartLinkList = refreshingSmartLinkList;
        smartLinkList = new ArrayList<>();
    }

    @NonNull
    @Override
    public SmartLinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SmartLinkBinding smartLinkBinding = SmartLinkBinding.inflate(LayoutInflater.from(context), parent, false);
        return new SmartLinkViewHolder(smartLinkBinding);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull SmartLinkViewHolder holder, int position) {
        final SmartLink smartLink = smartLinkList.get(position);
        ProgressBar progressBar = holder.smartLinkBinding.loadPhoto;
        progressBar.setVisibility(View.VISIBLE);
        Glide.with(context)
                .load(smartLink.getPhoto())
                .error(R.mipmap.ic_no_photo_foreground)
                .listener(new RequestListener<>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).into(holder.smartLinkBinding.photoOfSmartLink);
        Spanned spanned = Html.fromHtml("<a style=\"color:black\" href=\"" + smartLink.getUrl() +  "\">" +
                "<font color=${Color.BLACK}>" + smartLink.getTitle() + "</font></a>");
        TextView titleTextView = holder.smartLinkBinding.nameOfSmartLink;
        titleTextView.setLinkTextColor(Color.BLACK);
        titleTextView.setText(spanned);
        stripUnderlines(titleTextView);
        titleTextView.setMovementMethod(LinkMovementMethod.getInstance());
        String price = String.valueOf(smartLink.getPrice());
        holder.smartLinkBinding.priceOfSmartLink.setText(price);
        holder.smartLinkBinding.contactNumberOfSmartLink.setText(smartLink.getPhoneNumber());
    }

    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    @Override
    public int getItemCount() {
        return smartLinkList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateSmartLinkList(final List<SmartLink> smartLinkList) {
        this.smartLinkList.clear();
        this.smartLinkList.addAll(smartLinkList);
        notifyDataSetChanged();
    }

    class SmartLinkViewHolder extends RecyclerView.ViewHolder implements SmartLinkPresenter.SmartLinkView {
        public SmartLinkBinding smartLinkBinding;

        public SmartLinkViewHolder(@NonNull SmartLinkBinding smartLinkBinding) {
            super(smartLinkBinding.getRoot());
            this.smartLinkBinding = smartLinkBinding;
            smartLinkBinding.smartLink.setOnLongClickListener(v -> {
                new AlertDialog.Builder(itemView.getContext())
                        .setMessage(Html.fromHtml("<font color='#9E5B37'>Вы действительно хотите удалить ссылку?</font>"))
                        .setNegativeButton(R.string.cancel, ((dialog, which) -> {}))
                        .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                            SmartLinkPresenter smartLinkPresenter = new SmartLinkPresenter(context, this);
                            int adapterPosition = getAbsoluteAdapterPosition();
                            smartLinkPresenter.deleteSmartLink(smartLinkList.get(adapterPosition));
                        }).show();
                return false;
            });
        }

        @Override
        public void showNotify(String notify) {
            Toast.makeText(context, notify, Toast.LENGTH_SHORT).show();
            refreshSmartLinkFragment();
        }

        private void refreshSmartLinkFragment() {
            refreshingSmartLinkList.refreshSmartLinkFragment();
        }
    }

    private static class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }

    public interface RefreshingSmartLinkList {
        void refreshSmartLinkFragment();
    }
}

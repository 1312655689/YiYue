package com.minardwu.yiyue.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.minardwu.yiyue.R;
import com.minardwu.yiyue.utils.SystemUtils;
import com.minardwu.yiyue.utils.UIUtils;

/**
 * Created by wumingyuan on 2018/4/11.
 */

public class YesOrNoDialog extends Dialog {

    TextView tv_title;
    TextView tv_subtitle;
    TextView tv_yes;
    TextView tv_no;

    private static Context context;
    private static String title;
    private static String subtitle;
    private static String yes;
    private static String no;
    private static int titleTextColor = 0;
    private static int subtitleTextColor = 0;
    private static int yesTextColor = 0;
    private static int noTextColor = 0;
    private static PositiveClickListener positiveClickListener;
    private static NegativeClickListener negativeClickListener;

    public YesOrNoDialog(@NonNull Context context) {
        super(context);
    }

    public YesOrNoDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected YesOrNoDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static class Builder{

        public Builder(){
            context = null;
            title = null;
            subtitle = null;
        }

        public Builder context(Context c){
            context = c;
            return this;
        }

        public Builder title(String s){
            title = s;
            return this;
        }

        public Builder title(int id){
            title = UIUtils.getString(id);
            return this;
        }

        public Builder subtitle(String s){
            subtitle = s;
            return this;
        }

        public Builder subtitle(int id){
            subtitle = UIUtils.getString(id);
            return this;
        }

        public Builder yes(String s,PositiveClickListener listener){
            yes = s;
            positiveClickListener = listener;
            return this;
        }

        public Builder yes(int id,PositiveClickListener listener){
            yes = UIUtils.getString(id);
            positiveClickListener = listener;
            return this;
        }

        public Builder no(String s,NegativeClickListener listener){
            no = s;
            negativeClickListener = listener;
            return this;
        }

        public Builder no(int id,NegativeClickListener listener){
            no = UIUtils.getString(id);
            negativeClickListener = listener;
            return this;
        }

        public Builder titleTextColor(int color){
            titleTextColor = color;
            return this;
        }

        public Builder contentTextColor(int color){
            subtitleTextColor = color;
            return this;
        }

        public Builder yesTextColor(int color){
            yesTextColor = color;
            return this;
        }

        public Builder noTextColor(int color){
            noTextColor = color;
            return this;
        }

        public YesOrNoDialog build(){
            final YesOrNoDialog dialog =  new YesOrNoDialog(context,R.style.yes_or_no_dialog);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_yes_or_no,null);
            dialog.addContentView(view,new ConstraintLayout.LayoutParams(SystemUtils.getScreenWidth()*3/4, ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.tv_title = view.findViewById(R.id.tv_title);
            dialog.tv_subtitle = view.findViewById(R.id.tv_content);
            dialog.tv_yes = view.findViewById(R.id.tv_yes);
            dialog.tv_no = view.findViewById(R.id.tv_no);

            dialog.tv_title.setText(title);
            dialog.tv_title.setVisibility(TextUtils.isEmpty(title) ? View.GONE:View.VISIBLE);
            dialog.tv_subtitle.setText(subtitle);
            dialog.tv_yes.setText(yes);
            dialog.tv_no.setText(no);

            if(positiveClickListener!=null){
                dialog.tv_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        positiveClickListener.OnClick(dialog,view);
                    }
                });
            }

            if(negativeClickListener!=null){
                dialog.tv_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        negativeClickListener.OnClick(dialog,view);
                    }
                });
            }

            if (titleTextColor !=0){
                dialog.tv_title.setTextColor(titleTextColor);
            }

            if (subtitleTextColor !=0){
                dialog.tv_subtitle.setTextColor(subtitleTextColor);
            }

            if (yesTextColor !=0){
                dialog.tv_yes.setTextColor(yesTextColor);
            }

            if (noTextColor !=0){
                dialog.tv_no.setTextColor(noTextColor);
            }

            return dialog;
        }

    }

    public interface PositiveClickListener{
        void OnClick(YesOrNoDialog dialog,View view);
    }

    public interface NegativeClickListener{
        void OnClick(YesOrNoDialog dialog,View view);
    }
}

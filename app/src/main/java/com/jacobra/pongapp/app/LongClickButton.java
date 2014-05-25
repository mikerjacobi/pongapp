package com.jacobra.pongapp.app;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by mjacobi on 5/24/2014.
 */
public class LongClickButton extends Button {
    public LongClickButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LongClickButton(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs){
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LongClickButton);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; ++i) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.LongClickButton_onKeyLongPress: {
                    if (context.isRestricted()) {
                        throw new IllegalStateException("The " + getClass().getCanonicalName() + ":onKeyLongPress attribute cannot "
                                + "be used within a restricted context");
                    }

                    final String handlerName = a.getString(attr);
                    if (handlerName != null) {
                        setOnLongClickListener(new View.OnLongClickListener() {
                            private Method mHandler;

                            @Override
                            public boolean onLongClick(final View p_v) {
                                boolean result = false;
                                if (mHandler == null) {
                                    try {
                                        mHandler = getContext().getClass().getMethod(handlerName, View.class);
                                    } catch (NoSuchMethodException e) {
                                        int id = getId();
                                        String idText = id == NO_ID ? "" : " with id '"
                                                + getContext().getResources().getResourceEntryName(
                                                id) + "'";
                                        throw new IllegalStateException("Could not find a method " +
                                                handlerName + "(View) in the activity "
                                                + getContext().getClass() + " for onKeyLongPress handler"
                                                + " on view " + LongClickButton.this.getClass() + idText, e);
                                    }
                                }

                                try {
                                    mHandler.invoke(getContext(), LongClickButton.this);
                                    result = true;
                                } catch (IllegalAccessException e) {
                                    throw new IllegalStateException("Could not execute non "
                                            + "public method of the activity", e);
                                } catch (InvocationTargetException e) {
                                    throw new IllegalStateException("Could not execute "
                                            + "method of the activity", e);
                                }
                                return result;
                            }
                        });
                    }
                    break;
                }
                default:
                    break;
            }
        }
        a.recycle();
    }
}

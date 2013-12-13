package com.mareklatuszek.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewBariol extends TextView {

	public TextViewBariol(Context context) {
		super(context);
		setTypeface(FontBariol.getInstance(context).getTypeFace());
	}
	
	public TextViewBariol(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(FontBariol.getInstance(context).getTypeFace());
	}
	
	public TextViewBariol(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs);
		setTypeface(FontBariol.getInstance(context).getTypeFace());
	}

	
}

package com.mareklatuszek.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class EditTextBariol extends EditText {

	public EditTextBariol(Context context) {
		super(context);
		setTypeface(FontBariol.getInstance(context).getTypeFace());
	}
	
	public EditTextBariol(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(FontBariol.getInstance(context).getTypeFace());
	}
	
	public EditTextBariol(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTypeface(FontBariol.getInstance(context).getTypeFace());
	}

}

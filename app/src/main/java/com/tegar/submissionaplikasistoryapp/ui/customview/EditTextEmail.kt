package com.tegar.submissionaplikasistoryapp.ui.customview

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.tegar.submissionaplikasistoryapp.R

class EditTextEmail : AppCompatEditText, View.OnTouchListener {
    constructor(context: Context): super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        init()
    }

    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private fun init() {
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                error =  if (s.isNullOrBlank()) {
                    context.getString(R.string.email_empty)
                } else {
                    if (!s.toString().matches(emailPattern.toRegex())) {
                        context.getString(R.string.invalid_email_format)
                    } else {
                        null
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        setHintTextColor(context.getColor(R.color.black_600))
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }

}
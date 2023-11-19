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

class EditTextPassword : AppCompatEditText, View.OnTouchListener {
    constructor(context: Context): super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr){
        init()
    }

    private fun init() {
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                error = if ((s?.length ?: 0) < 8) {
                    context.getString(R.string.min_8_character)
                } else {
                    null
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
    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        return false
    }

}
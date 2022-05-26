/*
 * Dr. Ing. h.c. F. Porsche AG confidential. This code is protected by intellectual property rights.
 * The Dr. Ing. h.c. F. Porsche AG owns exclusive legal rights of use.
 */
package com.jidouauto.expandabletextview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.text.Layout
import android.text.Layout.Alignment.ALIGN_NORMAL
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.StaticLayout
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat

private const val MAX_COLLAPSED_LINES = 3

internal class ExpandTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    var expanded: Boolean = false
        set(value) {
            field = value
            requestLayout()
        }

    private var mText: String = ""
    private var ellipsize = context.resources.getString(R.string.ellipsize)
    private var ellipsizeText = context.resources.getString(R.string.ellipsize_text)
    private var maxCollapsedLines = MAX_COLLAPSED_LINES
    private var canExpand = false

    @ColorInt
    private var expandCollapseTextColor = ContextCompat.getColor(context, R.color.my_blue)

    init {
        initAttrs(context, attrs)
        setOnClickListener {
            expanded = !expanded
        }
    }

    @SuppressLint("CustomViewStyleable")
    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ExpandLayout)
        maxCollapsedLines =
            ta.getInt(R.styleable.ExpandLayout_maxCollapsedLines, MAX_COLLAPSED_LINES)
        expandCollapseTextColor = ta.getColor(
            R.styleable.ExpandLayout_expandCollapseTextColor,
            expandCollapseTextColor
        )
        ellipsize = ta.getString(R.styleable.ExpandLayout_ellipsize) ?: ellipsize
        ellipsizeText = ta.getString(R.styleable.ExpandLayout_ellipsizeText) ?: ellipsizeText
        ta.recycle()
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sl = if (VERSION.SDK_INT >= VERSION_CODES.M) {
            StaticLayout.Builder.obtain(
                mText,
                0,
                mText.length,
                paint,
                measuredWidth - paddingLeft - paddingRight
            ).setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(lineSpacingExtra, lineSpacingMultiplier)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(
                mText,
                paint,
                measuredWidth - paddingLeft - paddingRight,
                ALIGN_NORMAL,
                lineSpacingMultiplier,
                lineSpacingExtra,
                false
            )
        }

        var lineCount = sl.lineCount
        if (lineCount > maxCollapsedLines) {
            canExpand = true
            when (expanded) {
                true -> {
                    text = mText
                }
                false -> {
                    lineCount = maxCollapsedLines
                    val dotWidth = paint.measureText(ellipsize + ellipsizeText)
                    val stringBuild = StringBuilder()
                    for (index in 0 until lineCount) {
                        val start = sl.getLineStart(index)
                        val end = sl.getLineEnd(index)
                        if (index != lineCount - 1) {
                            stringBuild.append(mText.substring(start, end))
                            stringBuild.append("\n")
                        } else {
                            var endIndex = 0
                            val lineText = mText.substring(start, end)
                            for (i in lineText.length - 1 downTo 0) {
                                val str = lineText.substring(i, lineText.length)
                                if (paint.measureText(str) >= dotWidth) {
                                    endIndex = i
                                    break
                                }
                            }
                            stringBuild.append(lineText.substring(0, endIndex + 1))
                        }
                    }
                    stringBuild.append(ellipsize).append(ellipsizeText)
                    setClickAndColorSpan(
                        this,
                        stringBuild.toString(),
                        stringBuild.lastIndexOf(ellipsize) + ellipsize.length,
                        stringBuild.length,
                        expandCollapseTextColor
                    )
                }
            }
        } else {
            canExpand = false
            text = mText
        }
    }

    fun setContentText(text: String) {
        mText = text
        setText(text)
    }

    private fun setClickAndColorSpan(
        mTextView: TextView,
        content: String,
        start: Int,
        end: Int,
        colorId: Int
    ) {
        val ssb = SpannableStringBuilder(content)
        ssb.setSpan(
            ForegroundColorSpan(colorId),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ssb.setSpan(
            object : ClickableSpan() {
                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = false
                }

                override fun onClick(widget: View) {
                    (parent as ViewGroup).performClick()
                }
            },
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        mTextView.text = ssb
        mTextView.movementMethod = LinkMovementMethod.getInstance()
    }
}
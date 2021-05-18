package com.edwin.weatherapp.presentation.customView

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.edwin.weatherapp.R
import com.edwin.weatherapp.databinding.MaterialBannerBinding
import com.google.android.material.color.MaterialColors

class MaterialBanner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: MaterialBannerBinding

    var contentText: String? = null
        set(value) {
            field = value
            binding.contentTextView.text = value
        }

    var leftButtonText: String? = null
        set(value) {
            field = value
            binding.leftButton.text = value
        }

    var rightButtonText: String? = null
        set(value) {
            field = value
            binding.rightButton.text = value
        }

    var iconDrawableRes: Drawable? = null
        set(value) {
            field = value
            binding.contentIconView.setImageDrawable(value)
            binding.contentIconView.visibility = View.VISIBLE
        }

    init {
        val view = inflate(context, R.layout.material_banner, this)
        binding = MaterialBannerBinding.bind(view)
        takeValuesFromAttr(attrs, view)
    }

    private fun takeValuesFromAttr(attrs: AttributeSet?, view: View) = with(binding) {
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.MaterialBanner, 0, 0
        )

        contentText = typedArray.getString(
            R.styleable.MaterialBanner_contentText
        )

        leftButtonText = typedArray.getString(
            R.styleable.MaterialBanner_leftButtonText
        )

        rightButtonText = typedArray.getString(
            R.styleable.MaterialBanner_rightButtonText
        )

        iconDrawableRes = typedArray.getDrawable(
            R.styleable.MaterialBanner_icon
        )

        bannerLayout.setBackgroundColor(
            typedArray.getColor(
                R.styleable.MaterialBanner_bannerBackgroundColor,
                MaterialColors.getColor(view, R.attr.colorPrimary)
            )
        )

        contentTextView.setTextColor(
            typedArray.getColor(
                R.styleable.MaterialBanner_contentTextColor,
                ContextCompat.getColor(context, R.color.blue)
            )
        )

        leftButton.setTextColor(
            typedArray.getColor(
                R.styleable.MaterialBanner_buttonsTextColor,
                ContextCompat.getColor(context, R.color.blue)
            )
        )

        rightButton.setTextColor(
            typedArray.getColor(
                R.styleable.MaterialBanner_buttonsTextColor,
                ContextCompat.getColor(context, R.color.blue)
            )
        )

        typedArray.recycle()
    }

    fun showBanner(
        @StringRes message: Int?,
        @DrawableRes icon: Int?,
        @StringRes leftBtnText: Int?,
        @StringRes rightBtnText: Int?,
        leftButtonAction: MaterialBanner.() -> Unit,
        rightButtonAction: MaterialBanner.() -> Unit
    ) {
        contentText = message?.let { context.getString(it) }
        iconDrawableRes = icon?.let { ContextCompat.getDrawable(context, it) }
        leftButtonText = leftBtnText?.let { context.getString(it) }
        rightButtonText = rightBtnText?.let { context.getString(it) }
        setLeftButtonAction { leftButtonAction() }
        setRightButtonAction { rightButtonAction() }
        show()

    }

    fun setLeftButtonAction(action: () -> Unit) = binding.leftButton.setOnClickListener {
        action()
    }

    fun setRightButtonAction(action: () -> Unit) = binding.rightButton.setOnClickListener {
        action()
    }

    fun show() {
        measure(
            LayoutParams.MATCH_CONSTRAINT,
            LayoutParams.WRAP_CONTENT
        )
        val targetHeight = measuredHeight

        layoutParams.height = 0
        visibility = View.VISIBLE
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                layoutParams.height = if (interpolatedTime == 1f)
                    ViewGroup.LayoutParams.WRAP_CONTENT
                else
                    (targetHeight * interpolatedTime).toInt()
                requestLayout()
            }

            override fun willChangeBounds(): Boolean = true
        }

        animation.duration =
            (targetHeight / context.resources.displayMetrics.density).toInt().toLong()
        startAnimation(animation)
    }

    fun dismiss() {
        val initialHeight = measuredHeight
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    visibility = View.GONE
                } else {
                    layoutParams.height = initialHeight - (initialHeight * interpolatedTime).toInt()
                    requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean = true
        }

        animation.duration =
            (initialHeight / context.resources.displayMetrics.density).toInt().toLong()
        startAnimation(animation)
    }

}
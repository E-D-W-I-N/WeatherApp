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

    private var _contentText: String? = null
    private var _leftButtonText: String? = null
    private var _rightButtonText: String? = null
    private var _iconDrawableRes: Drawable? = null
    private var binding: MaterialBannerBinding

    var contentText: String?
        get() = _contentText
        set(value) {
            _contentText = value
            binding.contentTextView.text = value
        }

    var leftButtonText: String?
        get() = _leftButtonText
        set(value) {
            _leftButtonText = value
            binding.leftButton.text = value
        }

    var rightButtonText: String?
        get() = _rightButtonText
        set(value) {
            _rightButtonText = value
            binding.rightButton.text = value
        }

    var iconDrawableRes: Drawable?
        get() = _iconDrawableRes
        set(value) {
            _iconDrawableRes = value
            binding.contentIconView.setImageDrawable(value)
            binding.contentIconView.visibility = View.VISIBLE
        }

    init {
        val view = inflate(context, R.layout.material_banner, this)
        binding = MaterialBannerBinding.bind(view)

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

        binding.bannerLayout.setBackgroundColor(
            typedArray.getColor(
                R.styleable.MaterialBanner_bannerBackgroundColor,
                MaterialColors.getColor(view, R.attr.colorPrimary)
            )
        )

        binding.contentTextView.setTextColor(
            typedArray.getColor(
                R.styleable.MaterialBanner_contentTextColor,
                ContextCompat.getColor(context, R.color.blue)
            )
        )

        binding.leftButton.setTextColor(
            typedArray.getColor(
                R.styleable.MaterialBanner_buttonsTextColor,
                ContextCompat.getColor(context, R.color.blue)
            )
        )
        binding.rightButton.setTextColor(
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
        this.setLeftButtonAction { leftButtonAction() }
        this.setRightButtonAction { rightButtonAction() }
        this.expand()
    }

    fun dismiss() = this.collapse()

    fun setLeftButtonAction(action: () -> Unit) = binding.leftButton.setOnClickListener {
        action()
    }

    fun setRightButtonAction(action: () -> Unit) = binding.rightButton.setOnClickListener {
        action()
    }

    private fun View.expand() {
        this@expand.measure(
            LayoutParams.MATCH_CONSTRAINT,
            LayoutParams.WRAP_CONTENT
        )
        val targetHeight = this@expand.measuredHeight

        this@expand.layoutParams.height = 0
        this@expand.visibility = View.VISIBLE
        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                this@expand.layoutParams.height = if (interpolatedTime == 1f)
                    ViewGroup.LayoutParams.WRAP_CONTENT
                else
                    (targetHeight * interpolatedTime).toInt()
                this@expand.requestLayout()
            }

            override fun willChangeBounds(): Boolean = true
        }

        animation.duration =
            (targetHeight / this@expand.context.resources.displayMetrics.density).toInt().toLong()
        this@expand.startAnimation(animation)
    }

    private fun View.collapse() {
        val initialHeight = this.measuredHeight

        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                if (interpolatedTime == 1f) {
                    this@collapse.visibility = View.GONE
                } else {
                    this@collapse.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    this@collapse.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean = true
        }

        animation.duration =
            (initialHeight / this.context.resources.displayMetrics.density).toInt().toLong()
        this.startAnimation(animation)
    }


}
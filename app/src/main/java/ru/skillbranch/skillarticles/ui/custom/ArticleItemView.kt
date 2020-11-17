package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import kotlinx.android.extensions.LayoutContainer
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.ArticleItemData
import ru.skillbranch.skillarticles.extensions.attrValue
import ru.skillbranch.skillarticles.extensions.dpToIntPx
import ru.skillbranch.skillarticles.extensions.format
import kotlin.math.max

class ArticleItemView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attr, defStyleAttr) {

    private val padding = context.dpToIntPx(16)
    private val cornerRadius = context.dpToIntPx(8)
    private val posterSize = context.dpToIntPx(64)
    private val categorySize = context.dpToIntPx(40)
    private val iconSize = context.dpToIntPx(16)
    private val marginTop = context.dpToIntPx(8)
    private val marginBottom_8 = context.dpToIntPx(8)
    private val marginBottom_20 = context.dpToIntPx(20)
    private val marginStart_8 = context.dpToIntPx(8)
    private val marginStart_16 = context.dpToIntPx(16)
    private val marginEnd_16 = context.dpToIntPx(16)
    private val marginEnd_24 = context.dpToIntPx(24)

    private val smallTextSize = 12f
    private val normalTextSize = 14f
    private val bigTextSize = 18f

    private val colorGrey = context.getColor(R.color.color_gray)
    private val colorPrimary = context.attrValue(R.attr.colorPrimary)

    private val tvDate : TextView
    private val tvAuthor : TextView
    private val tvTitle : TextView
    private val tvDescription : TextView
    private val tvLikesCount : TextView
    private val tvCommentsCount : TextView
    private val tvReadDuration : TextView
    private val ivPoster : ImageView
    private val ivCategory : ImageView
    private val ivLikes : ImageView
    private val ivComments : ImageView
    private val ivBookmark : ImageView

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        setPadding(padding)

        tvDate = TextView(context).apply {
            id = R.id.tv_date
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            textSize = smallTextSize
            setTextColor(colorGrey)
        }
        addView(tvDate)

        tvAuthor = TextView(context).apply {
            id = R.id.tv_author
            //height = context.dpToIntPx(14)
            textSize = smallTextSize
            setTextColor(colorPrimary)
        }
        addView(tvAuthor)

        tvTitle = TextView(context).apply {
            id = R.id.tv_title
            setTextColor(colorPrimary)
            textSize = bigTextSize
            setTypeface(this.typeface, Typeface.BOLD)
        }
        addView(tvTitle)

        ivPoster = ImageView(context).apply {
            id = R.id.iv_poster
        }
        addView(ivPoster, LayoutParams(posterSize, posterSize))

        ivCategory = ImageView(context).apply {
            id = R.id.iv_category
        }
        addView(ivCategory, LayoutParams(categorySize, categorySize))

        tvDescription = TextView(context).apply {
            id = R.id.tv_description
            textSize = normalTextSize
            setTextColor(colorGrey)
        }
        addView(tvDescription)

        ivLikes = ImageView(context).apply {
            id = R.id.tv_author
            imageTintList = ColorStateList.valueOf(colorGrey)
            setImageResource(R.drawable.ic_favorite_black_24dp)
        }
        addView(ivLikes, LayoutParams(iconSize, iconSize))

        tvLikesCount = TextView(context).apply {
            id = R.id.tv_likes_count
            textSize = smallTextSize
            setTextColor(colorGrey)
        }
        addView(tvLikesCount, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))

        ivComments = ImageView(context).apply {
            id = R.id.iv_comments
            imageTintList = ColorStateList.valueOf(colorGrey)
            setImageResource(R.drawable.ic_insert_comment_black_24dp)
        }
        addView(ivComments, LayoutParams(iconSize, iconSize))

        tvCommentsCount = TextView(context).apply {
            id = R.id.tv_comments_count
            textSize = smallTextSize
            setTextColor(colorGrey)
        }
        addView(tvCommentsCount, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))

        tvReadDuration = TextView(context).apply {
            id = R.id.tv_read_duration
            textSize = smallTextSize
            setTextColor(colorGrey)
        }
        addView(tvReadDuration)

        ivBookmark = ImageView(context).apply {
            id = R.id.iv_bookmark
            imageTintList = ColorStateList.valueOf(colorGrey)
            setImageResource(R.drawable.bookmark_states)
        }
        addView(ivBookmark, LayoutParams(iconSize, iconSize))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)
        var usedHeight = paddingTop

        //tv_date
        measureChild(tvDate, widthMeasureSpec, heightMeasureSpec)

        //tv_author
        tvAuthor.maxWidth =
            width - (tvDate.measuredWidth + paddingRight + paddingLeft + marginStart_16)
        measureChild(tvAuthor, widthMeasureSpec, heightMeasureSpec)
        usedHeight += max(tvDate.measuredHeight, tvAuthor.measuredHeight)

        //titel + poster + category
        measureChild(ivPoster, widthMeasureSpec, heightMeasureSpec)
        measureChild(ivCategory, widthMeasureSpec, heightMeasureSpec)
        val sizeOfPosterAndCategoryImage = posterSize + (categorySize / 2)
        tvTitle.maxWidth =
            width - (paddingRight + paddingLeft + sizeOfPosterAndCategoryImage + context.dpToIntPx(8))
        measureChild(tvTitle, widthMeasureSpec, heightMeasureSpec)
        usedHeight += max(tvTitle.measuredHeight, sizeOfPosterAndCategoryImage) //marginTop + max(tvTitle.measuredHeight, sizeOfPosterAndCategoryImage)

        //tv_description
        tvDescription.maxWidth = width - (paddingLeft + paddingRight)
        measureChild(tvDescription, widthMeasureSpec, heightMeasureSpec)
        usedHeight += tvDescription.measuredHeight + 2 * marginTop

        //icons
        measureChild(ivLikes, widthMeasureSpec, heightMeasureSpec)
        measureChild(tvLikesCount, widthMeasureSpec, heightMeasureSpec)
        measureChild(ivComments, widthMeasureSpec, heightMeasureSpec)
        measureChild(tvCommentsCount, widthMeasureSpec, heightMeasureSpec)
        measureChild(ivBookmark, widthMeasureSpec, heightMeasureSpec)
        usedHeight += ivLikes.measuredHeight + marginTop + paddingBottom

        setMeasuredDimension(width, usedHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var usedHeight = paddingTop

        tvDate.layout(
            paddingLeft,
            paddingTop,
            paddingLeft + tvDate.measuredWidth,
            tvDate.measuredHeight + paddingTop
        )

        tvAuthor.layout(
            tvDate.right + marginStart_16,
            usedHeight,
            width - paddingRight,
            usedHeight + tvAuthor.measuredHeight
        )

        usedHeight += max(tvDate.measuredHeight, tvAuthor.measuredHeight) //+ marginTop

        val heightPosterCategory = posterSize + categorySize / 2

        val bottomTitle: Int
        val topPoster: Int
        val leftPoster: Int
        if (heightPosterCategory > tvTitle.measuredHeight){
            bottomTitle = usedHeight + marginTop + tvTitle.measuredHeight + (heightPosterCategory - tvTitle.measuredHeight) / 2
            topPoster = usedHeight + marginTop
            leftPoster = width - paddingRight - ivPoster.measuredWidth
            usedHeight += marginTop + heightPosterCategory
        } else {
            bottomTitle = usedHeight + marginTop + tvTitle.measuredHeight
            topPoster = usedHeight + marginTop + (tvTitle.measuredHeight - heightPosterCategory) / 2
            leftPoster = width - paddingRight - ivPoster.measuredWidth
            usedHeight += marginTop + tvTitle.measuredHeight
        }
        tvTitle.layout(
            paddingLeft,
            bottomTitle - tvTitle.measuredHeight,
            paddingLeft + tvTitle.measuredWidth,
            bottomTitle
        )
        ivPoster.layout(
            leftPoster,
            topPoster,
            leftPoster + ivPoster.measuredWidth,
            topPoster + ivPoster.measuredHeight
        )
        ivCategory.layout(
            ivPoster.left - ivCategory.measuredWidth / 2,
            ivPoster.bottom - ivCategory.measuredWidth / 2,
            ivPoster.left + ivCategory.measuredWidth / 2,
            ivPoster.bottom + ivCategory.measuredWidth / 2
        )

        val topDescription = usedHeight + marginTop
        tvDescription.layout(
            paddingLeft,
            topDescription,
            paddingLeft + tvDescription.measuredWidth,
            topDescription + tvDescription.measuredHeight
        )
        usedHeight += marginTop + tvDescription.measuredHeight

        val topIcon = usedHeight + marginTop
        val diffSizeIconAndCounter =
            (tvLikesCount.measuredHeight - ivLikes.measuredHeight) / 2
        ivLikes.layout(
            paddingLeft,
            topIcon + diffSizeIconAndCounter,
            paddingLeft + ivLikes.measuredWidth,
            topIcon + diffSizeIconAndCounter + ivLikes.measuredHeight
        )
        val leftLikesCount = ivLikes.right + marginStart_8
        tvLikesCount.layout(
            leftLikesCount,
            topIcon,
            leftLikesCount + tvLikesCount.measuredWidth,
            topIcon + tvLikesCount.measuredHeight
        )
        val leftComments = tvLikesCount.right + marginStart_16
        ivComments.layout(
            leftComments,
            topIcon + diffSizeIconAndCounter,
            leftComments + ivComments.measuredWidth,
            topIcon + diffSizeIconAndCounter + ivComments.measuredHeight
        )

        val leftCommentsCount = ivComments.right + marginStart_8
        tvCommentsCount.layout(
            leftCommentsCount,
            topIcon,
            leftCommentsCount + tvCommentsCount.measuredWidth,
            topIcon + tvCommentsCount.measuredHeight
        )

        val leftReadDuration = tvCommentsCount.right + marginStart_16
        val leftBookmark = width - paddingRight - ivBookmark.measuredWidth
        tvReadDuration.layout(
            leftReadDuration,
            topIcon,
            leftReadDuration + tvReadDuration.measuredWidth,
            topIcon + tvReadDuration.measuredHeight
        )
        ivBookmark.layout(
            leftBookmark,
            topIcon + diffSizeIconAndCounter,
            leftBookmark + ivBookmark.measuredWidth,
            topIcon + diffSizeIconAndCounter + ivBookmark.measuredHeight
        )
    }

    fun bind(item : ArticleItemData) {
        Glide.with(context)
            .load(item.poster)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .override(posterSize)
            .into(ivPoster)

        Glide.with(context)
            .load(item.categoryIcon)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .override(categorySize)
            .into(ivCategory)

        tvDate.text = item.date.format()
        tvAuthor.text = item.author
        tvTitle.text = item.title
        tvDescription.text = item.description
        tvLikesCount.text = item.likeCount.toString()
        tvCommentsCount.text = item.commentCount.toString()
        tvReadDuration.text = "${item.readDuration} min read"
    }
}


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

class ArticleItemView constructor(context: Context) : ViewGroup(context), LayoutContainer {
    override val containerView = this

    val tv_date: TextView
    val tv_author: TextView
    val tv_title: TextView
    val iv_poster: ImageView
    val iv_category: ImageView
    val tv_description: TextView
    val iv_likes: ImageView
    val tv_likes_count: TextView
    val iv_comments: ImageView
    val tv_comments_count: TextView
    val tv_read_duration: TextView
    val iv_bookmark: ImageView

    val padding = context.dpToIntPx(16)
    val defaultMargin = context.dpToIntPx(8)
    val posterSize = context.dpToIntPx(64)
    val categorySize = context.dpToIntPx(40)
    val iconSize = context.dpToIntPx(16)
    val grayColor = context.getColor(R.color.color_gray)
    val primaryColor = context.attrValue(R.attr.colorPrimary)

    init {
        this.setPadding(padding)

        tv_date = TextView(context).apply {
            id = R.id.tv_date
            textSize = 12f
            setTextColor(grayColor)
        }
        addView(tv_date)

        tv_author = TextView(context).apply {
            id = R.id.tv_author
            textSize = 12f
            setTextColor(primaryColor)
        }
        addView(tv_author)

        tv_title = TextView(context).apply {
            id = R.id.tv_title
            textSize = 18f
            setTextColor(primaryColor)
            setTypeface(typeface, Typeface.BOLD)
        }
        addView(tv_title)

        iv_poster = ImageView(context).apply {
            id = R.id.iv_poster
            layoutParams = LayoutParams(posterSize, posterSize)
        }
        addView(iv_poster)

        iv_category = ImageView(context).apply {
            id = R.id.iv_category
            layoutParams = LayoutParams(categorySize, categorySize)
        }
        addView(iv_category)

        tv_description = TextView(context).apply {
            id = R.id.tv_description
            textSize = 14f
            setTextColor(grayColor)
        }
        addView(tv_description)

        iv_likes = ImageView(context).apply {
            id = R.id.tv_author
            layoutParams = LayoutParams(iconSize, iconSize)
            imageTintList = ColorStateList.valueOf(grayColor)
            setImageResource(R.drawable.ic_favorite_black_24dp)
        }
        addView(iv_likes)

        tv_likes_count = TextView(context).apply {
            textSize = 12f
            setTextColor(grayColor)
        }
        addView(tv_likes_count)

        iv_comments = ImageView(context).apply {
            layoutParams = LayoutParams(iconSize, iconSize)
            imageTintList = ColorStateList.valueOf(grayColor)
            setImageResource(R.drawable.ic_insert_comment_black_24dp)
        }
        addView(iv_comments)

        tv_comments_count = TextView(context).apply {
            textSize = 12f
            setTextColor(grayColor)
        }
        addView(tv_comments_count)

        tv_read_duration = TextView(context).apply {
            id = R.id.tv_read_duration
            textSize = 12f
            setTextColor(grayColor)
        }
        addView(tv_read_duration)

        iv_bookmark = ImageView(context).apply {
            layoutParams = LayoutParams(iconSize, iconSize)
            imageTintList = ColorStateList.valueOf(grayColor)
            setImageResource(R.drawable.bookmark_states)
        }
        addView(iv_bookmark)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var currentHeight = paddingTop
        val width = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

        // date + author
        measureChild(tv_date, widthMeasureSpec, heightMeasureSpec)
        tv_author.maxWidth = width - (tv_date.measuredWidth + 3 * padding)
        measureChild(tv_author, widthMeasureSpec, heightMeasureSpec)
        currentHeight += tv_author.measuredHeight

        // title block
        val titleHeight = posterSize + categorySize / 2
        tv_title.maxWidth = width - (titleHeight + 2 * paddingLeft + defaultMargin)
        measureChild(tv_title, widthMeasureSpec, heightMeasureSpec)
        currentHeight += max(tv_title.measuredHeight, titleHeight) + 2 * defaultMargin

        // description block
        measureChild(tv_description, widthMeasureSpec, heightMeasureSpec)
        currentHeight += tv_description.measuredHeight + 2 * defaultMargin

        // icons block
        measureChild(tv_likes_count, widthMeasureSpec, heightMeasureSpec)
        measureChild(tv_comments_count, widthMeasureSpec, heightMeasureSpec)
        measureChild(tv_read_duration, widthMeasureSpec, heightMeasureSpec)

        currentHeight += iconSize + paddingBottom
        setMeasuredDimension(width, currentHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var currentHeight = paddingTop
        var paddingLeft = getPaddingLeft()
        val bodyWidth = right - left - paddingLeft - paddingRight
        //Log.e("ArticleItemView", "bodyWidth = $bodyWidth")

        // author + date
        tv_date.layout(paddingLeft, currentHeight, paddingLeft + tv_date.measuredWidth, currentHeight + tv_date.measuredHeight)
        paddingLeft += tv_date.right + padding
        tv_author.layout(paddingLeft, currentHeight, paddingLeft + tv_author.measuredWidth, currentHeight + tv_author.measuredHeight)
        currentHeight += tv_author.measuredHeight + defaultMargin
        paddingLeft = getPaddingLeft()

        // title block
        val titleHeight = posterSize + categorySize / 2
        if (titleHeight > tv_title.measuredHeight) {
            val diffH = (titleHeight - tv_title.measuredHeight) / 2
            tv_title.layout(paddingLeft, currentHeight + diffH, paddingLeft + tv_title.measuredWidth, currentHeight + diffH + tv_title.measuredHeight)
            paddingLeft = padding
            iv_poster.layout(paddingLeft + bodyWidth - posterSize, currentHeight, paddingLeft + bodyWidth, currentHeight + posterSize)
            iv_category.layout(iv_poster.left - categorySize / 2, iv_poster.bottom - categorySize / 2, iv_poster.left + categorySize / 2, iv_poster.bottom + categorySize / 2)
            currentHeight += titleHeight
        } else {
            val diffH = (tv_title.measuredHeight - titleHeight) / 2
            tv_title.layout(left, currentHeight, left + tv_title.measuredWidth, currentHeight + tv_title.measuredHeight)
            iv_poster.layout(left + bodyWidth - posterSize, currentHeight + diffH, left + bodyWidth, currentHeight + diffH + posterSize)
            iv_category.layout(iv_poster.left - categorySize / 2, iv_poster.bottom - categorySize / 2, iv_poster.left + categorySize / 2, iv_poster.bottom + categorySize / 2)
            currentHeight += tv_title.measuredHeight
        }
        paddingLeft = padding
        currentHeight += defaultMargin

        // description
        tv_description.layout(paddingLeft, currentHeight, paddingLeft + bodyWidth, currentHeight + tv_description.measuredHeight)
        currentHeight += tv_description.measuredHeight + defaultMargin

        // icons block
        val fontDiff = iconSize - tv_likes_count.measuredHeight
        iv_likes.layout(paddingLeft, currentHeight - fontDiff, paddingLeft + iconSize, currentHeight + iconSize - fontDiff)
        paddingLeft = iv_likes.right + defaultMargin

        tv_likes_count.layout(paddingLeft, currentHeight, paddingLeft + tv_likes_count.measuredWidth, currentHeight + tv_likes_count.measuredHeight)
        paddingLeft = tv_likes_count.right + padding

        iv_comments.layout(paddingLeft, currentHeight - fontDiff, paddingLeft + iconSize, currentHeight + iconSize - fontDiff)
        paddingLeft = iv_comments.right + defaultMargin

        tv_comments_count.layout(paddingLeft, currentHeight, paddingLeft + tv_comments_count.measuredWidth, currentHeight + tv_comments_count.measuredHeight)
        paddingLeft = tv_comments_count.right + padding

        tv_read_duration.layout(paddingLeft, currentHeight, paddingLeft + tv_read_duration.measuredWidth, currentHeight + tv_read_duration.measuredHeight)
        paddingLeft = padding

        iv_bookmark.layout(paddingLeft + bodyWidth - iconSize, currentHeight - fontDiff, paddingLeft + bodyWidth, currentHeight + iconSize - fontDiff)
    }

    fun bind(item: ArticleItemData) {
        val cornerRadius = context.dpToIntPx(8)

        Glide.with(context)
            .load(item.poster)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .override(posterSize)
            .into(iv_poster)

        Glide.with(context)
            .load(item.categoryIcon)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .override(categorySize)
            .into(iv_category)

        tv_date.text = item.date.format()
        tv_author.text = item.author
        tv_title.text = item.title
        tv_description.text = item.description
        tv_likes_count.text = item.likeCount.toString()
        tv_comments_count.text = item.commentCount.toString()
        tv_read_duration.text = "${item.readDuration} min read"
    }
}
/*
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
        usedHeight += marginTop + max(tvTitle.measuredHeight, sizeOfPosterAndCategoryImage)

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

        usedHeight += max(tvDate.measuredHeight, tvAuthor.measuredHeight) + marginTop

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

 */
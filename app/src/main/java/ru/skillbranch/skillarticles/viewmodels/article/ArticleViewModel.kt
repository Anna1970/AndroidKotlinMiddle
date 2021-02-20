package ru.skillbranch.skillarticles.viewmodels.article

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.skillbranch.skillarticles.data.models.CommentItemData
import ru.skillbranch.skillarticles.data.repositories.ArticleRepository
import ru.skillbranch.skillarticles.data.repositories.CommentsDataFactory
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement
import ru.skillbranch.skillarticles.data.repositories.clearContent
import ru.skillbranch.skillarticles.extensions.data.toAppSettings
import ru.skillbranch.skillarticles.extensions.indexesOf
import ru.skillbranch.skillarticles.extensions.shortFormat
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.base.Notify
import java.util.concurrent.Executors

class ArticleViewModel(
    handle: SavedStateHandle,
    private val articleID: String
) : BaseViewModel<ArticleState>(
        handle,
        ArticleState()
    ), IArticleViewModel {
    private val repository = ArticleRepository
    private var clearContent:String? = null
    private val listConfig by lazy {
        PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(5)
            .build()
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val listData: LiveData<PagedList<CommentItemData>> =
        Transformations.switchMap(repository.findArticleCommentCount(articleID)) {
            buildPagedList(repository.loadAllComments(articleID, it))
        }

    init {
        //subscribe on mutable data
        subscribeOnDataSource(repository.findArticle(articleID)) { article, state ->
            if (article.content == null) fetchContent()
            state.copy(
                shareLink = article.shareLink,
                title = article.title,
                category = article.category.title,
                categoryIcon = article.category.icon,
                date = article.date.shortFormat(),
                author = article.author,
                isBookmark = article.isBookmark,
                isLike = article.isLike,
                content = article.content?: emptyList(),
                isLoadingContent = article.content == null//,
                //hashtags = article.tags,
                //source = article.source
            )
        }

        subscribeOnDataSource(repository.getAppSettings()) { settings, state ->
            state.copy(
                isDarkMode = settings.isDarkMode,
                isBigText = settings.isBigText
            )
        }

        subscribeOnDataSource(repository.isAuth()) { auth, state ->
            state.copy(isAuth = auth)
        }
    }

    private fun fetchContent(){
        viewModelScope.launch(Dispatchers.IO) {
            repository.fetchArticleContent(articleID)
        }
    }

    //app settings
    override fun handleNightMode() {
        val settings = currentState.toAppSettings()
        repository.updateSettings(settings.copy(isDarkMode = !settings.isDarkMode))
    }

    override fun handleUpText() {
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = true))
    }

    override fun handleDownText(){
        repository.updateSettings(currentState.toAppSettings().copy(isBigText = false))
    }

    //personal article info
    override fun handleBookmark() {

        val msg = if (!currentState.isBookmark) "Add to bookmarks" else "Remove from bookmarks"
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleBookmark(articleID)
            withContext(Dispatchers.Main) {
                notify(Notify.TextMessage(msg))
            }
        }
    }

    override fun handleLike() {
        val isLiked = currentState.isLike
        val msg = if (!isLiked) Notify.TextMessage("Mark is liked")
        else {
            Notify.ActionMessage(
                "Don`t like it anymore", //message
                "No, still like it" //action label on snackbar
                 //handler function? is press "No, still like it" on snackbar? then toggle again
            ){ handleLike()}
        }
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleLike(articleID)
            if (isLiked) repository.decrementLike(articleID) else repository.incrementLike(articleID)
            withContext(Dispatchers.Main) {
                notify(msg)
            }
        }
    }

    override fun handleShare() {
        val msg = "Share is not implemented"
        notify(Notify.ErrorMessage(msg, "OK", null))
    }

    //session state
    override fun handleToggleMenu() {
        updateState { it.copy(isShowMenu = !it.isShowMenu)}
    }

    override fun handleSearchMode(isSearch: Boolean) {
        updateState { it.copy(isSearch = isSearch)/*, isShowMenu = false, searchPosition = 0)*/ }
    }

    override fun handleSearch(query: String?) {
        query ?: return
        if (clearContent == null && currentState.content.isNotEmpty()) clearContent = currentState.content.clearContent()
        val result = clearContent
            .indexesOf(query)
            .map {it to it + query.length}
        updateState { it.copy(searchQuery = query, searchResults = result, searchPosition = 0) }
    }

    override fun handleUpResult() {
        updateState { it.copy(searchPosition = it.searchPosition.dec()) }
    }

    override fun handleDownResult() {
        updateState { it.copy(searchPosition = it.searchPosition.inc()) }
    }

    override fun handleCopyCode() {
        notify(Notify.TextMessage("Code copy to clipboard"))
    }

    override fun handleSendComment(comment: String) {
        if (comment == null) {
            notify(Notify.TextMessage("Comment must be not empty"))
            return
        }

        updateState { it.copy(commentText = comment) }
        saveState()
        if (!currentState.isAuth) navigate(NavigationCommand.StartLogin())
        else {
            viewModelScope.launch(Dispatchers.IO) {
                repository.sendMessage(
                    articleID,
                    currentState.commentText!!,
                    currentState.answerToSlug
                )
                withContext(Dispatchers.Main) {
                    updateState { it.copy(answerTo = null, answerToSlug = null, commentText = null) }
                }
            }
        }
    }

    fun observeList(
        owner: LifecycleOwner,
        onChanged: (list:PagedList<CommentItemData>) -> Unit
    ) {
        listData.observe(owner, Observer { onChanged(it) })
    }

    private fun buildPagedList(
        dataFactory: CommentsDataFactory
    ): LiveData<PagedList<CommentItemData>> {
        return LivePagedListBuilder<String, CommentItemData>(
            dataFactory,
            listConfig
        )
            .setFetchExecutor(Executors.newSingleThreadExecutor())
            .build()
    }

    fun handleCommentFocus(hasFocus: Boolean) {
        updateState { it.copy(showBottombar = !hasFocus) }
    }

    fun handleClearComment() {
        updateState { it.copy(answerTo = null, answerToSlug = null, commentText = null) }
    }

    fun handleReplayTo(slug: String, name: String) {
        updateState { it.copy(answerToSlug = slug, answerTo = "Replay to $name") }
    }
}

data class ArticleState(
    val isAuth: Boolean = false, //пользователь авторизован
    val isLoadingContent: Boolean = true, //content загружается
    val isLoadingReviews: Boolean = true,//отзывы загружаются
    val isLike: Boolean = false, //лайкнуто
    val isBookmark: Boolean = false, //в закладках
    val isShowMenu: Boolean = false,
    val isBigText: Boolean = false,
    val isDarkMode: Boolean = false, //темный режим
    val isSearch: Boolean = false, //режим поиска
    val searchQuery: String? = null, //поисковый запрос
    val searchResults: List<Pair<Int, Int>> = emptyList(),//результаты поиска(стартовая и конечная позиции
    val searchPosition: Int = 0, //текущая позиция найденного результата
    val shareLink: String? = null, //ссылка Share
    val title: String? = null, //заголовок статьи
    val category: String? = null, //категория
    val categoryIcon: Any? = null, //иконка категории
    val date: String? = null, //дата публикации
    val author: Any? = null,//автор статьи
    val poster: String? = null, //обложка статьи
    val content: List<MarkdownElement> = emptyList(), //контент
    val commentText: String? = null,
    val commentsCount: Int = 0,
    val answerTo: String? = null,
    val answerToSlug: String? = null,
    val showBottombar: Boolean = true,
    val hashtags: List<String> = emptyList(),
    val source: String? = null
) : IViewModelState {
    override fun save(outState: SavedStateHandle) {
        outState.set("isSearch", isSearch)
        outState.set("searchQuery", searchQuery)
        outState.set("searchResults", searchResults)
        outState.set("searchPosition", searchPosition)
        outState.set("commentText", commentText)
        outState.set("answerTo", answerTo)
        outState.set("answerToSlug", answerToSlug)
    }

    override fun restore(savedState: SavedStateHandle): ArticleState {
        return copy(
            isSearch = savedState["isSearch"]  ?: false,
            searchQuery =  savedState["searchQuery"],
            searchResults = savedState["searchResults"]  ?: emptyList(),
            searchPosition =  savedState["searchPosition"]  ?: 0,
            commentText = savedState["commentText"],
            answerTo = savedState["answerTo"],
            answerToSlug = savedState["answerToSlug"]
        )
    }
}

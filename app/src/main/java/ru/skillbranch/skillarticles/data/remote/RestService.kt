package ru.skillbranch.skillarticles.data.remote

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import ru.skillbranch.skillarticles.data.models.User
import ru.skillbranch.skillarticles.data.remote.req.*
import ru.skillbranch.skillarticles.data.remote.res.*

interface RestService {
    //https://skill-articles.skill-branch.ru/api/v1/articles?last=article)d&limit=10
    @GET("articles")
    suspend fun articles(
        @Query("last") last: String? = null,
        @Query("limit") limit: Int = 10
    ): List<ArticleRes>

    //https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/content
    @GET("articles/{article}/content")
    suspend fun loadArticleContent(
        @Path("article") articleId: String
    ) : ArticleContentRes

    //https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/messages
    @GET("articles/{article}/messages")
    fun loadComments(
        @Path("article") articleId: String,
        @Query("last") last: String? = null,
        @Query("limit") limit: Int = 5
    ): Call<List<CommentRes>>

    //https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/messages
    @POST("articles/{article}/messages")
    suspend fun sendMessage(
        @Path("article") articleId: String,
        @Body message: MessageReq,
        @Header("Authorization") token: String?
    ): MessageRes

    //https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/counts
    @GET("articles/{article}/counts")
    suspend fun loadArticleCounts(
        @Path("article") articleId: String
    ): ArticleCountsRes

    //https://skill-articles.skill-branch.ru/api/v1/auth/login
    @POST("auth/login")
    suspend fun login(@Body loginReq: LoginReq): AuthRes

    @POST("auth/login")
    fun loginCall(@Body loginReq: LoginReq): Call<AuthRes>

    @POST("auth/register")
    suspend fun register(@Body registrationReq: RegistrationReq): AuthRes


    //POST https://skill-articles.skill-branch.ru/api/v1/auth/refresh
    @POST("auth/refresh")
    fun refreshAccessToken(@Body refresh: RefreshReq): Call<RefreshRes>

    //https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/decrementLikes
    @POST("articles/{article}/decrementLikes")
    suspend fun decrementLike(
        @Path("article") articleId: String,
        @Header("Authorization") token: String
    ): LikeRes

    //https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/incrementLikes
    @POST("articles/{article}/incrementLikes")
    suspend fun incrementLike(
        @Path("article") articleId: String,
        @Header("Authorization") token: String
    ): LikeRes

    //https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/addBookmark
    @POST("articles/{article}/addBookmark")
    suspend fun addBookmark(
        @Path("article") articleId: String,
        @Header("Authorization") token: String
    ): BookmarkRes

    //https://skill-articles.skill-branch.ru/api/v1/articles/{articleId}/removeBookmark
    @POST("articles/{article}/removeBookmark")
    suspend fun removeBookmark(
        @Path("article") articleId: String,
        @Header("Authorization") token: String
    ): BookmarkRes


    @Multipart
    @POST("profile/avatar/upload")
    suspend fun upload(
        @Part file: MultipartBody.Part?,
        @Header("Authorization") token: String
    ):UploadRes

    @PUT("profile/avatar/remove")
    suspend fun removeProfileAvatar(@Header("Authorization") auth: String): UploadRes

    @PUT("profile")
    suspend fun editProfile(@Body editProfileReq: EditProfileReq, @Header("Authorization") auth: String): User
}
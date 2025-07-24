package com.mobileni.gamehunt.data.network

import com.mobileni.gamehunt.data.model.GameDetailResponse
import com.mobileni.gamehunt.data.model.GameListResponse
import com.mobileni.gamehunt.data.model.GenreResponse
import com.mobileni.gamehunt.data.model.ScreenshotResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface defining the RAWG API endpoints used in the application.
 *
 * This service provides methods for retrieving genres, game listings,
 * detailed game information, and game screenshots.
 */
interface RawgApiService {

    /**
     * Retrieves a list of available game genres.
     *
     * @param apiKey The API key for authentication.
     * @return A response containing the list of genres.
     */
    @GET("genres")
    suspend fun getGenres(
        @Query("key") apiKey: String
    ): GenreResponse

    /**
     * Retrieves a paginated list of games with optional filters.
     *
     * @param apiKey The API key for authentication.
     * @param metacritic Optional filter to retrieve games by metacritic score range (e.g., "90,100").
     * @param genreSlug Optional genre slug to filter games.
     * @param search Optional search query string.
     * @param page The page number to retrieve.
     * @param pageSize Number of items per page.
     * @return A response containing a list of games.
     */
    @GET("games")
    suspend fun getGames(
        @Query("key") apiKey: String,
        @Query("metacritic") metacritic: String? = null,
        @Query("genres") genreSlug: String? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int,
        @Query("page_size") pageSize: Int
    ): GameListResponse

    /**
     * Retrieves detailed information for a specific game.
     *
     * @param slug The unique slug identifier of the game.
     * @param apiKey The API key for authentication.
     * @return A response containing detailed game data.
     */
    @GET("games/{slug}")
    suspend fun getGameDetail(
        @Path("slug") slug: String,
        @Query("key") apiKey: String
    ): GameDetailResponse

    /**
     * Retrieves a list of screenshots for a given game.
     *
     * @param id The unique ID of the game.
     * @param apiKey The API key for authentication.
     * @param pageSize Number of screenshots to return per request.
     * @return A response containing the game's screenshots.
     */
    @GET("games/{id}/screenshots")
    suspend fun getGameScreenshots(
        @Path("id") id: Int,
        @Query("key") apiKey: String,
        @Query("page_size") pageSize: Int
    ): ScreenshotResponse

}
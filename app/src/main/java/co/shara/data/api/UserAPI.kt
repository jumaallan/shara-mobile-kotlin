package co.shara.data.api

import co.shara.data.retrofit.UserLogin
import co.shara.data.retrofit.UserRegister
import co.shara.data.retrofit.UserResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPI {

    @POST("/api/v1/register")
    suspend fun registerUser(@Body userRegister: UserRegister): UserResponse?

    @POST("/api/v1/login")
    suspend fun loginUser(@Body userLogin: UserLogin): UserResponse?

}

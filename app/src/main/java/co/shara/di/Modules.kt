package co.shara.di

import androidx.room.Room
import co.shara.BuildConfig
import co.shara.data.Database
import co.shara.network.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun injectFeature() = loadFeature

private val loadFeature by lazy {

    loadKoinModules(
        listOf(
            retrofitModule,
            databaseModule,
            daoModule
        )
    )
}

val retrofitModule = module(override = true) {
    single {

        val baseUrl = "https://shara-api.herokuapp.com/"

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = when (BuildConfig.BUILD_TYPE) {
            "release" -> HttpLoggingInterceptor.Level.NONE
            else -> HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(AuthInterceptor(get()))
            .build()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }
}

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            Database::class.java,
            "shara_db"
        ).build()
    }
}

val daoModule = module {
    single { get<Database>().userDao() }
}
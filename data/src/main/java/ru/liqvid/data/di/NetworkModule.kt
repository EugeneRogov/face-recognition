package ru.liqvid.data.di

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {
    @Singleton
    @Provides
    @Named("retrofit")
    fun provideRetrofitOpenClinics(): Retrofit {
        val builder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl("https://yandex.ru/")
            .addConverterFactory(GsonConverterFactory.create())
        return builder.build()
    }

}
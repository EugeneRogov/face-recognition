package ru.liqvid.data.di

import dagger.Component
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface NetworkComponent {
    @Named("retrofit")
    fun retrofitOpenClinics(): Retrofit


}
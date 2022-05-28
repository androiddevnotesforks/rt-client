package com.automotivecodelab.featurefavoritesimpl.di

import com.automotivecodelab.featurefavoritesapi.AddToFavoriteUseCase
import com.automotivecodelab.featurefavoritesapi.DeleteFromFavoritesUseCase
import com.automotivecodelab.featurefavoritesapi.ObserveFavoritesUseCase
import com.automotivecodelab.featurefavoritesimpl.data.FavoritesDao
import com.automotivecodelab.featurefavoritesimpl.data.FavoritesRepositoryImpl
import com.automotivecodelab.featurefavoritesimpl.domain.AddToFavoritesUseCaseImpl
import com.automotivecodelab.featurefavoritesimpl.domain.DeleteFromFavoritesUseCaseImpl
import com.automotivecodelab.featurefavoritesimpl.domain.FavoritesRepository
import com.automotivecodelab.featurefavoritesimpl.domain.ObserveFavoritesUseCaseImpl
import com.automotivecodelab.featurefavoritesimpl.ui.FavoritesViewModel
import dagger.Binds
import dagger.Component
import dagger.Module
import javax.inject.Singleton

@Singleton
@Component(
    dependencies = [FavoritesComponentDeps::class],
    modules = [FavoritesModule::class]
)
interface FavoritesComponent {
    fun favoritesViewModel(): FavoritesViewModel
}

@Module
interface FavoritesModule {
    @Binds
    fun bindFavoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository

    @Binds
    fun bindObserveFavoritesUseCase(impl: ObserveFavoritesUseCaseImpl): ObserveFavoritesUseCase

    @Binds
    fun bindAddToFavoritesUseCase(impl: AddToFavoritesUseCaseImpl): AddToFavoriteUseCase

    @Binds
    fun bindDeleteFromFavoritesUseCase(
        impl: DeleteFromFavoritesUseCaseImpl
    ): DeleteFromFavoritesUseCase
}

interface FavoritesComponentDeps {
    val favoritesDao: FavoritesDao
}

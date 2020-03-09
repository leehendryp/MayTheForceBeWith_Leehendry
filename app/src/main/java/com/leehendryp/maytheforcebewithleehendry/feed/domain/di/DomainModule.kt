package com.leehendryp.maytheforcebewithleehendry.feed.domain.di

import com.leehendryp.maytheforcebewithleehendry.feed.domain.FetchPeopleUseCase
import com.leehendryp.maytheforcebewithleehendry.feed.domain.FetchPeopleUseCaseImpl
import com.leehendryp.maytheforcebewithleehendry.feed.domain.PeopleRepository
import com.leehendryp.maytheforcebewithleehendry.feed.domain.SaveFavoriteUseCase
import com.leehendryp.maytheforcebewithleehendry.feed.domain.SaveFavoriteUseCaseImpl
import com.leehendryp.maytheforcebewithleehendry.feed.domain.SearchCharacterUseCase
import com.leehendryp.maytheforcebewithleehendry.feed.domain.SearchCharacterUseCaseImpl
import dagger.Module
import dagger.Provides

@Module
class DomainModule {
    @Provides
    fun provideFetchPeopleUseCase(repo: PeopleRepository): FetchPeopleUseCase =
        FetchPeopleUseCaseImpl(repo)

    @Provides
    fun provideSearchCharacterUseCase(repo: PeopleRepository): SearchCharacterUseCase =
        SearchCharacterUseCaseImpl(repo)

    @Provides
    fun provideSaveFavoriteUseCase(repo: PeopleRepository): SaveFavoriteUseCase =
        SaveFavoriteUseCaseImpl(repo)
}
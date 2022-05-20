package com.automotivecodelab.rtclient.di

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloRequest
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.interceptor.ApolloInterceptor
import com.apollographql.apollo3.interceptor.ApolloInterceptorChain
import com.apollographql.apollo3.network.okHttpClient
import com.automotivecodelab.corenetwork.data.FirebaseRegistrationTokenHolder
import com.automotivecodelab.corenetwork.data.NetworkStatusListener
import com.automotivecodelab.corenetwork.data.NoInternetConnectionException
import com.automotivecodelab.featuredetails.data.TorrentDetailsServerApi
import com.automotivecodelab.featuredetails.di.TorrentDetailsDiConstants
import com.automotivecodelab.rtclient.BuildConfig
import com.automotivecodelab.rtclient.data.FirebaseRegistrationTokenHolderImpl
import com.automotivecodelab.rtclient.data.ServerApi
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@Module(includes = [NetworkModule.Bindings::class])
class NetworkModule {
    @Singleton
    @Provides
    fun provideOkHttpClient(networkStatusListener: NetworkStatusListener) = OkHttpClient.Builder()
        .addInterceptor { chain ->
            if (!networkStatusListener.isNetworkAvailable) throw NoInternetConnectionException()
            chain.proceed(chain.request())
        }
        .build()

    @Singleton
    @Provides
    fun provideServerApi(okHttpClient: OkHttpClient): ServerApi = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(BuildConfig.SERVER_URL)
        .addConverterFactory(TorrentDetailsServerApi.getGsonConverterFactory())
        .build()
        .create(ServerApi::class.java)

    @Singleton
    @Provides
    fun provideApolloGraphQlClient(
        okHttpClient: OkHttpClient,
        networkStatusListener: NetworkStatusListener
    ) = ApolloClient.Builder()
        .serverUrl(BuildConfig.SERVER_URL + "graphql")
        .okHttpClient(okHttpClient)
        // idk why okHttp interceptor not working here. the only reason that apollo applies its
        // own interceptors before okHttp ones
        .addInterceptor(object : ApolloInterceptor {
            override fun <D : Operation.Data> intercept(
                request: ApolloRequest<D>,
                chain: ApolloInterceptorChain
            ): Flow<ApolloResponse<D>> {
                if (!networkStatusListener.isNetworkAvailable) throw NoInternetConnectionException()
                return chain.proceed(request)
            }
        })
        .build()

    @Provides
    @Named(TorrentDetailsDiConstants.SERVER_URL)
    fun provideServerURL() = BuildConfig.SERVER_URL

    @Module
    interface Bindings {
        @Binds
        fun bindDetailsApi(impl: ServerApi): TorrentDetailsServerApi

        @Binds
        fun bindFirebaseTokenHolder(
            impl: FirebaseRegistrationTokenHolderImpl
        ): FirebaseRegistrationTokenHolder
    }
}

package com.example.stoma_mobile.di

import com.example.stoma_mobile.data.repository.AppointmentRepository
import com.example.stoma_mobile.data.repository.DoctorRepository
import com.example.stoma_mobile.data.repository.ScheduleRepository
import com.example.stoma_mobile.data.repository.ServiceRepository
import com.example.stoma_mobile.data.repository.impl.AppointmentRepositoryImpl
import com.example.stoma_mobile.data.repository.mock.MockDoctorRepository
import com.example.stoma_mobile.data.repository.mock.MockScheduleRepository
import com.example.stoma_mobile.data.repository.mock.MockServiceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindServiceRepository(impl: MockServiceRepository): ServiceRepository

    @Binds
    @Singleton
    abstract fun bindDoctorRepository(impl: MockDoctorRepository): DoctorRepository

    @Binds
    @Singleton
    abstract fun bindScheduleRepository(impl: MockScheduleRepository): ScheduleRepository

    @Binds
    @Singleton
    abstract fun bindAppointmentRepository(
        impl: AppointmentRepositoryImpl
    ): AppointmentRepository
}


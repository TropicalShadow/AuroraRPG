package me.tropicalshadow.aurorarpg.utils

import java.util.*


interface Service<T> {
    fun save(t: T)
    fun delete(uuid: UUID)
    fun update(uuid: UUID)
    fun find(uuid: UUID): T
    fun removeFromCache(uuid: UUID)
}
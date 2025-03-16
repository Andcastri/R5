package com.example.backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "home_plans")
data class HomePlan(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var planName: String,

    @Column(nullable = false)
    var internetSpeed: String,

    @Column(nullable = false)
    var television: String,

    @Column(nullable = false)
    var decoder: String,

    @Column(nullable = false)
    var localPhone: String,

    @Column(nullable = false)
    var price: Double,

    @Column(nullable = false)
    var tariffCode: String,

    @Column(nullable = false)
    var campaign: String,

    @Column(nullable = false)
    var imageUrl: String,

    @Column(columnDefinition = "TEXT")
    var description: String,

    @Column(nullable = false)
    var lastUpdated: LocalDateTime = LocalDateTime.now()
) 
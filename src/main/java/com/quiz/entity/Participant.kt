package com.quiz.entity

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.Nationalized

@Entity
@Table(name = "participants")
@DiscriminatorValue("PARTICIPANT")
open class Participant @JvmOverloads constructor (

    @Nationalized
    @Column(name = "first_name", nullable = false, length = 100)
    open var firstName: String? = null,

    @Nationalized
    @Column(name = "last_name", nullable = false, length = 100)
    open var lastName: String? = null,

    @Nationalized
    @Column(name = "father_name", nullable = true, length = 100)
    open var fatherName: String? = null,

    @Column(name = "email", nullable = false, unique = true, length = 100)
    open var email: String? = null,

    @Column(name = "password", nullable = false, length = 100)
    open var password: String? = null,

    @Column(name = "status", nullable = false, columnDefinition = "boolean default true")
    open var status: Boolean = true,

    @Column(name = "phone_number")
    open var phoneNumber: String? = null,

    @Column(name = "birth_date")
    open var birthDate: String? = null,

    @Column(name = "gender")
    open var gender: String? = null,

    @Column(name = "role", nullable = false)
    open var role: String = "ROLE_PARTICIPANT",

    @OneToOne(mappedBy = "participant", fetch = FetchType.LAZY)
    open var attachment: Attachment? = null,

): BaseEntity()
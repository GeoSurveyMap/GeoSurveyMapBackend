package com.loess.geosurveymap.user

fun UserEntity.toResponse() =
    User(id, kindeId, email, status, permissions)

fun User.toEntity() =
    UserEntity(id, kindeId, email, permissions, status)

fun UserRequest.toUser() =
    User(kindeId = kindeId, email = email, permissions = permissions)
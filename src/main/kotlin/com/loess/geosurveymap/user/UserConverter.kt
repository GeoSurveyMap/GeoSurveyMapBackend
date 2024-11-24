package com.loess.geosurveymap.user

fun UserEntity.toResponse() =
    User(id, kindeId, email, role, permissions)

fun User.toEntity() =
    UserEntity(id, kindeId, role, email, permissions)

fun UserRequest.toUser() =
    User(kindeId = kindeId, email = email, role = role, permissions = permissions)
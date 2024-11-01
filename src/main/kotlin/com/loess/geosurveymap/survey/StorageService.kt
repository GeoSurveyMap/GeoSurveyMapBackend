package com.loess.geosurveymap.survey

import io.minio.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class StorageService(
    private val minioClient: MinioClient,
    @Value("\${minio.bucket.name}") private val bucketName: String
) {

    fun uploadFile(objectName: String, inputStream: InputStream, contentType: String) {
        try {
            val found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())

            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
            }

            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(objectName)
                    .stream(inputStream, inputStream.available().toLong(), -1)
                    .contentType(contentType)
                    .build()
            )
        } catch (e: Exception) {
            throw RuntimeException("Error occurred: " + e.message)
        }
    }

}

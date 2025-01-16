package com.loess.geosurveymap.survey

import io.github.oshai.kotlinlogging.KotlinLogging
import io.minio.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.InputStream

@Service
class StorageService(
    private val minioClient: MinioClient,
    @Value("\${minio.bucket.name}") private val bucketName: String
) {

    private val log = KotlinLogging.logger {  }

    fun uploadFile(objectName: String, inputStream: InputStream, contentType: String) {
        try {
            log.info { "Looking for the bucket..." }
            val found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())
            log.info { "Bucket found: $found" }

            if (!found) {
                log.info { "Bucket not found, I am creating one..." }
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
            }

            log.info { "Putting the object into bucket..." }
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .`object`(objectName)
                    .stream(inputStream, inputStream.available().toLong(), -1)
                    .contentType(contentType)
                    .build()
            )
            log.info { "Object put successfully" }
        } catch (e: Exception) {
            throw RuntimeException("Error occurred: " + e.message)
        }
    }

}

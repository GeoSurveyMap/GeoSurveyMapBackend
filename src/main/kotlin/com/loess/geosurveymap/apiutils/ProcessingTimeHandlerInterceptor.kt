package com.loess.geosurveymap.apiutils
import com.mobile.amigoapp.api.utils.setProcessingStart
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor
import java.time.Instant

class ProcessingTimeHandlerInterceptor : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        request.setProcessingStart(Instant.now())
        return true
    }
}

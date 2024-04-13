package com.loess.geosurveymap.auditable

import org.springframework.data.domain.AuditorAware
import java.util.*

class SecurityAuditorAware : AuditorAware<String> {
    override fun getCurrentAuditor(): Optional<String> {
        return Optional.of("unknown_user") // TODO: implement taking user from security here
    }
}

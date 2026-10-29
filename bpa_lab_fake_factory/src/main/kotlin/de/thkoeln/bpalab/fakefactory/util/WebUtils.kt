package de.thkoeln.bpalab.fakefactory.util

import jakarta.servlet.http.HttpServletRequest
import org.springframework.context.MessageSource
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.servlet.LocaleResolver


@Component
class WebUtils(
    messageSource: MessageSource,
    localeResolver: LocaleResolver
) {

    init {
        WebUtils.messageSource = messageSource
        WebUtils.localeResolver = localeResolver
    }

    companion object {

        const val MSG_SUCCESS = "MSG_SUCCESS"

        const val MSG_INFO = "MSG_INFO"

        const val MSG_ERROR = "MSG_ERROR"

        lateinit var messageSource: MessageSource

        lateinit var localeResolver: LocaleResolver

        @JvmStatic
        fun getRequest(): HttpServletRequest = (RequestContextHolder.getRequestAttributes() as
                ServletRequestAttributes).request

        @JvmStatic
        fun getMessage(code: String, vararg args: Any): String? = messageSource.getMessage(code,
                args, code, localeResolver.resolveLocale(getRequest()))

    }

}

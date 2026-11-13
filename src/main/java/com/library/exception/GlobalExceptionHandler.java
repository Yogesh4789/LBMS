package com.library.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * SPRING CONTROLLER ADVICE: GlobalExceptionHandler
 * 
 * Intercepts uncaught domain exceptions across controllers, displays friendly
 * error notifications via flash attributes, and prevents internal stack traces
 * from leaking to end users.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            BookNotFoundException.class,
            BookUnavailableException.class,
            MemberNotFoundException.class,
            DuplicateBookException.class,
            InvalidBorrowingException.class,
            InvalidReturnException.class,
            IllegalArgumentException.class
    })
    public String handleDomainException(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());

        // Redirect back to referring page or dashboard if referer is missing
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return "redirect:/dashboard";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred: " + ex.getMessage());
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            return "redirect:" + referer;
        }
        return "redirect:/dashboard";
    }
}


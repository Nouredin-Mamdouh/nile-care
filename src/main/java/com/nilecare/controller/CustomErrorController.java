package com.nilecare.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

/**
 * Custom Error Controller to handle error page routing
 * Maps HTTP error codes to custom error page templates
 */
@Controller
public class CustomErrorController {

    /**
     * Main error handling endpoint
     * Routes to appropriate error page based on HTTP status code
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            // Access Denied
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error/403";
            }
            // Not Found
            else if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404";
            }
            // Internal Server Error
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error/500";
            }
        }

        // Default to 500 error page for any other errors
        return "error/500";
    }

    /**
     * Direct mapping for 403 error page
     */
    @GetMapping("/error/403")
    public String accessDenied() {
        return "error/403";
    }

    /**
     * Direct mapping for 404 error page
     */
    @GetMapping("/error/404")
    public String notFound() {
        return "error/404";
    }

    /**
     * Direct mapping for 500 error page
     */
    @GetMapping("/error/500")
    public String serverError() {
        return "error/500";
    }

    /**
     * Test endpoint to simulate a 500 error
     * Access this at: /test/error500
     */
    @GetMapping("/test/error500")
    public String testServerError() {
        throw new RuntimeException("This is a simulated 500 error for testing purposes");
    }
}

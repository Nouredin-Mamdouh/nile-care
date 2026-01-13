package com.nilecare.controller;

import com.nilecare.model.User;
import com.nilecare.repository.StudentProgressRepository;
import com.nilecare.repository.UserRepository;
import com.nilecare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * ReportsController handles admin reports and analytics
 * Provides dashboard view with charts and CSV export functionality
 */
@Controller
@RequestMapping("/admin/reports")
public class ReportsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProgressRepository studentProgressRepository;

    @Autowired
    private UserService userService;

    /**
     * Display admin reports dashboard with charts
     * Fetches module completion stats and user growth data for the last 6 months
     * 
     * @param model Spring MVC model for passing data to view
     * @param principal Spring Security Principal containing authenticated user info
     * @return reports view
     */
    @GetMapping
    public String showReports(Model model, Principal principal) {
        // Add current user to model for header
        if (principal != null) {
            String email = principal.getName();
            User currentUser = userService.findByEmail(email);
            if (currentUser != null) {
                model.addAttribute("currentUser", currentUser);
            }
        }

        // ========== MODULE COMPLETION STATISTICS ==========
        long completedCount = studentProgressRepository.countByStatus("COMPLETED");
        long inProgressCount = studentProgressRepository.countByStatus("IN_PROGRESS");
        long notStartedCount = studentProgressRepository.countByStatus("NOT_STARTED");

        model.addAttribute("completedCount", completedCount);
        model.addAttribute("inProgressCount", inProgressCount);
        model.addAttribute("notStartedCount", notStartedCount);

        // ========== USER GROWTH - LAST 6 MONTHS ==========
        List<String> monthLabels = new ArrayList<>();
        List<Long> userGrowthData = new ArrayList<>();
        List<java.util.Map<String, Object>> growthTableData = new ArrayList<>();

        // Calculate for the last 6 months
        LocalDateTime now = LocalDateTime.now();
        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.now().minusMonths(i);
            monthLabels.add(month.getMonth().name().substring(0, 3));

            // Count users created in this month
            LocalDateTime monthStart = month.atDay(1).atStartOfDay();
            LocalDateTime monthEnd = month.atEndOfMonth().atTime(23, 59, 59);
            long usersInMonth = userRepository.countByCreatedAtBetween(monthStart, monthEnd);
            userGrowthData.add(usersInMonth);

            // Build table data (month, year, count)
            java.util.Map<String, Object> monthData = new java.util.HashMap<>();
            monthData.put("month", month.format(DateTimeFormatter.ofPattern("MMM")));
            monthData.put("year", month.getYear());
            monthData.put("count", usersInMonth);
            growthTableData.add(monthData);
        }

        model.addAttribute("monthLabels", monthLabels);
        model.addAttribute("userGrowthData", userGrowthData);
        model.addAttribute("growthTableData", growthTableData);

        // ========== TOTAL STATISTICS ==========
        long totalUsers = userRepository.count();
        long totalStudents = userRepository.countStudents();
        long unverifiedUsers = userRepository.countByVerifiedFalse();
        long totalEnrollments = completedCount + inProgressCount + notStartedCount;

        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("unverifiedUsers", unverifiedUsers);
        model.addAttribute("totalEnrollments", totalEnrollments);

        return "admin/reports";
    }

    /**
     * Export admin report data as CSV
     * Generates a CSV file with module statistics and user growth data
     * Uses PrintWriter for direct response output to avoid view resolution issues
     * 
     * @param response HttpServletResponse for writing CSV directly
     * @param principal Spring Security Principal containing authenticated user info
     */
    @GetMapping("/export/csv")
    public void exportReportAsCSV(HttpServletResponse response, Principal principal) throws IOException {
        // Verify user is authenticated
        if (principal == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        // Set response headers for CSV download
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"nilecare_admin_report.csv\"");
        
        PrintWriter writer = response.getWriter();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // ========== CSV HEADER ==========
        writer.println("NileCare Admin Report");
        writer.println("Generated on: " + LocalDateTime.now().format(dateFormatter));
        writer.println();

        // ========== SECTION 1: MODULE STATISTICS ==========
        writer.println("=== Module Statistics ===");
        writer.println("Status,Count");

        long completedCount = studentProgressRepository.countByStatus("COMPLETED");
        long inProgressCount = studentProgressRepository.countByStatus("IN_PROGRESS");
        long notStartedCount = studentProgressRepository.countByStatus("NOT_STARTED");
        long totalEnrollments = completedCount + inProgressCount + notStartedCount;

        writer.println("Completed," + completedCount);
        writer.println("In Progress," + inProgressCount);
        writer.println("Not Started," + notStartedCount);
        writer.println("Total Enrollments," + totalEnrollments);
        writer.println();

        // ========== SECTION 2: USER GROWTH - LAST 6 MONTHS ==========
        writer.println("=== User Growth (Last 6 Months) ===");
        writer.println("Month,New Registrations");

        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.now().minusMonths(i);
            LocalDateTime monthStart = month.atDay(1).atStartOfDay();
            LocalDateTime monthEnd = month.atEndOfMonth().atTime(23, 59, 59);
            long usersInMonth = userRepository.countByCreatedAtBetween(monthStart, monthEnd);
            
            String monthLabel = month.format(DateTimeFormatter.ofPattern("MMM yyyy"));
            writer.println(monthLabel + "," + usersInMonth);
        }

        writer.println();

        // ========== SECTION 3: OVERALL STATISTICS ==========
        writer.println("=== Overall Statistics ===");
        writer.println("Metric,Value");
        
        long totalUsers = userRepository.count();
        long studentCount = userRepository.countStudents();
        long unverifiedCount = userRepository.countByVerifiedFalse();

        writer.println("Total Users," + totalUsers);
        writer.println("Total Students," + studentCount);
        writer.println("Unverified Users," + unverifiedCount);

        writer.flush();
    }
}

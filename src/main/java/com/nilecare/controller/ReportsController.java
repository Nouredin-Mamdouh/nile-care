package com.nilecare.controller;

import com.nilecare.model.User;
import com.nilecare.repository.StudentProgressRepository;
import com.nilecare.repository.UserRepository;
import com.nilecare.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
     * 
     * @param principal Spring Security Principal containing authenticated user info
     * @return ResponseEntity with CSV content and attachment headers
     */
    @GetMapping("/export/csv")
    public ResponseEntity<String> exportReportAsCSV(Principal principal) {
        // Verify user is authenticated and is admin
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Build CSV content
        StringBuilder csvContent = new StringBuilder();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // ========== CSV HEADER ==========
        csvContent.append("NileCare Admin Report\n");
        csvContent.append("Generated on: ").append(LocalDateTime.now().format(dateFormatter)).append("\n\n");

        // ========== SECTION 1: MODULE STATISTICS ==========
        csvContent.append("Module Statistics\n");
        csvContent.append("Report Type,Count\n");

        long completedCount = studentProgressRepository.countByStatus("COMPLETED");
        long inProgressCount = studentProgressRepository.countByStatus("IN_PROGRESS");
        long notStartedCount = studentProgressRepository.countByStatus("NOT_STARTED");
        long totalEnrollments = completedCount + inProgressCount + notStartedCount;

        csvContent.append("Completed,").append(completedCount).append("\n");
        csvContent.append("In Progress,").append(inProgressCount).append("\n");
        csvContent.append("Not Started,").append(notStartedCount).append("\n");
        csvContent.append("Total Enrollments,").append(totalEnrollments).append("\n\n");

        // ========== SECTION 2: USER GROWTH - LAST 6 MONTHS ==========
        csvContent.append("User Growth\n");
        csvContent.append("Month,New Registrations\n");

        for (int i = 5; i >= 0; i--) {
            YearMonth month = YearMonth.now().minusMonths(i);
            LocalDateTime monthStart = month.atDay(1).atStartOfDay();
            LocalDateTime monthEnd = month.atEndOfMonth().atTime(23, 59, 59);
            long usersInMonth = userRepository.countByCreatedAtBetween(monthStart, monthEnd);
            
            // Format as "Jan 2026" style
            String monthLabel = month.format(DateTimeFormatter.ofPattern("MMM yyyy"));
            csvContent.append(monthLabel).append(",").append(usersInMonth).append("\n");
        }

        csvContent.append("\n");

        // ========== SECTION 3: OVERALL STATISTICS ==========
        csvContent.append("Overall Statistics\n");
        csvContent.append("Metric,Value\n");
        
        long totalUsers = userRepository.count();
        long studentCount = userRepository.countStudents();
        long unverifiedCount = userRepository.countByVerifiedFalse();

        csvContent.append("Total Users,").append(totalUsers).append("\n");
        csvContent.append("Total Students,").append(studentCount).append("\n");
        csvContent.append("Unverified Users,").append(unverifiedCount).append("\n");

        // ========== SET RESPONSE HEADERS ==========
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=\"admin_report.csv\"");
        headers.add("Content-Type", "text/csv; charset=UTF-8");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvContent.toString());
    }
}

package ma.enset.ebankbackend.web;

import ma.enset.ebankbackend.dtos.DashboardStatsDTO;
import ma.enset.ebankbackend.services.DashboardService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin("*")
public class DashboardRestController {

    private final DashboardService dashboardService;

    public DashboardRestController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('USER')")
    public DashboardStatsDTO stats() {
        return dashboardService.getStats();
    }
}


package za.co.kgosi.expensetracker;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DemoModeController {

    private final Environment environment;

    public DemoModeController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/api/demo-mode")
    public Map<String, Boolean> getDemoMode() {
        boolean demoMode = environment.matchesProfiles("demo");

        return Map.of("demoMode", demoMode);
    }
}

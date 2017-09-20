package uk.tw.energy.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.service.PricePlanService;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/price-plans")
public class PricePlanComparatorController {

    private final PricePlanService pricePlanService;

    public PricePlanComparatorController(PricePlanService pricePlanService) {
        this.pricePlanService = pricePlanService;
    }

    @GetMapping("/compare-all/{smartMeterId}")
    public ResponseEntity<Map<String, BigDecimal>> calculatedCostForEachPricePlan(@PathVariable String smartMeterId) {
        Optional<Map<String, BigDecimal>> consumptionsForPricePlans = pricePlanService.getConsumptionCostOfElectricityReadingsForEachPricePlan(smartMeterId);

        return consumptionsForPricePlans.isPresent()
                ? ResponseEntity.ok(consumptionsForPricePlans.get())
                : ResponseEntity.notFound().build();
    }
}

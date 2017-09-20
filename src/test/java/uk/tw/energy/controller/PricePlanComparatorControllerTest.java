package uk.tw.energy.controller;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.service.MeterReadingService;
import uk.tw.energy.service.PricePlanService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PricePlanComparatorControllerTest {

    private PricePlanComparatorController controller;
    private MeterReadingService meterReadingService;

    private String PRICE_PLAN_1_ID = "test-price-plan";
    private String PRICE_PLAN_2_ID = "best-price-plan";

    @Before
    public void setUp() {
        meterReadingService = new MeterReadingService(new HashMap<>());
        PricePlan pricePlan = new PricePlan(PRICE_PLAN_1_ID, null, BigDecimal.TEN, null);
        PricePlan otherPricePlan = new PricePlan(PRICE_PLAN_2_ID, null, BigDecimal.ONE, null);
        List<PricePlan> pricePlans = Arrays.asList(pricePlan, otherPricePlan);
        PricePlanService pricePlanService = new PricePlanService(pricePlans, meterReadingService);
        controller = new PricePlanComparatorController(pricePlanService);
    }

    @Test
    public void shouldCalculateCostForMeterReadingsForEveryPricePlan() {
        Map<String, BigDecimal> pricePlanToCost = new HashMap<>();
        pricePlanToCost.put(PRICE_PLAN_1_ID, BigDecimal.valueOf(100.0));
        pricePlanToCost.put(PRICE_PLAN_2_ID, BigDecimal.valueOf(10.0));

        String smartMeterId = "smart-meter-id";
        ElectricityReading electricityReading = new ElectricityReading(Instant.now().minusSeconds(3600), BigDecimal.valueOf(15.0));
        ElectricityReading otherReading = new ElectricityReading(Instant.now(), BigDecimal.valueOf(5.0));
        meterReadingService.storeReadings(smartMeterId, Arrays.asList(electricityReading, otherReading));

        assertThat(controller.calculatedCostForEachPricePlan(smartMeterId).getBody()).isEqualTo(pricePlanToCost);
    }

    @Test
    public void givenNoMatchingMeterIdShouldReturnNotFound() {
        assertThat(controller.calculatedCostForEachPricePlan("not-found").getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}

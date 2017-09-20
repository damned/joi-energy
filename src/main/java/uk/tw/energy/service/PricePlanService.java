package uk.tw.energy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PricePlanService {

    @Autowired
    private final List<PricePlan> pricePlans;
    private MeterReadingService meterReadingService;

    public PricePlanService(List<PricePlan> pricePlans, MeterReadingService meterReadingService) {
        this.pricePlans = pricePlans;
        this.meterReadingService = meterReadingService;
    }

    public Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachPricePlan(String smartMeterId) {
        Optional<List<ElectricityReading>> electricityReadings = meterReadingService.getReadings(smartMeterId);

        if ( !electricityReadings.isPresent() ) {
            return Optional.empty();
        }

        return Optional.of(pricePlans.stream().collect(Collectors.toMap(PricePlan::getPlanName, t -> calculateCost(electricityReadings.get(), t))));
    }

    private BigDecimal calculateCost(List<ElectricityReading> electricityReadings, PricePlan pricePlan) {
        BigDecimal averageReading = electricityReadings.stream()
                .map(ElectricityReading::getReading)
                .reduce(BigDecimal.ZERO, (reading, accumulator) -> reading.add(accumulator))
                .divide(BigDecimal.valueOf(electricityReadings.size()), RoundingMode.HALF_UP);
        Instant firstReadingTime = electricityReadings.stream()
                .min(Comparator.comparing(ElectricityReading::getTime))
                .get()
                .getTime();
        Instant lastReadingTime = electricityReadings.stream()
                .max(Comparator.comparing(ElectricityReading::getTime))
                .get()
                .getTime();
        BigDecimal timeElapsed = BigDecimal.valueOf(Duration.between(firstReadingTime, lastReadingTime).getSeconds() / 3600.0);
        BigDecimal averagedCost = averageReading.divide(timeElapsed, RoundingMode.HALF_UP);
        return averagedCost.multiply(pricePlan.getUnitRate());
    }
    
}

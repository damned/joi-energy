package uk.tw.energy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.Tariff;

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
public class TariffService {

    @Autowired
    private final List<Tariff> tariffs;
    private MeterReadingService meterReadingService;

    public TariffService(List<Tariff> tariffs, MeterReadingService meterReadingService) {
        this.tariffs = tariffs;
        this.meterReadingService = meterReadingService;
    }

    public Optional<Map<String, BigDecimal>> getConsumptionCostOfElectricityReadingsForEachTariff(String meterId) {
        Optional<List<ElectricityReading>> electricityReadings = meterReadingService.getReadings(meterId);

        if ( !electricityReadings.isPresent() ) {
            return Optional.empty();
        }

        return Optional.of(tariffs.stream().collect(Collectors.toMap(Tariff::getName, t -> calculateCost(electricityReadings.get(), t))));
    }

    private BigDecimal calculateCost(List<ElectricityReading> electricityReadings, Tariff tariff) {
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
        return averagedCost.multiply(tariff.getUnitRate());
    }
    
}

package uk.tw.energy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.PricePlan;
import uk.tw.energy.generator.ElectricityReadingsGenerator;

import java.math.BigDecimal;
import java.util.*;

import static java.util.Collections.emptyList;

@Configuration
@SuppressWarnings("unused")
public class AppConfiguration {

    private static final String DR_EVILS_DARK_ENERGY_ENERGY_SUPPLIER = "Dr Evil's Dark Energy";
    private static final String THE_GREEN_ECO_ENERGY_SUPPLIER = "The Green Eco";
    private static final String POWER_FOR_EVERYONE_ENERGY_SUPPLIER = "Power for Everyone";
    
    private static final String MOST_EVIL_PRICE_PLAN_ID = "price-plan-0";
    private static final String RENEWABLES_PRICE_PLAN_ID = "price-plan-1";
    private static final String STANDARD_PRICE_PLAN_ID = "price-plan-2";
    
    private static final String SARAHS_SMART_METER_ID = "smart-meter-0";
    private static final String PETERS_SMART_METER_ID = "smart-meter-1";
    private static final String CHARLIES_SMART_METER_ID = "smart-meter-2";
    private static final String ANDREAS_SMART_METER_ID = "smart-meter-3";
    private static final String ALEXS_SMART_METER_ID = "smart-meter-4";

    @Bean
    public List<PricePlan> pricePlans(){
        List<PricePlan> pricePlans = new ArrayList<>();
        pricePlans.add(new PricePlan(MOST_EVIL_PRICE_PLAN_ID, DR_EVILS_DARK_ENERGY_ENERGY_SUPPLIER, BigDecimal.TEN, emptyList()));
        pricePlans.add(new PricePlan(RENEWABLES_PRICE_PLAN_ID, THE_GREEN_ECO_ENERGY_SUPPLIER, BigDecimal.valueOf(2), emptyList()));
        pricePlans.add(new PricePlan(STANDARD_PRICE_PLAN_ID, POWER_FOR_EVERYONE_ENERGY_SUPPLIER, BigDecimal.ONE, emptyList()));
        return pricePlans;
    }

    @Bean
    public Map<String, List<ElectricityReading>> perMeterElectricityReadings() {

        return generateMeterElectricityReadings();
    }

    private Map<String, List<ElectricityReading>> generateMeterElectricityReadings() {
        List<String> meterIds = Arrays.asList(SARAHS_SMART_METER_ID, PETERS_SMART_METER_ID, CHARLIES_SMART_METER_ID,
                ANDREAS_SMART_METER_ID, ALEXS_SMART_METER_ID);

        Map<String, List<ElectricityReading>> readings = new HashMap<>();
        ElectricityReadingsGenerator electricityReadingsGenerator = new ElectricityReadingsGenerator();
        meterIds.forEach(meterId -> readings.put(meterId, electricityReadingsGenerator.generate(20)));

        return readings;
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return objectMapper;
    }
}

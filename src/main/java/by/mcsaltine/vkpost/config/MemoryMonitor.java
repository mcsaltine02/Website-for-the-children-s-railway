package by.mcsaltine.vkpost.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class MemoryMonitor {

    private static final Logger log = LoggerFactory.getLogger(MemoryMonitor.class);

    @Scheduled(fixedRate = 10 * 60 * 1000)  // каждые 10 минут
    public void printMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();

        long totalMemory = runtime.totalMemory();      // всего выделено JVM
        long freeMemory = runtime.freeMemory();        // свободно внутри выделенного
        long usedMemory = totalMemory - freeMemory;    // используется
        long maxMemory = runtime.maxMemory();          // максимум (-Xmx)

        double usedPercent = (usedMemory * 100.0) / maxMemory;

        log.info("=== MEMORY REPORT ===");
        log.info("Используется: {} MB", usedMemory / 1024 / 1024);
        log.info("Всего выделено: {} MB", totalMemory / 1024 / 1024);
        log.info("Максимум (-Xmx): {} MB", maxMemory / 1024 / 1024);
        log.info("Загружено: {:.2f}%", usedPercent);
        log.info("=====================");
    }


}

package ru.polaina.project1boot.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.polaina.project1boot.services.JournalService;

import java.util.Calendar;
import java.util.Date;

@EnableScheduling
@Component
public class JournalCleanupTask {

    private final JournalService journalService;
    private static final int NUMBER_OF_DAYS_OF_RESERVE = 3;

    @Autowired
    public JournalCleanupTask(JournalService journalService) {
        this.journalService = journalService;
    }

    @Scheduled(cron = "0 * * * * ?") // Запускать каждый день в полночь
    public void cleanupExpiredReservations() {
        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.DAY_OF_MONTH, -NUMBER_OF_DAYS_OF_RESERVE); // Требуемый срок действия резерва (3 дня)
        Date currentDate = calendar.getTime();
        journalService.deleteExpiredReservations(currentDate);
    }
}

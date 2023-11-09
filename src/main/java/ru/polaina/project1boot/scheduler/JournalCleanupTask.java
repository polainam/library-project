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

    @Autowired
    public JournalCleanupTask(JournalService journalService) {
        this.journalService = journalService;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Запускать каждый день в полночь
    public void cleanupExpiredReservations() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        journalService.deleteExpiredReservations(currentDate);
    }
}

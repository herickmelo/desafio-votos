package com.example.demo.util;
import java.util.Timer;
import java.util.TimerTask;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class Reminder {
    Timer timer;
    
    public static Long idpauta = null;

    public Reminder(int seconds, Long idpauta) {
        timer = new Timer();
        timer.schedule(new RemindTask(), seconds*1000);
        this.idpauta = idpauta;
    }

    class RemindTask extends TimerTask {
        public void run() {
            System.out.println("Acabou o tempo!");
            if (idpauta != null) {
                RestTemplate restTemplate = new RestTemplate();
                String fooResourceUrl = "http://localhost:8080/v1/pauta/fecharSessao/" + idpauta;
                ResponseEntity<String> response = restTemplate.getForEntity(fooResourceUrl, String.class);
			}
            idpauta = null;
            timer.cancel(); //Terminate the timer thread
        }
    }
}
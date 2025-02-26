package pl.kathelan.cryptosageapp.zonda.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kathelan.cryptosageapp.zonda.model.Signal;
import pl.kathelan.cryptosageapp.zonda.repositories.SignalRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SignalService {

    private final SignalRepository signalRepository;
    private final AlertService alertService;

    @Autowired
    public SignalService(SignalRepository signalRepository, AlertService alertService) {
        this.signalRepository = signalRepository;
        this.alertService = alertService;
    }

    @Transactional
    public void saveSignal(String cryptoPair, String signalType) {
        Signal signal = new Signal();
        signal.setCryptoPair(cryptoPair);
        signal.setSignalType(signalType);
        signalRepository.save(signal);
        checkAndCreateAlert(cryptoPair, signalType);
    }

    private void checkAndCreateAlert(String cryptoPair, String signalType) {
        List<Signal> lastFiveSignals = signalRepository.findTop5ByCryptoPairOrderByCreatedAtDesc(cryptoPair);
        if (lastFiveSignals.size() < 5) {
            return;
        }
        boolean allSame = lastFiveSignals.stream()
                .allMatch(signal -> signal.getSignalType().equals(signalType));
        if (allSame) {
            alertService.createAlert(cryptoPair, signalType);
        }
    }

    @Transactional
    public void deleteByCreatedAtBefore(LocalDateTime localDateTime) {
        signalRepository.deleteByCreatedAtBefore(localDateTime);
    }
}

package pl.kathelan.cryptosageapp.zonda.services.calculation;

import org.springframework.stereotype.Component;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.dtos.SignalType;

@Component
public interface SignalListener {
    void onSignalGenerated(CryptoPair cryptoPair, SignalType signalType);
}

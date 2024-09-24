package pl.kathelan.cryptosageapp.zonda.services.calculation;

import org.springframework.stereotype.Component;
import pl.kathelan.cryptosageapp.zonda.dtos.CryptoPair;
import pl.kathelan.cryptosageapp.zonda.dtos.Signal;

@Component
public interface SignalListener {
    void onSignalGenerated(CryptoPair cryptoPair, Signal signal);
}

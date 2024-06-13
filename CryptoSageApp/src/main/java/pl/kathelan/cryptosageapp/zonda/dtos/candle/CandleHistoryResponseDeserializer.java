package pl.kathelan.cryptosageapp.zonda.dtos.candle;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CandleHistoryResponseDeserializer extends StdDeserializer<CandleHistoryResponse> {

    public CandleHistoryResponseDeserializer() {
        this(null);
    }

    public CandleHistoryResponseDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public CandleHistoryResponse deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        String status = node.get("status").asText();
        List<CandleItem> items = new ArrayList<>();

        JsonNode itemsNode = node.get("items");
        for (JsonNode itemNode : itemsNode) {
            long time = itemNode.get(0).asLong();
            JsonNode dataNode = itemNode.get(1);

            CandleData data = new CandleData(
                    dataNode.get("o").asDouble(),
                    dataNode.get("c").asDouble(),
                    dataNode.get("h").asDouble(),
                    dataNode.get("l").asDouble(),
                    dataNode.get("v").asDouble()
            );

            CandleItem item = new CandleItem(time, data);
            items.add(item);
        }

        CandleHistoryResponse response = new CandleHistoryResponse();
        response.setStatus(status);
        response.setItems(items);
        return response;
    }
}


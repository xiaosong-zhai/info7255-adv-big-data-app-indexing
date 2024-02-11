package northeastern.xiaosongzhai.medical.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import java.io.IOException;

/**
 * @Author: Xiaosong Zhai
 * @Date: 2024/2/10 18:17
 * @Description: strict integer deserializer
 */
public class StrictIntegerDeserializer extends JsonDeserializer<Integer> {

    /**
     * strict integer deserializer
     * @param p json parser
     * @param ctxt deserialization context
     * @return integer
     */
    @Override
    public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (!p.getCurrentToken().isNumeric()) {
            throw new InvalidFormatException(p, "Expected integer", p.getText(), Integer.class);
        }
        return p.getIntValue();
    }
}

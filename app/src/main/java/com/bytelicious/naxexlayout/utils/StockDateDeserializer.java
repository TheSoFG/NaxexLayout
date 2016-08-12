package com.bytelicious.naxexlayout.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import org.apache.commons.lang3.time.DateUtils;

import java.io.IOException;
import java.util.Date;

/**
 * Created by ACER PC on 8/12/2016.
 */
public class StockDateDeserializer extends JsonDeserializer<Date> {

    private static final long serialVersionUID = -2275951539867772400L;

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {

        if (jp.getCurrentTokenId() == JsonTokenId.ID_STRING) {
            try {
                return DateUtils.parseDate(jp.getText(), "dd/MM/yyyy HH:mm:ss");
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }
}

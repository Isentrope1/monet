package com.isentropy.monet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;

public class PriceChecker {

	public static final String COINMARKETCAP_API_V1_URL = "https://api.coinmarketcap.com/v1/ticker/";
	private HashMap<String,Double> prices = new HashMap<String,Double>();
	private PriceChecker() {}
	public static PriceChecker fromCoinMarketCapJson() throws IOException{
		return fromCoinMarketCapJson(COINMARKETCAP_API_V1_URL);
	}
	public static PriceChecker fromCoinMarketCapJson(String cmc_url) throws IOException{
		URL url = new URL(cmc_url);
		Reader r = new InputStreamReader(url.openStream(),StandardCharsets.UTF_8);
		return fromCoinMarketCapJson(r);
	}
	public Double getPrice(String symbol){
		return prices.get(symbol);
	}
	public static PriceChecker fromCoinMarketCapJson(Reader r){
		PriceChecker pc = new PriceChecker();
		JsonReader jr = Json.createReader(r);
		JsonArray currencies = jr.readArray();
		for(JsonValue v : currencies){
			JsonObject c = (JsonObject) v;
			String symb = c.getString("symbol");
			Double price_usd = Double.parseDouble(c.getString("price_usd"));
			pc.prices.put(symb, price_usd);
		}
		return pc;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

package com.anthill.coinswapscannerstore;

import com.anthill.coinswapscannerstore.beans.*;
import com.anthill.coinswapscannerstore.services.ForkService;
import com.anthill.coinswapscannerstore.services.RedisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootTest
class CoinswapScannerStoreApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	ForkService forkService;
	@Autowired
	RedisService redis;

	@Test
	void redisForkServiceTest() {
		//Arrange
		var quote = new Quote(new UsdPrice(BigDecimal.ONE, BigDecimal.ONE, new Date()));
		var fork = new Fork(
				new Token(1, "Some token", "some-token", "STK",
						new Platform(1, 1, "Binance"),
						quote),
				new Pair("STK/USDT", "some url",
						new Exchange(0, "Binance", quote),
						new Date(), BigDecimal.ONE, BigDecimal.ONE),
				new Pair("STK/USDT","some url",
						new Exchange(0, "PancakeSwap", quote),
						new Date(), BigDecimal.ONE, BigDecimal.ONE),
				BigDecimal.TEN,"some url", new Date()
		);
		var forks = new HashMap<String, Object>();
		forks.put(fork.hashCodeString(), fork);

		//Act
		forkService.save(forks);

		//Assert
		assert forkService.findAll().iterator().hasNext();
	}

	@Test
	void getHashFromForkShouldBeEqual(){
		var hashcode = -1915050797;

		var quote1 = new Quote(new UsdPrice(BigDecimal.ONE, BigDecimal.ONE, new Date()));
		var fork1 = new Fork(
				new Token(1, "Some token", "some-token", "STK",
						new Platform(1, 1, "Pancake"),
						quote1),
				new Pair("STK/USDT", "some url",
						new Exchange(0, "Binance", quote1),
						new Date(), BigDecimal.ONE, BigDecimal.ONE),
				new Pair("STK/USDT","some url",
						new Exchange(0, "PancakeSwap", quote1),
						new Date(), BigDecimal.ONE, BigDecimal.ONE),
				BigDecimal.TEN,"some url", new Date()
		);

		var quote2 = new Quote(new UsdPrice(BigDecimal.TEN, BigDecimal.TEN, new Date()));
		var fork2 = new Fork(
				new Token(2, "Some token22", "some-token11", "STKDD",
						new Platform(11, 12, "Pancake"),
						quote2),
				new Pair("STK/USDT", "some url22",
						new Exchange(22, "Binance", quote2),
						new Date(), BigDecimal.ONE, BigDecimal.TEN),
				new Pair("STK/USDT","some url",
						new Exchange(0, "PancakeSwap", quote2),
						new Date(), BigDecimal.TEN, BigDecimal.TEN),
				BigDecimal.TEN,"some url222", new Date()
		);

		var hashcode1 = fork1.hashCode();
		var hashcode2 = fork2.hashCode();

		assert hashcode1 == hashcode2 && hashcode1 == hashcode;
	}

	@Test
	void hasForkKey_ShouldHas(){
		var hashKey = "7bd76679-5ebc-428a-a703-a06cc7c9e624";
		var key = "Fork";

		var isExist = redis.hHasKey(key, hashKey);

		assert isExist;
	}

	@Test
	void countAllRealForks(){
		var forkCount = redis.hGetAll("ForkUpdate").values()
				.stream()
				.map(a -> (Object[])a)
				.flatMap(Stream::of)
				.count();

		assert forkCount > 0;
	}
}

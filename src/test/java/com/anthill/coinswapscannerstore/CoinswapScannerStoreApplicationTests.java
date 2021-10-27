package com.anthill.coinswapscannerstore;

import com.anthill.coinswapscannerstore.beans.*;
import com.anthill.coinswapscannerstore.services.ForkService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;

@SpringBootTest
class CoinswapScannerStoreApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	ForkService forkService;

	@Test
	void RedisForkServiceTest() {
		//Arrange
		var quote = new Quote(new UsdPrice(BigDecimal.ONE, BigDecimal.ONE, new Date()));
		var forks = Collections.singletonList(
				new Fork(
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
				)
		);

		//Act
		forkService.save(forks);

		//Assert
		assert forkService.findAll().iterator().hasNext();
	}
}

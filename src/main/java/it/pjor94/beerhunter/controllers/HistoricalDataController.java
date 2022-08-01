package it.pjor94.beerhunter.controllers;

import com.binance.api.client.domain.general.ExchangeInfo;
import it.pjor94.beerhunter.core.exchange.BinanceClient;
import it.pjor94.beerhunter.model.HistoricalData;
import it.pjor94.beerhunter.repository.HistoricalDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historicalData")
public class HistoricalDataController {

    @Autowired
    HistoricalDataRepository historicalDataRepository;

    @GetMapping("/all")
    Page<HistoricalData>  getAll(@PageableDefault(sort = { "id" }, value = 10) Pageable pageInfo){
        return historicalDataRepository.findAll(pageInfo);
    }
    @GetMapping("/exchangeinfo")
    ExchangeInfo exchangeInfo(){
        return new BinanceClient().getClient().getExchangeInfo();
    }
}

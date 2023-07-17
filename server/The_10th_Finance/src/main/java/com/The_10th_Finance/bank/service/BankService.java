package com.The_10th_Finance.bank.service;

import com.The_10th_Finance.bank.db.Bank;
import com.The_10th_Finance.bank.db.BankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor

public class BankService {
    private final BankRepository bankRepository;
    @Transactional
       public Bank post(Bank bankPostDtoToBank) {
       return bankRepository.save(bankPostDtoToBank);
    }
}

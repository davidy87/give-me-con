package com.givemecon.domain.repository;

import com.givemecon.IntegrationTestEnvironment;
import com.givemecon.domain.repository.voucher.RejectedSaleRepository;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class RepositoryTestEnvironment extends IntegrationTestEnvironment {

    @Autowired
    protected RejectedSaleRepository rejectedSaleRepository;
}

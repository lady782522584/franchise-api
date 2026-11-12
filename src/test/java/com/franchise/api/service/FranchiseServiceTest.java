package com.franchise.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.franchise.api.domain.model.Branch;
import com.franchise.api.domain.model.Franchise;
import com.franchise.api.domain.model.Product;
import com.franchise.api.exception.NotFoundException;
import com.franchise.api.repository.FranchiseRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FranchiseServiceTest {

    @Mock
    private FranchiseRepository repository;

    @InjectMocks
    private FranchiseService service;

    private Franchise sampleFranchise() {
        Product pizza = Product.builder().id("p1").name("Pizza").stock(10).build();
        Product soda = Product.builder().id("p2").name("Soda").stock(50).build();
        Product bread = Product.builder().id("p3").name("Pan").stock(5).build();

        Branch centro = Branch.builder().id("b1").name("Centro")
                .products(new ArrayList<>(List.of(pizza, soda))).build();
        Branch norte = Branch.builder().id("b2").name("Norte")
                .products(new ArrayList<>(List.of(bread))).build();

        return Franchise.builder().id("f1").name("Mi Franquicia")
                .branches(new ArrayList<>(List.of(centro, norte))).build();
    }

    @Test
    void topProductPerBranch_returnsTheHighestStockProductOfEachBranch() {
        when(repository.findById("f1")).thenReturn(Mono.just(sampleFranchise()));

        StepVerifier.create(service.topProductPerBranch("f1").collectList())
                .assertNext(result -> {
                    assertEquals(2, result.size());
                    assertEquals("Soda", result.get(0).productName());
                    assertEquals(50, result.get(0).stock());
                    assertEquals("Centro", result.get(0).branchName());
                    assertEquals("Pan", result.get(1).productName());
                    assertEquals("Norte", result.get(1).branchName());
                })
                .verifyComplete();
    }

    @Test
    void updateStock_changesTheStockOfTheProduct() {
        when(repository.findById("f1")).thenReturn(Mono.just(sampleFranchise()));
        when(repository.save(any(Franchise.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));

        StepVerifier.create(service.updateStock("f1", "b1", "p1", 99))
                .assertNext(franchise -> {
                    Product pizza = franchise.getBranches().get(0).getProducts().get(0);
                    assertEquals(99, pizza.getStock());
                })
                .verifyComplete();
    }

    @Test
    void removeProduct_whenProductDoesNotExist_failsWithNotFound() {
        when(repository.findById("f1")).thenReturn(Mono.just(sampleFranchise()));

        StepVerifier.create(service.removeProduct("f1", "b1", "no-existe"))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void addBranch_whenFranchiseDoesNotExist_failsWithNotFound() {
        when(repository.findById("x")).thenReturn(Mono.empty());

        StepVerifier.create(service.addBranch("x", "Sucursal"))
                .expectError(NotFoundException.class)
                .verify();
    }
}
package com.franchise.api.service;

import com.franchise.api.domain.model.Branch;
import com.franchise.api.domain.model.Franchise;
import com.franchise.api.domain.model.Product;
import com.franchise.api.exception.NotFoundException;
import com.franchise.api.repository.FranchiseRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class FranchiseService {

    private final FranchiseRepository repository;

    public FranchiseService(FranchiseRepository repository) {
        this.repository = repository;
    }

    public Mono<Franchise> createFranchise(String name) {
        return repository.save(Franchise.builder().name(name).build());
    }

    public Mono<Franchise> addBranch(String franchiseId, String branchName) {
        return findFranchise(franchiseId).flatMap(franchise -> {
            Branch branch = Branch.builder()
                    .id(UUID.randomUUID().toString())
                    .name(branchName)
                    .build();
            franchise.getBranches().add(branch);
            return repository.save(franchise);
        });
    }

    public Mono<Franchise> addProduct(String franchiseId, String branchId, String productName, Integer stock) {
        return findFranchise(franchiseId).flatMap(franchise -> {
            Branch branch = findBranch(franchise, branchId);
            branch.getProducts().add(Product.builder()
                    .id(UUID.randomUUID().toString())
                    .name(productName)
                    .stock(stock)
                    .build());
            return repository.save(franchise);
        });
    }

    public Mono<Franchise> removeProduct(String franchiseId, String branchId, String productId) {
        return findFranchise(franchiseId).flatMap(franchise -> {
            Branch branch = findBranch(franchise, branchId);
            boolean removed = branch.getProducts().removeIf(p -> p.getId().equals(productId));
            if (!removed) {
                return Mono.error(new NotFoundException("Producto no encontrado: " + productId));
            }
            return repository.save(franchise);
        });
    }

    public Mono<Franchise> updateStock(String franchiseId, String branchId, String productId, Integer stock) {
        return findFranchise(franchiseId).flatMap(franchise -> {
            Product product = findProduct(findBranch(franchise, branchId), productId);
            product.setStock(stock);
            return repository.save(franchise);
        });
    }

    public Flux<TopProduct> topProductPerBranch(String franchiseId) {
        return findFranchise(franchiseId).flatMapMany(franchise -> {
            List<TopProduct> result = new ArrayList<>();
            for (Branch branch : franchise.getBranches()) {
                branch.getProducts().stream()
                        .max(Comparator.comparing(Product::getStock))
                        .ifPresent(p -> result.add(new TopProduct(
                                branch.getId(), branch.getName(), p.getId(), p.getName(), p.getStock())));
            }
            return Flux.fromIterable(result);
        });
    }

    public Mono<Franchise> renameFranchise(String franchiseId, String newName) {
        return findFranchise(franchiseId).flatMap(franchise -> {
            franchise.setName(newName);
            return repository.save(franchise);
        });
    }

    public Mono<Franchise> renameBranch(String franchiseId, String branchId, String newName) {
        return findFranchise(franchiseId).flatMap(franchise -> {
            findBranch(franchise, branchId).setName(newName);
            return repository.save(franchise);
        });
    }

    public Mono<Franchise> renameProduct(String franchiseId, String branchId, String productId, String newName) {
        return findFranchise(franchiseId).flatMap(franchise -> {
            findProduct(findBranch(franchise, branchId), productId).setName(newName);
            return repository.save(franchise);
        });
    }

    private Mono<Franchise> findFranchise(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new NotFoundException("Franquicia no encontrada: " + id)));
    }

    private Branch findBranch(Franchise franchise, String branchId) {
        return franchise.getBranches().stream()
                .filter(b -> b.getId().equals(branchId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Sucursal no encontrada: " + branchId));
    }

    private Product findProduct(Branch branch, String productId) {
        return branch.getProducts().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Producto no encontrado: " + productId));
    }

    public record TopProduct(String branchId, String branchName, String productId,
                             String productName, Integer stock) {
    }
}
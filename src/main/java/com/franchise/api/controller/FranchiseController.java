package com.franchise.api.controller;

import com.franchise.api.domain.model.Franchise;
import com.franchise.api.dto.NameRequest;
import com.franchise.api.dto.ProductRequest;
import com.franchise.api.dto.StockRequest;
import com.franchise.api.service.FranchiseService;
import com.franchise.api.service.FranchiseService.TopProduct;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/franchises")
public class FranchiseController {

    private final FranchiseService service;

    public FranchiseController(FranchiseService service) {
        this.service = service;
    }

    // 2. Agregar franquicia
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> createFranchise(@Valid @RequestBody NameRequest request) {
        return service.createFranchise(request.name());
    }

    // 3. Agregar sucursal a una franquicia
    @PostMapping("/{franchiseId}/branches")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> addBranch(@PathVariable String franchiseId,
                                     @Valid @RequestBody NameRequest request) {
        return service.addBranch(franchiseId, request.name());
    }

    // 4. Agregar producto a una sucursal
    @PostMapping("/{franchiseId}/branches/{branchId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> addProduct(@PathVariable String franchiseId,
                                      @PathVariable String branchId,
                                      @Valid @RequestBody ProductRequest request) {
        return service.addProduct(franchiseId, branchId, request.name(), request.stock());
    }

    // 5. Eliminar producto de una sucursal
    @DeleteMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    public Mono<Franchise> removeProduct(@PathVariable String franchiseId,
                                         @PathVariable String branchId,
                                         @PathVariable String productId) {
        return service.removeProduct(franchiseId, branchId, productId);
    }

    // 6. Modificar el stock de un producto
    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/stock")
    public Mono<Franchise> updateStock(@PathVariable String franchiseId,
                                       @PathVariable String branchId,
                                       @PathVariable String productId,
                                       @Valid @RequestBody StockRequest request) {
        return service.updateStock(franchiseId, branchId, productId, request.stock());
    }

    // 7. Producto con más stock por sucursal de una franquicia
    @GetMapping("/{franchiseId}/top-products")
    public Flux<TopProduct> topProducts(@PathVariable String franchiseId) {
        return service.topProductPerBranch(franchiseId);
    }

    // Plus: renombrar franquicia
    @PatchMapping("/{franchiseId}")
    public Mono<Franchise> renameFranchise(@PathVariable String franchiseId,
                                           @Valid @RequestBody NameRequest request) {
        return service.renameFranchise(franchiseId, request.name());
    }

    // Plus: renombrar sucursal
    @PatchMapping("/{franchiseId}/branches/{branchId}")
    public Mono<Franchise> renameBranch(@PathVariable String franchiseId,
                                        @PathVariable String branchId,
                                        @Valid @RequestBody NameRequest request) {
        return service.renameBranch(franchiseId, branchId, request.name());
    }

    // Plus: renombrar producto
    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    public Mono<Franchise> renameProduct(@PathVariable String franchiseId,
                                         @PathVariable String branchId,
                                         @PathVariable String productId,
                                         @Valid @RequestBody NameRequest request) {
        return service.renameProduct(franchiseId, branchId, productId, request.name());
    }
}
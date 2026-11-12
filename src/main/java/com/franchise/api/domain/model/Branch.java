package com.franchise.api.domain.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Branch {
    private String id;
    private String name;
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}

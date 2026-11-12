package com.franchise.api.domain.model;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "franchises")
public class Franchise {
    @Id
    private String id;
    private String name;
    @Builder.Default
    private List<Branch> branches = new ArrayList<>();
}

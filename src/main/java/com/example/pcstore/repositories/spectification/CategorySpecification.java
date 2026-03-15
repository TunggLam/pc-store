package com.example.pcstore.repositories.spectification;

import com.example.pcstore.entity.Category;
import com.example.pcstore.repositories.CategoryRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CategorySpecification {

    private final CategoryRepository repository;

    public List<Category> findAllByStatus(Boolean status) {
        Specification<Category> conditions = Specification.where(byStatus(status));
        return repository.findAll(conditions);
    }

    private static Specification<Category> byStatus(Boolean status) {
        return (Root<Category> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (status == null) {
                return null;
            }
            return criteriaBuilder.equal(root.get("isActive"), status);
        };
    }

}


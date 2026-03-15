package com.example.pcstore.repositories.spectification;

import com.example.pcstore.entity.Cart;
import com.example.pcstore.logging.LoggingFactory;
import com.example.pcstore.repositories.CartRepository;
import com.example.pcstore.utils.StringUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CartSpecification {

    private static final Logger LOGGER = LoggingFactory.getLogger(CartSpecification.class);

    private static final String ID = "id";

    private final CartRepository cartRepository;

    public List<Cart> findAllById(String id) {
        Specification<Cart> conditions = Specification.where(hasId(id));
        return cartRepository.findAll(conditions);
    }

    private static Specification<Cart> hasId(String id) {
        return (Root<Cart> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (StringUtils.isNullOrEmpty(id)) {
                return null;
            }
            return criteriaBuilder.equal(root.get(ID), id);
        };
    }
}


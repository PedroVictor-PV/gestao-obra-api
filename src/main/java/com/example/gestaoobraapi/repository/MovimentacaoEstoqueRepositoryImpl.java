package com.example.gestaoobraapi.repository;

import com.example.gestaoobraapi.model.MovimentacaoEstoque;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class MovimentacaoEstoqueRepositoryImpl implements MovimentacaoEstoqueRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<MovimentacaoEstoque> findAllWithFilters(Long obraId, LocalDate dataInicio, LocalDate dataFim) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MovimentacaoEstoque> query = cb.createQuery(MovimentacaoEstoque.class);
        Root<MovimentacaoEstoque> root = query.from(MovimentacaoEstoque.class);

        root.fetch("obra", JoinType.INNER);
        root.fetch("material", JoinType.INNER);
        root.fetch("fornecedor", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        if (obraId != null) {
            predicates.add(cb.equal(root.get("obra").get("id"), obraId));
        }
        if (dataInicio != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("dataMovimentacao"), dataInicio));
        }
        if (dataFim != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("dataMovimentacao"), dataFim));
        }

        if (!predicates.isEmpty()) {
            query.where(predicates.toArray(Predicate[]::new));
        }

        query.orderBy(
                cb.desc(root.get("dataMovimentacao")),
                cb.desc(root.get("criadoEm")));

        return entityManager.createQuery(query).getResultList();
    }
}

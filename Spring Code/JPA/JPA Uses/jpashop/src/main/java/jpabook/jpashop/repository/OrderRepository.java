package jpabook.jpashop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
public class OrderRepository {
    private final EntityManager em;
    private final JPAQueryFactory query

    public OrderRepository(EntityManager em) {
        this.em = em;
        this.query = new JPAQueryFactory(em);
    }

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch) {
        String jpql = "SELECT o FROM Order o JOIN o.member m";

        boolean isFirstCondition = true;

        // 주문 상태 검색
        if(orderSearch.getOrderStatus() != null) {
            if(isFirstCondition) {
                jpql += " WHERE";
                isFirstCondition = false;
            } else {
                jpql += " AND";
            }
            jpql += " o.status = :status";
        }

        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if(isFirstCondition) {
                jpql += " WHERE";
                isFirstCondition = false;
            } else {
                jpql += " AND";
            }
            jpql += " m.name LIKE :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                                .setMaxResults(1000);

        if(orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }

        if(StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }

    /**
     * JPA Criteria
     */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);

        Join<Order, Member> m = o.join("member", JoinType.INNER); // 회원과 조인

        List<Predicate> criteria = new ArrayList<>();

        // 주문 상세 검색
        if(orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        // 회원 이름 검색
        if(StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);// 최대 1000건

        return query.getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery("SELECT o FROM Order o "
                        + "JOIN FETCH o.member m "
                        + "JOIN FETCH o.delivery d", Order.class)
                    .getResultList();
    }

    public List<Order> findAllWithItem() {
        return em.createQuery(
                        "SELECT DISTINCT o FROM Order o " +
                                "JOIN FETCH o.member m " +
                                "JOIN FETCH o.delivery d " +
                                "JOIN FETCH o.orderItems oi " +
                                "JOIN FETCH oi.item i", Order.class)
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery("SELECT o FROM Order o "
                        + "JOIN FETCH o.member m "
                        + "JOIN FETCH o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public List<Order> findAll(OrderSearch orderSearch) {
        QOrder order = QOrder.order; // static import 가능
        QMember member = QMember.member; // static import 가능

        return query.select(order)
                     .from(order)
                     .join(order.member, member)
                     .where(statusEq(orderSearch.getOrderStatus()), nameLike(orderSearch.getMemberName()))
                     .limit(1000)
                     .fetch();
    }

    private BooleanExpression nameLike(String memberName) {
        if(!StringUtils.hasText(memberName)) {
            return null;
        }

        return QMember.member.name.like("%" + memberName + "%"); // 💡 Hibernate 6 오류 (주의) : % + 검색어 + % 첨가 필요
    }

    private BooleanExpression statusEq(OrderStatus statusCond) {
        if(statusCond == null) {
            return null;
        }

        return QOrder.order.status.eq(statusCond);
    }
}

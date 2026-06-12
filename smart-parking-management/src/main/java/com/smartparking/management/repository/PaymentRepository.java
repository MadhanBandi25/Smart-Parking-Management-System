package com.smartparking.management.repository;

import com.smartparking.management.entity.Payment;
import com.smartparking.management.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPaymentNumber(String paymentNumber);
    Optional<Payment> findByTransactionId(String transactionId);
    Optional<Payment> findByBookingId(Long bookingId);
    boolean existsByBookingId(Long bookingId);

    List<Payment> findByBookingUserId(Long userId);
    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    List<Payment> findByBookingUserIdAndPaymentTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
    List<Payment> findByPaymentTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT COALESCE(SUM(p.amount),0)
    FROM Payment p
    WHERE p.paymentStatus = 'PAID'
    """)
    BigDecimal getTotalRevenue();

    @Query("""
    SELECT COALESCE(SUM(p.amount),0)
    FROM Payment p
    WHERE p.paymentStatus = 'PAID'
    AND DATE(p.paymentTime) = CURRENT_DATE
    """)
    BigDecimal getTodayRevenue();

    @Query("""
    SELECT COALESCE(SUM(p.amount),0)
    FROM Payment p
    WHERE p.booking.user.id = :userId
    AND p.paymentStatus = 'PAID'
    """)
    BigDecimal getTotalAmountPaidByUserId(Long userId);

    long countByPaymentStatusAndPaymentTimeBetween(PaymentStatus paymentStatus, LocalDateTime start, LocalDateTime end);

    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
    FROM Payment p
    WHERE p.paymentStatus = 'PAID'
    AND p.paymentTime BETWEEN :start AND :end
    """)
    BigDecimal getRevenueBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("""
    SELECT COALESCE(SUM(p.amount), 0)
    FROM Payment p
    WHERE p.paymentStatus = 'PAID'
    AND p.booking.parkingArea.owner.id = :ownerId
    """)
    BigDecimal getTotalRevenueByOwnerId(@Param("ownerId") Long ownerId);
}
